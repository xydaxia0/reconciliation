package com.letv.lepay.global.reconciliation.service;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by xavier on 2017/3/7.
 */
public interface ConciliationDataProducer {

    /**
     * Notify the server to produce the data.
     * @param channelIds - channel id List
     * @param busId - business id
     * @param startTime - start date
     * @param endTime - end date
     * @param beginIndex - data begin index
     * @param endIndex - date end index
     * @return - result
     */
    boolean produceConciliationDate(List<Integer> channelIds, int busId, DateTime startTime, DateTime endTime, int beginIndex, int endIndex);

}
