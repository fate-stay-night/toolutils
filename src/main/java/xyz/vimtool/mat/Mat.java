package xyz.vimtool.mat;

import java.net.Socket;
import java.util.concurrent.*;

/**
 * 按摩垫多线程模拟
 *
 * @author zhangzheng
 * @version 1.0
 * @since jdk1.8
 * @date 2018-3-1
 */
public class Mat {

    private static final String HOST = "116.62.131.210";

    private static final int PORT = 8088;

    private static Executor executor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {

        long msn = 1201708000040l;

        ThreadPoolExecutor pool = new ThreadPoolExecutor(3000, 11000, 60,
                TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(10000),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 10000; i++) {
            pool.execute(new MatClient(new Socket(HOST, PORT), msn + i));
//            executor.execute(new MatClient(new Socket(HOST, PORT), msn + i));
            Thread.sleep(1);
        }
    }
}
