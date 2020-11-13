package com.cheng.redisdemo.hash;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date:2020/11/12 16:32
 * @Author: Cheng
 * @Description:博客
 */
public class BlogDemo {


    private static Jedis jedis = new Jedis("127.0.0.1");

    public static void main(String[] args) {
        BlogDemo demo = new BlogDemo();

        long id = demo.getBlogId();

        Map<String,String> blogMap = new HashMap<>();
        blogMap.put("title","redis博客");
        blogMap.put("content","redis内容");
        blogMap.put("author","cheng");
        blogMap.put("date","2020-11-12");
        blogMap.put("id",String.valueOf(id));

        demo.publishBlog(id,blogMap);
        Map<String, String> blogResult = demo.findBlogById(id);
        System.out.println(blogResult);

        Map<String, String> updatedBlog = new HashMap<String, String>();
        updatedBlog.put("title", "修改的Redistitle");
        updatedBlog.put("content", "修改的Redis的内容");
        demo.updateBlog(id,updatedBlog);
        blogResult = demo.findBlogById(id);
        System.out.println(blogResult);

        demo.incrementBlogLikeAmount(id);
        blogResult = demo.findBlogById(id);
        System.out.println(blogResult);

        blogResult = demo.findBlogById(id);
        System.out.println(blogResult);
    }

    private static void incrementBlogLikeAmount(long id) {
        jedis.hincrBy("article::"+id,"like",1);
    }

    private static Map<String, String> findBlogById(long id) {
        jedis.hincrBy("article::"+id,"view",1);
        return jedis.hgetAll("article::"+id);
    }

    private static void updateBlog(long id,Map<String, String> updatedBlog) {
        jedis.hmset("article::"+id,updatedBlog);
    }

    private static void publishBlog(long id, Map<String, String> blogMap) {
        if(jedis.hexists("article::"+id,"title")){
            return;
        }
        jedis.hmset("article::"+id,blogMap);
    }

    private static long getBlogId() {
        return jedis.incr("blog_id");
    }
}
