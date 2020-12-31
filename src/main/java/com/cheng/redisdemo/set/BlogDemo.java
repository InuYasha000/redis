package com.cheng.redisdemo.set;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Date:2020/12/30 18:40
 * @Author: Cheng
 * @Description:博客
 */
public class BlogDemo {

    private static Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 获取blog id
     *
     * @return
     */
    public static long getBlogId() {
        return jedis.incr("blog_id_counter");
    }

    /**
     * 发表一篇博客
     */
    public static boolean publishBlogResult(long id, Map<String, String> blog, String[] tags) {
        //是否存在这个key的title
        if (jedis.hexists("article::" + id, "title")) {
            return false;
        }

        jedis.hmset("article::" + id, blog);

        jedis.lpush("blog_list", String.valueOf(id));

        //添加标签
        jedis.sadd("article::" + id + "::tags", tags);

//        jedis.msetnx("article:" + id + ":title", title,
//                "article:" + id + ":author", author, "article:" + id + ":time", time,
//                "article:" + id + ":content", content);
//
//        //每次发表长度记录长度
//        long length = jedis.strlen("article:" + id + ":content");
//        jedis.setnx("article:" + id + ":content_length", String.valueOf(length));
        return true;
    }

    /**
     * 查看一篇博客
     *
     * @param id
     * @return
     */
    public static Map<String, String> getBlog(long id) {
//    public static List<String> getBlog(long id) {
//        List<String> blogResult = jedis.mget("article:" + id + ":title",
//                "article:" + id + ":author",
//                "article:" + id + ":time",
//                "article:" + id + ":content",
//                "article:" + id + ":content_length",
//                "article:" + id + ":like_count",
//                "article:" + id + ":view_count");
//        //查看的时候增加浏览次数
//        viewCount(id);
//        return blogResult;

        //拿到标签
        Set<String> tags = jedis.smembers("article::" + id + "::tags");

        Map<String, String> blog = jedis.hgetAll("article::" + id);
        blog.put("tags",String.join(",",tags));
        viewCount(id);
        return blog;
    }

    /**
     * 更新一篇博客
     */
    public static void updateBlog(long id, Map<String, String> updatedMap) {
//    public static void updateBlog(long id, String title, String content) {
        String content = updatedMap.get("content");
        if (content != null && !"".equals(content)) {
            updatedMap.put("length", String.valueOf(content.length()));
        }

        jedis.hmset("article::" + id, updatedMap);

//        jedis.mset("article:" + id + ":title", title, "article:" + id + ":content", content);
//        long length = jedis.strlen("article:" + id + ":content");
//        jedis.setnx("article:" + id + ":content_length", String.valueOf(length));
    }

    /**
     * 预览博客
     * hash没办法支持 getrange 功能
     */
    public static String previewBlog(long id) {
        return jedis.getrange("article:" + id + ":content", 0, 10);
    }

    /**
     * 对博客点赞
     */
    public static void likeBlog(long id) {
        jedis.hincrBy("article::" + id, "like_count", 1);
//        jedis.incr("article:" + id + ":like_count");
    }

    /**
     * 增加博客浏览次数
     *
     * @param id
     */
    public static void viewCount(long id) {
//        jedis.incr("article:" + id + ":view_count");
        jedis.hincrBy("article::" + id, "view_count", 1);
    }

    public List<String> findBlogByPage(int pageNo, int pageSize) {
        int startIndex = (pageNo - 1) * pageSize;
        int endIndex = (pageNo * pageSize) - 1;

        return jedis.lrange("blog_list", startIndex, endIndex);
    }

    public static void main(String[] args) {
        BlogDemo demo = new BlogDemo();

        String title = "我喜欢雪redis22222";
        String content = "学习redis是快乐的事情22222";
        String author = "石衫22222";
        String time = "2020-01-01 10:00:00 22222";
        long id = getBlogId();

        //博客是hash数据结构
        Map<String, String> blog = new HashMap<>();
        blog.put("id", String.valueOf(id));
        blog.put("title", title);
        blog.put("author", author);
        blog.put("time", time);
        blog.put("content", content);
        blog.put("content_length", String.valueOf(content.length()));

        //发表博客
        demo.publishBlogResult(id, blog, new String[]{"redis", "中间件", "缓存"});

        //更新博客
        String updateTitle = "更新后" + title;
        String updatedContent = "更新后的" + content;

        Map<String, String> updatedBlog = new HashMap<>();
        updatedBlog.put("title", "我特别喜欢学习redis");
        updatedBlog.put("content", "我喜欢到官方网站学习");
        updatedBlog.put("content_length", String.valueOf("我喜欢到官方网站学习".length()));

        //更新博客
        demo.updateBlog(id, updatedBlog);

        //构造博客
        for (int i = 0; i < 20; i++) {
            title = "第" + i + "篇博客";
            content = "学习第" + i + "篇博客是很快乐的事情";
            author = "石衫 " + i + "-----";
            time = "2020-01-01 10:00:00 " + i + "-----";
            id = getBlogId();

            //博客是hash数据结构
            blog = new HashMap<>();
            blog.put("id", String.valueOf(id));
            blog.put("title", title);
            blog.put("author", author);
            blog.put("time", time);
            blog.put("content", content);
            blog.put("content_length", String.valueOf(content.length()));

            //发表博客
            demo.publishBlogResult(id, blog, new String[]{"redis", "中间件", "缓存"});
        }

        //分页浏览
        int pageNo = 1;
        int pageSize = 10;

        List<String> firstPage = demo.findBlogByPage(pageNo, pageSize);
        for (String tempId : firstPage) {
            blog = demo.getBlog(Long.valueOf(tempId));
            System.out.println("分页查看内容--" + blog);
        }

        pageNo = 2;
        List<String> secondPage = demo.findBlogByPage(pageNo, pageSize);
        for (String tempId : secondPage) {
            blog = demo.getBlog(Long.valueOf(tempId));
            System.out.println("分页查看内容--" + blog);
        }

        //预览博客
//        String prevContent = demo.previewBlog(id);
//        System.out.println("博客预览内容：" + prevContent);

        //点击查看具体内容
//        List<String> blog = demo.getBlog(id);
        Map<String, String> blogResult = demo.getBlog(id);
        System.out.println("查看博客详细内容：" + blogResult);

        //点赞
        demo.likeBlog(id);
        //点击查看具体内容
        blogResult = demo.getBlog(id);
        System.out.println("查看博客详细内容：" + blogResult);

    }
}
