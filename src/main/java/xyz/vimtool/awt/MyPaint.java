package xyz.vimtool.awt;

import java.awt.*;

/**
 * @author zhangzheng
 * @version 1.0.0
 * @date 2020/11/13
 */
public class MyPaint {

    public static void main(String[] args) {
        MyFrame myFrame = new MyFrame();
        myFrame.loadFrame();
    }
}

class MyFrame extends Frame {

    public void loadFrame() {
        setBounds(200, 200, 200, 200);
        setVisible(true);
    }

    /**
     * {@inheritDoc}
     *
     * @param g
     * @since 1.7
     */
    @Override
    public void paint(Graphics g) {
        g.drawOval(100, 100, 50, 50);
    }
}
