package com.cheng.redisdemo.hash;

import redis.clients.jedis.Jedis;

/**
 * @Date:2020/11/13 14:33
 * @Author: Cheng
 * @Description:短地址网站demo
 */
public class ShortUrlDemo {

    private Jedis jedis;

    public ShortUrlDemo(){
        jedis = new Jedis("127.0.0.1");
        jedis.set("short_url_seed","123456789");
    }

    private static final String[] X36_ARRAY = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(",");

    public static void main(String[] args) {
        ShortUrlDemo demo = new ShortUrlDemo();

        String shortUrl = demo.getShortUrl();
        System.out.println(shortUrl);

        for (int i = 0; i < 100; i++) {
            demo.increatShortUrl(shortUrl);
        }

        long count = demo.getShortUrlCount(shortUrl);
        System.out.println(count);
    }

    private long getShortUrlCount(String shortUrl) {
        return Long.valueOf(jedis.hget("short_url_access_count",shortUrl));
    }

    private void increatShortUrl(String shortUrl) {
        jedis.hincrBy("short_url_access_count",shortUrl,1);
    }

    private String getShortUrl() {
        long shortUrlSeed = jedis.incr("short_url_seed");

        StringBuilder sb = new StringBuilder();
        while (shortUrlSeed>0){
            sb.append(X36_ARRAY[(int)(shortUrlSeed%36)]);
            shortUrlSeed/=36;
        }

        String shortUrl = sb.reverse().toString();
        jedis.hincrBy("short_url_access_count",shortUrl,1);
        jedis.hincrBy("url_mapper",shortUrl,1);
        return shortUrl;
    }
}
