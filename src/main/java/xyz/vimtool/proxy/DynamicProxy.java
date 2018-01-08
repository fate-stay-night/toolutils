package xyz.vimtool.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理类
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2018-1-8
 */
public class DynamicProxy implements InvocationHandler {

    private Object parent;

    DynamicProxy(Object parent) {
        this.parent = parent;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("%%%%%%%%%%%%%%%");
        method.invoke(parent);
        return null;
    }

    public static void main(String[] args) {
        Parent client = new Client();
        InvocationHandler dynamicProxy = new DynamicProxy(client);
        Parent parent = (Parent) Proxy.newProxyInstance(
                dynamicProxy.getClass().getClassLoader(), client.getClass().getInterfaces(), dynamicProxy);

        parent.hello();
    }
}
