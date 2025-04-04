package com.example.sariapp.helpers.db.local.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey {
    String columnName();             // Column name in this table
    Class<?> referencedTable();      // Referenced model class
    String referencedColumn();       // Column in the referenced table
}
