package com.practicum.pexelsapp.data.remote


import com.practicum.pexelsapp.data.dto.CollectionsResponseDto
import com.practicum.pexelsapp.data.dto.PhotoDto
import com.practicum.pexelsapp.data.dto.PhotosResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PexelsApi {

    @GET("v1/curated")
    suspend fun getCuratedPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30
    ): PhotosResponseDto


    @GET("v1/search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30
    ): PhotosResponseDto


    @GET("v1/collections/featured")
    suspend fun getFeaturedCollections(
        @Query("per_page") perPage: Int = 7
    ): CollectionsResponseDto

    @GET("v1/photos/{id}")
    suspend fun getPhotoById(
        @Path("id") id: Int
    ): PhotoDto
}