package com.cheng.redisdemo.set;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @Date:2020/12/31 17:33
 * @Author: Cheng
 * @Description:商品搜索案例
 * sinter 取交集来表示有这些关键词的商品
 */
public class ProductSearch {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 添加商品附带关键词
     */
    public void addProduct(long productId, List<String> keywords) {
        for (String key : keywords) {
            jedis.sadd("keyword::" + key + "::products", String.valueOf(productId));
        }
    }

    /**
     * 根据关键词查询有这些关键词的商品
     */
    public Set<String> searchProduct(List<String> keywords) {
        List<String> keywordSetKeys = new ArrayList<>();

        for (String keyword : keywords) {
            keywordSetKeys.add("keyword::" + keyword + "::products");
        }
        String[] keywordArray = keywordSetKeys.toArray(new String[keywords.size()]);
        //查询有这些关键词的商品
        return jedis.sinter(keywordArray);
    }

    public static void main(String[] args) {
        ProductSearch demo = new ProductSearch();

        demo.addProduct(1, Arrays.asList("手机", "iphone", "潮流"));
        demo.addProduct(2, Arrays.asList("手机", "iphone", "炫酷"));
        demo.addProduct(3, Arrays.asList("天蓝色", "iphone", "64G"));
        demo.addProduct(4, Arrays.asList("天蓝色", "iphone", "128G"));

        System.out.println(demo.searchProduct(Arrays.asList("天蓝色")));

    }
}
