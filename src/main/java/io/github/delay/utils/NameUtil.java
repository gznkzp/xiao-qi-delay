package io.github.delay.utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 命名转换工具类
 *
 * @author 苦瓜不苦
 * @date 2023/6/14 14:25
 **/
public class NameUtil {

    private static final String REGEX = "(.)(\\p{Upper})";

    private static final String REPLACEMENT = "$1_$2";

    private static final Pattern PATTERN = Pattern.compile("_(\\w)");


    private NameUtil() {

    }

    /**
     * 驼峰转下划线
     *
     * @param text 名称
     * @return
     */
    public static String underline(String text) {
        if (Objects.isNull(text)) {
            return "";
        }
        return text.replaceAll(REGEX, REPLACEMENT).toLowerCase();
    }


    /**
     * 驼峰转下划线
     *
     * @param text 名称
     * @return
     */
    public static String hump(String text) {
        if (Objects.isNull(text)) {
            return null;
        }
        Matcher matcher = PATTERN.matcher(text);
        StringBuffer camelCase = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(camelCase, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(camelCase);
        return camelCase.toString();
    }


}
