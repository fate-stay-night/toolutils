package xyz.vimtool.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019/8/27
 */
public class TaskTest {

    public static void main(String[] args) throws Exception {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i + "任务");
        }

        ExecutorService executor = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1), new TaskThreadFactory("task_test"));
        long start = System.currentTimeMillis();
        List<Future<String>> urls = new ArrayList<>();
        for (String name : list) {
            Future<String> future = executor.submit(new Task(name));
            urls.add(future);
        }
        for (Future<String> f : urls) {
            System.out.println(f.get());
        }

//        for (String name : list) {
//            executor.execute(new TaskRunner(name));
//        }
        executor.shutdown();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
