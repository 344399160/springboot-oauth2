package com.scistor.tab.auth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm\'+0800\'");
		String timestamp = sdf.format(new Date());
		return timestamp;
	}
	
	    
    public static String getExecutionTime(Date d1, Date d2) {
        try {
            // 精确到秒
            long diff = d1.getTime() - d2.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            long seconds = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / (1000);

            if (days == 0) {
                return hours + "小时" + minutes + "分" + seconds + "秒";
            }
            return "" + days + "天" + hours + "小时" + minutes + "分" + seconds + "秒";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
