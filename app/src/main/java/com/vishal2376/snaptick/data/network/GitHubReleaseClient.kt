package com.vishal2376.snaptick.data.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.vishal2376.snaptick.domain.model.GitHubRelease
import com.vishal2376.snaptick.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tiny GitHub releases client. No auth header (60 req/hour per IP is plenty
 * for one-app-per-device check-on-launch). HttpURLConnection keeps zero
 * extra deps. Returns null on any failure - callers treat that as "no update
 * info available right now" rather than surfacing a parse error.
 */
@Singleton
class GitHubReleaseClient @Inject constructor() {

	private val gson = Gson()

	suspend fun fetchLatest(): GitHubRelease? = withContext(Dispatchers.IO) {
		runCatching {
			val conn = (URL(Constants.GITHUB_LATEST_RELEASE_API).openConnection() as HttpURLConnection).apply {
				requestMethod = "GET"
				connectTimeout = 8000
				readTimeout = 8000
				setRequestProperty("Accept", "application/vnd.github+json")
				setRequestProperty("User-Agent", "snaptick-android")
			}
			try {
				if (conn.responseCode != 200) return@runCatching null
				val text = conn.inputStream.bufferedReader().use { it.readText() }
				val json = gson.fromJson(text, JsonObject::class.java)
				GitHubRelease(
					tagName = json["tag_name"]?.asString.orEmpty(),
					name = json["name"]?.asString.orEmpty(),
					body = json["body"]?.asString.orEmpty(),
					htmlUrl = json["html_url"]?.asString.orEmpty(),
				).takeIf { it.tagName.isNotBlank() && it.htmlUrl.startsWith("https://") }
			} finally {
				conn.disconnect()
			}
		}.getOrNull()
	}
}
