package com.example.tubes.data.repository

import com.example.tubes.data.model.User
import com.example.tubes.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepositoryImpl : ProfileRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val quizAttemptsCollection = db.collection("quiz_attempts")

    override suspend fun getUserProfile(): User {
        val uid = auth.currentUser?.uid
            ?: throw Exception("User tidak login atau tidak ditemukan")

        return getUserById(uid)
    }

    override suspend fun getUserById(uid: String): User {
        try {
            val document = usersCollection
                .document(uid)
                .get()
                .await()

            return document.toObject(User::class.java)
                ?: throw Exception("Dokumen user tidak ada atau format salah")

        } catch (e: Exception) {
            throw Exception("Gagal mengambil data user: ${e.message}")
        }
    }

    override suspend fun getQuizAttemptsCount(uid: String): Int {
        return try {
            val snapshot = quizAttemptsCollection
                .whereEqualTo("uid", uid)
                .get()
                .await()

            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    // === FOLLOW SYSTEM ===

    override suspend fun followUser(targetUserId: String) {
        val currentUserId = auth.currentUser?.uid
            ?: throw Exception("User tidak login")

        if (currentUserId == targetUserId) {
            throw Exception("Tidak bisa follow diri sendiri")
        }

        try {
            db.runBatch { batch ->
                // Tambah targetUserId ke following list current user
                batch.update(
                    usersCollection.document(currentUserId),
                    "following",
                    FieldValue.arrayUnion(targetUserId)
                )

                // Tambah currentUserId ke followers list target user
                batch.update(
                    usersCollection.document(targetUserId),
                    "followers",
                    FieldValue.arrayUnion(currentUserId)
                )
            }.await()
        } catch (e: Exception) {
            throw Exception("Gagal follow user: ${e.message}")
        }
    }

    override suspend fun unfollowUser(targetUserId: String) {
        val currentUserId = auth.currentUser?.uid
            ?: throw Exception("User tidak login")

        try {
            db.runBatch { batch ->
                // Hapus targetUserId dari following list current user
                batch.update(
                    usersCollection.document(currentUserId),
                    "following",
                    FieldValue.arrayRemove(targetUserId)
                )

                // Hapus currentUserId dari followers list target user
                batch.update(
                    usersCollection.document(targetUserId),
                    "followers",
                    FieldValue.arrayRemove(currentUserId)
                )
            }.await()
        } catch (e: Exception) {
            throw Exception("Gagal unfollow user: ${e.message}")
        }
    }

    override suspend fun isFollowing(targetUserId: String): Boolean {
        val currentUserId = auth.currentUser?.uid ?: return false

        return try {
            val user = getUserById(currentUserId)
            user.following.contains(targetUserId)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getFollowers(uid: String): List<User> {
        return try {
            val user = getUserById(uid)
            val followerIds = user.followers

            if (followerIds.isEmpty()) {
                emptyList()
            } else {
                // Ambil detail semua followers
                followerIds.mapNotNull { followerId ->
                    try {
                        getUserById(followerId)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getFollowing(uid: String): List<User> {
        return try {
            val user = getUserById(uid)
            val followingIds = user.following

            if (followingIds.isEmpty()) {
                emptyList()
            } else {
                // Ambil detail semua following
                followingIds.mapNotNull { followingId ->
                    try {
                        getUserById(followingId)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
