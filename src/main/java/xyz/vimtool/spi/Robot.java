package xyz.vimtool.spi;

import org.apache.dubbo.common.extension.SPI;

/**
 * @author zhangzheng
 * @version 1.0.0
 * @date 2020/9/2
 */
@SPI("yyy")
public interface Robot {

    void sayHello();
}
