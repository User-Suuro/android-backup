package com.example.filipinofoodreceipesredesign.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipesModel {
    private final long id;
    private final String name;
    private final String description;
    private final String imageUrl;
    private final List<String> ingredients;
    private final List<String> steps;
    private final boolean favorite;
    private final String category;

    // Private constructor that takes a Builder and required fields.
    private RecipesModel(RecipeBuilder builder) {
        this.id = builder.id;  // Allowing id to be set via builder
        this.name = builder.name;
        this.description = builder.description;
        this.imageUrl = builder.imageUrl;
        this.favorite = builder.favorite;
        this.category = builder.category;

        // Ensuring immutability
        this.ingredients = Collections.unmodifiableList(new ArrayList<>(builder.ingredients));
        this.steps = Collections.unmodifiableList(new ArrayList<>(builder.steps));
    }

    // Getters for each field.
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getCategory() {
        return category;
    }

    // Static Builder class.
    public static class RecipeBuilder {
        // REQUIRED
        private final String name;
        private final String description;

        // OPTIONAL
        private long id = 0;  // Allowing optional ID initialization
        private String imageUrl = null;
        private List<String> ingredients = new ArrayList<>();
        private List<String> steps = new ArrayList<>();
        private boolean favorite = false;
        private String category = "Recipes";

        public RecipeBuilder(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public RecipeBuilder setId(long id) {
            this.id = id;
            return this;
        }

        public RecipeBuilder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
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
