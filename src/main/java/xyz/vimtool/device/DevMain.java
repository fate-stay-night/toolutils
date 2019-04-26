package xyz.vimtool.device;

import xyz.vimtool.mat.MatClient;

import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 按摩垫多线程模拟
 *
 * @author zhangzheng
 * @version 1.0
 * @since jdk1.8
 * @date 2018-3-1
 */
public class DevMain {

    private static final String HOST = "120.26.139.183";

    private static final int PORT = 8078;

    public static void main(String[] args) throws Exception {

        int msn = 20200006;

        ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 60,
                TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 1; i++) {
            pool.execute(new DevClient(new Socket(HOST, PORT), msn + i));
            TimeUnit.MILLISECONDS.sleep(1);
        }
    }
}
