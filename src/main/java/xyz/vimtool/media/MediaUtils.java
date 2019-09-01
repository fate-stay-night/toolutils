package xyz.vimtool.media;

import ws.schild.jave.DefaultFFMPEGLocator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 视频剪切公用类
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019-08-15
 */
public class MediaUtils {

    /**
     * ffmpeg程序路径
     */
    private static String FFMPEG_PATH;

    /**
     * 初始化时利用反射获取jave-1.0.1.jar中FFmpeg.exe的路径
     */
    static {
        DefaultFFMPEGLocator locator = new DefaultFFMPEGLocator();
        try {
            Method method = locator.getClass().getDeclaredMethod("getFFMPEGExecutablePath");
            method.setAccessible(true);
            FFMPEG_PATH = (String) method.invoke(locator);
            method.setAccessible(false);
            System.out.println("--- 获取FFmpeg可执行路径成功 --- 路径信息为：" + FFMPEG_PATH);
        } catch (Exception e) {
            System.out.println("--- 获取FFmpeg可执行路径失败！ --- 错误信息： " + e.getMessage());
        }
    }

    /**
     * 截取视频第一帧
     *
     * @param videoPath 视频地址
     * @param imagePath 图片地址
     * @return boolean
     * @throws Exception exception
     */
    public static boolean getImageFromVideo(String videoPath, String imagePath) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(FFMPEG_PATH);
        cmd.add("-i");
        cmd.add(videoPath);
        cmd.add("-vframes");
        cmd.add("1");
        cmd.add("-ss");
        cmd.add("0");
        cmd.add("-f");
        cmd.add("image2");
        cmd.add(imagePath);
        return execCmd(cmd);
    }

    /**
     * 剪切视频
     *
     * @param srcPath    源视频路径
     * @param targetPath 目标视频路径
     * @param startTime  剪切视频起始时间s
     * @param timeLength 剪切视频长度s
     * @param ratio      视频输出比例（4:3）
     * @return
     */
    public static boolean cutVideo(String srcPath, String targetPath, Long startTime,
                                  Long timeLength, String ratio) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(FFMPEG_PATH);
        if (Objects.nonNull(startTime) && Objects.nonNull(timeLength)) {
            cmd.add("-ss");
            cmd.add(startTime.toString());
            cmd.add("-t");
            cmd.add(timeLength.toString());
        }

        cmd.add("-i");
        cmd.add(srcPath);
        if (Objects.nonNull(ratio)) {
            cmd.add("-aspect");
            cmd.add(ratio);
        }

        cmd.add("-y");
        cmd.add(targetPath);

        return execCmd(cmd);
    }

    /**
     * 执行指令
     *
     * @param cmd 指令
     * @return boolean
     * @throws Exception exception
     */
    private static boolean execCmd(List<String> cmd) throws Exception {
        // 调用线程命令
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(cmd);
        // 将标准输出流和错误输出流合二为一
        builder.redirectErrorStream(true);
        Process process = builder.start();

        // 读取处理输出，防止出现阻塞问题
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = reader.readLine()) != null) {
                // todo 写入日志
            }
        }

        return process.waitFor() == 0;
    }
}
