package xyz.vimtool.sms;

import xyz.vimtool.commons.HttpUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangzheng
 * @version 1.0
 * @date 2018-4-11
 * @since jdk1.8
 */
public class SMS {

    public static void main(String[] args) throws Exception {
        ExecutorService executors = Executors.newCachedThreadPool();
        for (int i = 0; i < 225; i++) {
            executors.execute(new SmsThread());
            TimeUnit.SECONDS.sleep(1L);
            System.out.println(i);
        }
    }
}

class SmsThread implements Runnable {

    @Override
    public void run() {
        // 获取手机号
        String phoneUrl = "http://api.fxhyd.cn/UserInterface.aspx";
        String phoneParam = "action=getmobile&token=00493590178bfe0335461e9e77c032d32fe423dc&itemid=16402";
        String phone = HttpUtils.doGet(phoneUrl, phoneParam);
        if (phone.contains("success|")) {
            phone = phone.substring(8, 19);

            String sendUrl = "https://api.gncfs.com/api/sms/code?type=register&mobile=" + phone;
            String sendResult = HttpUtils.doPost(sendUrl, "");
            if (sendResult.contains("200")) {
                String smsResult = "";
                int count = 30;
                while (count > 0 && !smsResult.contains("success|")) {
                    try {
                        TimeUnit.SECONDS.sleep(1L);
                    } catch (InterruptedException e) {

                    }
                    String smsUrl = "http://api.fxhyd.cn/UserInterface.aspx";
                    String smsParam = "action=getsms&token=00493590178bfe0335461e9e77c032d32fe423dc&itemid=16402&release=1&mobile=" + phone;
                    smsResult = HttpUtils.doGet(smsUrl, smsParam);
                    count--;
                }

                if (count > 0) {
                    String code = smsResult.substring(27, 33);
                    String registerUrl = "https://api.gncfs.com/api/user/register" +
                            "?password=tttttttt&repeatPassword=tttttttt&recommendCode=15101513686" +
                            "&area=上海市上海市闵行区&mobile=" + phone + "&code=" + code;
                    String registerResult = HttpUtils.doPost(registerUrl, "");
                    if (registerResult.contains("200")) {
                        System.out.println("####################注册成功" + Thread.currentThread() + "####################");
                    }
                } else {
                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&注册失败" + Thread.currentThread() + "&&&&&&&&&&&&&&&&&&&&&&");
                }
            }
        }
    }
}
