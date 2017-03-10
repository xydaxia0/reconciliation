package com.letv.lepay.global.reconciliation.service.service;

import com.letv.lepay.common.beans.reconciliation.ReconciliationBO;

import java.util.List;

public interface QueryReconcilService {
    ReconciliationBO queryReconcile(int businessId, String start_time, String end_time, int begin, int end);
    ReconciliationBO queryReconcile(List<Integer> channelIds, int businessId, String start_time, String end_time, int begin, int end);
}
