package cc.joyreactor.core

import java.util.*

/**
 * Created by y2k on 31/03/2017.
 **/

class Profile(
    val userName: String,
    val userImage: ImageRef,
    val rating: Float,
    val stars: Int,
    val progressToNewStar: Float,
    val subRatings: List<SubRating>,
    val awards: List<Award>) {

    data class SubRating(
        val rating: Float,
        val tag: String)

    data class Award(
        val image: String,
        val title: String)
}

sealed class Source

object FeaturedSource : Source()
class TagSource(val name: String) : Source()
class Favorite(val user: String) : Source()

class Thread(
    val user: String,
    val message: String,
    val image: ImageRef,
    val date: Date)

class Message(
    val isMine: Boolean,
    val message: String,
    val date: Date)

class Posts(val posts: List<Post>, val nextPage: Int?)

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