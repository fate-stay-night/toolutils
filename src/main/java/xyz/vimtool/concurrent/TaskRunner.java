package xyz.vimtool.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019/8/28
 */
public class TaskRunner implements Runnable {

    private String name;

    public TaskRunner() {}

    public TaskRunner(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("返回结果" + name);
    }
}
