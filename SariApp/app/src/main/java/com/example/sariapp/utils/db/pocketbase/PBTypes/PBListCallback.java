package com.example.sariapp.utils.db.pocketbase.PBTypes;

import java.util.List;

public interface PBListCallback<T> {
    void onSuccess(List<T> result);
    void onError(String error);
}

