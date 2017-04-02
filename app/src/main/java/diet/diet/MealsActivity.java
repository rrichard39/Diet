package diet.diet;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MealsActivity extends AppCompatActivity {

    TextView tv_MealList;
    public static List<Meal> MealList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);
        tv_MealList = (TextView) findViewById(R.id.tv_MealList);
        tv_MealList.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv_MealList.setTextSize(12);
        ListMeals();
    }

    private void ListMeals()
    {
        String mealsString;
        String food;
        Double quantity;
        Double total;
        tv_MealList.setTextColor(Color.BLACK);
        mealsString =  "                                        TOTAL\n";
        mealsString += "QTY  MEAL                                CAL\n";
        mealsString += "---------------------------------------------\n";

        for (Integer i = 0; i <  MealList.size(); i++)
        {
            food = MealList.get(i).Food.length() > 35 ? MealList.get(i).Food.substring(0, 32) + " ..." : MealList.get(i).Food;
            quantity = MealList.get(i).Quantity;
            total = MealList.get(i).Quantity * MealList.get(i).Calories;
            mealsString += String.format(Locale.US, "%4.1f %-33s %+6.1f\n", quantity, food, total);
        }
        mealsString += "---------------------------------------------\n";
        if (FoodData.TOTAL_CALORIES > 800 && FoodData.TOTAL_CALORIES <= 1200)
        {
            tv_MealList.setTextColor(Color.YELLOW);
        }
        else if (FoodData.TOTAL_CALORIES > 1200)
        {
            tv_MealList.setTextColor(Color.RED);
        }
        mealsString += String.format(Locale.US, "                     TOTAL CALORIES: %+8.1f\n", FoodData.TOTAL_CALORIES);
        tv_MealList.setText(mealsString);
    }
}
