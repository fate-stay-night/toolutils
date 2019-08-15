package xyz.vimtool.image;

import com.alibaba.fastjson.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.URLEncoder;

/**
 * 图像处理
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019-06-16
 */
public class ImageQualityEnhance {

    public static String imageQualityEnhance() {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/image_quality_enhance";
        try {
            // 本地文件路径
            String filePath = "/Users/xiao/Desktop/111.jpg";
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = "24.93ff2d550bf58d978367f002cf8c727a.2592000.1563264875.282335-16533787";

            JSONObject object = JSONObject.parseObject(HttpUtil.post(url, accessToken, param));
            return object.getString("image");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 图片转化成base64字符串
    public static String getImageToBase64(String imgFile) {
        //imgFile = "C:/Users/Administrator/Desktop/12.png";// 待处理的图片
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }

    // base64字符串转化成图片
    public static boolean base64ToImage(String imgStr) { // 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) // 图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            // 生成jpeg图片
            String imgFilePath = "/Users/xiao/Desktop/222.jpg";// 新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String imageString = ImageQualityEnhance.imageQualityEnhance();
        base64ToImage(imageString);
    }

}
