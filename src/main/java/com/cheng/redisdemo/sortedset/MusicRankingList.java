package com.cheng.redisdemo.sortedset;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Set;

/**
 * @Date:2020/12/31 17:50
 * @Author: Cheng
 * @Description:音乐排行榜
 * sorted set ：zadd ，zrevrank，zrevrangeWithScores， zincrby
 */
public class MusicRankingList {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 把新的音乐加入排行榜
     */
    public void addMusic(long musicId){
        jedis.zadd("music_ranking_list",0.0,String.valueOf(musicId));
    }

    /**
     * 增加音乐分数
     */
    public void incrementMusicScore(long musicId,double score){
        jedis.zincrby("music_ranking_list",score,String.valueOf(musicId));
    }

    /**
     * 获取音乐在排行榜中排第几名
     */
    public long getMusicRank(long musicId){
        return jedis.zrevrank("music_ranking_list",String.valueOf(musicId));
    }

    /**
     * 获取音乐排行榜
     */
    public Set<Tuple> getMusicRankingList(){
//        return jedis.zrevrange("music_ranking_list",0,100);
        return jedis.zrevrangeWithScores("music_ranking_list",0,5);
    }

    public static void main(String[] args) {
        MusicRankingList demo = new MusicRankingList();

        for (int i = 0; i < 20; i++) {
            demo.addMusic(i+1);
        }

        demo.incrementMusicScore(1,9.5);
        demo.incrementMusicScore(2,4.5);
        demo.incrementMusicScore(3,5.5);
        demo.incrementMusicScore(4,6.5);

        long songRank = demo.getMusicRank(1);
        //这个排名不是1，是0
        System.out.println("查看歌曲1的排名："+songRank);

        Set<Tuple> musicRankingList = demo.getMusicRankingList();
        System.out.println("查看排名前5："+musicRankingList);
    }
}
