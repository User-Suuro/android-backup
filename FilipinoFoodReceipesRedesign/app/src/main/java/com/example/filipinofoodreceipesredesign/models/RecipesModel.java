package com.example.filipinofoodreceipesredesign.models;

import com.example.filipinofoodreceipesredesign.helpers.db.local.sql.Column;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RecipesModel {

    @Column(name = "id", type = "INTEGER PRIMARY KEY AUTOINCREMENT")
    private long id;

    @Column(name = "name", type = "TEXT")
    private String name;

    @Column(name = "description", type = "TEXT")
    private String description;

    @Column(name = "recipeUrl", type = "TEXT")
    private String recipeUrl;

    // Stored as JSON strings in SQLite.
    @Column(name = "ingredients", type = "TEXT")
    private List<String> ingredients;

    @Column(name = "steps", type = "TEXT")
    private List<String> steps;

    @Column(name = "favorite", type = "INTEGER")
    private boolean favorite;

    @Column(name = "category", type = "TEXT")
    private String category;

    // Public no-argument constructor for reflection (data-loading).
    public RecipesModel() {
        this.id = 0;
        this.name = "";
        this.description = "";
        this.recipeUrl = "";
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.favorite = false;
        this.category = "";
    }

    // Private constructor via Builder.
    private RecipesModel(RecipeBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.recipeUrl = builder.recipeUrl;
        this.favorite = builder.favorite;
        this.category = builder.category;
        // Defensive copy to ensure immutability after creation.
        this.ingredients = Collections.unmodifiableList(new ArrayList<>(builder.ingredients));
        this.steps = Collections.unmodifiableList(new ArrayList<>(builder.steps));
    }

    // Getters.
    public long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getRecipeUrl() { return recipeUrl; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getSteps() { return steps; }
    public boolean isFavorite() { return favorite; }
    public String getCategory() { return category; }

    // JSON helper methods for ingredients.
    public String getIngredientsJson() {
        return new Gson().toJson(ingredients);
    }
    public static List<String> parseIngredients(String json) {
        Type listType = new TypeToken<List<String>>(){}.getType();
        return new Gson().fromJson(json, listType);
    }

    // JSON helper methods for steps.
    public String getStepsJson() {
        return new Gson().toJson(steps);
    }
    public static List<String> parseSteps(String json) {
        Type listType = new TypeToken<List<String>>(){}.getType();
        return new Gson().fromJson(json, listType);
    }

    // Builder class.
    public static class RecipeBuilder {
        // REQUIRED
        private final String name;
        private final String description;
        private final String recipeUrl;
        // OPTIONAL
        private long id;

        private List<String> ingredients = new ArrayList<>();
        private List<String> steps = new ArrayList<>();
        private boolean favorite = false;
        private String category = "Recipes";

        public RecipeBuilder(String name, String description, String recipeUrl) {
            this.name = name;
            this.description = description;
            this.recipeUrl = recipeUrl;
        }

        public RecipeBuilder setId(long id) {
            this.id = id;
            return this;
        }

        public RecipeBuilder setIngredients(List<String> ingredients) {
            this.ingredients = new ArrayList<>(ingredients);
            return this;
        }
        public RecipeBuilder addIngredient(String ingredient) {
            this.ingredients.add(ingredient);
            return this;
        }
        public RecipeBuilder setSteps(List<String> steps) {
            this.steps = new ArrayList<>(steps);
            return this;
        }
        public RecipeBuilder addStep(String step) {
            this.steps.add(step);
            return this;
        }
        public RecipeBuilder setFavorite(boolean favorite) {
            this.favorite = favorite;
            return this;
        }
        public RecipeBuilder setCategory(String category) {
            this.category = category;
            return this;
        }
        public RecipesModel build() {
            return new RecipesModel(this);
        }
    }
}
