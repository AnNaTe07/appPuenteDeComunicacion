package com.softannate.apppuentedecomunicacion.data.api;

import com.google.gson.JsonObject;
import com.softannate.apppuentedecomunicacion.modelos.dto.TokenDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/auth/refresh")
    Call<TokenDto> refreshTokenSync(@Body JsonObject refreshToken);
}