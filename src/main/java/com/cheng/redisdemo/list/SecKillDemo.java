package com.cheng.redisdemo.list;

import redis.clients.jedis.Jedis;

/**
 * @Date:2020/12/31 9:42
 * @Author: Cheng
 * @Description:秒杀demo
 */
public class SecKillDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 入队秒杀请求
     */
    public void enqueueSecKillRequest(String secKillRequest) {
        jedis.lpush("sec_kill_request_queue", secKillRequest);
    }

    /**
     * 从秒杀请求出队,先进先出队列
     *
     * @return
     */
    public String dequeueSecKillRequest() {
        return jedis.rpop("sec_kill_request_queue");
    }

    public static void main(String[] args) throws Exception {
        SecKillDemo demo = new SecKillDemo();

        for (int i = 0; i < 20; i++) {
            demo.enqueueSecKillRequest("第" + i + "个秒杀请求");
        }

        String secKillRequest = "";
        while (secKillRequest!=null){
            secKillRequest = demo.dequeueSecKillRequest();
            System.out.println(secKillRequest);
        }
    }

}
