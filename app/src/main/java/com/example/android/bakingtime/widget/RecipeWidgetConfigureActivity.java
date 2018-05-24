package com.example.android.bakingtime.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.databinding.RecipeWidgetConfigureBinding;
import com.example.android.bakingtime.main.MainActivity;
import com.example.android.networkmodule.model.Ingredient;
import com.example.android.networkmodule.model.Recipe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * The configuration screen for the {@link RecipeWidget RecipeWidget} AppWidget.
 */
public class RecipeWidgetConfigureActivity extends Activity implements OnRecipeItemClickListener {

    private static final String PREFS_NAME = "com.example.android.bakingtime.widget.RecipeWidget";
    private static final String TITLE_PREFIX_KEY = "title_appwidget_";
    private static final String CONTENT_PREFIX_KEY = "content_appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private WidgetRecipesAdapter widgetRecipesAdapter;
    private List<Recipe> recipes;

    public RecipeWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void savePref(Context context, String prefixKey, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(prefixKey + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadContentPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String contentValue = prefs.getString(CONTENT_PREFIX_KEY + appWidgetId, null);
        if (contentValue != null) {
            return contentValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(TITLE_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    private RecipeWidgetConfigureBinding binding;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.recipe_widget_configure);

        binding = DataBindingUtil.setContentView(this, R.layout.recipe_widget_configure);

        initRecyclerView();

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        widgetRecipesAdapter.setRecipes(getRecipes());
        widgetRecipesAdapter.setClickListener(this);
    }

    private List<Recipe> getRecipes() {
        SharedPreferences prefs = getSharedPreferences(MainActivity.RECIPES_PREFERENCE_NAME, MODE_PRIVATE);
        String recipeJson = prefs.getString(MainActivity.RECIPES_PREFERENCE_KEY, null);

        if (recipeJson != null) {

            Type type = new TypeToken<List<Recipe>>() {
            }.getType();
            recipes = new Gson().fromJson(recipeJson, type);
            return recipes;

        } else {
            return null;
        }
    }

    private void initRecyclerView() {
        widgetRecipesAdapter = new WidgetRecipesAdapter(this);

        binding.widgetRecipesRecyclerView.setHasFixedSize(true);
        binding.widgetRecipesRecyclerView.setAdapter(widgetRecipesAdapter);
    }

    @Override
    public void onItemClick(int position) {
        final Context context = RecipeWidgetConfigureActivity.this;

        String recipeSelected = recipes.get(position).getName();
        String formattedIngredients = getFormattedIngredients(recipes.get(position).getIngredients());

        // When the button is clicked, store the string locally
        savePref(context, TITLE_PREFIX_KEY, mAppWidgetId, recipeSelected);
        savePref(context, CONTENT_PREFIX_KEY, mAppWidgetId, formattedIngredients);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RecipeWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private String getFormattedIngredients(List<Ingredient> ingredients) {
        List<String> ingredientsList = new ArrayList<>();
        ingredientsList.add(getString(R.string.ingredients));
        for (Ingredient ingredient : ingredients) {
            ingredientsList.add(ingredient.toString());
        }
        return TextUtils.join("\n", ingredientsList);
    }
}

