package xyz.fcampbell.rxgms.observables.location

import android.app.PendingIntent
import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationServices
import rx.Observable
import rx.Observer
import xyz.fcampbell.rxgms.observables.BaseLocationObservable
import xyz.fcampbell.rxgms.observables.StatusException

class RemoveLocationIntentUpdatesObservable private constructor(ctx: Context, private val intent: PendingIntent) : BaseLocationObservable<Status>(ctx) {

    override fun onGoogleApiClientReady(apiClient: GoogleApiClient, observer: Observer<in Status>) {
        LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, intent)
                .setResultCallback { status ->
                    if (status.isSuccess) {
                        observer.onNext(status)
                        observer.onCompleted()
                    } else {
                        observer.onError(StatusException(status))
                    }
                }
    }

    companion object {
        @JvmStatic
        fun createObservable(ctx: Context, intent: PendingIntent): Observable<Status> {
            return Observable.create(RemoveLocationIntentUpdatesObservable(ctx, intent))
        }
    }
}
