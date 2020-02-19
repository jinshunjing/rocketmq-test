package org.jim.rocketmq.config;


import org.jim.rocketmq.consumer.RocketMessageConsumer;
import org.jim.rocketmq.consumer.SimpleConcurrentListener;
import org.jim.rocketmq.producer.RocketMessageProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ
 *
 * @author JSJ
 */
@Configuration
public class RocketConfig {
    @Value("${spring.profiles.active:dev}")
    private String profile;

    @Value("${rocket.name_server.address:localhost:9876}")
    private String nameServerAddr;

    @Value("${rocket.producer.group:Group}")
    private String producerGroup;

    @Value("${rocket.producer.timeout:10000}")
    private int producerTimeout;

    @Value("${rocket.consumer.group:Group}")
    private String consumerGroup;

    @Bean
    public RocketMessageConsumer simpleConsumer() {
        SimpleConcurrentListener listener = new SimpleConcurrentListener();
        RocketMessageConsumer consumer = new RocketMessageConsumer(profile, nameServerAddr, consumerGroup, "topic2");
        consumer.registerMessageListener(listener);
        return consumer;
    }

    @Bean
    public RocketMessageProducer defaultProducer() {
        return new RocketMessageProducer(profile, nameServerAddr, producerGroup, producerTimeout);
    }
}
