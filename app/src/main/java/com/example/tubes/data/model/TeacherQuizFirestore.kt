import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class TeacherQuizFirestore(
    val title: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String? = null,
    @get:PropertyName("totalQuestions")
    @set:PropertyName("totalQuestions")
    var totalQuestions: Int = 0,

    val status: String = "draft",

    @get:PropertyName("totalParticipants")
    @set:PropertyName("totalParticipants")
    var totalParticipants: Int = 0,

    @get:PropertyName("averageScore")
    @set:PropertyName("averageScore")
    var averageScore: Double = 0.0,

    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    var createdAt: Timestamp? = null,

    @get:PropertyName("bannerUrl")
    @set:PropertyName("bannerUrl")
    var bannerUrl: String? = null
)
