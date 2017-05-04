import cc.joyreactor.core.Parsers
import cc.joyreactor.core.parsePost
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by y2k on 03/04/2017.
 **/
class ParserTests {

    @Test fun `parse feed with top comment`() {
        Parsers.parsePostsForTag(getHtml("feed_with_top_comment.html"))
    }

    @Test fun `parse posts with 5 comments`() {
        val post = parsePost(getHtml("post_with_5_comments.html"))
        assertEquals(5, post.comments.size)
    }
}

private fun getHtml(name: String): Document =
    ClassLoader
        .getSystemClassLoader()
        .getResource(name)
        .readText()
        .let { Jsoup.parse(it) }