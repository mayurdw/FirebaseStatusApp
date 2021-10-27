// The Cloud Functions for Firebase SDK
// to create Cloud Functions and set up triggers.
const functions = require("firebase-functions");

// The Firebase Admin SDK to access Firestore.
const admin = require("firebase-admin");
admin.initializeApp();

// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

exports.helloWorld = functions.https.onRequest((request, response) => {
  functions.logger.info("Hello logs!", {structuredData: true});
  response.send("My favourite emoji is \u{1F43C}");
});

exports.addUserToFirestore = functions.auth.user().onCreate((user) =>{
  const usersRef = admin.firestore().collection("users");

  return usersRef.doc(user.uid).set({
    displayName: user.displayName,
    emojis: "\u{1F910}\u{1F492}\u{4F421}",
  });
});
