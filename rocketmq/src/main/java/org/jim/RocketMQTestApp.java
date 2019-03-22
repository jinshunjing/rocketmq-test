package org.jim;

import org.jim.rocketmq.consumer.MySimpleConsumer;
import org.jim.rocketmq.producer.*;

public class RocketMQTestApp {

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MySimpleConsumer consumer = new MySimpleConsumer();
                    consumer.init();

                    Thread.sleep(10_000L);
                    consumer.run();

                    Thread.sleep(100_000L);
                    consumer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //MySyncProducer producer = new MySyncProducer();
                    //MyAsyncProducer producer = new MyAsyncProducer();
                    //MyOnewayProducer producer = new MyOnewayProducer();
                    MyScheduledProducer producer = new MyScheduledProducer();
                    //MyTxProducer producer = new MyTxProducer();

                    producer.init();

                    Thread.sleep(10_000L);
                    producer.run();

                    Thread.sleep(60_000L);
                    producer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
    }
}
