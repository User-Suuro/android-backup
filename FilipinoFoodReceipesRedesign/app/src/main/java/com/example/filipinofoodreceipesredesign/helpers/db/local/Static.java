package com.example.filipinofoodreceipesredesign.helpers.db.local;

import android.content.Context;
import android.widget.Toast;

import com.example.filipinofoodreceipesredesign.R;
import com.example.filipinofoodreceipesredesign.models.RecipesModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Static {

    public static List<RecipesModel> getData(Context context) {
        List<RecipesModel> data = new ArrayList<>();

        String[] names = context.getResources().getStringArray(R.array.names);
        String[] descriptions = context.getResources().getStringArray(R.array.descriptions);
        String[] ingredientsList = context.getResources().getStringArray(R.array.ingredients);
        String[] stepsList = context.getResources().getStringArray(R.array.steps);
        String[] category = context.getResources().getStringArray(R.array.category);
        String[] img = context.getResources().getStringArray(R.array.url);

        for (int i = 0; i < names.length; i++) {
            RecipesModel recipe = new RecipesModel.RecipeBuilder(
                    names[i],
                    descriptions[i],
                    img[i]
            )
                    .setIngredients(Arrays.asList(ingredientsList[i].split(", ")))
                    .setSteps(Arrays.asList(stepsList[i].split("; ")))
                    .setCategory(category[i])
                    .build();
            data.add(recipe);
        }

        return data;
    }
}
