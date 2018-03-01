package xyz.vimtool.mat;

import java.net.Socket;

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

    public static void main(String[] args) throws Exception {

        long msn = 1201708000040l;

        for (int i = 0; i < 21; i++) {
            new MatClient(new Socket(HOST, PORT), msn + i).start();
            Thread.sleep(3000);
        }
    }
}
