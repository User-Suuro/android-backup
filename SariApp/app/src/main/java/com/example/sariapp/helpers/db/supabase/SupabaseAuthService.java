package com.example.sariapp.helpers.db.supabase;

import com.example.sariapp.models.auth.SignUpRequest;
import com.example.sariapp.models.auth.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SupabaseAuthService {
    @Headers({
            "apikey: YOUR_SUPABASE_ANON_KEY",
            "Content-Type: application/json"
    })
    @POST("auth/v1/signup")
    Call<UserResponse> signUp(@Body SignUpRequest request);
}