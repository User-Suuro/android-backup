package com.example.filipinofoodreceipesredesign.helpers.db.local;

import com.example.filipinofoodreceipesredesign.models.RecipesModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Static {

    public static List<RecipesModel> getRecipes() {
        List<RecipesModel> recipes = new ArrayList<>();

        RecipesModel adobo = new RecipesModel.RecipeBuilder(
                "Adobo",
                "A savory Filipino dish made with chicken marinated in vinegar and soy sauce."
        )
                .setIngredients(Arrays.asList("Chicken", "Vinegar", "Soy Sauce", "Garlic", "Bay Leaves"))
                .setSteps(Arrays.asList("Marinate the chicken.", "Simmer until tender.", "Serve with rice."))
                .build();
        recipes.add(adobo);

        RecipesModel spaghettiBolognese = new RecipesModel.RecipeBuilder(
                "Spaghetti Bolognese",
                "A classic Italian pasta dish with a rich, meaty tomato sauce."
        )
                .setIngredients(Arrays.asList("Spaghetti", "Ground Beef", "Tomato Sauce", "Onion", "Garlic", "Carrot", "Celery"))
                .setSteps(Arrays.asList("Boil the spaghetti.", "Sauté the beef with onions and garlic.", "Add vegetables and tomato sauce.", "Simmer until flavors meld.", "Combine with pasta and serve."))
                .build();
        recipes.add(spaghettiBolognese);

        RecipesModel chickenTikkaMasala = new RecipesModel.RecipeBuilder(
                "Chicken Tikka Masala",
                "A popular Indian dish featuring grilled chicken in a spiced creamy tomato sauce."
        )
                .setIngredients(Arrays.asList("Chicken", "Yogurt", "Tomato Puree", "Cream", "Garlic", "Ginger", "Spices"))
                .setSteps(Arrays.asList("Marinate the chicken in yogurt and spices.", "Grill or bake the chicken until cooked.", "Prepare the sauce with tomato puree, cream, and spices.", "Combine the chicken with the sauce and simmer.", "Serve with rice or naan."))
                .build();
        recipes.add(chickenTikkaMasala);

        RecipesModel vegetableStirFry = new RecipesModel.RecipeBuilder(
                "Vegetable Stir Fry",
                "A healthy and quick dish featuring a mix of colorful vegetables tossed in a savory sauce."
        )
                .setIngredients(Arrays.asList("Broccoli", "Bell Peppers", "Carrots", "Snow Peas", "Soy Sauce", "Garlic", "Ginger"))
                .setSteps(Arrays.asList("Chop the vegetables into bite-sized pieces.", "Heat a wok and add garlic and ginger.", "Stir fry the vegetables on high heat.", "Add soy sauce and toss until evenly coated.", "Serve hot over rice or noodles."))
                .build();
        recipes.add(vegetableStirFry);

        RecipesModel pancakes = new RecipesModel.RecipeBuilder(
                "Pancakes",
                "Fluffy breakfast pancakes perfect with a drizzle of maple syrup."
        )
                .setIngredients(Arrays.asList("Flour", "Eggs", "Milk", "Baking Powder", "Salt", "Sugar", "Butter"))
                .setSteps(Arrays.asList("Mix the dry ingredients in a bowl.", "Whisk in eggs and milk until smooth.", "Heat a griddle and add butter.", "Pour batter and cook until bubbles form, then flip.", "Serve with maple syrup and fresh fruit."))
                .build();
        recipes.add(pancakes);

        return recipes;
    }
}
