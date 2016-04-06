package diet.diet;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    WeightData WD;

    TextView tv_StartDate;
    TextView tv_TargetDate;
    TextView tv_AchieveDate;

    TextView tv_StartWeight;
    TextView tv_LastWeight;
    TextView tv_TargetWeight;

    TextView tv_TotalLoss;
    TextView tv_WeeklyLoss;
    TextView tv_DailyLoss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        tv_StartDate = (TextView)findViewById(R.id.tv_StartDate);
        tv_TargetDate = (TextView)findViewById(R.id.tv_TargetDate);
        tv_AchieveDate = (TextView)findViewById(R.id.tv_AchieveDate);

        tv_StartWeight = (TextView)findViewById(R.id.tv_StartWeight);
        tv_LastWeight = (TextView)findViewById(R.id.tv_LastWeight);
        tv_TargetWeight = (TextView)findViewById(R.id.tv_TargetWeight);

        tv_TotalLoss = (TextView)findViewById(R.id.tv_TotalLoss);
        tv_WeeklyLoss = (TextView)findViewById(R.id.tv_WeeklyLoss);
        tv_DailyLoss = (TextView)findViewById(R.id.tv_DailyLoss);

        tv_StartDate.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_TargetDate.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_AchieveDate.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

        tv_StartWeight.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_LastWeight.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_TargetWeight.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

        tv_TotalLoss.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_WeeklyLoss.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_DailyLoss.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

        try {
            PopulateData();
        } catch (Exception e) {
                        StackTraceElement[] stack = e.getStackTrace();
                        String Trace = "";
                        for(StackTraceElement line : stack)
                        {
                            Trace += line.toString();
                            Trace += "\n";
                        }
                        Log.i("CYBERON", "1 Stack Trace:\n" + Trace);
        }
    }

    private void PopulateData()
    {
        Float totalLoss = 0f;
        Long diff = 0L;
        Long weeks = 0L;
        Long days = 0L;

        Float dayLoss = 0f;
        Float weekLoss = 0f;

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        totalLoss = WD.StartWeight - WD.LastWeight;

        diff = WD.LastDate.getTime() - WD.StartDate.getTime();
        weeks = Math.abs(diff / (1000 * 60 * 60 * 24*7));
        days = weeks * 7;

        weekLoss = (WD.StartWeight - WD.LastWeight) / weeks;
        dayLoss = (WD.StartWeight - WD.LastWeight) / days;

        tv_StartDate.setText(String.format("%-19s %s", "Start date:", formatter.format(WD.StartDate)));
        tv_TargetDate.setText(String.format("%-19s %s", "Targer date:", formatter.format(WD.TargetDate)));
        tv_AchieveDate.setText(String.format("%-19s %s", "Achieve date:", formatter.format(WD.AchieveDate)));

        tv_StartWeight.setText(String.format("%-20s %s lbs", "Start weight:", WD.StartWeight.toString()));
        tv_LastWeight.setText(String.format("%-20s %s lbs", "Last weight:", WD.LastWeight.toString()));
        tv_TargetWeight.setText(String.format("%-20s %s lbs", "Target weight:", "180.0"));

        tv_TotalLoss.setText(String.format("%-20s %5.1f lbs", "Total loss:",  totalLoss));
        tv_WeeklyLoss.setText(String.format("%-20s %5.1f lbs", "Weekly loss:", weekLoss));
        tv_DailyLoss.setText(String.format("%-20s %5.1f lbs", "Daily loss:", dayLoss));
    }
}
