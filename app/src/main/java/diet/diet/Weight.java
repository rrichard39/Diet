package diet.diet;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rrichard39 on 3/23/2016.
 */
public class Weight {

    Float goal = 0f;
    Date targetDate = new Date();
    Float startWeight = 0f;
    Float prevWeight = 0f;
    String measureDate = "";

    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    public Weight()
    {
//        String hitCounts = String.format(Locale.US, "WEIGHT WeightData.FirstRun: %s", (WeightData.FirstRun) ? "TRUE" : "FALSE");
//        Log.i("CYBERON", hitCounts);
    }

    public void PopulateGraphArray()
    {
        Integer x = 0;
        goal = 0f;

        Log.i("CYBERON", "PopulateGraphArray");
        try
        {
            for (Integer i = 0; i < WeightData.GraphArray.size(); i++)
            {
                if (WeightData.GraphArray.get(i).weight1 != 0)
                {
                    if (goal == 0) {
                        goal = WeightData.GraphArray.get(i).weight1;
                        startWeight = WeightData.GraphArray.get(i).weight1;
                        WeightData.StartWeight = WeightData.GraphArray.get(i).weight1;
                        WeightData.LowestWeight = WeightData.GraphArray.get(i).weight1;
                        try {
                            WeightData.StartDate = formatter.parse(WeightData.GraphArray.get(0).measureDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if ( WeightData.GraphArray.get(i).weight1 < WeightData.LowestWeight)
                    {
                        WeightData.LowestWeight = WeightData.GraphArray.get(i).weight1;
                    }
                    WeightData.GraphArray.get(i).goal = goal;
                    WeightData.GraphArray.get(i).x = x;
                    WeightData.GraphArray.get(i).xy = x * WeightData.GraphArray.get(i).weight1;
                    WeightData.GraphArray.get(i).v = Math.pow((double) x, 2);
                    prevWeight = WeightData.LastWeight;
                    WeightData.LastWeight = WeightData.GraphArray.get(i).weight1;
                    try {
                        WeightData.LastDate = formatter.parse(WeightData.GraphArray.get(i).measureDate);
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
            WeightData.GainLoss = WeightData.LastWeight - prevWeight;
            ++x;

            // Create GraphItem for every week until goal is reached
            // based on latest least squares calculation
            while (goal >= PersonalData.TargetWeight)
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
                WeightData.GraphArray.add(GraphItem);
            }
            measureDate = WeightData.GraphArray.get(WeightData.GraphArray.size() - 1).measureDate;
            WeightData.TargetDate = formatter.parse(WeightData.GraphArray.get(WeightData.GraphArray.size() - 1).measureDate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void CalculateGraphArray()
    {
        Integer numPts = 0;         // $S$43
        Double sumNumPts = 0d;      // $S$41
        Double sumNumPtsSq;
        Double sumWeight = 0d;      // $T$41
        Double sumXY = 0d;          // $U$41
        Double sumX_Sq = 0d;        // $V$41    sum all x's for valid weight's
        Double slope;               // $S$44     =(($S$43  * $U$41)-($S$41     *   $T$41  ))/(($S$43  *  $V$41 )-$S$41^2)
        //                                       =((numPts * sumXY)-(sumNumPts * sumWeight))/((numPts * sumX_Sq)-Math.Pow(sumNumPts, 2))
        Double intercept;           // $S$45     =(($V$41   *   $T$41  )-($S$41     * $U$41))/(($S$43  *  $V$41 )-($S$41^2))
        //                                       =((sumX_Sq * sumWeight)-(sumNumPts * sumXY))/((numPts * sumX_Sq)-(Math.Pow(sumNumPts, 2))
        Double intConverter;
        Integer slopeMultiplier;
        Double actual;

        Log.i("CYBERON", "CalculateGraphArray");
        String hitCounts = String.format(Locale.US, "CalculateGraphArray WeightData.FirstRun: %s", (WeightData.FirstRun) ? "TRUE" : "FALSE");
        Log.i("CYBERON", hitCounts);
        try
        {
            for (Integer i = 0; i < WeightData.GraphArray.size(); i++)
            {
                if (WeightData.GraphArray.get(i).weight1 > 0)
                {
                    numPts++;
                    sumNumPts += WeightData.GraphArray.get(i).recNum - 1;
                    sumWeight += WeightData.GraphArray.get(i).weight1;
                    sumXY += WeightData.GraphArray.get(i).xy;
                    sumX_Sq += WeightData.GraphArray.get(i).v;
                }
            }

            sumNumPtsSq = Math.pow(sumNumPts, 2);

            //      ((numPts * sumXY) - (sumNumPts * sumWeight)) / ((numPts * sumX_Sq) - sumNumPtsSq)
            slope = ((numPts * sumXY) - (sumNumPts * sumWeight)) / ((numPts * sumX_Sq) - sumNumPtsSq);

            //         =((sumX_Sq * sumWeight) - (sumNumPts * sumXY)) / ((numPts * sumX_Sq) - sumNumPtsSq)
            intercept = ((sumX_Sq * sumWeight) - (sumNumPts * sumXY)) / ((numPts * sumX_Sq) - sumNumPtsSq);

            for (Integer i = 0; i < WeightData.GraphArray.size(); i++)
            {
                WeightData.GraphArray.get(i).actual = (slope * (WeightData.GraphArray.get(i).recNum - 1)) + intercept;
            }

//            WeightData.Variance = (float)(WeightData.GraphArray.get(numPts - 1).actual - WeightData.GraphArray.get(numPts - 1).goal);
            WeightData.Variance = WeightData.LastWeight - PersonalData.TargetWeight;

            slopeMultiplier = WeightData.GraphArray.size();
            actual  = WeightData.GraphArray.get(WeightData.GraphArray.size() - 1).actual;
            WeightData.DevFromLS = WeightData.LastWeight - actual.floatValue();

                    intConverter = (7 * (PersonalData.TargetWeight - intercept) / slope);

            WeightData.AchieveDate = DateUtil.addDays(formatter.parse(WeightData.GraphArray.get(0).measureDate), intConverter.intValue() + 7);

//            if (WeightData.FirstRun) {

//                Log.i("CYBERON", "Extending actual");
                while (actual > PersonalData.TargetWeight) {
                    WeightItem GraphItem = new WeightItem();
                    measureDate = formatter.format(DateUtil.addDays(formatter.parse(measureDate), 7));
                    GraphItem.measureDate = measureDate;
                    GraphItem.recNum = slopeMultiplier;
                    actual = (slope * slopeMultiplier) + intercept;
                    GraphItem.actual = actual;
                    GraphItem.goal = 0f;
                    GraphItem.weight1 = 0f;
                    WeightData.GraphArray.add(GraphItem);
                    slopeMultiplier++;
                }
//                WeightData.FirstRun = false;
//            }

            WeightData.BMI = (WeightData.LastWeight * 703.0f)/(float)Math.pow(PersonalData.height, 2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
