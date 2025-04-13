package com.example.sariapp.utils.db.pocketbase.PBTypes;

public interface PBCallback {
    void onSuccess(String result);

    void onError(String error);
}
