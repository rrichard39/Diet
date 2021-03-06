package diet.diet;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static diet.diet.MainActivity.returnFromActivity;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_StartDate;
    TextView tv_LowestWeightDate;
    TextView tv_TargetDate;
    TextView tv_AchieveDate;
    TextView tv_Variance;

    TextView tv_StartWeight;
    TextView tv_LastWeight;
    TextView tv_DevFromLS;
    TextView tv_GainLoss;
    TextView tv_LowestWeight;
    TextView tv_TargetWeight;

    TextView tv_TotalLoss;
    TextView tv_WeeklyLoss;
    TextView tv_DailyLoss;

    TextView tv_BMI;

    Button btn_Return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        tv_StartDate = (TextView)findViewById(R.id.tv_StartDate);
        tv_LowestWeightDate = (TextView)findViewById(R.id.tv_LowestWeightDate);
        tv_TargetDate = (TextView)findViewById(R.id.tv_TargetDate);
        tv_AchieveDate = (TextView)findViewById(R.id.tv_AchieveDate);
        tv_Variance = (TextView)findViewById(R.id.tv_Variance);

        tv_StartWeight = (TextView)findViewById(R.id.tv_StartWeight);
        tv_LastWeight = (TextView)findViewById(R.id.tv_LastWeight);
        tv_DevFromLS = (TextView)findViewById(R.id.tv_DevFromLS);
        tv_GainLoss = (TextView)findViewById(R.id.tv_GainLoss);
        tv_LowestWeight = (TextView)findViewById(R.id.tv_LowestWeight);
        tv_TargetWeight = (TextView)findViewById(R.id.tv_TargetWeight);

        tv_TotalLoss = (TextView)findViewById(R.id.tv_TotalLoss);
        tv_WeeklyLoss = (TextView)findViewById(R.id.tv_WeeklyLoss);
        tv_DailyLoss = (TextView)findViewById(R.id.tv_DailyLoss);

        tv_BMI = (TextView)findViewById(R.id.tv_BMI);

        btn_Return = (Button) findViewById(R.id.btn_Return);
        btn_Return.setOnClickListener(this);

        tv_StartDate.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_LowestWeightDate.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_TargetDate.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_AchieveDate.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_Variance.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

        tv_StartWeight.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_LastWeight.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_DevFromLS.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_GainLoss.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_LowestWeight.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_TargetWeight.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

        tv_TotalLoss.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_WeeklyLoss.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_DailyLoss.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

        tv_BMI.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void PopulateData()
    {
        Float totalLoss;
        Long diff;
        Long weeks;
        Long days;
        Date LowestWeightDate = null;

        Float dayLoss;
        Float weekLoss;

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        totalLoss = WeightData.StartWeight - WeightData.LastWeight;

        diff = WeightData.LastDate.getTime() - WeightData.StartDate.getTime();
        weeks = Math.abs(diff / (1000 * 60 * 60 * 24*7));
        days = weeks * 7;

        try {
            LowestWeightDate = formatter.parse(WeightData.LowestWeightDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        weekLoss = (WeightData.StartWeight - WeightData.LastWeight) / weeks;
        dayLoss = (WeightData.StartWeight - WeightData.LastWeight) / days;

        tv_StartDate.setText(String.format(Locale.US, "%-18s %s", "Start date:", formatter.format(WeightData.StartDate)));
        tv_LowestWeightDate.setText(String.format(Locale.US, "%-18s %s", "Lowest wt date:", WeightData.LowestWeightDate));
        tv_TargetDate.setText(String.format(Locale.US, "%-18s %s", "Target date:", formatter.format(WeightData.TargetDate)));
        tv_AchieveDate.setText(String.format(Locale.US, "%-18s %s", "Achieve date:", formatter.format(WeightData.AchieveDate)));

        tv_StartWeight.setText(String.format(Locale.US, "%-19s %5.1f lbs", "Start weight:", WeightData.StartWeight));
        tv_LastWeight.setText(String.format(Locale.US, "%-19s %5.1f lbs", "Last weight:", WeightData.LastWeight));
        tv_DevFromLS.setText(String.format(Locale.US, "%-19s %+5.1f lbs", "Dev. From LS:", WeightData.DevFromLS));
        tv_GainLoss.setText(String.format(Locale.US, "%-19s %+5.1f lbs", "Week Loss/Gain:", WeightData.GainLoss));
        tv_LowestWeight.setText(String.format(Locale.US, "%-19s %5.1f lbs", "Lowest weight:", WeightData.LowestWeight));
        tv_TargetWeight.setText(String.format(Locale.US, "%-19s %5.1f lbs", "Target weight:", PersonalData.TargetWeight));
        tv_Variance.setText(String.format(Locale.US, "%-19s %5.1f lbs", "Left to lose:", WeightData.Variance));

        tv_TotalLoss.setText(String.format(Locale.US, "%-19s %5.1f lbs", "Total loss:",  totalLoss));
        tv_WeeklyLoss.setText(String.format(Locale.US, "%-19s %5.1f lbs", "Avg. Weekly loss:", weekLoss));
        tv_DailyLoss.setText(String.format(Locale.US, "%-19s %5.1f lbs", "Avg. Daily loss:", dayLoss));

        tv_BMI.setText(String.format(Locale.US, "%-19s %5.1f", "BMI:", WeightData.BMI));
    }

    @Override
    public void onClick(View v)
    {
        Intent returnIntent = new Intent(this, MainActivity.class);
        returnIntent.putExtra("true", returnFromActivity);
        returnIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(returnIntent);
//        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
