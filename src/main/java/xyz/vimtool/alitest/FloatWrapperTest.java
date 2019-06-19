package xyz.vimtool.alitest;

/**
 * 浮点数测试
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019-06-19
 */
public class FloatWrapperTest {

    public static void main(String[] args) {
        Float a = Float.valueOf(1.0f - 0.9f);
        Float b = Float.valueOf(0.9f - 0.8f);
        if (a.equals(b)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }
}
