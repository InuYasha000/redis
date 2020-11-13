package com.cheng.redisdemo.list;

import redis.clients.jedis.Jedis;

/**
 * @Date:2020/11/12 17:47
 * @Author: Cheng
 * @Description:秒杀demo
 */
public class SecKillDemo {

    private static Jedis jedis;

    public SecKillDemo(){
        jedis = new Jedis("127.0.0.1");
    }

    public static void main(String[] args) {
        SecKillDemo demo = new SecKillDemo();

        for (int i = 0; i < 100; i++) {
            demo.enqueueSecKillRequest("第"+(i+1)+"个秒杀");
        }

        while (true){
            String secKillRequest = demo.dequeueSecKillRequest();

            if(null==secKillRequest||"".equals(secKillRequest)||"null".equals(secKillRequest)){
                break;
            }
            System.out.println(secKillRequest);
        }
    }

    private String dequeueSecKillRequest() {
        return jedis.rpop("sec_kill_request");
    }

    private void enqueueSecKillRequest(String s) {
        jedis.lpush("sec_kill_request",s);
    }
}
