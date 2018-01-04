package cc.joyreactor.core

import org.jsoup.nodes.Element
import java.util.*
import java.util.regex.Pattern

object Parsers {

    fun getMessages(document: Element): Pair<List<Message>, String?> {
        val messages = document
            .select("div.messages_wr > div.article")
            .map {
                val username = it.select("div.mess_from > a").text()
                Message(
                    text = it.select("div.mess_text").text(),
                    date = 1000 * it.select("span[data-time]").attr("data-time").toLong(),
                    isMine = it.select("div.mess_reply").isEmpty(),
                    userName = username,
                    userImage = getUserImage(username))
            }

        val nextPage = document
            .select("a.next")
            .first()
            ?.attr("href")

        return messages to nextPage
    }

    private fun getUserImage(name: String): String {
        val id = TagResolver.tryGetImageId(TagResolver.userIcons, name)
        return if (id == null)
            "http://img0.joyreactor.cc/images/default_avatar.jpeg"
        else "http://img0.joyreactor.cc/pics/avatar/user/" + id
    }

    fun readingTags(document: Element): List<Tag> =
        document
            .select("h2.sideheader")
            .filter { it.text() == "Читает" }
            .flatMap { it.nextElementSibling().children() }
            .map { Tag(it.text(), resolveTagImage(it)) }

    private fun resolveTagImage(it: Element) =
        TagResolver.tryGetImageId(TagResolver.tagIcons, it.text())
            .mapOption { "http://img1.joyreactor.cc/pics/avatar/tag/$it" }
            ?: "http://img0.joyreactor.cc/images/default_avatar.jpeg"

    fun profile(document: Element) = Profile(
        document.select("div.sidebarContent > div.user > span").text(),
        ImageRef(1f, document.select("div.sidebarContent > div.user > img").attr("src")),
        document.select("#rating-text > b").text().replace(" ", "").toFloat(),
        document.select(".star-row-0 > .star-0").size,
        getProgressToNewStar(document),
        getSubRatings(document),
        getAwards(document))

    private fun getProgressToNewStar(document: Element): Float {
        val style = document.select("div.stars div.poll_res_bg_active").first().attr("style")
        val m = Pattern.compile("width:(\\d+)%;").matcher(style)
        if (!m.find()) throw IllegalStateException()
        return java.lang.Float.parseFloat(m.group(1))
    }

    private fun getSubRatings(document: Element): List<Profile.SubRating> =
        document
            .select("div.blogs tr")
            .filter { !it.select("small").isEmpty() }
            .map {
                Profile.SubRating(
                    "\\d[\\d\\. ]*".toRegex().find(it.select("small").text())!!.value.replace(" ", "").toFloat(),
                    it.select("img").attr("alt"))
            }

    private fun getAwards(document: Element): List<Profile.Award> =
        document
            .select("div.award_holder > img")
            .map { Profile.Award(it.absUrl("src"), it.attr("alt")) }

    fun parseNewPageNumber(element: Element): Int =
        element
            .select("a.next").first()
            .attr("href").split('/').last()
            .let(::findNumber).toInt()

    fun parsePostsForTag(element: Element): List<Post> =
        element
            .select("div.postContainer")
            .map(::parserSinglePost)


    fun post(element: Element): Post =
        element
            .first("div.postContainer")
            .let(::parserSinglePost)
}

private fun parserSinglePost(body: Element): Post =
    Post(
        userImage = ImageRef(1f, body.select("div.uhead_nick > img").attr("src")),
        userName = body.select("div.uhead_nick > a").text(),
        rating = getRating(body),
        created = getCreated(body),
        tags = parseTagsInPost(body),
        id = findNumber(body.id()),
        title = body.select("div.post_content > div > h3").first()?.text(),
        image = queryImage(body),
        attachments = parseAttachments(body),
        comments = parseComments(body))

private fun getRating(element: Element): Float {
    val e = element.select("span.post_rating > span").first()
    val m = RATING_REGEX.matcher(e.text())
    return if (m.find()) java.lang.Float.parseFloat(m.group()) else 0f
}

private fun getCreated(element: Element): Long {
    val e = element.select("span.date > span")
    return 1000L * java.lang.Long.parseLong(e.attr("data-time"))
}

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

private fun parseAttachments(document: Element): List<Attachment> =
    document
        .first("div.post_top")
        .let {
            parserThumbnails(it)
                .union(parseYoutubeThumbnails(it))
                .union(parseVideoThumbnails(it))
        }
        .map(::Attachment)

private fun Element.first(cssQuery: String): Element =
    select(cssQuery).first() ?: throw Exception("Can't find DOM for '$cssQuery'")

private fun parserThumbnails(element: Element): List<ImageRef> =
    element
        .select("div.post_content img")
        .filter { it != null && it.hasAttr("width") }
        .filterNot { it.attr("height").endsWith("%") }
        .map {
            ImageRef(
                it.attr("width").toFloat() / it.attr("height").toFloat(),
                getThumbnailImageLink(it))
        }

private fun getThumbnailImageLink(it: Element): String {
    fun hasFull(img: Element): Boolean = "a" == img.parent().tagName()
    return if (hasFull(it))
        it.parent().attr("href").replace("(/full/).+(-\\d+\\.)".toRegex(), "$1$2")
    else
        it.attr("src").replace("(/post/).+(-\\d+\\.)".toRegex(), "$1$2")
}

private fun parseYoutubeThumbnails(element: Element): List<ImageRef> =
    element
        .select("iframe.youtube-player")
        .map {
            val m = SRC_PATTERN.matcher(it.attr("src"))
            if (!m.find()) throw IllegalStateException(it.attr("src"))
            ImageRef(
                it.attr("width").toFloat() / it.attr("height").toFloat(),
                "http://img.youtube.com/vi/" + m.group(1) + "/0.jpg")
        }

private val SRC_PATTERN = Pattern.compile("/embed/([^?]+)")
private fun parseVideoThumbnails(element: Element): List<ImageRef> =
    element
        .select("video[poster]")
        .map {
            ImageRef(
                it.attr("width").toFloat() / it.attr("height").toFloat(),
                element.select("span.video_gif_holder > a").first().attr("href").replace("(/post/).+(-)".toRegex(), "$1$2"))
        }

private fun parseComments(document: Element): List<Comment> {
    val postId = findNumber(document.id())
    val comments = ArrayList<Comment>()
    for (node in document.select("div.comment[parent]")) {
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

private val RATING_REGEX = Pattern.compile("[\\d\\.]+")