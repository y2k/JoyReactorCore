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

    fun postUrl(postId: Long) = "post/$postId"
}

internal fun getPostsWithNextPages(doc: Document) =
    Posts(parsePostsForTag(doc), parseNewPageNumber(doc))

internal fun List<Comment>.filterTop(): List<Comment> =
    filter { it.parentId == 0L && it.rating >= 0 }
        .sortedByDescending { it.rating }
        .take(10)

internal fun limitComment(post: Post): Post =
    post.copy(
        comments = post.comments.filterTop(),
        attachments = post.attachments.take(3))