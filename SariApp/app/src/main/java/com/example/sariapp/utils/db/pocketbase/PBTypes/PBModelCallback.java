package com.example.sariapp.utils.db.pocketbase.PBTypes;

public interface PBModelCallback<T> {
    void onSuccess(T result);
    void onError(String error);
}

