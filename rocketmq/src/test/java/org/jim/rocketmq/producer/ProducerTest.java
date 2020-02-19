package org.jim.rocketmq.producer;

import org.jim.rocketmq.consumer.RocketMessageConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProducerTest {

    @Autowired
    private RocketMessageProducer producer;

    @Autowired
    private RocketMessageConsumer consumer;

    @Test
    public void testDemo() throws Exception {
        consumer.start();
        Thread.sleep(200L);

        String topic = "topic2";
        String tag = "tag";
        String text = "Hello " + "Sync";
        producer.send(topic, tag, text);
        Thread.sleep(200L);

        text = "Hello " + "Async";
        producer.sendAsync(topic, tag, text);
        Thread.sleep(200L);

        text = "Hello " + "One Way";
        producer.sendOneWay(topic, tag, text);
        Thread.sleep(200L);

        text = "Hello " + "Delay";
        producer.sendDelay(topic, tag, text, 1);
        Thread.sleep(200L);

        Thread.sleep(5000L);

        producer.shutdown();
        Thread.sleep(200L);

        consumer.shutdown();
        Thread.sleep(200L);
    }

}
