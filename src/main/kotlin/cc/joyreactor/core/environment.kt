package cc.joyreactor.core

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL

/**
 * Created by y2k on 16/04/2017.
 **/

class Environment {

    fun downloadDocument(path: String): Document {
        val url = "http://joyreactor.cc/$path"
        return URL(url)
            .openConnection()
            .apply { addRequestProperty("User-Agent", "Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16") }
            .getInputStream()
            .use { Jsoup.parse(it, "UTF-8", url) }
    }
}