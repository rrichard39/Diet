package diet.diet;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

public class MealsActivity extends AppCompatActivity {

    TextView tv_MealList;

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

        for (Integer i = 0; i <  Meals.MealList.size(); i++)
        {
            food = Meals.MealList.get(i).Food.length() > 35 ? Meals.MealList.get(i).Food.substring(0, 32) + " ..." : Meals.MealList.get(i).Food;
            quantity = Meals.MealList.get(i).Quantity;
            total = Meals.MealList.get(i).Quantity * Meals.MealList.get(i).Calories;
            mealsString += String.format(Locale.US, "%4.1f %-33s %+6.1f\n", quantity, food, total);
        }
        mealsString += "---------------------------------------------\n";
        if (Meals.TOTAL_CALORIES > 800 && Meals.TOTAL_CALORIES <= 1200)
        {
            tv_MealList.setTextColor(Color.YELLOW);
        }
        else if (Meals.TOTAL_CALORIES > 1200)
        {
            tv_MealList.setTextColor(Color.RED);
        }
        mealsString += String.format(Locale.US, "                     TOTAL CALORIES: %+8.1f\n", Meals.TOTAL_CALORIES);
        tv_MealList.setText(mealsString);
    }
}
