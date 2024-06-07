package io.aasmart.goalpost.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.aasmart.goalpost.api.interceptors.AuthInterceptor
import io.aasmart.goalpost.api.interceptors.CSRFCookieInterceptor
import io.aasmart.goalpost.api.GoalpostApi
import io.aasmart.goalpost.api.GoalpostRepository
import io.aasmart.goalpost.api.GoalpostRepositoryImpl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAuthApi(app: Application): GoalpostApi {
        return Retrofit.Builder()
            .baseUrl("http://172.17.157.57:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(app.applicationContext))
                    .addInterceptor(CSRFCookieInterceptor(app.applicationContext))
                    .build()
            )
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideSharedPref(app: Application): SharedPreferences {
        return app.getSharedPreferences("prefs", MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: GoalpostApi, prefs: SharedPreferences): GoalpostRepository {
        return GoalpostRepositoryImpl(api, prefs)
    }
}