package com.example.finebyme.data.datasource

import android.util.Log
import com.example.finebyme.BuildConfig
import com.example.finebyme.data.model.UnsplashPhoto
import com.example.finebyme.data.network.RetrofitService
import com.example.finebyme.utils.NetworkUtils

class UnSplashDataSource(private val retrofitService: RetrofitService) {

    private val apiKey = BuildConfig.UNSPLASH_API_KEY

    // Fetch random photos
    fun getRandomPhotoList(onResult: (Result<List<UnsplashPhoto>>?) -> Unit) {
        Log.d("UnSplashDataSource", "API 호출 시작")
        NetworkUtils.enqueueCall(retrofitService.getRandomPhoto(apiKey, 1000),
            onSuccess = { response ->
                val photos = response.body()
                if (!photos.isNullOrEmpty()) {
                    Log.d("UnSplashDataSource", "API 호출 성공: ${photos.size}개의 사진")
                    onResult(Result.success(photos))
                } else {
                    Log.d("UnSplashDataSource", "API 호출 성공: 사진 없음")
                    onResult(Result.success(emptyList()))
                }
//                onResult(Result.failure(Exception("Forced exception for testing onFailure")))
            },
            onFailure = { t ->
                // 에러를 정의하고 에러코드에 따라 처리
                Log.e("UnSplashDataSource", "API 호출 실패: ${t.message}")
                onResult(Result.failure(t))
            })
    }

    // Search photos by query
    fun getSearchPhotoList(query: String, onResult: (Result<List<UnsplashPhoto>>?) -> Unit) {
        NetworkUtils.enqueueCall(retrofitService.getSearchPhoto(apiKey, query),
            onSuccess = { response ->
                val photos = response.body()?.results ?: emptyList()
                if (photos.isNotEmpty()) {
                    onResult(Result.success(photos))
                } else {
                    Log.d("RetrofitInstance", "No photos found")
                    onResult(Result.success(emptyList()))
                }
            }, onFailure = { t ->
                Log.e("RetrofitInstance", "Failed to fetch data: ${t.message}")
                onResult(Result.failure(t))
            })
    }
}