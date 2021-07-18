package com.example.bookreview.api

import com.example.bookreview.model.BestSellerDTO
import com.example.bookreview.model.SearchBookDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface BookService {
    @GET("/api/search.api?output=json")
    fun getBookByName(@Query("key") key : String, @Query("query") keyword : String): Call<SearchBookDTO>

    @GET("/api/bestSeller.api?output=json&categoryId=100")
    fun getBestSellerBooks(@Query("key") key: String) : Call<BestSellerDTO>
}