package cc.joyreactor.core

/**
 * Created by y2k on 31/03/2017.
 **/

data class Post(
    val tags: List<String>,
    val id: Long,
    val title: String?,
    val image: ImageRef?,
    var attachments: List<Attachment> = emptyList(),
    val comments: List<Comment> = emptyList())

class Attachment(
    val image: ImageRef)

class Comment(
    val postId: Long,
    val id: Long,
    val rating: Float,
    val parentId: Long,
    val image: ImageRef,
    val text: String)

data class ImageRef(
    val aspect: Float,
    val url: String)