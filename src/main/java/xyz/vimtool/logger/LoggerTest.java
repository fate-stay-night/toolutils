package xyz.vimtool.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志框架测试
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019/9/3
 */
public class LoggerTest {

    private static final Logger logger = LoggerFactory.getLogger("IvrLog");
//    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    public static void main(String[] args) {
//        String format = "flowpage:{}|uuid:{}|servicecode:{}|" +
//                "mobile:{}|flowfunction:{}|keyrecord:{}|" +
//                "businesscode:{}|price:{}|returncode:{}" +
//                "|description:{}|channelid:{}|remark:{}";
//        logger.info(format, "main", UUID.randomUUID().toString(),
//                "5432", "15109269725", "doSomething", "#",
//                "67554", "5", "000000", "设置成功", "748965", "");
//        logger.info(format, "main", UUID.randomUUID().toString(),
//                "5432", "15109269725", "doSomething", "*");


        Super sup = new Sub(); // Upcast
        System.out.println("sup.field = " + sup.field +
                ", sup.getField() = " + sup.getField());
        Sub sub = new Sub();
        System.out.println("sub.field = " + sub.field +
                ", sub.getField() = " + sub.getField()
                + ", sub.getSuperField() = " + sub.getSuperField());
    }
}

class Super {
    public int field = 0;

    public int getField() {
        return field;
    }
}

class Sub extends Super {
    public int field = 1;

    @Override
    public int getField() {
        return field;
    }

    public int getSuperField() {
        return super.field;
    }
}
