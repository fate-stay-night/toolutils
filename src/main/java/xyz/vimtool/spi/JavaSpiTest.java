package xyz.vimtool.spi;

import org.apache.commons.io.IOUtils;
import sun.net.www.protocol.file.FileURLConnection;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.ServiceLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * java spi
 *
 * @author zhangzheng
 * @version 1.0.0
 * @date 2020/9/2
 */
public class JavaSpiTest {

    public static void main(String[] args) throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources1 = loader.getResources("META-INF/dubbo/");
        while (resources1.hasMoreElements()) {
            URL url = resources1.nextElement();
            URLConnection urlConnection = url.openConnection();
            if (urlConnection instanceof FileURLConnection) {
                System.out.println(url.getFile());
                FileURLConnection fu = (FileURLConnection) urlConnection;
                System.out.println(fu.getProperties());
            } else if (urlConnection instanceof JarURLConnection) {
                JarURLConnection ju = (JarURLConnection) urlConnection;
                JarFile jarFile = ju.getJarFile();
                System.out.println("$$$$$$$$$$" + ju.getJarEntry().getName());
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    System.out.println(jarEntry.getName());
                }
            }
            System.out.println(url.getFile());
            System.out.println();
        }

        System.out.println("$$$$$$$$$$$$$$$$$$");


        // 自己实现spi
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = contextClassLoader.getResources("META-INF/services/" + Robot.class.getName());
        URL url = resources.nextElement();
        List<String> list = IOUtils.readLines(url.openStream(), StandardCharsets.UTF_8);
        System.out.println(list);
        for (String className : list) {
            Class<?> aClass = Class.forName(className, false, contextClassLoader);
            Robot robot = (Robot) aClass.newInstance();
            robot.sayHello();
        }

        // java封装的spi
        ServiceLoader<Robot> load = ServiceLoader.load(Robot.class);
        load.forEach(Robot::sayHello);
    }
}
