package com.cheng.redisdemo.list;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @Date:2020/11/13 17:41
 * @Author: Cheng
 * @Description:代办事宜
 */
public class TodoEventDemo {

    private Jedis jedis;

    public TodoEventDemo(){
        jedis = new Jedis("127.0.0.1");
    }

    public static void main(String[] args) {
        TodoEventDemo demo = new TodoEventDemo();
        long userId = 1;

        //新增
        for (int i = 0; i < 20; i++) {
            demo.addTodoEvent(1,"第"+i+"个代办事宜");
        }

        //批量
        int pageNo = 1;
        int pageSize = 10;
        List<String> list = demo.getTodoEventByPage(pageNo,pageSize,userId);
        for (String str:list){
            System.out.println(str);
        }


    }

    private List<String> getTodoEventByPage(int pageNo, int pageSize, long userId) {
        int pageStart = (pageNo-1)*pageSize;
        int pageEnd = pageNo*pageSize-1;
        List<String> list = jedis.lrange("todoevent::"+userId,pageStart,pageEnd);
        return list;
    }

    private void addTodoEvent(long userId,String event) {
        jedis.lpush("todoevent::"+userId,event);
    }
}
