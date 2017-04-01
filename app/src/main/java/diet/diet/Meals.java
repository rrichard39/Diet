package diet.diet;

import android.app.Application;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by rrichard39 on 3/26/2016.
 * Modified 1/23/2017
 */
class Meals extends Application {
    static HashMap<Integer, FoodItem> FoodTable = new HashMap<Integer, FoodItem>();
//    static List<Meal> MealList = new ArrayList<>();
    static double TOTAL_CALORIES;
    static Date DATE = null;
}
