package diet.diet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.PointValue;

/**
 * Created by rrichard39 on 3/25/2016.
 */
public class WeightData {

    public static List<PointValue> GraphWeight;
    public static List<PointValue> GraphTarget;
    public static List<PointValue> GraphActual;
    public static List<PointValue> GraphFirstLabel;
    public static List<PointValue> GraphLastLabel;

    public static List<WeightItem> GraphArray = new ArrayList<WeightItem>();
    public static Date StartDate;
    public static Date LastDate;
    public static Date TargetDate;
    public static Double Variance;
    public static Date AchieveDate;
    public static String LowestWeightDate;

    public static Float StartWeight;
    public static Float LastWeight;
    public static Float DevFromLS;
    public static Float GainLoss;
    public static Float LowestWeight;
    public static Float BMI;

    public static Boolean FirstRun = true;
}
