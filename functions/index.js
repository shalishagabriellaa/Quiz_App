/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */
const { setGlobalOptions } = require("firebase-functions");
const { onDocumentCreated, onDocumentDeleted } =
  require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

setGlobalOptions({ maxInstances: 10 });

admin.initializeApp();
const db = admin.firestore();

// 🔔 Quiz dibuat
exports.onQuizCreated = onDocumentCreated(
  "quizzes/{quizId}",
  async (event) => {

    const quiz = event.data?.data();
    if (!quiz) return;

    await db.collection("notifications").add({
      userId: quiz.authorId,
      role: "teacher",
      type: "QUIZ_CREATED",
      title: "Quiz berhasil dibuat",
      message: `Quiz "${quiz.title}" berhasil dibuat`,
      quizId: event.params.quizId,
      isRead: false,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });
  }
);

// 🔔 Quiz dihapus
exports.onQuizDeleted = onDocumentDeleted(
  "quizzes/{quizId}",
  async (event) => {

    const quiz = event.data?.data();
    if (!quiz) return;

    await db.collection("notifications").add({
      userId: quiz.authorId,
      role: "teacher",
      type: "QUIZ_DELETED",
      title: "Quiz dihapus",
      message: `Quiz "${quiz.title}" telah dihapus`,
      quizId: event.params.quizId,
      isRead: false,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });
  }
);

// For cost control, you can set the maximum number of containers that can be
// running at the same time. This helps mitigate the impact of unexpected
// traffic spikes by instead downgrading performance. This limit is a
// per-function limit. You can override the limit for each function using the
// `maxInstances` option in the function's options, e.g.
// `onRequest({ maxInstances: 5 }, (req, res) => { ... })`.
// NOTE: setGlobalOptions does not apply to functions using the v1 API. V1
// functions should each use functions.runWith({ maxInstances: 10 }) instead.
// In the v1 API, each function can only serve one request per container, so
// this will be the maximum concurrent request count.
setGlobalOptions({ maxInstances: 10 });

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
