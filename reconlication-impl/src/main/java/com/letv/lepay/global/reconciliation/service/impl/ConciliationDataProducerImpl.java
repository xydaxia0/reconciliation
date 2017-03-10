package com.letv.lepay.global.reconciliation.service.impl;

import com.google.common.base.Preconditions;
import com.letv.lepay.common.beans.reconciliation.ReconciliationBO;
import com.letv.lepay.global.reconciliation.service.ConciliationDataProducer;
import com.letv.lepay.global.reconciliation.service.kafka.KafkaProducerService;
import com.letv.lepay.global.reconciliation.service.service.QueryReconcilService;
import com.letv.lepay.global.reconciliation.service.util.JsonUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Conciliation data producer implementation
 * Created by xavier on 2017/3/7.
 */
@Slf4j
@Service
public class ConciliationDataProducerImpl implements ConciliationDataProducer {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Resource
    private QueryReconcilService queryReconcil;

    @Resource
    private KafkaProducerService kafkaProducerService;

    @Resource
    private ExecutorService threadPool;

    private static final String TOPIC = "conciliation_data";

    /**
     * Notify the server to produce the data.
     *
     * @param channelIds - channel id List
     * @param busId      - business id
     * @param startTime  - start date
     * @param endTime    - end date
     * @param beginIndex - data begin index
     * @param endIndex   - date end index
     * @return - result
     */
    @Override
    public boolean produceConciliationDate(List<Integer> channelIds, int busId, @NonNull DateTime startTime, @NonNull DateTime endTime, int beginIndex, int endIndex) {
        Preconditions.checkArgument(beginIndex >= 0, "begin index should greater than zero! Now begin index is : " + beginIndex);
        Preconditions.checkArgument(beginIndex <= endIndex, "begin index should less than or equal with end index! begin index : " + beginIndex + " end index : " + endIndex);
        CompletableFuture.runAsync(() -> {ReconciliationBO reconciliationBO = channelIds.isEmpty() ? queryReconcil.queryReconcile(busId, startTime.toString(PATTERN), endTime.toString(PATTERN), beginIndex, endIndex) : queryReconcil.queryReconcile(channelIds, busId, startTime.toString(PATTERN), endTime.toString(PATTERN), beginIndex, endIndex);
            String key = UUID.randomUUID().toString();
            log.info("START TO SEND MESSAGE TOPIC : {}, KEY : {}", TOPIC, key);
            try {
                kafkaProducerService.sendMessage(TOPIC, key, JsonUtil.toJson(reconciliationBO));
            } catch (IOException e) {
                log.error("send msg to kafka error!", e);
            }
            log.info("SEND MESSAGE TOPIC : {}, KEY : {} completed!", TOPIC, key);}, threadPool);
        return true;
    }
}
