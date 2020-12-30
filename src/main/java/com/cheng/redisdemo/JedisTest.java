package com.cheng.redisdemo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.List;

/**
 * @Date:2020/12/30 16:24
 * @Author: Cheng
 * @Description:
 * 最简单的key-value
 * 分布式锁：基于nx实现的最简单的分布式锁
 * mset/msetnx,mget:博客发布和查看
 * getrange：拿到某个范围内的数据，可以用作预览
 * strlen：计数内容
 * append：追加日志
 * incr/decr:递增，博客点赞
 */
public class JedisTest {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1");

        //最简单的key-value
        jedis.set("key","1");
        System.out.println(jedis.get("key"));
        jedis.del("key");

        jedis.del("lock_test");
        //分布式锁
        String result = jedis.set("lock_test","lock_value", SetParams.setParams().nx());
        System.out.println("第一次加锁结果："+result);

        jedis.del("lock_test");
        Long result1 = jedis.setnx("lock_test","lock_value");
        System.out.println("第二次加锁结果："+result1);

        result = jedis.set("lock_test","value_test",SetParams.setParams().nx());
        System.out.println("第三次加锁结果："+result);
        jedis.del("lock_test");

        //mget和mset
        //博客发布，修改和查看
        jedis.del("article:1:title","如何学好redis",
                "article:1:author","zhss","article:1:time","2020-12-31 00:00:00",
                "article:1:content","redis内容");
        Long publishBlogResult = jedis.msetnx("article:1:title","如何学好redis",
                "article:1:author","zhss","article:1:time","2020-12-31 00:00:00",
                "article:1:content","redis内容");
//        jedis.mset("article:1:title","如何学好redis","article:1:author","zhss","article:1:time","2020-12-31 00:00:00","article:1:content","redis内容");
        System.out.println("发布博客结果："+publishBlogResult);

        //查看博客
        List<String> blogResult = jedis.mget("article:1:title","article:1:author","article:1:time","article:1:content");
        System.out.println("查看blog结果："+blogResult);

        //修改博客
        jedis.mset("article:1:title","修改后的title","article:1:content","修改后的content");
        blogResult = jedis.mget("article:1:title","article:1:author","article:1:time","article:1:content");
        System.out.println("修改后再次查看blog结果："+blogResult);

        //统计博客次数
        Long length = jedis.strlen("article:1:content");
        System.out.println("blog内容长度："+length);

        //预览功能
        String contentPreview = jedis.getrange("article:1:content",0,5);
        System.out.println("博客预览："+contentPreview);

        jedis.del("article:1:title","如何学好redis",
                "article:1:author","zhss","article:1:time","2020-12-31 00:00:00",
                "article:1:content","redis内容");

        //操作日志审计功能
        jedis.setnx("operation_log_2020_12_30","");
        for (int i = 0; i < 10; i++) {
            jedis.append("operation_log_2020_12_30","今天第"+i+"条日志："+i+"\n");
        }
        String operationLog = jedis.get("operation_log_2020_12_30");
        System.out.println("今天所有的操作日志：\n"+operationLog);
        jedis.del("operation_log_2020_12_30");

        //递增
        for (int i = 0; i < 10; i++) {
            //已经递增后的值
            Long orderId = jedis.incr("order_id_counter");
//            jedis.incrBy("order_id_counter",1);
//            jedis.incrByFloat("order_id_counter",2.0);
            System.out.println("生成的唯一id："+orderId);
        }
        jedis.del("order_id_counter");

        //点赞
        for (int i = 0; i < 30; i++) {
            jedis.incr("article:1:dianzan");
        }
        String blogDZCount = jedis.get("article:1:dianzan");
        System.out.println("点赞次数:"+blogDZCount);

        //取消点赞
        Long count = jedis.decr("article:1:dianzan");
        System.out.println("取消点赞后的点赞次数:"+count);
        jedis.del("article:1:dianzan");

    }
}
