package com.aljawad.sons.verifoxxcore.viewModel

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.aljawad.sons.business.AddUserUseCase
import com.aljawad.sons.dtos.core.FileReader
import com.aljawad.sons.dtos.model.FaceNetModel
import com.aljawad.sons.dtos.model.Models
import com.aljawad.sons.mainlibrary.base.BaseViewModel
import com.aljawad.sons.verifoxxcore.R
import com.aljawad.sons.dtos.utils.Logger
import com.aljawad.sons.mainlibrary.states.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import javax.inject.Inject

@HiltViewModel
class ImageProcessingViewModel @Inject constructor(
    private val addUserUseCase: AddUserUseCase,
    val faceNetModel: FaceNetModel,
    private val sharedPreferences: SharedPreferences,
    private val handle: SavedStateHandle
) : BaseViewModel() {

    //region add user
    private lateinit var _userAddFlow: Flow<BaseState<Pair<ArrayList<Pair<String, FloatArray>>, Int>>>

    val userAddFlow: Flow<BaseState<Pair<ArrayList<Pair<String, FloatArray>>, Int>>>
        get() = _userAddFlow

    fun addUser(users: ArrayList<Pair<String, Bitmap>>) { //: Flow<BaseState>
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO)
                .launch {
                    _userAddFlow = addUserUseCase(users).map { poKo -> poKo }
                }
        }
    }
    //endregion


    val faceList = MutableLiveData<MutableList<Pair<String, FloatArray>>>()

    val loggedUser = MutableLiveData<String>()

}