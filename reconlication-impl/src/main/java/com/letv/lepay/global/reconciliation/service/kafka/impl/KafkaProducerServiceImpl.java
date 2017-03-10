package com.letv.lepay.global.reconciliation.service.kafka.impl;

import com.letv.lepay.common.constant.Consts;
import com.letv.lepay.global.reconciliation.service.kafka.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
public class KafkaProducerServiceImpl implements KafkaProducerService {

    @Override
    public void sendMessage(String topic, String key, String value) {
        try{
            String newtopic=topic;
            KafkaServerUtilHolder.producer.send(new ProducerRecord<String, String>(newtopic, key, value));
            log.debug("kafkaProducerStatistic-topic:{},key:{}",newtopic,key);//观查一段时间以后会去掉。
        } catch (Exception e) {
            log.error("send kafka message error!", e);
            log.error(String.format("KafkaServerUtil sendMessage error with producer:%s,topic:%s,key:%s,value:%s",  KafkaServerUtilHolder.producer,topic,key,value));
        }
    }

    private static class KafkaServerUtilHolder {
        private static KafkaProducer<String, String> producer;
        static {
            try {
                Properties props = new Properties();
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Consts.APP.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
                props.put(ProducerConfig.ACKS_CONFIG, "1");
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
                props.put(ProducerConfig.CLIENT_ID_CONFIG, Consts.APP.getProperty(ProducerConfig.CLIENT_ID_CONFIG));
                producer = new KafkaProducer<String, String>(props);
            } catch (Exception e) {
                log.error("kafka config error!", e);
            }
        }
    }
}
