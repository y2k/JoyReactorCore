import cc.joyreactor.core.ImageRef
import cc.joyreactor.core.Message
import cc.joyreactor.core.Parsers
import cc.joyreactor.core.Profile
import cc.joyreactor.core.Profile.SubRating
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Created by y2k on 03/04/2017.
 **/
class ParserTests {

    @Test
    fun `parse small_favorite should success`() {
        val element = getHtml("small_favorite.html")

        val actual = Parsers.parsePostsForTag(element)
        assertEquals(1, actual.size)

        assertNull(Parsers.parseNewPageNumber(element))
    }

    @Test
    fun `parse profile should success`() {
        val actual = Parsers.profile(getHtml("profile.html"))
        assertEquals(
            Profile(
                userName = "_y2k",
                userImage = ImageRef(1f, "http://img1.joyreactor.cc/pics/avatar/user/331291"),
                rating = 40f,
                stars = 1,
                progressToNewStar = 67f,
                subRatings = listOf(
                    SubRating(rating = 0.1f, tag = "High fantasy"),
                    SubRating(rating = 0.1f, tag = "Zootopia"),
                    SubRating(rating = 0.1f, tag = "Dragon Age"),
                    SubRating(rating = 0.0f, tag = "Смешные комиксы"),
                    SubRating(rating = 0.0f, tag = "Игра престолов"),
                    SubRating(rating = 0.0f, tag = "Mass Effect")),
                awards = emptyList()),
            actual)
    }

    @Test
    fun `parse messages from first page should success`() {
        val (messages, nextPage) = Parsers.getMessages(getHtml("messages_first.html"))

        assertEquals("/private/list/2", nextPage)
        assertEquals(20, messages.size)
        assertEquals(10, messages.filter(Message::isMine).size)

        assertEquals(
            listOf(
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/157352",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291"),
            messages.map { it.userImage })
    }

    @Test
    fun `parse messages from last page should success`() {
        val (messages, nextPage) = Parsers.getMessages(getHtml("messages_last.html"))

        assertEquals(null, nextPage)
        assertEquals(3, messages.size)
        assertEquals(0, messages.filter(Message::isMine).size)

        assertEquals(
            listOf(
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291"),
            messages.map { it.userImage })
    }

    @Test
    fun `reading tags`() {
        val actual = Parsers.readingTags(getHtml("tags_test.html"))
        assertEquals(36, actual.size)
    }

    @Test
    fun `parse feed with top comment`() {
        Parsers.parsePostsForTag(getHtml("feed_with_top_comment.html"))
    }

    @Test
    fun `parse posts with 5 comments`() {
        val post = Parsers.post(getHtml("post_with_5_comments.html"))

        assertEquals(
            listOf("Pinguin", "WRZESZCZ", "Diablero", "LYVrus", "Cobold"),
            post.comments.map { it.userName })

        assertEquals(
            listOf(emptyList(), emptyList(), emptyList(),
                listOf(ImageRef(
                    290f / 245f,
                    "http://img0.joyreactor.cc/pics/comment/-2583972.gif"
                ))
                , emptyList()),
            post.comments.map { it.attachments.map { it.image } })

        assertEquals(5, post.comments.size)
    }

    @Test
    fun `parse posts with image in comments`() {
        val post = Parsers.post(getHtml("post_with_image_in_comments.html"))

        assertEquals(
            listOf(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(),
                listOf(ImageRef(
                    1f,
                    "http://img1.joyreactor.cc/pics/comment/-3039289.jpeg"
                ))
                , emptyList()),
            post.comments.map { it.attachments.map { it.image } })
    }
}

private fun getHtml(name: String): Document =
    ClassLoader
        .getSystemClassLoader()
        .getResource(name)
        .readText()
        .let { Jsoup.parse(it) }