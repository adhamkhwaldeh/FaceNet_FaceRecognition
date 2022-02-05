package com.aljawad.sons.business

import android.graphics.Bitmap
import com.aljawad.sons.dtos.core.FileReader
import com.aljawad.sons.mainlibrary.base.BaseFlowUseCase
import com.aljawad.sons.mainlibrary.states.BaseState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AddUserUseCase @Inject constructor(
    private var fileReader: FileReader
) : BaseFlowUseCase<Pair<ArrayList<Pair<String, FloatArray>>, Int>, ArrayList<Pair<String, Bitmap>>>() {

    override suspend fun run(params: ArrayList<Pair<String, Bitmap>>): Flow<BaseState<Pair<ArrayList<Pair<String, FloatArray>>, Int>>> {
        fileReader.feedTheData(params)
        return fileReader.run(params)
    }

}