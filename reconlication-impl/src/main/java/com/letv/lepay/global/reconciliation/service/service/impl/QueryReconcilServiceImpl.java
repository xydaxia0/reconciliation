package com.letv.lepay.global.reconciliation.service.service.impl;

import com.letv.lepay.common.beans.reconciliation.ReconciliationBO;
import com.letv.lepay.common.pojo.dao.ReconciliationRecord;
import com.letv.lepay.dao.ReconciliationRecordDao;
import com.letv.lepay.global.common.enums.MoneyType;
import com.letv.lepay.global.reconciliation.service.service.QueryReconcilService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service("queryReconcil")
public class QueryReconcilServiceImpl implements QueryReconcilService {
    @Resource
    private ReconciliationRecordDao reconciliationRecordDao;

    /**
     * 查询对账数据
     * @param businessId
     * @param start_time
     * @param end_time
     * @param begin
     * @param end
     * @return
     */
    @Override
    public ReconciliationBO queryReconcile(int businessId, String start_time, String end_time, int begin, int end) {
        List<ReconciliationRecord> reconciliationRecordList = reconciliationRecordDao.getRecordsByPage(businessId, start_time, end_time, begin, end);
        List<ReconciliationBO.Order> boOrderList = reconciliationRecordList.stream().map(this::convertOriginalRecordToBo).collect(toList());
        ReconciliationBO reconciliationBO = new ReconciliationBO();
        reconciliationBO.setOrders(boOrderList);
        int total = reconciliationRecordDao.getCountByBusinessIdAndTime(businessId, start_time, end_time);
        reconciliationBO.setPage_total(total);
        return reconciliationBO;
    }

    /**
     * 带channelid查询账单
     * @param channelIds
     * @param businessId
     * @param start_time
     * @param end_time
     * @param begin
     * @param end
     * @return
     */
    @Override
    public ReconciliationBO queryReconcile(List<Integer> channelIds, int businessId, String start_time, String end_time, int begin, int end) {
        List<ReconciliationRecord> reconciliationRecordList = reconciliationRecordDao.getRecByTimePaging(channelIds,businessId, start_time, end_time, begin, end);
        List<ReconciliationBO.Order> boOrderList = reconciliationRecordList.stream().map(this::convertOriginalRecordToBo).collect(toList());
        ReconciliationBO reconciliationBO = new ReconciliationBO();
        reconciliationBO.setOrders(boOrderList);
        int total = reconciliationRecordDao.getCountByChannelIdBusinessIdAndTime(channelIds,businessId, start_time, end_time);
        reconciliationBO.setPage_total(total);
        return reconciliationBO;
    }

    /**
     * 转换Original Record TO BO Order
     * @param reconciliationRecord - 原始数据
     * @return - BO Order
     */
    private ReconciliationBO.Order convertOriginalRecordToBo(ReconciliationRecord reconciliationRecord) {
        ReconciliationBO.Order boOrder = new ReconciliationBO.Order();
        boOrder.setBusiness_id(reconciliationRecord.getBusinessId());
        boOrder.setChannel_id(reconciliationRecord.getChannelId());
        boOrder.setChannel_seq(reconciliationRecord.getGatewaySequenceId());
        boOrder.setCompany_order_no(reconciliationRecord.getCompanyOrderNumber());
        boOrder.setCompany_refund_no(reconciliationRecord.getCompanyRefundNumber());
        boOrder.setCurrency(MoneyType.values()[reconciliationRecord.getPaymentCurrencyCode()].getCode());
        boOrder.setGateway_price(reconciliationRecord.getGatewayPrice());
        boOrder.setPrice(reconciliationRecord.getPaymentPrice());
        boOrder.setUser_id(reconciliationRecord.getUserId());
        boOrder.setUser_name(reconciliationRecord.getUserName());
        boOrder.setLepay_order_no(reconciliationRecord.getOrderNumber());
        boOrder.setLepay_payment_id(reconciliationRecord.getPaymentId());
        boOrder.setProcess_date(new DateTime(reconciliationRecord.getPaymentCompletedTime()));
        boOrder.setReconciliation_type(reconciliationRecord.getReconciliationType());
        boOrder.setSubmit_date(new DateTime(reconciliationRecord.getPaymentSubmitTime()));
        return boOrder;
    }
}
