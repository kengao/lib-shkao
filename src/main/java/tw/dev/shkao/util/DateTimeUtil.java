/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kengao
 */
public class DateTimeUtil {

    
    public static boolean isOverlap(Date period1Start, Date period1End, Date period2Start, Date period2End){
        //https://stackoverflow.com/questions/3196099/date-range-overlap-with-nullable-dates
        
        boolean CondA = (period1Start != null) && (period2End != null) && ( period1Start.getTime() > period2End.getTime() );
        boolean CondB = (period2Start != null) && (period1End != null) && (period2Start.getTime() > period1End.getTime() );
        
        return !CondA && !CondB;
    }
    
    public static boolean isContained(Date start1, Date end1, Date start2, Date end2){
        if( compareDay(start1, start2)>0)
            return false;
        if(end1 == null && end2 != null) {
            return true ;
        } else if (end1 == null && end2 == null) {
            return true ;
        } else if(end1 != null && end2 == null) {
            return false ;
        } else if(compareDay(end1, end2) < 0) {
            return false ;
        } else {
            return true ;
        }
    }
    public static boolean isContained(Date start1, Date end1, Date day) {
        return isContained(start1, end1, day, day) ;
    }
    public static int compareDay(Date d1, Date d2){
        //return compare(date1, date2, Calendar.DAY_OF_MONTH);
        
        if (d1 == null && d2 == null) {
            return 0;
        } else if (d1 != null && d2 == null) {
            return -1;
        } else if (d1 == null && d2 != null) {
            return 1;
        }

        return compare(d1, d2, Calendar.DAY_OF_MONTH);
    }

    public static int compare(Date date1, Date date2, int field){
        return trim(getCalendar(date1), field).compareTo( trim(getCalendar(date2), field) );
        
    }
    
    public static int compare(Calendar calendar1, Calendar calendar2, int field){
        return trim(getCalendar(calendar1), field).compareTo( trim(getCalendar(calendar2), field) );
    }
    
    public static Date add(Date date, int filed, int amount){
        Calendar calendar = getCalendar(date);
        calendar.add(filed, amount);
        return calendar.getTime();
    }
    public static Calendar getCalendar(){
        Calendar calendar = Calendar.getInstance(Locale.TAIWAN);
        return calendar;
    }
    public static Calendar getCalendar(Date date){
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar;
    }
    public static Calendar getCalendar(Calendar calendar){
        Calendar tcalendar = Calendar.getInstance(Locale.TAIWAN);
        tcalendar.setTime(calendar.getTime());
        return tcalendar;
    }
    public static Date trim(Date date, int field){
        return trim( getCalendar(date), field ).getTime();
    }
    
    public static Calendar trim(Calendar calendar, int field){
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        int mils = calendar.get(Calendar.MILLISECOND);
        
        switch(field){
            case Calendar.YEAR:
                month = calendar.getActualMinimum(Calendar.MONTH);
            case Calendar.MONTH:
                day = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
            case Calendar.DAY_OF_MONTH:
                hour = calendar.getActualMinimum(Calendar.HOUR_OF_DAY);
            case Calendar.HOUR_OF_DAY:
                min = calendar.getActualMinimum(Calendar.MINUTE);
            case Calendar.MINUTE:
                sec = calendar.getActualMinimum(Calendar.SECOND);
            case Calendar.SECOND:
                mils = calendar.getActualMinimum(Calendar.MILLISECOND);
            case Calendar.MILLISECOND:
        }
        
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, mils);

        return calendar;
    }
    public static long countDay(Date start, Date end){
        
        
        long result = TimeUnit.DAYS.convert(trim(end, Calendar.DATE).getTime() - trim(start, Calendar.DATE).getTime(), TimeUnit.MILLISECONDS);
        return result;
    }
    public static long countMonth(Date start, Date end){
        if(start.getYear() == end.getYear()){
            return end.getMonth() - start.getMonth();
        }
        else{
            return 12*(end.getYear() - start.getYear()) + (end.getMonth() - start.getMonth());
        }
        
    }
    
    public static String getDateOfWeek(Date date) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        Integer numOfWeek = dateCal.get(Calendar.DAY_OF_WEEK);
        switch (numOfWeek) {
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
            default:
                return "";
        }
    }
    public static Date stringToDatetime(String strTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date theTime = null;
        try {
            theTime = timeFormat.parse(strTime);
        } catch (ParseException ex) {
            Logger.getLogger(DateTimeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return theTime;
    }

    public static java.util.Date stringToDate(String strTime) {
        if(strTime==null || strTime.equals("null"))
            return null;
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date theTime = null;
        try {
            theTime = timeFormat.parse(strTime);
        } catch (ParseException ex) {
            Logger.getLogger(DateTimeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return theTime;
    }
    
    public static String dateToString(Date date) {
        if(date==null || date.equals("null"))
            return null;
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
        return timeFormat.format(date);
    }
    
    public static String dateTimeToString(Date date) {
        if(date==null || date.equals("null"))
            return null;
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return timeFormat.format(date);
    }
}
