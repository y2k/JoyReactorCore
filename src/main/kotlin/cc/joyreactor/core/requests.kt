package cc.joyreactor.core

/**
 * Created by y2k on 17/04/2017.
 **/

fun Environment.get(source: Source, page: Int? = null): Posts =
    UrlCreator.tagsPath(source, page)
        .let { downloadDocument(it) }
        .let(::getPostsWithNextPages)