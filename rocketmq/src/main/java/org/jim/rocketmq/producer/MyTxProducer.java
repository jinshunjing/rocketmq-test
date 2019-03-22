package org.jim.rocketmq.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分布式事务
 * 1. 保证本地事务的执行和消息发送到MQ的原子性：Half消费发送成功了回调executeLocalTransaction
 * 2. 回查机制：每隔10秒回调checkLocalTransaction
 * 3. 本地事务commit之后才把消息发给消费者
 */
public class MyTxProducer {

    private static final String NAME_SERVER_ADDR = "localhost:9876";

    private static final String PRODUCER_GROUP = "groupp1";
    private static final String TOPIC = "topic2";
    private static final String TAG = "tag1";

    private TransactionMQProducer producer;

    public void init() throws MQClientException {
        System.out.println("[Producer] init");

        producer = new TransactionMQProducer(PRODUCER_GROUP);
        producer.setNamesrvAddr(NAME_SERVER_ADDR);

        // 默认开启VIP，连接内部IP的10909端口
        producer.setVipChannelEnabled(false);

        // 默认3秒，经常timeout
        producer.setSendMsgTimeout(20_000);

        // 回查机制
        TransactionListener transactionListener = new TransactionListenerImpl();
        producer.setTransactionListener(transactionListener);

        // 线程池：回查的时候使用线程池
        ExecutorService executorService = new ThreadPoolExecutor(
                2, 5,
                100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("[client-transaction-msg-check-thread]");
                        return thread;
                    }
                });
        producer.setExecutorService(executorService);

        System.out.printf("[Producer] to start %s %n", System.currentTimeMillis());
        System.out.println();

        producer.start();

        System.out.printf("[Producer] started %s %s %n", System.currentTimeMillis(), Thread.currentThread().getName());
        System.out.println();
    }

    public void run() {
        System.out.println("[Producer] running");

        for (int i = 0; i < 1; i++) {
            System.out.println("[Producer] run " + i);
            try {
                Message msg = new Message(TOPIC, TAG,
                        ("Hello RocketMQ." + i).getBytes(RemotingHelper.DEFAULT_CHARSET));

                System.out.printf("[Producer] to send %s %n",
                        System.currentTimeMillis());
                System.out.println();

                SendResult result = producer.sendMessageInTransaction(msg, null);

                System.out.printf("[Producer] sent %s %s %s %n",
                        System.currentTimeMillis(), Thread.currentThread().getName(), result.toString());
                System.out.println();

                Thread.sleep(10);
            } catch (MQClientException|UnsupportedEncodingException|InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        producer.shutdown();
        System.out.println("[Producer] stopped");
    }

    private static class TransactionListenerImpl implements TransactionListener {
        private AtomicInteger transactionIndex = new AtomicInteger(0);

        private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();

        @Override
        public LocalTransactionState executeLocalTransaction(Message msg, Object o) {
            // Half消息发送成功之后，执行本地事务
            System.out.printf("[Execute Local Tx] begin %s %s %s %n",
                    System.currentTimeMillis(), Thread.currentThread().getName(), msg.getTransactionId());
            System.out.println();

            int value = transactionIndex.getAndIncrement();
            int status = value % 3;
            localTrans.put(msg.getTransactionId(), status);

            System.out.println("[Execute Local Tx] end");
            return LocalTransactionState.COMMIT_MESSAGE;
        }

        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt msg) {
            // 检查本地事务的状态
            System.out.printf("[Check Local Tx] begin %s %s %s %n",
                    System.currentTimeMillis(), Thread.currentThread().getName(), msg.getTransactionId());
            System.out.println();

            Integer status = localTrans.get(msg.getTransactionId());

            if (null != status) {
                switch (status) {
                    case 0:
                        return LocalTransactionState.UNKNOW;
                    case 1:
                        return LocalTransactionState.COMMIT_MESSAGE;
                    case 2:
                        return LocalTransactionState.ROLLBACK_MESSAGE;
                }
            }

            System.out.println("[Check Local Tx] end");
            return LocalTransactionState.COMMIT_MESSAGE;
        }
    }
}
