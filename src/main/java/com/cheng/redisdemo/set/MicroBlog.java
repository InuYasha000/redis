package com.cheng.redisdemo.set;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * @Date:2020/12/31 15:58
 * @Author: Cheng
 * @Description:微博关注案例
 */
public class MicroBlog {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * userId 关注 followUserId
     */
    public void follow(long userId, long followUserId) {
        //被关注人的关注人集合
        jedis.sadd("user::" + followUserId + "::followers", String.valueOf(userId));
        //自己的关注集合
        jedis.sadd("user::" + userId + "::follow_users", String.valueOf(followUserId));
    }

    /**
     * userId 取消关注 followUserId
     */
    public void unfollow(long userId,long followUserId){
        //被关注人的关注人集合
        jedis.srem("user::" + followUserId + "::followers", String.valueOf(userId));
        //自己的关注集合
        jedis.srem("user::" + userId + "::follow_users", String.valueOf(followUserId));
    }

    /**
     * 查看自己关注了那些人
     */
    public Set<String> getFollowUsers(long userId){
        return jedis.smembers("user::"+userId+"::follow_users");
    }
    /**
     * 查看自己关注人数
     */
    public long getFollowUsersCount(long userId){
        return jedis.scard("user::"+userId+"::follow_users");
    }

    /**
     * 查看自己被哪些人关注
     */
    public Set<String> getFollower(long userId){
        return jedis.smembers("user::"+userId+"::followers");
    }
    /**
     * 查看自己被关注的人数
     */
    public long getFollowerCount(long userId){
        return jedis.scard("user::"+userId+"::followers");
    }

    public static void main(String[] args) {
        MicroBlog demo = new MicroBlog();
        //定义用户id
        long userId = 1;
        long friendId = 2;
        long superstarId = 3;

        //定义关注的关系链
        demo.follow(userId,friendId);
        demo.follow(userId,superstarId);
        demo.follow(friendId,superstarId);

        //明星看看自己有哪些关注
        Set<String> superstarFollowers = demo.getFollower(superstarId);
        long superstarFollowersCount = demo.getFollowerCount(superstarId);
        System.out.println("明星看看被关注了："+superstarFollowers);
        System.out.println("明星看看被关注的人数："+superstarFollowersCount);

        //你朋友看看自己关注了谁
        Set<String> friendFollows = demo.getFollowUsers(friendId);
        long friendFollowCount = demo.getFollowUsersCount(friendId);
        System.out.println("朋友看看关注了："+friendFollows);
        System.out.println("朋友看看关注的人数："+friendFollowCount);

        //你朋友看看自己被谁关注了
        Set<String> friendFollowers = demo.getFollower(friendId);
        long friendFollowersCount = demo.getFollowerCount(friendId);
        System.out.println("朋友看看被谁关注了："+friendFollowers);
        System.out.println("朋友看看被关注的人数："+friendFollowersCount);

        //你看看自己关注了谁
        Set<String> myFollows = demo.getFollowUsers(userId);
        long myFollowCount = demo.getFollowUsersCount(userId);
        System.out.println("自己看看关注了："+myFollows);
        System.out.println("自己看看关注的人数："+myFollowCount);

        //你看看自己被谁关注了
        Set<String> myFollowers = demo.getFollower(userId);
        long myFollowersCount = demo.getFollowerCount(userId);
        System.out.println("看看自己被谁关注了："+myFollowers);
        System.out.println("看看自己被关注的人数："+myFollowersCount);

    }
}
