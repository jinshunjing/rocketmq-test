package org.jim.rocketmq.consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class MySimpleConsumerTest {

    @Test
    public void testConsumer() throws Exception {
        MySimpleConsumer consumer = new MySimpleConsumer();
        consumer.init();
        consumer.run();
    }

}
