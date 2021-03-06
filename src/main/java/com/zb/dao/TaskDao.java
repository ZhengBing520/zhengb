package com.zb.dao;

import com.zb.entity.Task;
import com.zb.param.TaskParam;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by bzheng on 2019/1/6.
 */
public interface TaskDao extends BaseDao<Task> {

    // 批量新增
    int insertList(@Param("list") List<Task> list);

    /**
     * 删除任务
     * @param businessId
     * @param dateTask
     * @return
     */
    @Delete("delete from t_task where business_id = #{businessId} and date_task = #{dateTask}")
    int deleteTasks(@Param("businessId") Integer businessId, @Param("dateTask") Date dateTask);

    /**
     * 查询任务条数
     * @param taskParam
     * @return
     */
    int selectCount(TaskParam taskParam);

    /**
     * 批量删除任务
     * @param ids
     * @param dateTask 时间
     */
    int deleteByIdS(@Param("ids") Integer[] ids, @Param("dateTask") Date dateTask);

    /**
     * 查询任务
     * @param businessId 商家id
     * @param dateTask 时间
     * @return
     */
    List<Task> findAll(@Param("businessId") Integer businessId, @Param("dateTask") Date dateTask);
}
