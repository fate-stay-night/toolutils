package xyz.vimtool.proxy;

/**
 * 子类
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2018-1-8
 */
public class Client implements Parent {

    public Client() {}

    @Override
    public void hello() {
        System.out.println("This is client object");
    }
}
