package cc.joyreactor.core

import cc.joyreactor.core.UrlCreator.postUrl
import cc.joyreactor.core.UrlCreator.tagsPath
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL

/**
 * Created by y2k on 16/04/2017.
 **/

fun Environment.get(source: Source, page: Int? = null): Posts =
    tagsPath(source, page)
        .let(this.download)
        .let(::getPostsWithNextPages)

fun Environment.getDetailedPost(postId: Long): Post =
    postUrl(postId)
        .let(this.download)
        .let(::parsePost)
        .let(::limitComment)

class Environment(
    val download: (String) -> Document = Environment.Companion::defaultDownload) {

    companion object {

        private fun defaultDownload(path: String): Document {
            val url = "http://joyreactor.cc/$path"
            return URL(url)
                .openConnection()
                .apply { addRequestProperty("User-Agent", "Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16") }
                .getInputStream()
                .use { Jsoup.parse(it, "UTF-8", url) }
        }
    }
}