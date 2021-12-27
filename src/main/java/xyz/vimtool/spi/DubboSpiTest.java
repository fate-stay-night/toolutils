package xyz.vimtool.spi;

import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.extension.SPI;

/**
 * java spi
 *
 * @author zhangzheng
 * @version 1.0.0
 * @date 2020/9/2
 */
public class DubboSpiTest {

    public static void main(String[] args) {
        ExtensionLoader<Robot> extensionLoader = ExtensionLoader.getExtensionLoader(Robot.class);
        Robot optimusPrime = extensionLoader.getExtension("optimusPrime");
        optimusPrime.sayHello();
        Robot bumblebee = extensionLoader.getExtension("bumblebee");
        bumblebee.sayHello();
        final SPI defaultAnnotation = Robot.class.getAnnotation(SPI.class);
        System.out.println("dddd" + defaultAnnotation.value() + "jkjk");

    }
}
