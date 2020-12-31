package com.cheng.redisdemo.set;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

/**
 * @Date:2020/12/31 17:19
 * @Author: Cheng
 * @Description:抽奖案例
 * sranmember(key,count),count值表示抽几个
 */
public class LotteryDraw {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 增加抽奖候选人
     */
    public void addLotteryDrawCandidate(long userId, long lotteryDrawEventId) {
        jedis.sadd("lottery_draw_event::" + lotteryDrawEventId + "::candidate", String.valueOf(userId));
    }

    /**
     * 实际进行抽奖,抽count个奖
     */
    public List<String> doLotteryDraw(long lotteryDrawEventId, int count){
        return jedis.srandmember("lottery_draw_event::"+lotteryDrawEventId+"::candidate",count);
    }

    public static void main(String[] args) {
        LotteryDraw lotteryDraw = new LotteryDraw();

        int lotteryDrawEventId = 100;

        for (int i = 0; i < 20; i++) {
            lotteryDraw.addLotteryDrawCandidate(i+1,lotteryDrawEventId);
        }

        List<String> lotteryDrawUsers = lotteryDraw.doLotteryDraw(lotteryDrawEventId,3);
        System.out.println("随机开奖："+lotteryDrawUsers);
    }
}
