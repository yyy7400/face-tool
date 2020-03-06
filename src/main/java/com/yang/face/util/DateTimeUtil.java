package com.yang.face.util;

import cn.hutool.core.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wanyifan
 * @date 2019/12/19 11:14
 */
public class DateTimeUtil {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);
    /**
     * 获取当前系统时间
     *
     * @return String: yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentTimestamp() {
        return DateUtil.date().toString();
    }

    /**
     * 获取当前系统格式化日期
     *
     * @return String: yyyyMMdd 20191219
     */
    public static String getCurrentDateToStr() {
        return DateUtil.format(new Date(), "yyyyMMdd");
    }

    /**
     * 获取格式化日期
     *
     * @return String: yyyyMMdd 20191219
     */
    public static String getCurrentDateToStr(Date date) {
        return DateUtil.format(date, "yyyyMMdd");
    }

    /**
     * 获取当前系统格式化日期
     *
     * @return Integer: yyyyMMdd 20191219
     */
    public static Integer getCurrentDateToInt() {
        return Integer.parseInt(getCurrentDateToStr());
    }

    /**
     * 获取格式化日期
     *
     * @return Integer: yyyyMMdd 20191219
     */
    public static Integer getCurrentDateToInt(Date date) {
        return Integer.parseInt(getCurrentDateToStr(date));
    }

    /**
     * 将字符串转换为Date
     *
     * @param timestamp 字符串 如 yyyy-MM-dd HH-mm-ss 格式
     * @return Date
     */
    public static Date getDateTime(String timestamp) {
        return DateUtil.parse(timestamp);
    }

    /**
     * 可生成文件名称
     *
     * @return 1576727765434
     */
    public static String getTimeMill() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 可生成文件名称
     *
     * @return 20191219115741041
     */
    public static String getTimeStamp() {
        return DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
    }


    /**
     * 时间差
     * @param date1 被减数
     * @param date2 减数
     * @return 返回 单位 分钟
     */
    public static double dateDiffToHour(Date date1,Date date2) {
        double hour;
        long start = date1.getTime();
        long end = date2.getTime();
        long diff = end - start;
        hour = ((double)diff/(60*60*1000));
        // 保留小数
        NumberFormat nf = new DecimalFormat("0.0");
        hour = Double.parseDouble(nf.format(hour));
        return hour;
    }

    /**
     * 比较两个的时间大小 只比较时间  不比较日期
     * 小于 则 返回true
     * @param date1 date1
     * @param date2 date2
     * @return boolean
     */
    public static boolean onlyCompareToTime(Date date1, Date date2){
        int t1 = Integer.parseInt(DateUtil.format(date1, "HHmmssSS"));
        int t2 = Integer.parseInt(DateUtil.format(date2, "HHmmssSS"));
        return t1<t2;
    }

    /**
     * 改变时间
     * @param date  保留时间
     * @param c   保留日期
     * @return c的日期 + date的时间
     */
    public static Date changeDate(Date date,Date c){
        String prefix = DateUtil.format(c, "yyyy-MM-dd");
        String blank = " ";
        String time = DateUtil.format(date, "HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date res = null;
        try {
            res = sdf.parse(prefix + blank + time);
        } catch (ParseException e) {
            logger.error("{}",e.getMessage());
        }
        return res;
    }



}
