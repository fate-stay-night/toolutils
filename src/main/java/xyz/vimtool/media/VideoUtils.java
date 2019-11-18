package xyz.vimtool.media;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

/**
 * 视频处理
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019/10/25
 */
public class VideoUtils {

    public static void main(String[] args) throws Exception {
        File file = new File("/Users/xiao/Desktop/dd.mp4");
        FileInputStream stream = new FileInputStream(file);
//        ByteArrayInputStream byteStream = new ByteArrayInputStream("st".getBytes());

        // 截取视频首帧图片，javacv
        FFmpegFrameGrabber ffmpegFrameGrabber = new FFmpegFrameGrabber(stream);
        ffmpegFrameGrabber.start();

        //截取首帧图片
        Frame frame = ffmpegFrameGrabber.grabFrame();

        //首帧无图像时，继续截取，直到有图像
        if (frame.image == null) {
            int i = 0;
            int length = ffmpegFrameGrabber.getLengthInFrames();
            while (i < length) {
                frame = ffmpegFrameGrabber.grabFrame();
                if (frame.image != null) {
                    break;
                }
            }
        }
        Java2DFrameConverter frameConverter = new Java2DFrameConverter();
        BufferedImage bufferedImage = frameConverter.getBufferedImage(frame);
        ffmpegFrameGrabber.stop();
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileOutputStream outputStream = new FileOutputStream(new File("/Users/xiao/Desktop/ddd.jpg"));
        ImageIO.write(bufferedImage, "jpg", outputStream);
    }

    public static byte[] decodeValue(ByteBuffer bytes) {
        int len = bytes.limit() - bytes.position();
        byte[] bytes1 = new byte[len];
        bytes.get(bytes1);
        return bytes1;
    }
}