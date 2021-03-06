package com.zb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zb.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by bzheng on 2019/1/10.
 */
public class SummaryDto extends BaseDto {

    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("日期")
    private Date dateSummary;

    /**
     *
     */
    @ApiModelProperty("总单")
    private Integer billSum;

    /**
     *
     */
    @ApiModelProperty("总收（每个店铺每日应收款统计）")
    private BigDecimal receivableSum;

    /**
     *
     */
    @ApiModelProperty("总放（每个店铺每日成本统计）")
    private BigDecimal putSum;

    /**
     *
     */
    @ApiModelProperty("余（每个店铺每日利润统计）")
    private BigDecimal residue;

    /**
     *
     */
    @ApiModelProperty("剩余（实际最终到手利润，余-其他人提成）")
    private BigDecimal residueLast;

    /**
     *
     */
    @ApiModelProperty("实际收款（每个店铺每日实收统计）")
    private BigDecimal receipt;

    /**
     *
     */
    @ApiModelProperty("差额（总收-实际收款）")
    private BigDecimal balance;

    /**
     *
     */
    @ApiModelProperty("备注")
    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "收款账户")
    List<CardAccountCollectDto> cardAccountCollectDtos;

    @ApiModelProperty(value = "介绍人提成")
    List<ReferrerAccountCollectDto> referrerAccountCollectDtos;

    public Date getDateSummary() {
        return dateSummary;
    }

    public void setDateSummary(Date dateSummary) {
        this.dateSummary = dateSummary;
    }

    public Integer getBillSum() {
        return billSum;
    }

    public void setBillSum(Integer billSum) {
        this.billSum = billSum;
    }

    public BigDecimal getReceivableSum() {
        return receivableSum;
    }

    public void setReceivableSum(BigDecimal receivableSum) {
        this.receivableSum = receivableSum;
    }

    public BigDecimal getPutSum() {
        return putSum;
    }

    public void setPutSum(BigDecimal putSum) {
        this.putSum = putSum;
    }

    public BigDecimal getResidue() {
        return residue;
    }

    public void setResidue(BigDecimal residue) {
        this.residue = residue;
    }

    public BigDecimal getResidueLast() {
        return residueLast;
    }

    public void setResidueLast(BigDecimal residueLast) {
        this.residueLast = residueLast;
    }

    public BigDecimal getReceipt() {
        return receipt;
    }

    public void setReceipt(BigDecimal receipt) {
        this.receipt = receipt;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<CardAccountCollectDto> getCardAccountCollectDtos() {
        return cardAccountCollectDtos;
    }

    public void setCardAccountCollectDtos(List<CardAccountCollectDto> cardAccountCollectDtos) {
        this.cardAccountCollectDtos = cardAccountCollectDtos;
    }

    public List<ReferrerAccountCollectDto> getReferrerAccountCollectDtos() {
        return referrerAccountCollectDtos;
    }

    public void setReferrerAccountCollectDtos(List<ReferrerAccountCollectDto> referrerAccountCollectDtos) {
        this.referrerAccountCollectDtos = referrerAccountCollectDtos;
    }
}
