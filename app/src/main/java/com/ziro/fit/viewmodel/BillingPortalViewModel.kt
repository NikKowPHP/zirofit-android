package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.SubscriptionInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingPortalViewModel @Inject constructor(
    private val api: ZiroApi
) : ViewModel() {

    var subscription by mutableStateOf<SubscriptionInfo?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isLoadingPortal by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var portalUrl by mutableStateOf<String?>(null)
        private set

    fun loadSubscription() {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val response = api.getBillingSubscription()
                if (response.success == true) {
                    subscription = response.data
                } else {
                    error = response.message ?: "Failed to load subscription"
                }
            } catch (e: Exception) {
                error = e.message ?: "An error occurred"
            } finally {
                isLoading = false
            }
        }
    }

    fun openBillingPortal() {
        viewModelScope.launch {
            isLoadingPortal = true
            error = null
            portalUrl = null
            try {
                val response = api.getBillingPortalUrl()
                if (response.success == true) {
                    portalUrl = response.data?.url
                } else {
                    error = response.message ?: "Failed to get billing portal URL"
                }
            } catch (e: Exception) {
                error = e.message ?: "An error occurred"
            } finally {
                isLoadingPortal = false
            }
        }
    }

    fun clearPortalUrl() {
        portalUrl = null
    }

    fun clearError() {
        error = null
    }
}
