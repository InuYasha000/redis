package com.cheng.redisdemo.hash;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @Date:2020/11/12 16:57
 * @Author: Cheng
 * @Description:session1登录
 * hset   key  field  value
 * key代表一个hash  field-value才是key-value对
 */
public class SessionDemo {

    SessionDemo() {
        jedis = new Jedis("127.0.0.1");
    }

    private static Jedis jedis;

    public static void main(String[] args) throws Exception {
        SessionDemo demo = new SessionDemo();

        boolean isValid = demo.isValidToken(null);
        System.out.println(isValid);

        String token = demo.login("cheng", "123456");
        System.out.println(token);

        isValid = demo.isValidToken(token);
        System.out.println(isValid);
    }

    private String login(String username, String password) {
        Random random = new Random();
        long userId = random.nextInt() * 100;

        String token = UUID.randomUUID().toString().replace("-","");
        initSession(token,userId);
        return token;
    }

    private void initSession(String token, long userId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //1天
        calendar.add(Calendar.HOUR,24);
        Date date = calendar.getTime();

        jedis.hset("session1","session1_token",token);
        jedis.hset("session1","session1_expireTime",sdf.format(date));
    }

    private boolean isValidToken(String s) throws Exception {

        if (s == null || s.equals("")) {
            return false;
        }
        String session1Token = jedis.hget("session1", "session1_token");
        if (session1Token == null || session1Token.equals("")) {
            return false;
        }

        String expireTimeString = jedis.hget("session1", "session1_expireTime");
        Date expireTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(expireTimeString);
        Date now = new Date();

        if (now.after(expireTime)) {
            return false;
        }

        //如果token不为空，而且获取到的session1不为空，而且session1没过期
        // 此时可以认为session1在有效期内
        return true;
    }
}
