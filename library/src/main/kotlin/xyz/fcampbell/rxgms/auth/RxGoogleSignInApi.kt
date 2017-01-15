package xyz.fcampbell.rxgms.auth

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.common.api.Status
import rx.Observable
import xyz.fcampbell.rxgms.auth.exception.SignInException
import xyz.fcampbell.rxgms.common.ApiClientDescriptor
import xyz.fcampbell.rxgms.common.ApiDescriptor
import xyz.fcampbell.rxgms.common.RxGmsApi
import xyz.fcampbell.rxgms.common.util.ResultActivity
import xyz.fcampbell.rxgms.common.util.pendingResultToObservable

@Suppress("unused")
class RxGoogleSignInApi(
        private val apiClientDescriptor: ApiClientDescriptor,
        googleSignInOptions: GoogleSignInOptions,
        vararg scopes: Scope
) : RxGmsApi<GoogleSignInOptions>(
        apiClientDescriptor.setAccountName(""), //don't set account name in GoogleApiClient with Auth API. See https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient.Builder.html#setAccountName(java.lang.String)
        ApiDescriptor(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions, *scopes)
) {
    constructor(
            context: Context,
            googleSignInOptions: GoogleSignInOptions,
            vararg scopes: Scope
    ) : this(ApiClientDescriptor(context), googleSignInOptions, *scopes)

    fun getSignInIntent(): Observable<Intent> {
        return apiClient.map { Auth.GoogleSignInApi.getSignInIntent(it.first) }
    }

    fun signIn(): Observable<GoogleSignInAccount> {
        return getSignInIntent().flatMap { intent ->
            val pendingIntent = PendingIntent.getActivity(apiClientDescriptor.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val intentSender = pendingIntent.intentSender
            ResultActivity.getResult(apiClientDescriptor.context, intentSender)
        }
                .switchIfEmpty(Observable.error(SignInException("sign-in intent returned was null")))
                .map { Auth.GoogleSignInApi.getSignInResultFromIntent(it).signInAccount }
    }

    fun silentSignIn(): Observable<GoogleSignInAccount> {
        return apiClient.pendingResultToObservable { Auth.GoogleSignInApi.silentSignIn(it.first) }
                .map { it.signInAccount }
    }

    fun revokeAccess(): Observable<Status> {
        return apiClient.pendingResultToObservable { Auth.GoogleSignInApi.revokeAccess(it.first) }
    }

    fun signOut(): Observable<Status> {
        return apiClient.pendingResultToObservable { Auth.GoogleSignInApi.signOut(it.first) }
    }
}