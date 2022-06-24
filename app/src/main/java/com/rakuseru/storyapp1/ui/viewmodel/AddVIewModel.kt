package com.rakuseru.storyapp1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rakuseru.storyapp1.data.ResponseMsg
import com.rakuseru.storyapp1.network.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddViewModel : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _isFinish = MutableLiveData<Boolean>()
    val isFinish: LiveData<Boolean> = _isFinish

    fun uploadForm(file: MultipartBody.Part, desc: RequestBody, token: String) {
        _isLoading.value = true
        _isFinish.value = false
        val service = ApiConfig.getApiService().uploadImage(
            file, desc, "Bearer $token"
        )

        service.enqueue(object : Callback<ResponseMsg> {
            override fun onResponse(
                call: Call<ResponseMsg>,
                response: Response<ResponseMsg>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _message.value = responseBody.message
                        _isFinish.value = true
                    }
                } else {
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<ResponseMsg>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Failed to Upload File (Network Issue)"
            }
        })
    }
}