package xyz.vimtool.spi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 自定义类加载器
 *
 * @author zhangzheng
 * @version 1.0.0
 * @date 2021/5/31
 */
public class SelfClassLoader extends ClassLoader {


    public static void main(String[] args) throws Exception {
        SelfClassLoader loader = new SelfClassLoader();
        Class<?> aClass = loader.loadClass("xyz.vimtool.spi.SelfClassLoader");
        System.out.println(aClass.getName());
        URLClassLoader parent = (URLClassLoader)loader.getParent();
        for (URL url : parent.getURLs()) {
            System.out.println(url);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        StringBuilder sb = new StringBuilder(name.length() + 6);
        sb.append(name.replace('.', '/')).append(".class");
        InputStream is = this.getResourceAsStream(sb.toString());
        if (is == null) {
            throw new ClassNotFoundException("Class not found" + sb);
        } else {
            Class var19;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];

                int len;
                while((len = is.read(buf)) >= 0) {
                    baos.write(buf, 0, len);
                }

                buf = baos.toByteArray();
                int i = name.lastIndexOf(46);
                if (i != -1) {
                    String pkgname = name.substring(0, i);
                    Package pkg = this.getPackage(pkgname);
                    if (pkg == null) {
                        this.definePackage(pkgname, (String)null, (String)null, (String)null, (String)null, (String)null, (String)null, (URL)null);
                    }
                }

                var19 = this.defineClass(name, buf, 0, buf.length);
            } catch (IOException var17) {
                throw new ClassNotFoundException(name, var17);
            } finally {
                try {
                    is.close();
                } catch (IOException var16) {
                }

            }

            return var19;
        }
    }
}
