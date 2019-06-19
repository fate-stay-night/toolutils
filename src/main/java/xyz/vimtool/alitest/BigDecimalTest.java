package xyz.vimtool.alitest;

import java.math.BigDecimal;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019-06-19
 */
public class BigDecimalTest {

    public static void main(String[] args) {
        BigDecimal a = new BigDecimal(0.1);
        System.out.println(a);
        BigDecimal b = new BigDecimal("0.1");
        System.out.println(b);
    }
}
