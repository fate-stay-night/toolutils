package xyz.vimtool.listener;

/**
 * @author zhangzheng
 * @version 1.0.0
 * @date 2021/2/21
 */
public class TestListener {

    public static void main(String[] args) {
        Robot robot = new Robot();
        robot.registerListener(new MyRobotListener());
        robot.working();
        robot.dancing();
    }
}
