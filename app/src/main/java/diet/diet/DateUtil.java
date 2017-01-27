package diet.diet;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rrichard39 on 3/23/2016.
 */
public class DateUtil {
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    // Version Numbers
    public static final String MAJOR = "1"; // ALL NEW
    public static final String MINOR = "2"; // new feature
    public static final String DEBUG = "3";
    public static final String BUILD = Integer.toString(BuildConfig.VERSION_CODE);
}
