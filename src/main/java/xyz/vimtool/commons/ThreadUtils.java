package xyz.vimtool.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 线程相关操作的简易封装
 */
public class ThreadUtils {

    /**
     * 执行shell命令
     *
     * @param command 命令
     *
     * @return 执行命令后的输出
     */
    public static String execute(String... command) {
        List<String> list = new ArrayList<>();
        if (ThreadUtils.isLinux()) {
            list.add("/bin/sh");
            list.add("-c");
        } else {
            list.add("cmd");
            list.add("/c");
        }

        list.addAll(Arrays.asList(command));
        StringBuffer result = new StringBuffer();
        Process process = null;
        BufferedReader input = null;
        BufferedReader error = null;
        try {
            process = Runtime.getRuntime().exec(list.toArray(new String[list.size()]));

            input = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            error = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            while (true) {
                String line = input.readLine();
                if (line != null) {
                    result.append(line + "\n");
                } else {
                    line = error.readLine();
                    if (line != null) {
                        result.append(line + "\n");
                    } else {
                        break;
                    }
                }
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (error != null) {
                try {
                    error.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result.toString();
    }

    /**
     * 获取当前进程PID
     *
     * @return PID
     */
    public static String getPID() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

    /**
     * 判断当前系统是否为Linux系统
     */
    public static boolean isLinux() {
        return System.getProperty("os.name").matches(".*Linux*.");
    }
    /**
     * 当前线程睡眠一段时间
     * @param ms 单位毫秒
     * @return true/false
     */
    public static boolean sleep(long ms) {
        try {
            Thread.sleep(ms);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 重置性能计数器
     */
    public static void resetTimer() {
        timer.set(System.currentTimeMillis());
    }

    public static long getTime() {
        long now = System.currentTimeMillis();
        long time = (now - timer.get());
        timer.set(now);
        return time;
    }

    private static ThreadLocal<Long> timer = new ThreadLocal<Long>();
}
