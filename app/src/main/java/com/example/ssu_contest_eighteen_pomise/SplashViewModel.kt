package com.example.ssu_contest_eighteen_pomise

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ssu_contest_eighteen_pomise.network.LoginService
import com.example.ssu_contest_eighteen_pomise.sharedpreferences.TokenSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    var canLogin=MutableLiveData<Boolean>()
    var canNotLogin=MutableLiveData<Boolean>()

    fun isCanLogin(shPre:TokenSharedPreferences){
        Log.d("kmj", "refresh전: "+shPre.refreshToken!!)
        Log.d("kmj", "refresh전: "+shPre.accessToken!!)
        Log.d("kmj", "refresh전: "+shPre.email!!)

        if((shPre.refreshToken!=null) and (!shPre.refreshToken!!.isEmpty())){
            viewModelScope.launch(Dispatchers.IO) {
                val response = App.loginService.refreshTokenRequest(
                    shPre.refreshToken!!,shPre.email!!, LoginService.USER,shPre.refreshToken!!
                )

                Log.d("kmj", response.isSuccessful.toString())
                if (response.isSuccessful) {
                    val refreshUser = response.body()!!
                    // 재발급 받은걸로 갱신.
                    shPre.accessToken=refreshUser.accessToken
                    shPre.refreshToken=refreshUser.refreshToken
                    shPre.email=refreshUser.email

                    Log.d("kmj", "refresh후: "+refreshUser.refreshToken!!)
                    Log.d("kmj", "refresh후: "+refreshUser.accessToken!!)
                    Log.d("kmj", "refresh후: "+refreshUser.email!!)
                    canLogin.postValue(true)
                } else {
                    Log.d("kmj","자동로그인 실패")
                    shPre.refreshToken = ""
                    shPre.accessToken = ""
                    shPre.name = ""
                    shPre.email = ""
                    shPre.phoneNumber=""
                    shPre.isGuardian=false
                    canNotLogin.postValue(true)
                }

            }
        }
        else{
            viewModelScope.launch(Dispatchers.IO) {
                App.loginService.deleteTokenRequest(shPre.refreshToken!!)
            }
            canNotLogin.postValue(true)
        }
    }
}