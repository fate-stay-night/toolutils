package xyz.vimtool.qt;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.qt.Qt5Widgets.QApplication;
import org.bytedeco.qt.Qt5Widgets.QTextEdit;

import java.io.File;

/**
 * qt程序
 *
 * @author zhangzheng
 * @version 1.0.0
 * @date 2020/6/4
 */
public class QtStart {

    private static IntPointer argc;
    private static PointerPointer argv;

    public static void main(String[] args) {
        String path = Loader.load(org.bytedeco.qt.global.Qt5Core.class);
        argc = new IntPointer(new int[]{3});
        argv = new PointerPointer("gettingstarted", "-platformpluginpath", new File(path).getParent(), null);

        QApplication app = new QApplication(argc, argv);

        QTextEdit textEdit = new QTextEdit();
        textEdit.show();

        int exec = QApplication.exec();
        System.exit(exec);
    }
}
