package xyz.vimtool.concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * 最大线程数
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2020/4/6
 */
public class MaxThread {

    public static void main(String[] args) {

        for (int i = 0;; i++) {
            System.out.println("i = " + i);
            new Thread(new HoldThread()).start();
        }
    }
}

class HoldThread extends Thread {
    CountDownLatch cdl = new CountDownLatch(1);

    public HoldThread() {
        this.setDaemon(true);
    }

    @Override
    public void run() {
        try {
            cdl.await();
        } catch (InterruptedException e) {
        }
    }
}
