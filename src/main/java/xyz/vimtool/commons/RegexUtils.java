package xyz.vimtool.commons;

/**
 * 正则表达式工具类
 *
 * @author    qinxiaoqing
 * @date      2017/04/15
 * @version   1.0
 */
public class RegexUtils {

    /**
     * 匹配全网IP的正则表达式
     */
    public static final String IP_REGEX = "^((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))$";

    /**
     * 手机号码:
     * 13[0-9], 14[5,7], 15[0, 1, 2, 3, 5, 6, 7, 8, 9], 17[0, 1, 6, 7, 8], 18[0-9]
     * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188,198
     * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186,166
     * 电信号段: 133,149,153,170,173,177,180,181,189,199
     */
    public static final String CELL_PHONE_REGEX = "^1(3[0-9]|4[57]|5[0-35-9]|6[6]|7[0135678]|8[0-9]|9[89])\\d{8}$";

    /**
     * 匹配邮箱的正则表达式
     * <br>"www."可省略不写
     */
    public static final String EMAIL_REGEX = "^(www\\.)?\\w+@\\w+(\\.\\w+)+$";

    /**
     * 匹配汉子的正则表达式，个数限制为一个或多个
     */
    public static final String CHINESE_REGEX = "^[\u4e00-\u9f5a]+$";

    /**
     * 电话号码的正则表达式
     */
    public static final String TEL_PHONE_REGEX = "^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$";

    /**
     * 匹配身份证号的正则表达式
     */
    public static final String ID_NUMBER_REGEX = "^(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)|" +
            "(^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$)$";

    /**
     * 匹配URL的正则表达式
     */
    public static final String URL_REGEX = "^(([hH][tT]{2}[pP][sS]?)|" +
            "([fF][tT][pP]))\\:\\/\\/[wW]{3}\\.[\\w-]+\\.\\w{2,4}(\\/.*)?$";

    /**
     * 匹配车牌号的正则表达式
     */
    public static final String CPN_REGEX = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}" +
            "[A-Z]{1}[A-Z0-9]{4,5}[A-Z0-9挂学警港澳]{1}$";

    /**
     * 验证是否是一个邮箱账号，"www."可省略不写
     * @param text 给定的字符串
     * @return true：是
     */
    public static boolean isEmail(String text){
        return isMatched(text, EMAIL_REGEX);
    }

    /**
     * 验证是否是一个手机号码，支持130——139、150——153、155——159、180、183、185、186、188、189号段
     * @param text 给定的字符串
     * @return true：是
     */
    public static boolean isMobile(String text){
        return isMatched(text, CELL_PHONE_REGEX);
    }

    /**
     * 验证是否是一个电话号码
     * @param text 给定的字符串
     * @return true：是
     */
    public static boolean isTelPhone(String text) {
        return isMatched(text, TEL_PHONE_REGEX);
    }

    /**
     * 验证是否是一个全网IP
     * @param text 给定的字符串
     * @return true：是
     */
    public static boolean isIp(String text){
        return isMatched(text, IP_REGEX);
    }

    /**
     * 验证是否全部由汉子组成
     * @param text 给定的字符串
     * @return true：是
     */
    public static boolean isChinese(String text){
        return isMatched(text, CHINESE_REGEX);
    }

    /**
     * 验证是否是身份证号
     * 身份证15位编码规则：dddddd yymmdd xx p
     * dddddd：6位地区编码
     * yymmdd：出生年(两位年)月日，如：910215
     * xx：顺序编码，系统产生，无法确定
     * p：性别，奇数为男，偶数为女
     *
     * 身份证18位编码规则：dddddd yyyymmdd xxx y
     * dddddd：6位地区编码
     * yyyymmdd：出生年(四位年)月日，如：19910215
     * xxx：顺序编码，系统产生，无法确定，奇数为男，偶数为女
     * y：校验码，该位数值可通过前17位计算获得
     * 前17位号码加权因子为 Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 ]
     * 验证位 Y = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ]
     * 如果验证码恰好是10，为了保证身份证是十八位，那么第十八位将用X来代替 校验位计算公式：Y_P = mod( ∑(Ai×Wi),11 )
     * i为身份证号码1...17 位; Y_P为校验码Y所在校验码数组位置
     * @param text 文本
     * @return boolean
     */
    public static boolean isIdNumber(String text){
        return isMatched(text, ID_NUMBER_REGEX);
    }

    /**
     * 验证是否是URL，仅支持http、https、ftp
     * @param text url字符串
     * @return boolean
     */
    public static boolean isURL(String text){
        return isMatched(text, URL_REGEX);
    }

    /**
     * 验证是否是车牌号
     * @param text 字符串
     * @return boolean
     */
    public static boolean isCpn(String text){
        return isMatched(text, CPN_REGEX);
    }

    public static boolean isMatched(String text, String regex) {
        return text != null && regex != null && text.matches(regex);
    }
}
