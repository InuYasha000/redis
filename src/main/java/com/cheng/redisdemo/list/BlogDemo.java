package com.cheng.redisdemo.list;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
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

        Map<String, String> blog = new HashMap<>();
        // 构造20篇博客数据
        for (int i = 0; i < 20; i++) {
            id = demo.getBlogId();

            blog = new HashMap<String, String>();
            blog.put("id", String.valueOf(id));
            blog.put("title", "第" + (i + 1) + "篇博客");
            blog.put("content", "学习第" + (i + 1) + "篇博客，是一件很有意思的事情");
            blog.put("author", "石杉");
            blog.put("time", "2020-01-01 10:00:00");

            demo.publishBlog(id, blog);
        }

        int pageNo = 1;
        int pageSize = 10;

        List<String> blogPage = demo.findBlogByPage(pageNo, pageSize);
        System.out.println("展示第一页的博客......");
        for (String blogId : blogPage) {
            blog = demo.findBlogById(Long.valueOf(blogId));
            System.out.println(blog);
        }

        pageNo = 2;

        blogPage = demo.findBlogByPage(pageNo, pageSize);
        System.out.println("展示第二页的博客......");
        for (String blogId : blogPage) {
            blog = demo.findBlogById(Long.valueOf(blogId));
            System.out.println(blog);
        }

        Map<String, String> blogResult = demo.findBlogById(id);
        System.out.println(blogResult);

        Map<String, String> updatedBlog = new HashMap<String, String>();
        updatedBlog.put("title", "修改的Redistitle");
        updatedBlog.put("content", "修改的Redis的内容");
        demo.updateBlog(id, updatedBlog);
        blogResult = demo.findBlogById(id);
        System.out.println(blogResult);

        demo.incrementBlogLikeAmount(id);
        blogResult = demo.findBlogById(id);
        System.out.println(blogResult);

        blogResult = demo.findBlogById(id);
        System.out.println(blogResult);
    }

    private List<String> findBlogByPage(int pageNo, int pageSize) {
        int start = (pageNo - 1) * pageSize;
        int end = pageNo * pageSize - 1;
        return jedis.lrange("article_list",start,end);
    }

    private static void incrementBlogLikeAmount(long id) {
        jedis.hincrBy("article::" + id, "like", 1);
    }

    private static Map<String, String> findBlogById(long id) {
        jedis.hincrBy("article::" + id, "view", 1);
        return jedis.hgetAll("article::" + id);
    }

    private static void updateBlog(long id, Map<String, String> updatedBlog) {
        jedis.hmset("article::" + id, updatedBlog);
    }

    private static void publishBlog(long id, Map<String, String> blogMap) {
        if (jedis.hexists("article::" + id, "title")) {
            return;
        }
        jedis.hmset("article::" + id, blogMap);
        jedis.lpush("article_list",String.valueOf(id));
    }

    private static long getBlogId() {
        return jedis.incr("blog_id");
    }
}
