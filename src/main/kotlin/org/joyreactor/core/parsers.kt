package org.joyreactor.core

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

internal fun parsePostsForTag(document: Document): List<Post> =
    document
        .select("div.postContainer")
        .map(::parserSinglePost)

private fun parserSinglePost(body: Element): Post =
    Post(tags = parseTagsInPost(body),
        id = findNumber(body.id()),
        title = body.select("div.post_content > div > h3").first()?.text(),
        image = queryImage(body),
        attachments = parseAttachments(body),
        comments = parseComments(body))

private fun parseTagsInPost(body: Element): List<String> =
    body.select(".taglist a").map { it.text() }

private val NUMBER_REGEX = Regex("\\d+")
private fun findNumber(value: String): Long {
    val m = NUMBER_REGEX.find(value) ?: throw Exception("Can't find number in '$value'")
    return m.value.toLong()
}

private fun queryImage(element: Element): ImageRef? =
    element
        .select("div.post_content img")
        .filter { it.hasAttr("width") }
        .filterNot { it.attr("height").endsWith("%") }
        .map {
            val aspect = it.attr("width").toFloat() / it.attr("height").toFloat()
            it.attr("src")
                .let(::normalizeUrl)
                .let { ImageRef(aspect, it) }
        }
        .firstOrNull()

private fun normalizeUrl(link: String): String =
    link.replace("(/comment/).+(-\\d+\\.[\\w\\d]+)$".toRegex(), "$1$2")
        .replace("(/full/).+(-\\d+\\.)".toRegex(), "$1$2")
        .replace("(/post/).+(-\\d+\\.)".toRegex(), "$1$2")

fun parseAttachments(document: Element): List<Attachment> {
    return document
        .first("div.post_top")
        .let {
            parserThumbnails(it)
                .union(parseYoutubeThumbnails(it))
                .union(parseVideoThumbnails(it))
        }
        .map(::Attachment)
}

fun Element.first(cssQuery: String): Element {
    return select(cssQuery).first() ?: throw Exception("Can't find DOM for '$cssQuery'")
}

private fun parserThumbnails(element: Element): List<ImageRef> {
    return element
        .select("div.post_content img")
        .filter { it != null && it.hasAttr("width") }
        .filterNot { it.attr("height").endsWith("%") }
        .map {
            ImageRef(
                it.attr("width").toFloat() / it.attr("height").toFloat(),
                getThumbnailImageLink(it))
        }
}

private fun getThumbnailImageLink(it: Element): String {
    fun hasFull(img: Element): Boolean = "a" == img.parent().tagName()
    return if (hasFull(it))
        it.parent().attr("href").replace("(/full/).+(-\\d+\\.)".toRegex(), "$1$2")
    else
        it.attr("src").replace("(/post/).+(-\\d+\\.)".toRegex(), "$1$2")
}

private fun parseYoutubeThumbnails(element: Element): List<ImageRef> {
    return element
        .select("iframe.youtube-player")
        .map {
            val m = SRC_PATTERN.matcher(it.attr("src"))
            if (!m.find()) throw IllegalStateException(it.attr("src"))
            ImageRef(
                it.attr("width").toFloat() / it.attr("height").toFloat(),
                "http://img.youtube.com/vi/" + m.group(1) + "/0.jpg")
        }
}

private val SRC_PATTERN = java.util.regex.Pattern.compile("/embed/([^?]+)")
private fun parseVideoThumbnails(element: Element): List<ImageRef> {
    return element
        .select("video[poster]")
        .map {
            ImageRef(
                it.attr("width").toFloat() / it.attr("height").toFloat(),
                element.select("span.video_gif_holder > a").first().attr("href").replace("(/post/).+(-)".toRegex(), "$1$2"))
        }
}

private fun parseComments(document: Element): List<Comment> {
    val postId = findNumber(document.id())
    val comments = ArrayList<Comment>()
    for (node in document.select("div.comment")) {
        val parent = node.parent()
        val parentId = if ("comment_list" == parent.className()) findNumber(parent.id()) else 0

        val comment = Comment(
            text = node.select("div.txt > div").first().text(),
            image = ImageRef(1f, node.select("img.avatar").attr("src")),
            parentId = parentId,
            rating = node.select("span.comment_rating").text().trim { it <= ' ' }.toFloat(),
            postId = postId,
            id = (node.select("span.comment_rating").attr("comment_id")).toLong())
        comments.add(comment)
    }
    return comments
}