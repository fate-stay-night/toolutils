package xyz.vimtool.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


/**
 * @author zhangzheng
 * @version 1.0.0
 * @date 2020/12/17
 */
public class SshHelper {

    public static void main(String[] args) throws Exception {
        JSch jSch = new JSch();
        Session session = jSch.getSession("test", "127.0.0.1", 22);
        session.setPassword("password");
        //设置不用检查hostKey
        //如果设置成“yes”，ssh就不会自动把计算机的密匙加入“$HOME/.ssh/known_hosts”文件，
        //并且一旦计算机的密匙发生了变化，就拒绝连接。
//        //默认值是 “yes” 此处是由于我们SFTP服务器的DNS解析有问题，则把UseDNS设置为“no”
//        config.put("UseDNS", "no");
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPortForwardingL(37021, "127.0.0.1", 27021);
        session.connect();
    }
}
