import cc.joyreactor.core.parsePostsForTag
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Test

/**
 * Created by y2k on 03/04/2017.
 **/
class ParserTests {

    @Test fun `parser feed with top comment`() {
        parsePostsForTag(getHtml("feed_with_top_comment.html"))
    }
}

private fun getHtml(name: String): Document =
    ClassLoader
        .getSystemClassLoader()
        .getResource(name)
        .readText()
        .let { Jsoup.parse(it) }