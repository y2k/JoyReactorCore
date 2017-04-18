package cc.joyreactor.core

import cc.joyreactor.core.UrlCreator.postUrl
import cc.joyreactor.core.UrlCreator.tagsPath

/**
 * Created by y2k on 17/04/2017.
 **/

fun Environment.get(source: Source, page: Int? = null): Posts =
    tagsPath(source, page)
        .let(this::downloadDocument)
        .let(::getPostsWithNextPages)

fun Environment.getDetailedPost(postId: Long): Post =
    postUrl(postId)
        .let(this::downloadDocument)
        .let(::parsePost)
        .let {
            it.copy(
                comments = it.comments.filterTop(),
                attachments = it.attachments.take(3))
        }