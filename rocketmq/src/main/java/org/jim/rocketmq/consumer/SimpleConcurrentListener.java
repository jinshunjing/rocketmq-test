package org.jim.rocketmq.consumer;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Message Listener
 *
 * @author JSJ
 */
public class SimpleConcurrentListener implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                    ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        System.out.printf("%s %s Receive New Messages: %s %n", System.currentTimeMillis(), Thread.currentThread().getName(), msgs);
        for (MessageExt msg : msgs) {
            String text = new String(msg.getBody());
            System.out.println(text);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
