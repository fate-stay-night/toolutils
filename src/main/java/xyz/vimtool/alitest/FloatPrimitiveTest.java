package xyz.vimtool.alitest;

/**
 * 浮点数测试
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019-06-19
 */
public class FloatPrimitiveTest {

    public static void main(String[] args) {
        float a = 1.0f - 0.9f;
        float b = 0.9f - 0.8f;
        if (a == b) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }
}
