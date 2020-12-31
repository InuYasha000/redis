package com.cheng.redisdemo.set;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * @Date:2020/12/31 15:27
 * @Author: Cheng
 * @Description:朋友圈
 */
public class Moments {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 对朋友圈点赞
     */
    public void likeMoment(long userId, long momentId) {
        jedis.sadd("moment_like_users::" + momentId, String.valueOf(userId));
    }

    /**
     * 取消点赞
     */
    public void disLike(long userId, long momentId) {
        //去掉
        jedis.srem("moment_like_users::" + momentId, String.valueOf(userId));
    }

    /**
     * 查看自己是否对某个朋友圈点赞过
     */
    public boolean hasLikedMoment(long userId, long momentId) {
        //是否在集合里面
        return jedis.sismember("moment_like_users::" + momentId, String.valueOf(userId));
    }

    /**
     * 获取你的一条朋友圈有哪些人点赞
     */
    public Set<String> getMomentLikeUsers(long momentId) {
        return jedis.smembers("moment_like_users::" + momentId);
    }

    /**
     * 获取你的一条朋友圈有多少人点赞
     */
    public long getMomentLikeUsersCount(long momentId) {
        return jedis.scard("moment_like_users::" + momentId);
    }

    public static void main(String[] args) throws Exception{
        //你的用户id
        long userId = 1;
        //你的朋友圈id
        long momentId = 100;
        //朋友1 id
        long friendId = 2;
        //朋友2 id
        long otherFriendId = 3;

        Moments moments = new Moments();
        //你的朋友1对你的朋友圈点赞
        moments.likeMoment(friendId,momentId);
        //你的朋友1取消点赞
        moments.disLike(friendId,momentId);

        boolean hasLikeMoment = moments.hasLikedMoment(friendId,momentId);
        System.out.println("朋友1是否对你的朋友圈点赞过："+hasLikeMoment);

        //你的朋友2对你的朋友圈点赞
        moments.likeMoment(otherFriendId,momentId);
        hasLikeMoment = moments.hasLikedMoment(otherFriendId,momentId);
        System.out.println("朋友2是否对你的朋友圈点赞过："+hasLikeMoment);

        //自己查看自己朋友圈点赞情况
        Set<String> momentLikes = moments.getMomentLikeUsers(momentId);
        long momentLikeCount = moments.getMomentLikeUsersCount(momentId);
        System.out.println("查看自己的朋友圈，被"+momentLikeCount+"个人点赞,"+"是："+momentLikes);

    }

}
