package com.cheng.redisdemo.hash;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @Auther: cheng
 * @Date: 2020/12/30 20:29
 * @Description:登录会话
 */
public class SessionDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 检查session是否有效
     */
    public boolean isSessionValid(String token) throws Exception {
        if (token == null && "".equals(token)) {
            return false;
        }

        //这里拿到的session就是一个json字符串，放一个用户 userId
        String session = jedis.hget("sessions", "session::" + token);
        if (session == null || "".equals(session)) {
            return false;
        }

        //有session还需要检查是否在有效期内
        String expireTime = jedis.hget("sessions::expire_time", "session::" + token);
        if (expireTime == null && "".equals(expireTime)) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date expireTimeDate = sdf.parse(expireTime);
        Date now = new Date();

        if (now.after(expireTimeDate)) {
            return false;
        }

        //token不为空，获取到的session不为空，session没有过期
        //此时表示session在有效期
        return true;
    }

    /**
     * 用户登录成功后初始化session
     */
    public void initSession(String userId, String token) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //设置24小时过期
        calendar.add(Calendar.HOUR, 24);
        String expireTimeDate = sdf.format(calendar.getTime());

        jedis.hset("sessions", "session::" + token, userId);
        jedis.hset("sessions::expire_time", "session::" + token, expireTimeDate);
    }

    /**
     * 模拟的登录方法
     */
    public String login(String userName, String pwd) throws Exception{
        System.out.println("基于用户名和密码登录：" + userName + "," + pwd);
        Random random = new Random();
        long userId = random.nextInt() * 100;
        //登录成功后生成一块令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        //基于令牌和用户id初始化session
        initSession(String.valueOf(userId), token);
        return token;
    }

    public static void main(String[] args) throws Exception {
        SessionDemo demo = new SessionDemo();

        //第一次访问系统，token都是空的
        boolean isSessionValid = demo.isSessionValid(null);
        System.out.println("第一次访问系统的session校验结果：" + isSessionValid);

        //登录,获取到token
        String token = demo.login("张三","123456");

        isSessionValid = demo.isSessionValid(token);
        System.out.println("第二次访问系统的session校验结果：" + isSessionValid);
    }
}
