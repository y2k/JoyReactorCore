import org.junit.Test
import java.lang.System.currentTimeMillis

/**
 * Created by y2k on 16/04/2017.
 **/
class MonadTests {

    @Test fun `test wrap try`() {
        try_ { 1 / Math.random() }
            .let { x ->
                when (x) {
                    is Ok -> x.value
                    is Error -> {
                        x.exception.printStackTrace()
                        -1.0
                    }
                }
            }
    }

    @Test fun `test monad`() {
        currentTimeMillisIO()
            .invoke(Unit) { x ->
                println("" + x)
            }

        currentTimeMillisIO()
            .bind { x -> currentTimeMillisIO().fmap { y -> x + y } }

        currentTimeMillisIO()
            .zip(currentTimeMillisIO())
            .fmap { (x, y) -> x + y }
            .invoke(Unit) { x ->
                println("" + x)
            }
    }
}

fun currentTimeMillisIO(): AsyncReader<Unit, Long> =
    { _, callback ->
        callback.invoke(currentTimeMillis())
    }

sealed class Result<T>
class Ok<T>(val value: T) : Result<T>()
class Error<T>(val exception: Exception) : Result<T>()

inline fun <T> try_(f: () -> T): Result<T> =
    try {
        Ok(f())
    } catch (e: Exception) {
        Error(e)
    }

typealias AsyncReader<E, T> = (E, (T) -> Unit) -> Unit

fun <E, T, R> AsyncReader<E, T>.zip(next: AsyncReader<E, R>): AsyncReader<E, Pair<T, R>> =
    bind { x -> next.fmap { y -> x to y } }

fun <E, T, R> AsyncReader<E, T>.bind(x: (T) -> AsyncReader<E, R>): AsyncReader<E, R> {
    TODO()
}

fun <E, T, R> AsyncReader<E, T>.fmap(x: (T) -> R): AsyncReader<E, R> {
    TODO()
}