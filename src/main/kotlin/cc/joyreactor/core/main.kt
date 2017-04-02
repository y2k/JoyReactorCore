package cc.joyreactor.core

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by y2k on 31/03/2017.
 **/

fun main(args: Array<String>) =
    println("Posts = ${Environment().getPosts()}")

fun Environment.getPosts(tagId: String = ""): List<Post> =
    makeTagsPath(tagId, null)
        .let { downloadDocument(it) }
        .let(::parsePostsForTag)

private fun makeTagsPath(tagId: String, page: Int?): String {
    val url = if (tagId == "") "" else "tag/$tagId"
    return url + "/" + (page ?: 0)
}

class Environment {

    fun downloadDocument(url: String): Document =
        Jsoup.connect("http://joyreactor.cc/$url")
            .userAgent("Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16")
            .timeout(10_000)
            .get()
}