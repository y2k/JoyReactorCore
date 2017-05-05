package cc.joyreactor.core

import cc.joyreactor.core.Parsers.parseNewPageNumber
import cc.joyreactor.core.Parsers.parsePostsForTag
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URLEncoder

/**
 * Created by y2k on 16/04/2017.
 **/

object JoyReactor {

    fun postWithComments(element: Element, config: CommentConfig): Post {
        val post = Parsers.post(element)
        return when (config) {
            is AllComments -> post
            is TopComments ->
                post.copy(comments = post.comments.filterTop(config.limit))
        }
    }
}

sealed class CommentConfig
class AllComments : CommentConfig()
class TopComments(val limit: Int) : CommentConfig()

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

internal fun List<Comment>.filterTop(limit: Int): List<Comment> =
    filter { it.parentId == 0L && it.rating >= 0 }
        .sortedByDescending { it.rating }
        .take(limit)

internal fun limitComment(post: Post): Post =
    post.copy(
        comments = post.comments.filterTop(10),
        attachments = post.attachments.take(3))