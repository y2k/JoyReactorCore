import cc.joyreactor.core.Environment
import java8.util.concurrent.Flow
import java8.util.concurrent.SubmissionPublisher
import org.jsoup.nodes.Document
import rx.Single
import rx.Single.fromCallable
import rx.schedulers.Schedulers
import kotlin.concurrent.thread

/**
 * Created by y2k on 23/04/2017.
 **/

class FlowTests {

    fun test() {

        Environment()
            .downloadFlow("http://google.com/")
            .subscribe(object : SubscriberAdapter<Document>() {
                override fun onNext(item: Document): Unit = println("Document = $item")
            })
    }
}

abstract class SubscriberAdapter<T> : Flow.Subscriber<T> {
    override fun onNext(item: T): Unit = TODO()
    override fun onComplete(): Unit = TODO()
    override fun onError(throwable: Throwable?): Unit = TODO()
    override fun onSubscribe(subscription: Flow.Subscription?): Unit = TODO()
}

fun Environment.downloadFlow(url: String): Flow.Publisher<Document> {

    val p = SubmissionPublisher<Document>()

    thread {
        val d = download(url)
        p.submit(d)
        p.close()
    }

    return p
}

fun Environment.downloadFlowSingle(url: String): Flow.Publisher<Document> =
    fromCallable { download(url) }.subscribeOn(Schedulers.io()).toFlow()

private fun <T> Single<T>.toFlow(): Flow.Publisher<T> =
    Flow.Publisher<T> { subscribe(it::onNext, it::onError) }