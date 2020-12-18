package jp.senchan.android.wasatter.repository

import kotlinx.coroutines.suspendCancellableCoroutine
import twitter4j.AsyncTwitter
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.TwitterAdapter
import twitter4j.TwitterException
import twitter4j.TwitterMethod
import twitter4j.auth.AccessToken
import javax.inject.Inject
import javax.inject.Provider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface TwitterRepository {
    suspend fun getHomeTimeline(): ResponseList<Status>
}

class TwitterRepositoryImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val twitterProvider: Provider<AsyncTwitter>,
) : TwitterRepository {
    private fun createClient(): AsyncTwitter = twitterProvider.get().also { client ->
        val twitterToken = settingsRepository.twitterToken
        val twitterTokenSecret = settingsRepository.twitterTokenSecret
        if (twitterToken == null || twitterTokenSecret == null) {
            throw Exception("Token is empty token: $twitterToken tokenSecret: $twitterTokenSecret")
        }
        client.oAuthAccessToken =
            AccessToken(twitterToken, twitterTokenSecret)
    }

    override suspend fun getHomeTimeline(): ResponseList<Status> = suspendCancellableCoroutine {
        val client = createClient()
        client.addListener(object : TwitterAdapter() {
            override fun gotHomeTimeline(statuses: ResponseList<Status>) {
                it.resume(statuses)
            }

            override fun onException(te: TwitterException, method: TwitterMethod) {
                it.resumeWithException(te)
            }
        })
        client.getHomeTimeline()
        it.invokeOnCancellation {
            client.shutdown()
        }
    }
}
