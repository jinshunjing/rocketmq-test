package org.jim.rocketmq.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class MySyncProducerTest {

    @Test
    public void testSend() throws Exception {
        MySyncProducer producer = new MySyncProducer();
        producer.init();
        producer.run();

        Thread.sleep(2_000L);
    }

}
