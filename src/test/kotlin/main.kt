import cc.joyreactor.core.Environment
import cc.joyreactor.core.Posts
import cc.joyreactor.core.TagSource
import cc.joyreactor.core.get

/**
 * Created by y2k on 31/03/2017.
 **/

fun main(args: Array<String>) {
    val source = TagSource(args[0])
    val env = Environment()

    val posts = env.get(source)
    posts.print("1)")
    env.get(source, posts.nextPage).print("2)")
}

private fun Posts.print(prefix: String) =
    println("$prefix page = $nextPage | data = ${posts.joinToString(separator = "\n")}")