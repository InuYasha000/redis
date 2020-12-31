package com.cheng.redisdemo.list;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ListPosition;

import java.util.List;
import java.util.Random;

/**
 * @Date:2020/12/31 10:36
 * @Author: Cheng
 * @Description:OA待办事项
 */
public class TodoDemo {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 添加待办事项
     */
    public void addTodoEvent(long userId, String todoEvent) {
        jedis.lpush("todo_event::" + userId, todoEvent);
    }

    /**
     * 分页查询
     */
    public List<String> findTodoByPage(long userId, int pageNo, int pageSize) {
        int startIndex = (pageNo - 1) * pageSize;
        int endIndex = pageNo * pageSize - 1;

        return jedis.lrange("todo_event::" + userId, startIndex, endIndex);
    }

    /**
     * 插入待办事项,ListPosition表示插到前面还是后面
     */
    public void insertTodoEvent(long userId,
                                ListPosition position,
                                String todoEvent,
                                String targetTodoEvent) {
        jedis.linsert("todo_event::" + userId, position, targetTodoEvent, todoEvent);
    }

    /**
     * 修改待办事项
     */
    public void updateTodoEvent(long userId, int index, String todoEvent) {
        jedis.lset("todo_event::" + userId, index, todoEvent);
    }

    /**
     * 完成待办事项
     */
    public void finishTodoEvent(long userId, String todoEvent) {
        //删除列表中0个跟todoEvent相等的元素，也就是全部删掉
        jedis.lrem("todo_event::" + userId, 0, todoEvent);
    }

    public static void main(String[] args) {
        TodoDemo demo = new TodoDemo();
        long userId = 1;
        //加入待办事项
        for (int i = 0; i < 20; i++) {
            demo.addTodoEvent(userId, "第" + i + "个待办事项");
        }

        //分页查询
        List<String> todoEventPage = demo.findTodoByPage(userId,1,10);
        for (String str:todoEventPage){
            System.out.println(str);
        }

        //插入待办事项
        Random random = new Random();
        int index = random.nextInt(todoEventPage.size());
        System.out.println("随机数index:"+index);
        String targetTodoEvent = todoEventPage.get(index);
        demo.insertTodoEvent(userId,ListPosition.BEFORE,"插入的待办事项",targetTodoEvent);

        //分页查询
        todoEventPage = demo.findTodoByPage(userId,1,10);
        for (String str:todoEventPage){
            System.out.println(str);
        }

        //修改待办事项
        index = random.nextInt(todoEventPage.size());
        System.out.println("随机数index："+index);
        demo.updateTodoEvent(userId,index,"随机修改的事项");

        //完成第一个待办事项
        demo.finishTodoEvent(userId,todoEventPage.get(0));

        //分页查询
        todoEventPage = demo.findTodoByPage(userId,1,10);
        for (String str:todoEventPage){
            System.out.println(str);
        }

    }

}
