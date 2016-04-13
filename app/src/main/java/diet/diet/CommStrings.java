package diet.diet;

/**
 * Created by rrichard39 on 3/27/2016.
 */
public class CommStrings {
    public static final Integer TIMEOUT = 180000;   // 3 minutes

    public static final String NAMESPACE = "http://tempuri.org/";
    // ********************************************************
    //
    // This can be altered by MainActiity
    //
    // ********************************************************
    public static String URL;

    // Food Table
    public static final String METHOD_GET_FOOD_LIST = "GetFoodList";
    public final static String SOAP_ACTION_GET_FOOD_LIST = "http://tempuri.org/IDietService/GetFoodList";

    // Add Daily Meal
    public static final String METHOD_ADD_DAILY_FOOD_ITEM = "AddDailyFoodItem";
    public final static String SOAP_ACTION_ADD_DAILY_FOOD_ITEM ="http://tempuri.org/IDietService/AddDailyFoodItem";

    // Get Daily total calories
    public static final String METHOD_GET_DAILY_TOTAL = "GetDailyTotal";
    public final static String SOAP_ACTION_GET_DAILY_TOTAL ="http://tempuri.org/IDietService/GetDailyTotal";

    // Weight Table
    public static final String METHOD_GET_WEIGHT = "GetWeight";
    public final static String SOAP_ACTION_GET_WEIGHT = "http://tempuri.org/IDietService/GetWeight";

    // Daily Table
    public static final String METHOD_GET_MEALS = "GetMeals";
    public final static String SOAP_ACTION_GET_MEALS = "http://tempuri.org/IDietService/GetMeals";

    // Food Table
    public static final String METHOD_ADD_NEW_FOOD = "AddNewFood";
    public final static String SOAP_ACTION_ADD_NEW_FOOD = "http://tempuri.org/IDietService/AddNewFood";

    // Add Weight
    public static final String METHOD_ADD_WEIGHT = "AddWeight";
    public final static String SOAP_ACTION_ADD_WEIGHT = "http://tempuri.org/IDietService/AddWeight";
}
