package core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Functions {
	//×ª»»Ê±¼ä
	public static Date parseStrToDate(String dateString){
		DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");				
		try {
			Date date=dateFormat.parse(dateString);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return null;
	}
	public static String  parseDateToStr(Date date){
		DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return dateFormat.format(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return null;
	}
	public static Date addDate(Date date,int addCount,int calendarType){
		Calendar calendar = Calendar.getInstance();     
	    calendar.setTime(date); 
	    calendar.add(calendarType,addCount );
	    return calendar.getTime();
	}
}
