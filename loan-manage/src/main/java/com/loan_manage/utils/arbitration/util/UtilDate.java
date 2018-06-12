package com.loan_manage.utils.arbitration.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class UtilDate {
    private static Logger log = Logger.getLogger(UtilDate.class);
    public static final String Y_m_D_H_M_S_L = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL";
    public static final String Y_m_D_H_M_S = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS";
    public static final String YYYY_MM_DD = "%1$tY-%1$tm-%1$td";
    public static final String YYYYMMDD = "%1$tY%1$tm%1$td";
    public static final String YYYY_MM = "%1$tY-%1$tm";
    public static final String MM_DD = "%1$tm-%1$td";
    public static final String C_YYYY_MM_DD = "%1$tY��%1$tm��%1$td��";
    public static final String C_YYYY_MM = "%1$tY��%1$tm��";
    public static final String F_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String F_YYYY_MM = "yyyy-MM";
    public static final String YYYYMMDDHHMMSSLLL = "%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL";
    public static final String YYYYMMDDHHMMSS = "%1$tY%1$tm%1$td%1$tH%1$tM%1$tS";
    public static final String F_MM_DD = "MM-dd";
    public static final Map<String, String> CHINESE_YEAR_NUM = new HashMap();
    public static final Map<String, String> CHINESE_DAY_NUM = new HashMap();
    public static final Map<String, String> CHINESE_WEEK_NUM = new HashMap();

    static {
        CHINESE_YEAR_NUM.put("0", "〇");
        CHINESE_YEAR_NUM.put("1", "一");
        CHINESE_YEAR_NUM.put("2", "二");
        CHINESE_YEAR_NUM.put("3", "三");
        CHINESE_YEAR_NUM.put("4", "四");
        CHINESE_YEAR_NUM.put("5", "五");
        CHINESE_YEAR_NUM.put("6", "六");
        CHINESE_YEAR_NUM.put("7", "七");
        CHINESE_YEAR_NUM.put("8", "八");
        CHINESE_YEAR_NUM.put("9", "九");

        CHINESE_WEEK_NUM.put("1", "星期日");
        CHINESE_WEEK_NUM.put("2", "星期一");
        CHINESE_WEEK_NUM.put("3", "星期二");
        CHINESE_WEEK_NUM.put("4", "星期三");
        CHINESE_WEEK_NUM.put("5", "星期四");
        CHINESE_WEEK_NUM.put("6", "星期五");
        CHINESE_WEEK_NUM.put("7", "星期六");

        CHINESE_DAY_NUM.put("01", "一");
        CHINESE_DAY_NUM.put("02", "二");
        CHINESE_DAY_NUM.put("03", "三");
        CHINESE_DAY_NUM.put("04", "四");
        CHINESE_DAY_NUM.put("05", "五");
        CHINESE_DAY_NUM.put("06", "六");
        CHINESE_DAY_NUM.put("07", "七");
        CHINESE_DAY_NUM.put("08", "八");
        CHINESE_DAY_NUM.put("09", "九");
        CHINESE_DAY_NUM.put("10", "十");
        CHINESE_DAY_NUM.put("11", "十一");
        CHINESE_DAY_NUM.put("12", "十二");
        CHINESE_DAY_NUM.put("13", "十三");
        CHINESE_DAY_NUM.put("14", "十四");
        CHINESE_DAY_NUM.put("15", "十五");
        CHINESE_DAY_NUM.put("16", "十六");
        CHINESE_DAY_NUM.put("17", "十七");
        CHINESE_DAY_NUM.put("18", "十八");
        CHINESE_DAY_NUM.put("19", "十九");
        CHINESE_DAY_NUM.put("20", "二十");
        CHINESE_DAY_NUM.put("21", "二十一");
        CHINESE_DAY_NUM.put("22", "二十二");
        CHINESE_DAY_NUM.put("23", "二十三");
        CHINESE_DAY_NUM.put("24", "二十四");
        CHINESE_DAY_NUM.put("25", "二十五");
        CHINESE_DAY_NUM.put("26", "二十六");
        CHINESE_DAY_NUM.put("27", "二十七");
        CHINESE_DAY_NUM.put("28", "二十八");
        CHINESE_DAY_NUM.put("29", "二十九");
        CHINESE_DAY_NUM.put("30", "三十");
        CHINESE_DAY_NUM.put("31", "三十一");
    }

    public static String formatChineseDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        StringBuilder str = new StringBuilder(stringFormat(date, "%1$tY年%1$tm月%1$td日"));
        StringBuilder result = new StringBuilder(20);
        StringBuilder year = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            year.append((String) CHINESE_YEAR_NUM.get(str.charAt(i)));
        }
        result.append(year.toString()).append("年");
        String strMonth = (String) CHINESE_DAY_NUM.get(str.substring(5, 7));
        result.append(strMonth).append("月");
        String strDay = (String) CHINESE_DAY_NUM.get(str.substring(8, 10));
        result.append(strDay).append("日");

        return result.toString();
    }

    public static String stringFormat(java.util.Date date, String format) {
        if (date == null) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.format(format, new Object[]{cal});
    }

    public static String simpleDateFormat(java.util.Date d, String format) {
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat da = new SimpleDateFormat(format);
        return da.format(d);
    }

    public static boolean isHoliday(java.util.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return (cal.get(7) == 7) ||
                (cal.get(7) == 1);
    }

    public static java.util.Date parseDate(String date, String format) {
        try {
            SimpleDateFormat formater = new SimpleDateFormat(format);
            return formater.parse(date);
        } catch (Exception e) {
            log.info("无法将字符串[" + date + "] 格式化为[" + format + "]类型的日期");
        }
        return null;
    }

    public static java.util.Date parseDate(String strDate) {
        java.util.Date date = null;
        if (StringUtils.isEmpty(strDate)) {
            return date;
        }
        String format = "yyyy-MM-dd HH:mm:ss.SSS";
        try {
            SimpleDateFormat formater = new SimpleDateFormat(format);
            return formater.parse(strDate);
        } catch (Exception e) {
            date = null;
            if (date != null) {
                return date;
            }
            format = "yyyy-MM-dd HH:mm:ss";
            try {
                SimpleDateFormat formater = new SimpleDateFormat(format);
                return formater.parse(strDate);
            } catch (Exception e1) {
                date = null;
                if (date != null) {
                    return date;
                }
                format = "yyyy-MM-dd HH:mm";
                try {
                    SimpleDateFormat formater = new SimpleDateFormat(format);
                    return formater.parse(strDate);
                } catch (Exception e2) {
                    date = null;
                    if (date != null) {
                        return date;
                    }
                    format = "yyyy-MM-dd HH";
                    try {
                        SimpleDateFormat formater = new SimpleDateFormat(format);
                        return formater.parse(strDate);
                    } catch (Exception e3) {
                        date = null;
                        if (date != null) {
                            return date;
                        }
                        format = "yyyy-MM-dd";
                        try {
                            SimpleDateFormat formater = new SimpleDateFormat(format);
                            return formater.parse(strDate);
                        } catch (Exception e4) {
                            date = null;
                            if (date != null) {
                                return date;
                            }
                            format = "yyyy-MM";
                            try {
                                SimpleDateFormat formater = new SimpleDateFormat(format);
                                return formater.parse(strDate);
                            } catch (Exception e5) {
                                date = null;
                                if (date != null) {
                                    return date;
                                }
                                format = "HH:mm";
                                try {
                                    SimpleDateFormat formater = new SimpleDateFormat(format);
                                    return formater.parse(strDate);
                                } catch (Exception e6) {
                                    date = null;
                                    if (date != null) {
                                        return date;
                                    }
                                    format = "yyyy";
                                    try {
                                        SimpleDateFormat formater = new SimpleDateFormat(format);
                                        return formater.parse(strDate);
                                    } catch (Exception e7) {
                                        date = null;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return date;
    }

    public static java.util.Date setFirstMonthOfYear(java.util.Date date, boolean flag) {
        if (date == null) {
            throw new IllegalArgumentException("参数日期为null");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(2, cal.getActualMinimum(2));
        if (flag) {
            cal.set(5,
                    cal.getActualMinimum(5));
            cal.set(11,
                    cal.getActualMinimum(11));
            cal.set(12, cal.getActualMinimum(12));
            cal.set(13, cal.getActualMinimum(13));
            cal.set(14,
                    cal.getActualMinimum(14));
        }
        return cal.getTime();
    }

    public static java.util.Date setLastMonthOfYear(java.util.Date date) {
        if (date == null) {
            throw new IllegalArgumentException("参数日期为null");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(2, cal.getActualMaximum(2));
        return cal.getTime();
    }

    public static java.util.Date setFirstDayOfMonth(java.util.Date date, boolean flag) {
        if (date == null) {
            throw new IllegalArgumentException("参数日期为null");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(5,
                cal.getActualMinimum(5));
        if (flag) {
            cal.set(11,
                    cal.getActualMinimum(11));
            cal.set(12, cal.getActualMinimum(12));
            cal.set(13, cal.getActualMinimum(13));
            cal.set(14,
                    cal.getActualMinimum(14));
        }
        return cal.getTime();
    }

    public static java.util.Date setLastDayOfMonth(java.util.Date date, boolean flag) {
        if (date == null) {
            throw new IllegalArgumentException("参数日期为null");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(5,
                cal.getActualMaximum(5));
        if (flag) {
            cal.set(11,
                    cal.getActualMaximum(11));
            cal.set(12, cal.getActualMaximum(12));
            cal.set(13, cal.getActualMaximum(13));
            cal.set(14,
                    cal.getActualMaximum(14));
        }
        return cal.getTime();
    }

    public static java.util.Date setTimeToBegin(java.util.Date date) {
        if (date == null) {
            throw new IllegalArgumentException("参数日期为null");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(11,
                cal.getActualMinimum(11));
        cal.set(12, cal.getActualMinimum(12));
        cal.set(13, cal.getActualMinimum(13));
        return cal.getTime();
    }

    public static java.util.Date setTimeToEnd(java.util.Date date) {
        if (date == null) {
            throw new IllegalArgumentException("参数日期为null");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(11,
                cal.getActualMaximum(11));
        cal.set(12, cal.getActualMaximum(12));
        cal.set(13, cal.getActualMaximum(13));
        return cal.getTime();
    }

    public static int dateDiff(java.util.Date sdate, java.util.Date edate, int field) {
        if ((sdate == null) || (edate == null)) {
            return 0;
        }
        Calendar cals = Calendar.getInstance();
        Calendar cale = Calendar.getInstance();
        cals.setTime(sdate);
        cale.setTime(edate);
        int diff = 0;
        switch (field) {
            case 1:
                diff = cals.get(1) - cale.get(1);
                break;
            case 2:
                diff = cals.get(1) * 12 + cals.get(2) - (
                        cale.get(1) * 12 + cale.get(2));
                break;
            case 5:
                diff = (int) ((cals.getTimeInMillis() - cale.getTimeInMillis()) / 60L / 60L / 24L / 1000L);
                break;
            case 3:
            case 4:
            default:
                throw new RuntimeException("本方法只计算日、月或者年之间的差");
        }
        return Math.abs(diff);
    }

    public static java.sql.Date getSQLDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    public static java.util.Date getUtilDate(java.sql.Date date) {
        if (date == null) {
            return null;
        }
        return new java.util.Date(date.getTime());
    }

    public static int getMaxDay(java.util.Date d) {
        if (d == null) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.getActualMaximum(5);
    }

    public static int getMaxDay(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(1, year);
        cal.set(2, month);
        return cal.getActualMaximum(5);
    }

    public static String[] getCAMonth(java.util.Date d) {
        String[] date = {"", ""};
        date[0] = simpleDateFormat(d, "yyyy-MM-01");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(2, 1);
        date[1] = simpleDateFormat(calendar.getTime(), "yyyy-MM-01");
        return date;
    }

    public static String getZHweek(java.util.Date d) {
        if (d == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        int w = calendar.get(7);
        switch (w) {
            case 1:
                return "日";
            case 2:
                return "一";
            case 3:
                return "二";
            case 4:
                return "三";
            case 5:
                return "四";
            case 6:
                return "五";
            case 7:
                return "六";
        }
        return null;
    }

    public static java.util.Date delay(java.util.Date begin, int days) {
        if (begin == null) {
            throw new IllegalArgumentException("开始日期不能为空");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(begin);
        cal.add(5, days);
        return cal.getTime();
    }

    public static java.util.Date delay(java.util.Date begin, int field, int delay) {
        if (begin == null) {
            throw new IllegalArgumentException("开始日期不能为空");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(begin);
        cal.add(field, delay);
        return cal.getTime();
    }

    public static java.util.Date setFirstDayOfWeek(java.util.Date date, boolean flag) {
        if (date == null) {
            throw new IllegalArgumentException("参数日期为null");
        }
        Calendar cal = new GregorianCalendar();
        cal.setFirstDayOfWeek(2);
        cal.setTime(new java.util.Date());
        cal.set(7, cal.getFirstDayOfWeek());
        if (flag) {
            cal.set(11,
                    cal.getActualMinimum(11));
            cal.set(12, cal.getActualMinimum(12));
            cal.set(13, cal.getActualMinimum(13));
            cal.set(14,
                    cal.getActualMinimum(14));
        }
        return cal.getTime();
    }

    public static String getDate(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static String getYear(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(date);
    }

    public static String getMonth(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(date);
    }

    public static String getCurrDate(java.util.Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(data);
    }

    public static String getAbcDate(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }

    public static String getDetailDate(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date);
    }

    public static String getDate(java.util.Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static int getMonthDiff(String startDate, String endDate)
            throws Exception {
        String s = startDate;
        String s1 = endDate;
        java.util.Date m = new java.util.Date();
        java.util.Date d = null;
        java.util.Date d1 = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        int result = 0;
        try {
            d = df.parse(s);
            d1 = df.parse(s1);

            Calendar c = Calendar.getInstance();
            c.setTime(d);

            int year = c.get(1);
            int month = c.get(2);

            c.setTime(d1);
            int year1 = c.get(1);
            int month1 = c.get(2);
            if (year == year1) {
                result = month1 - month;
            } else {
                result = 12 * (year1 - year) + month1 - month;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getStr2sub(String date) {
        if (date == null) {
            return "";
        }
        return date.substring(0, 10);
    }

    public static int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            java.util.Date dt1 = df.parse(DATE1);
            java.util.Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("第一个值大");
                return 1;
            }
            if (dt1.getTime() < dt2.getTime()) {
                System.out.println("第二个值大");
                return -1;
            }
            return 0;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static String getRandom(int count) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
