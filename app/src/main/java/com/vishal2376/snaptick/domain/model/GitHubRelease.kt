package com.vishal2376.snaptick.domain.model

/**
 * Subset of GitHub's `/repos/{owner}/{repo}/releases/latest` payload that we
 * actually consume. `tag_name` is the canonical version source; `name` and
 * `body` are display-only. `html_url` is the public release page we open in
 * an external browser when the user wants to install.
 */
data class GitHubRelease(
	val tagName: String,
	val name: String,
	val body: String,
	val htmlUrl: String,
) {
	/**
	 * Strips a leading "v" and parses the rest as dotted numeric components.
	 * "v4.0" -> [4, 0]. Non-parseable -> empty list (treated as oldest).
	 */
	fun versionParts(): List<Int> = tagName
		.trim()
		.removePrefix("v")
		.removePrefix("V")
		.split('.', '-')
		.mapNotNull { it.takeWhile(Char::isDigit).toIntOrNull() }
}

/**
 * Lexicographic-by-component compare: missing trailing components count as 0.
 * "4.0" > "3.3", "4.0.1" > "4.0", "4.0" == "v4.0".
 */
fun compareVersions(a: List<Int>, b: List<Int>): Int {
	val n = maxOf(a.size, b.size)
	for (i in 0 until n) {
		val cmp = a.getOrElse(i) { 0 }.compareTo(b.getOrElse(i) { 0 })
		if (cmp != 0) return cmp
	}
	return 0
}
