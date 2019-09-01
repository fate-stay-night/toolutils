package xyz.vimtool.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 能够获取结果的异步任务
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019/8/27
 */
public class Task implements Callable<String> {

    private String name;

    public Task() {}

    public Task(String name) {
        this.name = name;
    }

    @Override
    public String call() throws Exception {
        TimeUnit.SECONDS.sleep(3);
        return "返回结果" + name;
    }
}
