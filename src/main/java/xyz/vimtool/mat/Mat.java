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

    private static final String HOST = "120.26.139.183";

    private static final int PORT = 8088;

    private static Executor executor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {

        long msn = 2123456789000000l;

        ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 60,
                TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(10),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 1; i++) {
            pool.execute(new MatClient(new Socket(HOST, PORT), msn + i));
//            executor.execute(new MatClient(new Socket(HOST, PORT), msn + i));
            Thread.sleep(1);
        }
    }
}
