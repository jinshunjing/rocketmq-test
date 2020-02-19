package org.jim.rocketmq.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class MySimpleConsumer {
    private static final String NAME_SERVER_ADDR = "47.99.196.196:9876";

    private static final String GROUP = "group1";
    private static final String TOPIC = "topic2";

    private DefaultMQPushConsumer consumer;

    public void init() throws MQClientException  {
        System.out.println("[Consumer] init");

        consumer = new DefaultMQPushConsumer(GROUP);
        consumer.setNamesrvAddr(NAME_SERVER_ADDR);

        // 不使用VIP
        consumer.setVipChannelEnabled(false);

        consumer.subscribe(TOPIC, "*");

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext consumeConcurrentlyContext) {

                System.out.printf("%s %s Receive New Messages: %s %n", System.currentTimeMillis(), Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        System.out.println("[Consumer] to start");

        consumer.start();

        System.out.println("[Consumer] started");
    }

    public void run() {
        System.out.println("[Consumer] running");
    }

    public void close() {
        consumer.shutdown();
        System.out.println("[Consumer] stopped");
    }
}
