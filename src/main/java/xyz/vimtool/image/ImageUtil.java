package xyz.vimtool.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 图片处理
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019/12/26
 */
public class ImageUtil {

    public static void main(String[] args) throws Exception {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(getByte(new File("/Users/xiao/Desktop/testimage.jpeg")));
////        BufferedImage read = ImageIO.read(inputStream);
//        FileOutputStream out = new FileOutputStream(new File("/Users/xiao/Desktop/testimage1.jpeg"));
//        zoomImage(inputStream, out, 640, 480);

//        zoomTo400("/Users/xiao/Desktop/testimage.jpeg", "/Users/xiao/Desktop/testimage2.jpeg", 640, 480);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(getByte(new File("/Users/xiao/Desktop/test1.jpg")));
//        BufferedImage read = ImageIO.read(inputStream);
        FileOutputStream out = new FileOutputStream(new File("/Users/xiao/Desktop/test2.jpg"));
//        changeSize(inputStream, out, 640, 480);
//        zoomAndFill(inputStream, out, 640, 480);
//        cut(inputStream, out, 5);
        cutAndFill(inputStream, out, 640, 480);
    }

    /**
     * 将file文件转为字节数组
     * @param file
     * @return
     */
    public static byte[] getByte(File file){
        byte[] bytes = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 按固定长宽进行缩放
     * @param is      输入流
     * @param os      输出流
     * @param width   指定长度
     * @param height  指定宽度
     * @throws Exception
     */
    public static void zoomImage(InputStream is, OutputStream os, int width, int height) throws Exception {
        //读取图片
        BufferedImage bufImg = ImageIO.read(is);
        is.close();
        //获取缩放比例
        double wRatio = width * 1.0/ bufImg.getWidth();
        double hRatio = height * 1.0 / bufImg.getHeight();

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wRatio, hRatio), null);
        BufferedImage bufferedImage = ato.filter(bufImg, null);
        //写入缩减后的图片
        ImageIO.write(bufferedImage, "jpg", os);
    }

    /**
     * 等比例缩放，以宽或高较大者达到指定长度为准
     * @param src      输入文件路径
     * @param dest     输出文件路径
     * @param width    指定宽
     * @param height   指定高
     */
    public static void zoomTo400(String src, String dest, Integer width, Integer height){
        try {
            File srcFile = new File(src);
            File destFile = new File(dest);
            BufferedImage bufImg = ImageIO.read(srcFile);
            int w0 = bufImg.getWidth();
            int h0 = bufImg.getHeight();
            // 获取较大的一个缩放比率作为整体缩放比率
            double wRatio = 1.0 * width / w0;
            double hRatio = 1.0 * height / h0;
            double ratio = Math.min(wRatio, hRatio);
            // 缩放
            AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
            BufferedImage bufferedImage = ato.filter(bufImg, null);
            // 输出
            ImageIO.write(bufferedImage, dest.substring(dest.lastIndexOf(".")+1), destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 等比例图片压缩，以宽或高较大者达到指定长度为准
     * @param is     输入流
     * @param os     输出流
     * @param width  宽
     * @param height 高
     * @throws IOException
     */
    public static void changeSize(InputStream is, OutputStream os, int width, int height) throws IOException {
        BufferedImage bis = ImageIO.read(is);
        is.close();

        int srcWidth = bis.getWidth();
        int srcHeight = bis.getHeight(null);

        if (width <= 0 || width > srcWidth) {
            width = bis.getWidth();
        }
        if (height <= 0 || height > srcHeight) {
            height = bis.getHeight();
        }
        // 若宽高小于指定最大值，不需重新绘制
        if (srcWidth <= width && srcHeight <= height) {
            ImageIO.write(bis, "jpg", os);
        } else {
            double scale = Math.min(((double) width / srcWidth), ((double) height / srcHeight));
            width = (int) (srcWidth * scale);
            height = (int) (srcHeight * scale);

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // 绘制缩小后的图
            bufferedImage.getGraphics().drawImage(bis, 0, 0, width, height, Color.WHITE, null);
            ImageIO.write(bufferedImage, "jpg", os);
        }
        os.close();
    }

    /**
     * 不变形缩放（留白）
     * @param is
     * @param os
     * @param width
     * @param height
     * @throws IOException
     */
    public static void zoomAndFill(InputStream is, OutputStream os, int width, int height) throws IOException {
        BufferedImage bis = ImageIO.read(is);
        is.close();

        // 得到源图宽;得到源图高
        int srcWidth = bis.getWidth(null);
        int srcHeight = bis.getHeight(null);

        // 获取较大的一个缩放比率作为整体缩放比率
        double ratio = Math.min(1.0 * width / srcWidth, 1.0 * height / srcHeight);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        // 绘制缩小后的图
        graphics.drawImage(bis,(int) (width - srcWidth * ratio)/2, 0, (int) (srcWidth * ratio), height, Color.WHITE, null);
        ImageIO.write(bufferedImage, "jpg", os);
        os.close();
    }

    public static void cut(InputStream is, OutputStream os, int scale) throws Exception {
        BufferedImage src = ImageIO.read(is);
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        int per = srcHeight / scale;
        BufferedImage subimage = src.getSubimage(0, per, srcWidth, srcHeight - 2 * per);
        ImageIO.write(subimage, "jpg", os);
    }

    /**
     * 竖屏图片缩放横屏图片（保持图片原有比例；填充留白）
     *
     * @param width  宽
     * @param height 高
     * @return 目标图片
     */
    public static void cutAndFill(InputStream is, OutputStream os, int width, int height) throws Exception {
        BufferedImage srcImage = ImageIO.read(is);
        int srcWidth = srcImage.getWidth();
        int srcHeight = srcImage.getHeight();
        // 横屏图片不处理
        if (srcWidth >= srcHeight) {
            return;
        }

        double scale = srcHeight * 1.0 / srcWidth;
        int per = 0;
        if (scale < 1.3) {
            per = srcHeight / 9;
        } else if (scale < 1.7) {
            per = srcHeight / 7;
        } else {
            per = srcHeight / 5;
        }
        srcImage = srcImage.getSubimage(0, per, srcWidth, srcHeight - 2 * per);

        // 获取较大的一个缩放比率作为整体缩放比率
        srcWidth = srcImage.getWidth();
        srcHeight = srcImage.getHeight();
        double wRatio = 1.0 * width / srcWidth;
        double hRatio = 1.0 * height / srcHeight;
        double ratio = Math.min(wRatio, hRatio);

        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = target.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        // 绘制缩小后的图
        graphics.drawImage(srcImage, (int) (width - srcWidth * ratio)/2, 0,
                (int) (srcWidth * ratio), height, Color.WHITE, null);
        ImageIO.write(target, "jpg", os);
    }
}
