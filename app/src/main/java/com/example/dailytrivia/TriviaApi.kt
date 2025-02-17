package com.example.dailytrivia

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApi {

    // Example of a GET request with parameters
    @GET("api.php")
    fun getTriviaQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String,
        @Query("type") type: String,
        @Query("encode") encode: String
    ): Call<TriviaResponseQuestions>
    // Example of a GET request with parameters
    @GET("api_category.php")
    fun getTriviaCategories(
    ): Call<TriviaResponseCategories>

}