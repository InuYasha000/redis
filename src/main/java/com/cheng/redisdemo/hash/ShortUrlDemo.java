package com.cheng.redisdemo.hash;

import redis.clients.jedis.Jedis;

/**
 * @Date:2020/12/30 18:10
 * @Author: Cheng
 * @Description:短网址追踪案例
 */
public class ShortUrlDemo {

    public ShortUrlDemo(){
        jedis.set("short_url_seed","100000000000");
    }

    private static final String X36 = "0123456789ABCDEFGHIJKLMNOBQRSTUVWXYZ";
    private static final String[] X36_Array = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,B,Q,R,S,T,U,V,W,X,Y,Z".split(",");

    private Jedis jedis = new Jedis("127.0.0.1");

    public String getShortUrl(String longUrl) {
        Long num = jedis.incr("short_url_seed");
        StringBuffer sb = new StringBuffer();
        while (num > 0) {
            sb.append(X36_Array[(int) (num % 36)]);
            num /= 36;
        }
        String shortUrl = sb.reverse().toString();

        //初始化到redis中这个短连接的次数为0
        //short_url_access_count代表map
        //short_url_access_count: {
        //	http://t.cn/XsGGA9d: 152,
        //	http://t.cn/I93yUUaF: 269
        //}
        jedis.hset("short_url_access_count",shortUrl,"0");
        //维护短连接和长连接
        jedis.hset("url_mapping",shortUrl,longUrl);

        return shortUrl;
    }

    /**
     * 点击短连接==>对短连接访问次数增长
     * @param shortUrl
     */
    public void incrShortUrlAccessCount(String shortUrl){
        jedis.hincrBy("short_url_access_count",shortUrl,1);
    }

    /**
     * 获取短连接地址的访问次数
     * @param shortUrl
     * @return
     */
    public Long getShortUrlCount(String shortUrl){
        return Long.valueOf(jedis.hget("short_url_access_count",shortUrl));
    }

    /**
     * 根据短连接获取长连接
     * @param shortUrl
     * @return
     */
    public String getLongUrlCount(String shortUrl){
        return jedis.hget("url_mapping",shortUrl);
    }

    public static void main(String[] args) throws Exception{
        ShortUrlDemo demo = new ShortUrlDemo();
        String shortUrl = demo.getShortUrl("http://redis.com/index.html?dfd=sf&dfd=sf&dfd=sf&dfd=sf&dfd=sf&");
        System.out.println("短连接地址："+shortUrl);

        //点击访问短连接
        for (int i = 0; i < 129; i++) {
            demo.incrShortUrlAccessCount(shortUrl);
        }

        Long accessCount = demo.getShortUrlCount(shortUrl);
        System.out.println("短连接访问次数："+accessCount);

        System.out.println("短连接对应长连接："+demo.getLongUrlCount(shortUrl));
    }
}
