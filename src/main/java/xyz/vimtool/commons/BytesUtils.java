package xyz.vimtool.commons;

/**
 * 基本数据类型与bytes的转化（大端模式）
 *
 * @author    qinxiaoqing
 * @date      2018/01/03
 * @version   1.0
 */
public class BytesUtils {

    /**
     * 将LittleEndian转换为BigEndian
     */
    public static byte[] littleToBig(byte[] bytes) {
        byte[] temp = new byte[bytes.length];
        for (int i = bytes.length - 1; i >= 0; i--) {
            temp[i] = bytes[bytes.length - 1 - i];
        }
        return temp;
    }

    /**
     * 打印bytes
     */
    public static void print(byte[] bytes) {
        if (bytes == null) {
            return;
        }

        for (int i = 0; i < bytes.length; i++) {
            System.out.println(i + " = " + bytes[i]);
        }
    }

    /**
     * 比较是否一样
     */
    public static boolean compare(byte[] bytes1, byte[] bytes2) {
        if (bytes1 == null && bytes2 == null) {
            return true;
        }

        if (bytes1 == null || bytes2 == null || bytes1.length != bytes2.length) {
            return false;
        }

        for (int i = 0; i < bytes1.length; i++) {
            if (bytes1[i] != bytes2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * bytes转换为byte
     */
    public static byte toByte(byte[] bytes) {
        return toByte(bytes, 0);
    }

    /**
     * bytes转换为byte
     */
    public static byte toByte(byte[] bytes, int offset) {
        return bytes[offset];
    }

    /**
     * bytes转换为boolean
     */
    public static boolean toBoolean(byte[] bytes) {
        return toBoolean(bytes, 0);
    }

    /**
     * bytes转换为boolean
     */
    public static boolean toBoolean(byte[] bytes, int offset) {
        return bytes[offset] > 0;
    }

    /**
     * bytes转换为short
     */
    public static short toShort(byte[] bytes) {
        return toShort(bytes, 0);
    }

    /**
     * bytes转换为short
     */
    public static short toShort(byte[] bytes, int offset) {
        return (short) (((bytes[offset] & 0xff) << 8) | (bytes[offset + 1] & 0xff));
    }

    /**
     * bytes转换为unsignedShort
     */
    public static int toUnsignedShort(byte[] bytes) {
        return toUnsignedShort(bytes, 0);
    }

    /**
     * bytes转换为unsignedShort
     */
    public static int toUnsignedShort(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xff) << 8)
                | (bytes[offset + 1] & 0xff);
    }

    /**
     * bytes转换为int
     */
    public static int toInt(byte[] bytes) {
        return toInt(bytes, 0);
    }

    /**
     * bytes转换为int
     */
    public static int toInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xff) << 24)
                | ((bytes[offset + 1] & 0xff) << 16)
                | ((bytes[offset + 2] & 0xff) << 8)
                | (bytes[offset + 3] & 0xff);
    }

    /**
     * bytes转换为unsignedInt
     */
    public static long toUnsignedInt(byte[] bytes) {
        return toUnsignedInt(bytes, 0);
    }

    /**
     * bytes转换为unsignedInt
     */
    public static long toUnsignedInt(byte[] bytes, int offset) {
        return (((long) bytes[offset] & 0xff) << 24)
                | (((long) bytes[offset + 1] & 0xff) << 16)
                | (((long) bytes[offset + 2] & 0xff) << 8)
                | ((long) bytes[offset + 3] & 0xff);
    }

    /**
     * bytes转换为long
     */
    public static long toLong(byte[] bytes) {
        return toLong(bytes, 0);
    }

    /**
     * bytes转换为long
     */
    public static long toLong(byte[] bytes, int offset) {
        return (((long) bytes[offset] & 0xff) << 56)
                | (((long) bytes[offset + 1] & 0xff) << 48)
                | (((long) bytes[offset + 2] & 0xff) << 40)
                | (((long) bytes[offset + 3] & 0xff) << 32)
                | (((long) bytes[offset + 4] & 0xff) << 24)
                | (((long) bytes[offset + 5] & 0xff) << 16)
                | (((long) bytes[offset + 6] & 0xff) << 8)
                | ((long) bytes[offset + 7] & 0xff);
    }

    /**
     * bytes转换为String
     */
    public static String toString(byte[] bytes, int offset, int length) {
        return toString(bytes, offset, length, "UTF-8");
    }

    /**
     * bytes转换为String
     */
    public static String toString(byte[] bytes, int offset, int length, String charsetName) {
        return new String(bytes, offset, length);
    }

    /**
     * byte转换为bytes
     */
    public static byte[] toBytes(byte b) {
        byte[] bytes = new byte[1];
        bytes[0] = b;
        return bytes;
    }

    /**
     * boolean转换为bytes
     */
    public static byte[] toBytes(boolean b) {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) (b ? 1 : 0);
        return bytes;
    }

    /**
     * short转换为bytes
     */
    public static byte[] toBytes(short s) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (s >> 8 & 0xff);
        bytes[1] = (byte) (s & 0xff);
        return bytes;
    }

    /**
     * int转换为bytes
     */
    public static byte[] toBytes(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i >> 24 & 0xff);
        bytes[1] = (byte) (i >> 16 & 0xff);
        bytes[2] = (byte) (i >> 8 & 0xff);
        bytes[3] = (byte) (i & 0xff);
        return bytes;
    }

    /**
     * long转换为bytes
     */
    public static byte[] toBytes(long l) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (l >> 56 & 0xff);
        bytes[1] = (byte) (l >> 48 & 0xff);
        bytes[2] = (byte) (l >> 40 & 0xff);
        bytes[3] = (byte) (l >> 32 & 0xff);
        bytes[4] = (byte) (l >> 24 & 0xff);
        bytes[5] = (byte) (l >> 16 & 0xff);
        bytes[6] = (byte) (l >> 8 & 0xff);
        bytes[7] = (byte) (l & 0xff);
        return bytes;
    }

    /**
     * string转换为bytes
     */
    public static byte[] toBytes(String s) {
        return s.getBytes();
    }
}
