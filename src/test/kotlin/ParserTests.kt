import cc.joyreactor.core.ImageRef
import cc.joyreactor.core.Message
import cc.joyreactor.core.Parsers
import cc.joyreactor.core.Profile
import cc.joyreactor.core.Profile.SubRating
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by y2k on 03/04/2017.
 **/
class ParserTests {

    @Test
    fun `parse profile is success`() {
        val actual = Parsers.profile(getHtml("profile.html"))
        Assert.assertEquals(
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
    fun `parse messages from first page is success`() {
        val (messages, nextPage) = Parsers.getMessages(getHtml("messages_first.html"))

        Assert.assertEquals("/private/list/2", nextPage)
        Assert.assertEquals(20, messages.size)
        Assert.assertEquals(10, messages.filter(Message::isMine).size)

        Assert.assertEquals(
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
    fun `parse messages from last page is success`() {
        val (messages, nextPage) = Parsers.getMessages(getHtml("messages_last.html"))

        Assert.assertEquals(null, nextPage)
        Assert.assertEquals(3, messages.size)
        Assert.assertEquals(0, messages.filter(Message::isMine).size)

        Assert.assertEquals(
            listOf(
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291",
                "http://img0.joyreactor.cc/pics/avatar/user/331291"),
            messages.map { it.userImage })
    }

    @Test
    fun `reading tags`() {
        val actual = Parsers.readingTags(getHtml("tags_test.html"))
        Assert.assertEquals(36, actual.size)
    }

    @Test
    fun `parse feed with top comment`() {
        Parsers.parsePostsForTag(getHtml("feed_with_top_comment.html"))
    }

    @Test
    fun `parse posts with 5 comments`() {
        val post = Parsers.post(getHtml("post_with_5_comments.html"))
        assertEquals(5, post.comments.size)
    }
}

private fun getHtml(name: String): Document =
    ClassLoader
        .getSystemClassLoader()
        .getResource(name)
        .readText()
        .let { Jsoup.parse(it) }