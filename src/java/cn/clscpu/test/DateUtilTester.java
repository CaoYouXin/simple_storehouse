/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.clscpu.test;

import static cn.clscpu.storehouse.util.DateUtil.TIMEUNIT.day;
import static cn.clscpu.storehouse.util.DateUtil.TIMEUNIT.second;
import static cn.clscpu.storehouse.util.DateUtil.*;
import java.util.Date;

/**
 *
 * @author CPU
 */
public final class DateUtilTester {
    
    public static void main(String args[]) {
        long time1 = 735964l;
        long time2 = 736695l * 24 * 60 * 60 * 1000;
        long time3 = 737790l * 24 * 60 * 60 * 1000;
        System.out.println(formatDateTime(later(2, day, new Date(time2 + parseDateTime("0-1-1 8:0:0").getTime()))));
        System.out.println(formatDateTime(later(2, day, new Date(time3 + parseDateTime("0-1-1 8:0:0").getTime()))));
        System.out.println(formatDateTime(new Date(1000)));
        System.out.println(formatDateTime(later(1, day, parseDateTime("2013-12-31 13:59:49"))));
        System.out.println(formatDateTime(new Date(1000 * later(new Date(time3), parseDateTime("2020-1-1 8:00:00"), second))));
        System.out.println(formatDateTime(new Date(1000 * later(parseDateTime("3990-01-01 08:00:00"), parseDateTime("2020-1-1 8:00:00"), second))));
        System.out.println(formatDateTime(parseDateTime("0-1-1 0:0:0")));
        System.out.println(parseDateTime("0-1-1 0:0:0").getTime());
        System.out.println(formatDateTime(new Date(1000 * later(new Date(time3), parseDateTime("2020-1-1 8:00:00"), second) + parseDateTime("1-1-1 8:0:0").getTime()))); 
        System.out.println(formatDate(parseDateTime("2014-1-1 0:0:0")));
        System.out.println(formatDate(parseDateTime("2014-1-1 24:0:0")));
        System.out.println(formatDateTime(parseDate("2014-1-1")));
    }
    
}
