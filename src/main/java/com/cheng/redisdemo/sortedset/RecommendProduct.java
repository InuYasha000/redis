package com.cheng.redisdemo.sortedset;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 * @Date:2020/12/31 18:23
 * @Author: Cheng
 * @Description:推荐其他商品案例
 */
public class RecommendProduct {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 继续购买商品
     */
    public void continuePurchase(long productId, long otherProductId) {
        jedis.zincrby("continue_purchase_products::" + productId, 1, String.valueOf(otherProductId));
    }

    /**
     * 推荐其他人购买过的商品
     */
    public Set<Tuple> recommendProducts(long productId) {
        //这个是倒序排的？？为什么是倒序？
        return jedis.zrevrangeWithScores("continue_purchase_products::" + productId, 0, 5);
    }

    public static void main(String[] args) {
        RecommendProduct demo = new RecommendProduct();

        long productId = 2;

        for (int i = 0; i < 20; i++) {
            demo.continuePurchase(productId, i + 2);
        }

        for (int i = 0; i < 3; i++) {
            demo.continuePurchase(productId, i + 2);
        }

        Set<Tuple> recommendProducts = demo.recommendProducts(productId);
        System.out.println("推荐其他人购买过的商品："+recommendProducts);
    }
}
