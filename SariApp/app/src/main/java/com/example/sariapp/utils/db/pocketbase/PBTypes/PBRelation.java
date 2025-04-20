package com.example.sariapp.utils.db.pocketbase.PBTypes;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PBRelation {
    Class<?> relatedType(); // Class of related model
    boolean isList() default false;
}
