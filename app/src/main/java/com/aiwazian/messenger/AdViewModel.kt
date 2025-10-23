package com.aiwazian.messenger

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeAdEventListener
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdViewModel @Inject constructor() : ViewModel() {
    
    private var nativeAdLoader: NativeAdLoader? = null
    
    var nativeAd: NativeAd? = null
        private set
    
    private val _isAdLoaded = MutableStateFlow(false)
    val isAdLoaded = _isAdLoaded.asStateFlow()
    
    private val RELOAD_INTERVAL_MS = 60_000L
    
    init {
        viewModelScope.launch {
            while (true) {
                delay(RELOAD_INTERVAL_MS)
                
                if (_isAdLoaded.value) {
                    _isAdLoaded.value = false
                    
                    delay(1000)
                    
                    nativeAdLoader?.let {
                        loadAd()
                    }
                }
            }
        }
    }
    
    fun initialize(context: Context) {
        if (nativeAdLoader == null) {
            nativeAdLoader = createNativeAdLoader(context)
            loadAd()
        }
    }
    
    private fun createNativeAdLoader(context: Context): NativeAdLoader {
        return NativeAdLoader(context).apply {
            setNativeAdLoadListener(object : NativeAdLoadListener {
                override fun onAdLoaded(nativeAd: NativeAd) {
                    Log.d(
                        "YandexAds",
                        ">>> Yandex Ads onAdLoaded"
                    )
                    this@AdViewModel.nativeAd = nativeAd
                    _isAdLoaded.value = true
                }
                
                override fun onAdFailedToLoad(error: AdRequestError) {
                    Log.d(
                        "YandexAds",
                        ">>> Yandex Ads onAdFailedToLoad: ${error.description}"
                    )
                    _isAdLoaded.value = false
                }
            })
        }
    }
    
    private fun loadAd() {
        nativeAdLoader?.loadAd(NativeAdRequestConfiguration.Builder("R-M-15520718-2").build())
    }
    
    override fun onCleared() {
        super.onCleared()
        nativeAdLoader?.cancelLoading()
    }
    
    inner class NativeAdEventLogger : NativeAdEventListener {
        override fun onAdClicked() {}
        
        override fun onLeftApplication() {}
        
        override fun onReturnedToApplication() {}
        
        override fun onImpression(impressionData: ImpressionData?) {}
    }
}