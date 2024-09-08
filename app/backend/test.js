const admin = require('firebase-admin');
const serviceAccount = require('/home/omer/Downloads/chatify-dec73-firebase-adminsdk-z3zgs-e11cba3a7f.json');

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: 'https://chatify-dec73-default-rtdb.firebaseio.com/'
});

const message = {
  notification: {
    title: 'New Message',
    body: 'You have received a new message.',
  },
  token: 'user-device-token',
};

admin.messaging().send(message)
  .then((response) => {
    console.log('Successfully sent message:', response);
  })
  .catch((error) => {
    console.log('Error sending message:', error);
  });
