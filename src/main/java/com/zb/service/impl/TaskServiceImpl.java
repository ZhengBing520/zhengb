package com.zb.service.impl;

import com.zb.common.Constant;
import com.zb.common.utils.DateUtil;
import com.zb.dao.TaskDao;
import com.zb.dto.*;
import com.zb.entity.Task;
import com.zb.param.TaskParam;
import com.zb.request.TaskRequest;
import com.zb.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by bzheng on 2019/1/6.
 */
@Service
public class TaskServiceImpl extends BaseServiceImpl<TaskDto, Task, TaskDao> implements TaskService {

    @Autowired
    BusinessService businessService;

    @Autowired
    DetailService detailService;

    @Autowired
    CommissionService commissionService;

    @Autowired
    LoginService loginService;

    /**
     * 批量插入任务(导入的任务是第二天的，重复导入就覆盖)
     *
     * @param taskRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertList(TaskRequest taskRequest) {
        // 获取登录用户
        UserDto loginUser = loginService.getLoginUser();
        // 通过商家名称找到商家id
        String businessName = taskRequest.getBusinessName();
        Assert.notNull(businessName, "商家名称不能为空");
        BusinessDto business = businessService.selectByName(businessName);
        if (!Objects.isNull(business)) {
            // 修改商家的时间
            BusinessDto dto = new BusinessDto();
            BeanUtils.copyProperties(business, dto);
            businessService.update(dto);
        } else {
            // 新增商家
            business = new BusinessDto();
            business.setName(businessName);
            Integer insertId = businessService.insert(business);
            business.setId(insertId);
        }
        Integer id = business.getId();
        List<Task> taskList = taskRequest.getTaskList();
        if (ObjectUtils.isEmpty(taskList)) {
            throw new IllegalArgumentException("导入任务为空");
        }
        // 获取佣金规则
        List<CommissionDto> commissionDtoList = commissionService.selectCommissionListByBusinessId(id);
        Date nextDay = DateUtil.getBeginDayOfTomorrow();
        // 删除第二天任务
        deleteTasks(loginUser, id, nextDay);
        // 总单数
        Integer billTotal = taskList.size();
        // 应收 (所有任务的应收 单个任务应收：单个总价 + 佣金)
        BigDecimal receivableDecimal = new BigDecimal(0);
        // 放  (所有任务的放 单个任务放：单个总价 + 成本佣金)
        BigDecimal putDecimal = new BigDecimal(0);

        for (Task task : taskList) {
            task.setCreateId(loginUser.getId());
            task.setBusinessId(id);
            // 第二天
            task.setDateTask(nextDay);

            // 佣金（通过总价找到佣金） 总价 = 单价 * 件数
            BigDecimal price = task.getPrice();
            Integer buyNum = task.getBuyNum();
            // 单个任务的总价 -- 计算佣金
            BigDecimal totalPriceByOne = price.multiply(new BigDecimal(buyNum));
            // 佣金
            CommissionDto commissionDto = getReceivableToTask(commissionDtoList, totalPriceByOne.doubleValue());
            if (!Objects.isNull(commissionDto)) {
                receivableDecimal = receivableDecimal.add(totalPriceByOne.add(commissionDto.getCommission()));
                putDecimal = putDecimal.add(totalPriceByOne.add(commissionDto.getCommissionCost()));
            }

        }

        // 余 (应收-放)
        BigDecimal residue = receivableDecimal.subtract(putDecimal);
        int i = dao.insertList(taskList);
        if (0 < i) {

            // 生成每日的明细
            DetailDto detailDto = new DetailDto();
            // 商家id
            detailDto.setBusinessId(id);
            // 日期
            detailDto.setDateDetail(nextDay);
            // 总单商家任务表行数统计
            detailDto.setBillTotal(billTotal);
            // 应收
            detailDto.setReceivable(receivableDecimal);
            // 放
            detailDto.setPut(putDecimal);
            // 余
            detailDto.setResidue(residue);
            // 实收默认等于应收
            detailDto.setReceipt(receivableDecimal);
            detailService.insert(detailDto);
        }
        return 0 < i;
    }

    /**
     * 删除任务级联删除明细
     * @param loginUser
     * @param businessId
     * @param nextDay
     * @return
     */
    @Override
    public Boolean deleteTasks(UserDto loginUser, Integer businessId, Date nextDay) {
        // 判断能否删除，同一商家一天只能被同意用户删除
        if (Objects.nonNull(loginUser)) {
            TaskParam taskParam = new TaskParam(businessId, null, nextDay);
            int count = dao.selectCount(taskParam);
            if (0 < count) {
                // 说明明天有任务
                taskParam.setCreateId(loginUser.getId());
                int i = dao.selectCount(taskParam);
                if (0 >= i) {
                    // 说明不是此用户导入的任务，不能导入
                    throw new IllegalArgumentException("导入失败：同一商家一天只能被同一用户导入");
                }
            }
        }

        int i = dao.deleteTasks(businessId, nextDay);
        if (i > 0) {
            // 删除明细
            detailService.deleteByBusinessIdAndDate(businessId, nextDay);
        }

        return i > 0;
    }

    /**
     * 通过单价找佣金规则
     *
     * @param commissionDtoList 佣金规则
     * @param price             单价
     * @return
     */
    private CommissionDto getReceivableToTask(List<CommissionDto> commissionDtoList, double price) {
        CommissionDto commissionDto = null;
        if (Objects.isNull(commissionDtoList)) {
            return commissionDto;
        }
        for (CommissionDto dto : commissionDtoList) {
            if (price - dto.getPriceMin() >= 0 && price - dto.getPriceMax() <= 0) {
                commissionDto = dto;
                break;
            } else {
                continue;
            }

        }
        return commissionDto;
    }

    @Override
    protected void setParam(TaskDto taskDto) {
        super.setParam(taskDto);
        if (Objects.isNull(taskDto)) {
            taskDto = new TaskDto();
        }
        // 获取登录用户信息
        UserDto loginUser = loginService.getLoginUser();
        if (Objects.equals(Constant.UserType.GENERAL, loginUser.getType())) {
            // 普通用户，只能看到自己导入数据
            taskDto.setCreateId(loginUser.getId());
        }

    }
}
