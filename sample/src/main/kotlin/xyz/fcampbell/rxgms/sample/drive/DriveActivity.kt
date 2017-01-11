package xyz.fcampbell.rxgms.sample.drive

import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.MetadataBuffer
import com.google.android.gms.drive.MetadataChangeSet
import rx.Observable
import xyz.fcampbell.rxgms.RxGms

/**
 * Created by francois on 2017-01-10.
 */
class DriveActivity : AppCompatActivity() {

    private val driveApi = RxGms(this).getDriveApi("", Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER)

    private val gso = GoogleSignInOptions.Builder()
            .requestEmail()
            .build()
    private val authApi = RxGms(this).getAuthApi("", gso)

    override fun onStart() {
        super.onStart()

        getRootFolder()
        getGoogleAccount()
    }

    private fun getRootFolder() {
        driveApi.getFileUploadPreferences()
                .subscribe({
                    Log.d(TAG, "Got file upload prefs: $it")
                    Log.d(TAG, "batteryUsagePreference: ${it.batteryUsagePreference}")
                    Log.d(TAG, "isRoamingAllowed: ${it.isRoamingAllowed}")
                    Log.d(TAG, "networkTypePreference: ${it.networkTypePreference}")
                }, { throwable ->
                    Log.d(TAG, "Error", throwable)
                })

        driveApi.getAppFolder()
                .flatMap { appFolder ->
                    appFolder.listChildren()
                            .flatMap {
                                if (it.none()) {
                                    Observable.error<MetadataBuffer>(Exception())
                                } else {
                                    Observable.just(it)
                                }
                            }
                            .retryWhen { errors ->
                                errors.flatMap {
                                    driveApi.newDriveContents()
                                }.doOnNext {
                                    val changeSet = MetadataChangeSet.Builder()
                                            .setTitle("CreatedFile")
                                            .setDescription("Created by RxGMS sample app")
                                            .build()
                                    appFolder.createFile(changeSet, it.driveContents)
                                }
                            }
                }
                .subscribe({
                    Log.d(TAG, "Got root folder: $it")
                    it.forEach { Log.d(TAG, "Item: ${it.title}") }
                }, { throwable ->
                    Log.d(TAG, "Error", throwable)
                })
    }

    private fun getGoogleAccount() {
        authApi.signIn()
                .subscribe({ account ->
                    Log.d(TAG, account.email)
                }, { throwable ->
                    Log.d(TAG, "Error: $throwable")
                })
    }

    override fun onStop() {
        super.onStop()

        driveApi.disconnect()
    }

    companion object {
        const val TAG = "DriveActivity"
    }
}