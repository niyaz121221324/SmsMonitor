package com.example.smsmonitor;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TelegramService {
    @POST("sendMessage")
    Call<String> sendMessage(@Query("chatId") Long chatId ,@Body Message message);

    @GET("getChatId")
    Call<Long> getChatId(@Query("userName") String userName);
}
