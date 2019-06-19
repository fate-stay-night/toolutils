package xyz.vimtool.alitest;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019-06-19
 */
public class SwitchTest {

    public static void main(String[] args) {
        String param = null;
        switch (param) {
            case "null":
                System.out.println("null");
                break;
            default:
                System.out.println("default");
        }
    }
}
