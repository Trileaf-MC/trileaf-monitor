package cn.sanyeyun.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * @author 徐亚松
 * 2025-05-04 00:40
 **/
public class DateUtil {

    // 解析日期字符串，处理不同精度并转为 Date
    public static Date parseDateToDate(String dateStr) {
        try {
            // 使用 DateTimeFormatter 解析日期字符串
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            Instant instant = Instant.from(formatter.parse(dateStr));
            return Date.from(instant);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("无法解析时间字符串: " + dateStr, e);
        }
    }


    // 格式化 Date 为指定格式的字符串
    public static String formatDateToStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);  // 转换为 "yyyy-MM-dd HH:mm:ss" 格式字符串
    }


}
