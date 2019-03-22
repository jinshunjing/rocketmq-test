package org.jim.rocketmq.producer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;

public class MyAsyncProducer {

    private static final String NAME_SERVER_ADDR = "localhost:9876";

    private static final String PRODUCER_GROUP = "groupp1";
    private static final String TOPIC = "topic2";
    private static final String TAG = "tag1";
    private static final String KEY = "key1";

    private DefaultMQProducer producer;

    public void init() throws MQClientException {
        producer = new DefaultMQProducer(PRODUCER_GROUP);
        producer.setNamesrvAddr(NAME_SERVER_ADDR);

        // 默认开启VIP，连接内部IP的10909端口
        producer.setVipChannelEnabled(false);

        // 默认3秒，经常timeout
        producer.setSendMsgTimeout(20_000);

        System.out.printf("[Producer] to start %s %n", System.currentTimeMillis());
        System.out.println();

        producer.start();

        System.out.printf("[Producer] started %s %s %n", System.currentTimeMillis(), Thread.currentThread().getName());
        System.out.println();
    }

    public void run() throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException, MQBrokerException {
        for (int i = 0; i < 2; i++) {
            System.out.println("[Producer] run " + i);

            Message msg = new Message(TOPIC, TAG, KEY,
                    ("Hello RocketMQ." + i).getBytes(RemotingHelper.DEFAULT_CHARSET));

            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult result) {
                    System.out.printf("[Producer] sent %s %s %s %n",
                            System.currentTimeMillis(), Thread.currentThread().getName(), result.toString());
                    System.out.println();
                }

                @Override
                public void onException(Throwable throwable) {
                    throwable.printStackTrace();
                }
            }, 20_000L);
        }
    }

    public void close() {
        producer.shutdown();
        System.out.println("[Producer] stopped");
    }
}
