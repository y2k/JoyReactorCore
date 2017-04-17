package cc.joyreactor.core

import org.jsoup.nodes.Document
import java.net.URLEncoder

/**
 * Created by y2k on 16/04/2017.
 **/

internal object UrlCreator {

    fun tagsPath(source: Source, page: Int?): String =
        when (source) {
            is FeaturedSource -> ""
            is TagSource -> "tag/${URLEncoder.encode(source.name, "UTF-8")}"
            is Favorite -> TODO()
        }.let { "$it/${page ?: 0}" }
}

internal fun getPostsWithNextPages(doc: Document) =
    Posts(parsePostsForTag(doc), parseNewPageNumber(doc))