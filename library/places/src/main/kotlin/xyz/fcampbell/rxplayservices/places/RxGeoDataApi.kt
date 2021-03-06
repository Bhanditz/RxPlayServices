package xyz.fcampbell.rxplayservices.places

import android.content.Context
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.model.LatLngBounds
import io.reactivex.Observable
import xyz.fcampbell.rxplayservices.base.ApiClientDescriptor
import xyz.fcampbell.rxplayservices.base.ApiDescriptor
import xyz.fcampbell.rxplayservices.base.RxPlayServicesApi

/**
 * Wraps [Places.GeoDataApi]
 */
@Suppress("unused")
class RxGeoDataApi(
        apiClientDescriptor: ApiClientDescriptor
) : RxPlayServicesApi<GeoDataApi, PlacesOptions>(
        apiClientDescriptor,
        ApiDescriptor(Places.GEO_DATA_API, Places.GeoDataApi)
) {
    constructor(
            context: Context
    ) : this(ApiClientDescriptor(context))

    /**
     * Returns observable that fetches a place from the Places API using the place ID.
     *
     * @param placeIds The Place IDs to search for
     * *
     * @return Observable that emits places buffer and completes
     */
    fun getPlaceById(vararg placeIds: String): Observable<PlaceBuffer> {
        return fromPendingResult { getPlaceById(it, *placeIds) }
    }

    /**
     * Returns observable that fetches autocomplete predictions from Places API.
     *
     * @param query  search query
     * *
     * @param bounds bounds where to fetch suggestions from
     * *
     * @param filter filter
     * *
     * @return observable with suggestions buffer and completes
     */
    fun getPlaceAutocompletePredictions(query: String, bounds: LatLngBounds, filter: AutocompleteFilter?): Observable<AutocompletePredictionBuffer> {
        return fromPendingResult { getAutocompletePredictions(it, query, bounds, filter) }
    }

    /**
     * Returns observable that fetches photo metadata from the Places API using the place ID.
     *
     * @param placeId id for place
     * *
     * @return observable that emits metadata buffer and completes
     */
    fun getPlacePhotos(placeId: String): Observable<PlacePhotoMetadataResult> {
        return fromPendingResult { getPlacePhotos(it, placeId) }
    }

    /**
     * Returns observable that fetches a placePhotoMetadata from the Places API using the place placePhotoMetadata metadata.
     * Use after fetching the place placePhotoMetadata metadata with [getPlacePhotos]
     *
     * @param placePhotoMetadata the place photo meta data
     * *
     * @return observable that emits the photo result and completes
     */
    fun getPlacePhoto(placePhotoMetadata: PlacePhotoMetadata): Observable<PlacePhotoResult> {
        return fromPendingResult { placePhotoMetadata.getPhoto(it) }
    }
}