package com.letv.lepay.global.reconciliation.service.kafka;

/**
 * Created by masen on 2016/12/12.
 */
public interface KafkaProducerService {
    void sendMessage(final String topic, final String key, final String value);
}
