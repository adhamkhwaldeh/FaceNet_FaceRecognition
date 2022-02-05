package com.aljawad.sons.verifoxxrepository

import android.content.Context
import com.aljawad.sons.dtos.core.FileReader
import com.aljawad.sons.dtos.model.FaceNetModel
import com.aljawad.sons.dtos.model.Models
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {

    // Serialized data will be stored ( in app's private storage ) with this filename.
    val SERIALIZED_DATA_FILENAME = "image_data"

    // Shared Pref key to check if the data was stored.
    val SHARED_PREF_IS_DATA_STORED_KEY = "is_data_stored"

    // Use the device's GPU to perform faster computations.
    // Refer https://www.tensorflow.org/lite/performance/gpu
    private val useGpu = true

    // Use XNNPack to accelerate inference.
    // Refer https://blog.tensorflow.org/2020/07/accelerating-tensorflow-lite-xnnpack-integration.html
    private val useXNNPack = true

    // You may the change the models here.
    // Use the model configs in Models.kt
    // Default is Models.FACENET ; Quantized models are faster
    private val modelInfo = Models.FACENET


    @Provides
    @Singleton
    fun provideFaceNetModel(@ApplicationContext context: Context): FaceNetModel =
        FaceNetModel(modelInfo, useGpu, useXNNPack, context)

    @Provides
    @Singleton
    fun provideFileReader(faceNetModel: FaceNetModel): FileReader =
        FileReader(faceNetModel)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

}