package xyz.vimtool.device;

import xyz.vimtool.commons.BytesUtils;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @since   jdk1.8
 * @date    2018/8/30
 */
public class Pro {

    /**
     * 开始标志，1字节
     */
    private byte head = 'M';

    /**
     * 版本号，1字节
     */
    private byte version = 3;

    /**
     * 类型，1字节
     */
    private byte type;

    /**
     * 参数1，4字节
     */
    private int param1;

    /**
     * 参数1，2字节
     */
    private short param2;

    /**
     * 预留，2字节
     */
    private short reserve;

    /**
     * 结束标志，1字节
     */
    private byte tail = 'H';

    public byte getHead() {
        return head;
    }

    public void setHead(byte head) {
        this.head = head;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getParam1() {
        return param1;
    }

    public void setParam1(int param1) {
        this.param1 = param1;
    }

    public short getParam2() {
        return param2;
    }

    public void setParam2(short param2) {
        this.param2 = param2;
    }

    public short getReserve() {
        return reserve;
    }

    public void setReserve(short reserve) {
        this.reserve = reserve;
    }

    public byte getTail() {
        return tail;
    }

    public void setTail(byte tail) {
        this.tail = tail;
    }

    public static Pro toProtocol(byte[] bytes) {
        Pro pro = new Pro();
        pro.setHead(BytesUtils.toByte(bytes, 0));
        pro.setVersion(BytesUtils.toByte(bytes, 1));
        pro.setType(BytesUtils.toByte(bytes, 2));
        pro.setParam1(BytesUtils.toInt(bytes, 3));
        pro.setParam2(BytesUtils.toShort(bytes, 7));
        pro.setReserve(BytesUtils.toShort(bytes, 9));
        pro.setTail(BytesUtils.toByte(bytes, 11));
        return pro;
    }

    public static byte[] toBytes(Pro pro) {
        byte[] bytes = new byte[12];
        System.arraycopy(BytesUtils.toBytes(pro.getHead()), 0, bytes, 0, 1);
        System.arraycopy(BytesUtils.toBytes(pro.getVersion()), 0, bytes, 1, 1);
        System.arraycopy(BytesUtils.toBytes(pro.getType()), 0, bytes, 2, 1);
        System.arraycopy(BytesUtils.toBytes(pro.getParam1()), 0, bytes, 3, 4);
        System.arraycopy(BytesUtils.toBytes(pro.getParam2()), 0, bytes, 7, 2);
        System.arraycopy(BytesUtils.toBytes(pro.getReserve()), 0, bytes, 9, 2);
        System.arraycopy(BytesUtils.toBytes(pro.getTail()), 0, bytes, 11, 1);
        return bytes;
    }

    @Override
    public String toString() {
        return "Pro{" +
                "head=" + head +
                ", version=" + version +
                ", type=" + type +
                ", param1=" + param1 +
                ", param2=" + param2 +
                ", reserve=" + reserve +
                ", tail=" + tail +
                '}';
    }
}
