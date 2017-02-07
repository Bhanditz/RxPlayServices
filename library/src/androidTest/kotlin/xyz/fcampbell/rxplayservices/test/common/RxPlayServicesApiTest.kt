package xyz.fcampbell.rxplayservices.test.common

import android.support.test.InstrumentationRegistry
import com.google.android.gms.common.api.GoogleApiClient
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Test
import xyz.fcampbell.rxplayservices.locationservices.RxFusedLocationApi
import java.util.concurrent.TimeUnit

/**
 * Created by francois on 2017-02-06.
 */
class RxPlayServicesApiTest {
    private val testApi = RxFusedLocationApi(InstrumentationRegistry.getContext()) //RxFusedLocationApi chosen at random

    @Test
    fun observableEmitsOneClient() {
        testApi.disconnect()

        testApi.apiClient
                .test()
                .awaitDone(20, TimeUnit.SECONDS)
                .assertSubscribed()
                .assertValueCount(1)
                .assertTerminated()
    }

    @Test
    fun observableIsReplayed() {
        testApi.disconnect()

        val firstClient = testApi.apiClient.blockingFirst()
        val secondClient = testApi.apiClient.blockingFirst()

        Assert.assertSame(firstClient, secondClient)
    }

    @Test
    fun apiClientDisconnects() {
        testApi.disconnect()

        val client = testApi.apiClient.blockingFirst()
        Assert.assertTrue(client.isConnected)

        testApi.disconnect()
        Assert.assertFalse(client.isConnected)
    }

    @Test
    fun reconnection() {
        testApi.disconnect()

        val firstClient = testApi.apiClient.blockingFirst()
        Assert.assertTrue(firstClient.isConnected)

        testApi.disconnect()

        val secondClient = testApi.apiClient.blockingFirst()
        Assert.assertTrue(secondClient.isConnected)

        Assert.assertNotSame(firstClient, secondClient)
    }

    @Test
    fun observerDisposedOnDisconnect() {
        testApi.disconnect()

        val testObserver = testApi.apiClient
                .flatMap { Observable.never<GoogleApiClient>() }
                .test() //an infinite stream, should be terminated on dispose
        val apiClient = testApi.apiClient.blockingFirst()

        testObserver.assertSubscribed()
        testObserver.assertNotTerminated()

        Assert.assertTrue(apiClient.isConnected)

        testObserver.dispose()
        Assert.assertTrue(testObserver.isDisposed)

        testApi.disconnect()
        Assert.assertFalse(apiClient.isConnected)
    }
}