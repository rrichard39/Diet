package diet.diet;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by rrichard39 on 3/23/2016.
 */
public class Weight {

    WeightData WD;

    Float goal = 0f;
    Date targetDate = new Date();
    Float startWeight = 0f;
    String measureDate = "";

    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    public Weight()
    {
        WD.FirstRun = true;
    }

    public void PopulateGraphArray()
    {
        Integer x = 0;
        goal = 0f;
//        WD.GraphArray = new ArrayList<WeightItem>();

        try
        {
            for (Integer i = 0; i < WD.GraphArray.size(); i++)
            {
                WeightItem GraphItem = new WeightItem();
                if (WD.GraphArray.get(i).weight1 != 0)
                {
                    if (goal == 0) {
                        goal = WD.GraphArray.get(i).weight1;
                        startWeight = WD.GraphArray.get(i).weight1;
                        WD.StartWeight = WD.GraphArray.get(i).weight1;
                        try {
                            WD.StartDate = formatter.parse(WD.GraphArray.get(0).measureDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    WD.GraphArray.get(i).goal = goal;
                    WD.GraphArray.get(i).x = x;
                    WD.GraphArray.get(i).xy = x * WD.GraphArray.get(i).weight1;
                    WD.GraphArray.get(i).v = Math.pow((double) x, 2);
                    WD.LastWeight = WD.GraphArray.get(i).weight1;
                    try {
                        WD.LastDate = formatter.parse(WD.GraphArray.get(i).measureDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    goal -= 2.5f;
                    x++;
                }
                else
                {
                    break;
                }
            }
            ++x;

            while (goal >= 181f)
            {
                WeightItem GraphItem = new WeightItem();
                targetDate = DateUtil.addDays(targetDate, 7);
                GraphItem.recNum = x;
                GraphItem.measureDate = formatter.format(targetDate);
                GraphItem.weight1 = 0f;
                GraphItem.goal = goal;
                GraphItem.x = x;
                GraphItem.xy = 0f;
                GraphItem.v = Math.pow((double)x, 2);
                goal -= 2.5f;
                x++;
                WD.GraphArray.add(GraphItem);
            }
            measureDate = WD.GraphArray.get(WD.GraphArray.size() - 1).measureDate;
            WD.TargetDate = formatter.parse(WD.GraphArray.get(WD.GraphArray.size() - 1).measureDate);
        }
        catch (Exception ex)
        {
            Log.i("CYBERON", ex.getMessage());
        }
    }

    public void CalculateGraphArray()
    {
        Integer numPts = 0;         // $S$43
        Double sumNumPts = 0d;      // $S$41
        Double sumNumPtsSq = 0d;
        Double sumWeight = 0d;      // $T$41
        Double sumXY = 0d;          // $U$41
        Double sumX_Sq = 0d;        // $V$41    sum all x's for valid weight's
        Double slope = 0d;          // $S$44     =(($S$43  * $U$41)-($S$41     *   $T$41  ))/(($S$43  *  $V$41 )-$S$41^2)
        //                                       =((numPts * sumXY)-(sumNumPts * sumWeight))/((numPts * sumX_Sq)-Math.Pow(sumNumPts, 2))
        Double intercept = 0d;      // $S$45     =(($V$41   *   $T$41  )-($S$41     * $U$41))/(($S$43  *  $V$41 )-($S$41^2))
        //                                       =((sumX_Sq * sumWeight)-(sumNumPts * sumXY))/((numPts * sumX_Sq)-(Math.Pow(sumNumPts, 2))
        Double intConverter = 0d;
        Integer slopeMultiplier = 0;
        Double actual = 0d;
        try
        {
            for (Integer i = 0; i < WD.GraphArray.size(); i++)
            {
                if (WD.GraphArray.get(i).weight1 > 0)
                {
                    numPts++;
//                    sumNumPts += WD.GraphArray.get(i).recnum - 1;
                    sumNumPts += WD.GraphArray.get(i).recNum - 1;
                    sumWeight += WD.GraphArray.get(i).weight1;
                    sumXY += WD.GraphArray.get(i).xy;
                    sumX_Sq += WD.GraphArray.get(i).v;
                }
            }

            sumNumPtsSq = Math.pow(sumNumPts, 2);

            //      ((numPts * sumXY) - (sumNumPts * sumWeight)) / ((numPts * sumX_Sq) - sumNumPtsSq)
            slope = ((numPts * sumXY) - (sumNumPts * sumWeight)) / ((numPts * sumX_Sq) - sumNumPtsSq);

            //         =((sumX_Sq * sumWeight) - (sumNumPts * sumXY)) / ((numPts * sumX_Sq) - sumNumPtsSq)
            intercept = ((sumX_Sq * sumWeight) - (sumNumPts * sumXY)) / ((numPts * sumX_Sq) - sumNumPtsSq);

            for (Integer i = 0; i < WD.GraphArray.size(); i++)
            {
                WD.GraphArray.get(i).actual = (slope * (WD.GraphArray.get(i).recNum - 1)) + intercept;
            }
            slopeMultiplier = WD.GraphArray.size();
            actual  = WD.GraphArray.get(WD.GraphArray.size() - 1).actual;

            intConverter = (7 * (181 - intercept) / slope);
//            measureDate = formatter.format(DateUtil.addDays(formatter.parse(WD.GraphArray.get(0).measureDate), intConverter.intValue()));
            WD.AchieveDate = DateUtil.addDays(formatter.parse(WD.GraphArray.get(0).measureDate), intConverter.intValue());

            if (WD.FirstRun) {
                while (actual > 181d) {
                    WeightItem GraphItem = new WeightItem();
                    measureDate = formatter.format(DateUtil.addDays(formatter.parse(measureDate), 7));
                    GraphItem.measureDate = measureDate;
                    GraphItem.recNum = slopeMultiplier;
                    actual = (slope * slopeMultiplier) + intercept;
                    GraphItem.actual = actual;
                    GraphItem.goal = 0f;
                    GraphItem.weight1 = 0f;
                    WD.GraphArray.add(GraphItem);
                    slopeMultiplier++;
                }
                WD.FirstRun = false;
            }
        }
        catch (Exception ex)
        {
            Log.i("CYBERON", ex.getMessage());
        }
    }
}
