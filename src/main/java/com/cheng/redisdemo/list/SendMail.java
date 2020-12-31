package com.cheng.redisdemo.list;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @Date:2020/12/31 11:22
 * @Author: Cheng
 * @Description:发送邮件
 * list的brpop阻塞式获取
 */
public class SendMail {

    private Jedis jedis = new Jedis("127.0.0.1");

    /**
     * 入队发送邮件队列
     */
    public void enqueueSendMailQueue(String sendMailTask){
        jedis.lpush("send_mail_task_queue",sendMailTask);
    }

    /**
     * 阻塞式获取发送邮件任务
     */
    public List<String> takeSendMailTask(){
       return jedis.brpop(5,"send_mail_task_queue");
    }

    public static void main(String[] args) {
        SendMail sendMail = new SendMail();
        System.out.println("阻塞式获取");
        List<String> task = sendMail.takeSendMailTask();
        System.out.println("阻塞完毕");
        System.out.println(task);

        sendMail.enqueueSendMailQueue("第一个邮件发送任务");
        task = sendMail.takeSendMailTask();
        System.out.println(task);
    }
}
