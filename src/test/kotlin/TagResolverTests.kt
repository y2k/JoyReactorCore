import cc.joyreactor.core.TagResolver.tagIcons
import cc.joyreactor.core.TagResolver.tryGetImageId
import cc.joyreactor.core.TagResolver.userIcons
import org.junit.Assert.assertNotNull
import org.junit.Test

class TagResolverTests {

    @Test
    fun `class initialize is success`() {
        assertNotNull(tagIcons)
        assertNotNull(userIcons)
    }

    @Test
    fun `find url for tag is success`() {
        listOf(
            "Warhammer 40000", "JaGo", "оглаф", "Этти",
            "texic", "countryballs", "adventure time", "Халява")
            .forEach {
                assertNotNull(it, tryGetImageId(tagIcons, it))
            }
    }
}