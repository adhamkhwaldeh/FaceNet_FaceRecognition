package com.aljawad.sons.network

import android.content.Context
import com.aljawad.sons.network.apiServices.apiInterfaces.UserApi
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tatweer.rwandacore.apiServices.interceptors.HeaderRequestInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideGSon(): Gson =
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .serializeNulls()
            .setLenient()
            .create()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
//        sharedPreferenceRepository: SharedPreferenceRepository,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder().apply {
        interceptors().add(httpLoggingInterceptor)
        interceptors().add(HeaderRequestInterceptor())
//        interceptors().add(NetworkConnectionInterceptor(context))
    }.build()

    @Provides
    @Singleton
    fun provideRetrofit(gSon: Gson, okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().apply {
//            baseUrl("https://gorest.co.in/public/v1/")
            baseUrl(BuildConfig.END_POINT)
            addConverterFactory(GsonConverterFactory.create(gSon))
            addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            client(okHttpClient)
        }.build()

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create<UserApi>(UserApi::class.java)

}