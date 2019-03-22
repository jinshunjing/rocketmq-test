# rocketmqtest
RocketMQ Test


## 异常处理

### connect to <XXX:10909> failed
默认开启VIP，broker注册时使用内网地址，端口为10909。关闭：
`setVipChannelEnabled(false)`

启动broker的时候指定配置文件
`-c conf/broker.conf`
在配置文件里设置IP地址
`brokerIP1 = XXX`

### timeout
Producer连上Broker之后需要预热，直接send容易timeout
发送消息默认3秒，可以设置的长一点
`producer.setSendMsgTimeout(20_000);`