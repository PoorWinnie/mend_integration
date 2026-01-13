package com.example.back.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DatetimeConverterProduct {
    
    /**
     * 將字串轉換為 LocalDateTime
     * @param datetime 日期時間字串
     * @param pattern 日期時間格式，例如："yyyy-MM-dd HH:mm:ss"
     * @return LocalDateTime 物件，如果轉換失敗則返回 null
     */
    public static LocalDateTime parseDateTime(String datetime, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(datetime, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 將字串轉換為當天的開始時間
     * @param date 日期字串，格式為 "yyyy-MM-dd"
     * @return 該日期的開始時間（00:00:00）
     */
    public static LocalDateTime parseDateStartTime(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDateTime.parse(date + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 將字串轉換為當天的結束時間
     * @param date 日期字串，格式為 "yyyy-MM-dd"
     * @return 該日期的結束時間（23:59:59）
     */
    public static LocalDateTime parseDateEndTime(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDateTime.parse(date + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 將 LocalDateTime 轉換為指定格式的字串
     * @param datetime LocalDateTime 物件
     * @param pattern 輸出格式，例如："yyyy-MM-dd HH:mm:ss"
     * @return 格式化後的字串，如果轉換失敗或datetime為null則返回空字串
     */
    public static String format(LocalDateTime datetime, String pattern) {
        if (datetime == null) {
            return "";  // 如果日期為null，返回空字串
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return datetime.format(formatter);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
