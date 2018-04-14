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
public class MatMain {

    private static final String HOST = "120.26.139.183";

    private static final int PORT = 8088;

    public static void main(String[] args) throws Exception {

        long msn = 20190001L;

        int icn = 10000;

        ThreadPoolExecutor pool = new ThreadPoolExecutor(3000, 3000, 60,
                TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(3000),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 3000; i++) {
            pool.execute(new MatClient(new Socket(HOST, PORT), msn + i, icn + i));
            TimeUnit.MILLISECONDS.sleep(1);
        }
    }
}
