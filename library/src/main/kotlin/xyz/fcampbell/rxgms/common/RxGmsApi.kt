package xyz.fcampbell.rxgms.common

import android.os.Bundle
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.GoogleApiClient
import rx.Observable
import rx.Subscription
import rx.subscriptions.Subscriptions
import xyz.fcampbell.rxgms.common.action.GoogleApiClientOnSubscribe

/**
 * Created by francois on 2016-12-29.
 */
abstract class RxGmsApi<O : Api.ApiOptions>(
        apiClientDescriptor: ApiClientDescriptor,
        vararg apis: ApiDescriptor<O>
) {
    private val googleApiClientOnSubscribe = GoogleApiClientOnSubscribe(apiClientDescriptor, *apis)

    private var currentSubscription: Subscription = Subscriptions.unsubscribed()
    private var currentApiClient: Observable<Pair<GoogleApiClient, Bundle?>>? = null

    val apiClient: Observable<Pair<GoogleApiClient, Bundle?>>
        get() {
            var localRxApiClient = currentApiClient
            if (localRxApiClient != null && !currentSubscription.isUnsubscribed) return localRxApiClient

            localRxApiClient = Observable.create(googleApiClientOnSubscribe)
                    .replay(1)
                    .autoConnect(1) { subscription ->
                        currentSubscription = subscription //to unsub from main client and disconnect from GMS
                    }
                    .first() //to force a terminal event to subscribers after one GoogleApiClient emission
            currentApiClient = localRxApiClient
            return localRxApiClient
        }

    fun disconnect() {
        currentSubscription.unsubscribe()
        currentApiClient = null
    }
}
