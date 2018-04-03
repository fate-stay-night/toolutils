package xyz.vimtool.socket;

import xyz.vimtool.commons.BytesUtils;

/**
 * 协议
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2018-1-5
 */
public class Protocol {

    /** 协议长度 */
    public static final int LENGTH = 25;

    /** 协议开始/结束标志 */
    public static final String HEAD = "LG";
    public static final String TAIL = "MH";

    /** 协议类型（心跳、启动、调节力度，调节感应） */
    public static final byte TYPE_PULSE = 0;
    public static final byte TYPE_START = 1;
    public static final byte TYPE_ADJUST_INTENSITY = 2;
    public static final byte TYPE_ADJUST_INDUCE = 3;

    public Protocol() {}

    public Protocol(byte[] bytes) {
        byte[] decryptBytes = encrypt(bytes);
        this.head = BytesUtils.toString(decryptBytes, 0, 2);
        this.version = BytesUtils.toByte(decryptBytes, 2);
        this.length = BytesUtils.toByte(decryptBytes, 3);
        this.type = BytesUtils.toByte(decryptBytes, 4);
        this.msn = BytesUtils.toLong(decryptBytes, 5);
        this.time = BytesUtils.toInt(decryptBytes, 13);
        this.load = BytesUtils.toBoolean(decryptBytes, 17);
        this.rssi = BytesUtils.toByte(decryptBytes, 18);
        this.intensity = BytesUtils.toByte(decryptBytes, 19);
        this.induce = BytesUtils.toByte(decryptBytes, 20);
        this.reserve = BytesUtils.toShort(decryptBytes, 21);
        this.tail = BytesUtils.toString(decryptBytes, 23, 2);
    }

    /**
     * 开始标志，2字节
     */
    private String head = HEAD;

    /**
     * 版本号，1字节
     */
    private byte version = 2;

    /**
     * 总长度，1字节
     */
    private byte length = LENGTH;

    /**
     * 类型，1字节
     */
    private byte type;

    /**
     * 按摩垫编号，8字节
     */
    private long msn;

    /**
     * 时长/秒，4字节
     */
    private int time = 0;

    /**
     * 载重，1字节
     */
    private boolean load = false;

    /**
     * 信号强度，1字节
     */
    private byte rssi = 0;

    /**
     * 力度，1字节，0-无用，1-轻微，2-舒适，3-劲道，默认2
     */
    private byte intensity = 0;

    /**
     * 感应，1字节,0-开启，2-关闭
     */
    private byte induce = 0;

    /**
     * 预留，2字节
     */
    private short reserve = 0;

    /**
     * 结束标志，2字节
     */
    private String tail = TAIL;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public long getMsn() {
        return msn;
    }

    public void setMsn(long msn) {
        this.msn = msn;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isLoad() {
        return load;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }

    public byte getRssi() {
        return rssi;
    }

    public void setRssi(byte rssi) {
        this.rssi = rssi;
    }

    public byte getIntensity() {
        return intensity;
    }

    public void setIntensity(byte intensity) {
        this.intensity = intensity;
    }

    public byte getInduce() {
        return induce;
    }

    public void setInduce(byte induce) {
        this.induce = induce;
    }

    public short getReserve() {
        return reserve;
    }

    public void setReserve(short reserve) {
        this.reserve = reserve;
    }

    public String getTail() {
        return tail;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    public byte[] toBytes() {
        byte[] result = new byte[Protocol.LENGTH];
        System.arraycopy(BytesUtils.toBytes(head), 0, result, 0, 2);
        System.arraycopy(BytesUtils.toBytes(version), 0, result, 2, 1);
        System.arraycopy(BytesUtils.toBytes(length), 0, result, 3, 1);
        System.arraycopy(BytesUtils.toBytes(type), 0, result, 4, 1);
        System.arraycopy(BytesUtils.toBytes(msn), 0, result, 5, 8);
        System.arraycopy(BytesUtils.toBytes(time), 0, result, 13, 4);
        System.arraycopy(BytesUtils.toBytes(load), 0, result, 17, 1);
        System.arraycopy(BytesUtils.toBytes(rssi), 0, result, 18, 1);
        System.arraycopy(BytesUtils.toBytes(intensity), 0, result, 19, 1);
        System.arraycopy(BytesUtils.toBytes(induce), 0, result, 20, 1);
        System.arraycopy(BytesUtils.toBytes(reserve), 0, result, 21, 2);
        System.arraycopy(BytesUtils.toBytes(tail), 0, result, 23, 2);
        return encrypt(result);
    }

    public byte[] encrypt(byte[] bytes) {
        byte[] result = new byte[bytes.length];
        int key = 123;
        for(int i = 0; i < Protocol.LENGTH; i++) {
            if (i > 1 && i < Protocol.LENGTH - 2) {
                result[i] = (byte)(bytes[i] ^ key);
            } else {
                result[i] = bytes[i];
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "Protocol{" +
                "head='" + head + '\'' +
                ", version=" + version +
                ", length=" + length +
                ", type=" + type +
                ", msn=" + msn +
                ", time=" + time +
                ", load=" + load +
                ", rssi=" + rssi +
                ", intensity=" + intensity +
                ", induce=" + induce +
                ", reserve=" + reserve +
                ", tail='" + tail + '\'' +
                '}';
    }
}
