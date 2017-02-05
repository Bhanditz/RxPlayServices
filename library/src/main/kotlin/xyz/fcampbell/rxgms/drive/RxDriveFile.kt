package xyz.fcampbell.rxgms.drive

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.DriveFile
import io.reactivex.Observable
import xyz.fcampbell.rxgms.common.RxWrappedApi

/**
 * Created by francois on 2017-01-10.
 */
@Suppress("unused")
class RxDriveFile(
        override val apiClient: Observable<GoogleApiClient>,
        override val original: DriveFile
) : RxWrappedApi<DriveFile>,
    RxDriveResource<DriveFile>(apiClient, original) {

    fun open(mode: Int, progressListener: DriveFile.DownloadProgressListener): Observable<RxDriveContents> {
        return fromPendingResult { open(it, mode, progressListener) }
                .map { RxDriveContents(apiClient, it.driveContents) }
    }
}