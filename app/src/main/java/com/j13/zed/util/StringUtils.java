package com.j13.zed.util;

import android.content.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lz on 15/9/18.
 */
public class StringUtils {

    private static final NavigableMap<Long, String> suffixes = new TreeMap<Long, String>();
    private static final NavigableMap<Long, String> suffixesCN = new TreeMap<Long, String>();

    static {
        suffixes.put(1000L, "k");
        suffixes.put(1000000L, "m");
        suffixes.put(1000000000L, "b");

        suffixesCN.put(10000L, "wan");
        suffixesCN.put(10000000L, "qianwan");
        suffixesCN.put(100000000L, "yi");
    }

    private static HashMap<String, String> zhCNUnit = new HashMap<String, String>();
    private static HashMap<String, String> zhTWUnit = new HashMap<String, String>();

    static {
        // 中文进制（万），无法翻译，所以不放在strings.xml中
        zhCNUnit.put("wan", "万");
        zhCNUnit.put("qianwan", "千万");
        zhCNUnit.put("yi", "亿");

        zhTWUnit.put("wan", "萬");
        zhTWUnit.put("qianwan", "千萬");
        zhTWUnit.put("yi", "億");
    }

    public static String formatNum(long value, Context context) {
        String currentLanguage = Locale.getDefault().toString().toLowerCase();
        if (currentLanguage.startsWith("zh")) {
            return formatNumForCN(value);
        } else {
            return formatNum(value);
        }
    }

    public static String formatNum(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatNum(Long.MIN_VALUE + 1);
        if (value <= 0) return "";
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static String formatNumForCN(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatNum(Long.MIN_VALUE + 1);
        if (value < 0) return "";
        if (value < 10000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixesCN.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = getSuffix(e.getValue());

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    //动态加载后缀文字
    private static String getSuffix(String map) {
        String suffix = "";
        String currentLanguage = Locale.getDefault().toString().toLowerCase();
        if (Locale.CHINA.toString().equalsIgnoreCase(currentLanguage)) {
            suffix = zhCNUnit.get(map);
        } else if (Locale.TAIWAN.toString().equalsIgnoreCase(currentLanguage)) {
            suffix = zhTWUnit.get(map);
        }
        return suffix;
    }


////    test
//    public static void main(String args[]) {
//        long[] numbers = {0, 5, 999, 1000, -5821, 10500, -101800, 2000000, -7800000, 92150000, 123200000, 9999999, 999999999999999999L, 1230000000000000L, Long.MIN_VALUE, Long.MAX_VALUE};
//        String[] expected = {"0", "5", "999", "1k", "-5.8k", "10k", "-101k", "2M", "-7.8M", "92M", "123M", "9.9M", "999P", "1.2P", "-9.2E", "9.2E"};
//        for (int i = 0; i < numbers.length; i++) {
//            long n = numbers[i];
//            String formatted = formatNum(n);
//            System.out.println(n + " => " + formatted);
////            if (!formatted.equals(expected[i])) throw new AssertionError("Expected: " + expected[i] + " but found: " + formatted);
//        }
//    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static long toLong(String str, long defValue) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    public static String trimNull(String s) {
        return s != null ? s : "";
    }

    //replace \n and \r >=2 to only 1 \n
    public static String replaceBackSpace(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\n{2,}|\r{2,}");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("\n");
        }
        return dest;
    }
}
