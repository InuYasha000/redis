package com.cheng.redisdemo.set;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * @Date:2020/12/31 15:47
 * @Author: Cheng
 * @Description:网站投票
 */
public class Vote {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 投票
     */
    public void vote(long userId, long voteItemId) {
        jedis.sadd("vote_item_users::" + voteItemId, String.valueOf(userId));
    }

    /**
     * 检查用户对投票箱是否投过票
     */
    public boolean hasVoted(long userId, long voteItemId) {
        return jedis.sismember("vote_item_users::" + voteItemId, String.valueOf(userId));
    }

    /**
     * 获取投票项有哪些投票人
     */
    public Set<String> getVotedUsers(long voteItemId) {
        return jedis.smembers("vote_item_users::" + voteItemId);
    }

    /**
     * 获取投票项的投票人数
     */
    public long getVoteUsersCount(long voteItemId) {
        return jedis.scard("vote_item_users::" + voteItemId);
    }

    public static void main(String[] args) {
        //用户id
        long userId1 = 1;
        long userId2 = 2;
        long userId3 = 3;

        //投票项id
        long voteItemId = 100;
        Vote vote = new Vote();
        vote.vote(userId1, voteItemId);
        vote.vote(userId2, voteItemId);
        vote.vote(userId3, voteItemId);

        System.out.println("用户1是否投票过：" + vote.hasVoted(userId1, voteItemId));
        System.out.println("用户2是否投票过：" + vote.hasVoted(userId2, voteItemId));
        System.out.println("用户3是否投票过：" + vote.hasVoted(userId3, voteItemId));

        System.out.println("有哪些人投票过："+vote.getVotedUsers(voteItemId));

        System.out.println("有多少人投票过："+vote.getVoteUsersCount(voteItemId));
    }
}
