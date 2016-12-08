package xyz.fcampbell.rxgms.observables.geofence

import android.app.PendingIntent
import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationServices
import rx.Observer
import xyz.fcampbell.rxgms.observables.StatusException

internal class RemoveGeofenceByPendingIntentObservable(ctx: Context, private val pendingIntent: PendingIntent) : RemoveGeofenceObservable<Status>(ctx) {

    override fun removeGeofences(locationClient: GoogleApiClient, observer: Observer<in Status>) {
        LocationServices.GeofencingApi.removeGeofences(locationClient, pendingIntent)
                .setResultCallback { status ->
                    if (status.isSuccess) {
                        observer.onNext(status)
                        observer.onCompleted()
                    } else {
                        observer.onError(StatusException(status))
                    }
                }
    }
}
