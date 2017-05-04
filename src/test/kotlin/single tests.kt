import cc.joyreactor.core.Environment
import org.jsoup.nodes.Document
import rx.Single
import rx.Single.fromCallable
import rx.schedulers.Schedulers

/**
 * Created by y2k on 23/04/2017.
 **/

class SingleTests {

    fun test() {
        Environment()
            .downloadSingle("http://joyreactor.cc/")
            .subscribe(::println)
    }
}

fun Environment.downloadSingle(url: String): Single<Document> =
    fromCallable { download(url) }.subscribeOn(Schedulers.io())