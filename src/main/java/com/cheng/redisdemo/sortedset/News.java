package com.cheng.redisdemo.sortedset;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Date;
import java.util.Set;

/**
 * @Date:2020/12/31 18:08
 * @Author: Cheng
 * @Description:新闻案例
 */
public class News {
    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 添加新闻
     */
    public void addNews(long newsId, double timestamp) {
        jedis.zadd("news", timestamp, String.valueOf(newsId));
    }

    public Set<Tuple> searchNews(long maxTimestamp, long minTimestamp, int index, int count) {
        return jedis.zrevrangeByScoreWithScores("news", maxTimestamp, minTimestamp, index, count);
    }

    public static void main(String[] args) {
        News news = new News();
        for (int i = 0; i < 20; i++) {
            news.addNews(i + 1, i + 1);
        }

        int pageNo = 1;
        int pageSize = 10;
        int startIndex = (pageNo - 1) * pageSize;

        long maxTimestamp = 18;
        long minTimestamp = 2;

        Set<Tuple> searchResult = news.searchNews(maxTimestamp, minTimestamp, startIndex, pageSize);
        System.out.println("搜索结果：" + searchResult);
    }
}
