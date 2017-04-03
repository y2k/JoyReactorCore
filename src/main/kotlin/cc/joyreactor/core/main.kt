package cc.joyreactor.core

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import java.net.URLEncoder

/**
 * Created by y2k on 31/03/2017.
 **/

fun main(args: Array<String>) {
    val source = TagSource("эротика")
    val posts = Environment().get(source)
    posts.print("1)")

    val posts2 = Environment().get(source, posts.nextPage)
    posts2.print("2)")
}

private fun Posts.print(prefix: String) =
    println("$prefix page = $nextPage | data = ${posts.joinToString(separator = "\n")}")

fun Environment.get(source: Source, page: Int? = null): Posts =
    makeTagsPath(source, page)
        .let { downloadDocument(it) }
        .let { Posts(parsePostsForTag(it), parseNewPageNumber(it)) }

private fun makeTagsPath(source: Source, page: Int?): String =
    when (source) {
        is FeaturedSource -> ""
        is TagSource -> "tag/${URLEncoder.encode(source.name, "UTF-8")}"
        is Favorite -> TODO()
    }.let { "$it/${page ?: 0}" }

class Environment {

    fun downloadDocument(path: String): Document {
        val url = "http://joyreactor.cc/$path"
        return URL(url)
            .openConnection()
            .apply { addRequestProperty("User-Agent", "Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16") }
            .getInputStream()
            .use { Jsoup.parse(it, "UTF-8", url) }
    }
}

sealed class Source
object FeaturedSource : Source()
class TagSource(val name: String) : Source()
class Favorite(val user: String) : Source()