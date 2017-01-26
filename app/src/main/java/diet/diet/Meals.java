package diet.diet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rrichard39 on 3/26/2016.
 * Modified 1/23/2017
 */
class Meals {
    static HashMap<Integer, FoodItem> FoodTable = new HashMap<Integer, FoodItem>();
    static List<Meal> MealList = new ArrayList<>();
    static double TOTAL_CALORIES;
    static Date DATE = null;
}
