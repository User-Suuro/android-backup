package com.example.filipinofoodreceipesredesign.helpers.db.local.sql;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey {
    String columnName();             // Column name in this table
    Class<?> referencedTable();      // Referenced model class
    String referencedColumn();       // Column in the referenced table
}
