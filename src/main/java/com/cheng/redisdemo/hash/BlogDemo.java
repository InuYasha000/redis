package com.cheng.redisdemo.hash;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

/**
 * @Date:2020/12/30 18:40
 * @Author: Cheng
 * @Description:博客
 */
public class BlogDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 获取blog id
     *
     * @return
     */
    public long getBlogId() {
        return jedis.incr("blog_id_counter");
    }

    /**
     * 发表一篇博客
     *
     * @param title
     * @param author
     * @param time
     * @param content
     */
    public void publishBlogResult(String title, String author, String time, String content, int id) {
        Long publishBlogResult = jedis.msetnx("article:" + id + ":title", title,
                "article:" + id + ":author", author, "article:" + id + ":time", time,
                "article:" + id + ":content", content);

        //每次发表长度记录长度
        long length = jedis.strlen("article:" + id + ":content");
        jedis.setnx("article:" + id + ":content_length", String.valueOf(length));
    }

    /**
     * 查看一篇博客
     *
     * @param id
     * @return
     */
    public List<String> getBlog(int id) {
        List<String> blogResult = jedis.mget("article:" + id + ":title",
                "article:" + id + ":author",
                "article:" + id + ":time",
                "article:" + id + ":content",
                "article:" + id + ":content_length",
                "article:" + id + ":like_count");
        return blogResult;
    }

    /**
     * 更新一篇博客
     */
    public void updateBlog(long id, String title, String content) {
        jedis.mset("article:" + id + ":title", title, "article:" + id + ":content", content);

        long length = jedis.strlen("article:" + id + ":content");
        jedis.setnx("article:" + id + ":content_length", String.valueOf(length));
    }

    /**
     * 预览博客
     */
    public String previewBlog(long id) {
        return jedis.getrange("article:" + id + ":content", 0, 10);
    }

    /**
     * 对博客点赞
     *
     * @param id
     */
    public void likeBlog(long id) {
        jedis.incr("article:" + id + ":like_count");
    }

    public static void main(String[] args) {
        BlogDemo demo = new BlogDemo();
    }
}
