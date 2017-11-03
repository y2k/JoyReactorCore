package cc.joyreactor.core

object TagResolver {

    class Storage(val names: IntArray, val icons: IntArray)

    val tagIcons = Storage(
        loadIndexes("tag.names"),
        loadIndexes("tag.icons"))
    val userIcons = Storage(
        loadIndexes("user.names"),
        loadIndexes("user.icons"))

    private fun loadIndexes(name: String): IntArray =
        javaClass.classLoader
            .getResourceAsStream("$name.dat")
            .readBytes()
            .toIntArray()

    fun tryGetImageId(storage: Storage, name: String): Int? =
        name.toIndexKey()
            .let(storage.names::binarySearchOption)
            .mapOption(storage.icons::get)

    private fun String.toIndexKey() =
        toLowerCase().hashCode()
}