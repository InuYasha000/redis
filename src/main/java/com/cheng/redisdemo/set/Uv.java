package com.cheng.redisdemo.set;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Date:2020/12/31 11:39
 * @Author: Cheng
 * @Description:统计Uv set结构去重
 */
public class Uv {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 添加用户访问记录
     */
    public void addUserAccess(long userId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = sdf.format(new Date());
        //当天用户访问集合
        jedis.sadd("user_access::" + today, String.valueOf(userId));
    }

    /**
     * 获取当天网站UV的值
     */
    public long getUV() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = sdf.format(new Date());
        return jedis.scard("user_access::" + today);
    }

    public static void main(String[] args) {
        Uv uv = new Uv();
        for (int i = 0; i < 105; i++) {
            for (int j = 0; j < 10; j++) {
                //105个用户每个都访问10次，此时UV应该是100个
                uv.addUserAccess(i+1);
            }
        }
        long Uvresult = uv.getUV();
        System.out.println("当日UV为:"+Uvresult);
    }
}
