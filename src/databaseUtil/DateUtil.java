package databaseUtil;

import java.text.SimpleDateFormat;
import java.sql.Date;

/**
 *
 * @author remcoruijsenaars
 */
public class DateUtil {
    
        public static java.sql.Date toSqlDate(int dag, int maand, int jaar) {

            java.util.Calendar cal = java.util.Calendar.getInstance();

            //Clear all fields
            cal.clear();

            cal.set(java.util.Calendar.YEAR, jaar);
            cal.set(java.util.Calendar.MONTH, maand-1); //start bij 0, januari is dus maand 0
            cal.set(java.util.Calendar.DAY_OF_MONTH, dag);
           
            //Create instance of java.sql.Date
            Date sqlDate = new java.sql.Date(cal.getTimeInMillis());

            return sqlDate;

        }
        
        
     public static String fromSqlDateToString(Date date)
     {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = simpleDateFormat.format(date);
        return dateString;
     }   
    
}
