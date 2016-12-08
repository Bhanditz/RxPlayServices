package xyz.fcampbell.rxgms.observables.geocode

import android.content.Context
import android.location.Address
import android.location.Geocoder
import rx.Observable
import rx.Subscriber
import rx.schedulers.Schedulers
import java.io.IOException
import java.util.*

class ReverseGeocodeObservable private constructor(private val ctx: Context, private val locale: Locale, private val latitude: Double, private val longitude: Double, private val maxResults: Int) : Observable.OnSubscribe<List<Address>> {

    override fun call(subscriber: Subscriber<in List<Address>>) {
        val geocoder = Geocoder(ctx, locale)
        try {
            subscriber.onNext(geocoder.getFromLocation(latitude, longitude, maxResults))
            subscriber.onCompleted()
        } catch (e: IOException) {
            // If it's a service not available error try a different approach using google web api
            if (e.message.equals("Service not Available", ignoreCase = true)) {
                Observable
                        .create(FallbackReverseGeocodeObservable(locale, latitude, longitude, maxResults))
                        .subscribeOn(Schedulers.io())
                        .subscribe(subscriber)
            } else {
                subscriber.onError(e)
            }
        }

    }

    companion object {
        @JvmStatic
        fun createObservable(ctx: Context, locale: Locale, latitude: Double, longitude: Double, maxResults: Int): Observable<List<Address>> {
            return Observable.create(ReverseGeocodeObservable(ctx, locale, latitude, longitude, maxResults))
        }
    }
}
