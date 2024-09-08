const admin = require('./firebaseAdmin')
const sendNotification = async (userId, message) => {
    try {
        const tokenSnapshot = await admin.database().ref(`users/${userId}/fcmToken`).once('value');
        const token = tokenSnapshot.val();

        if (token) {
            const messagePayload = {
                notification: {
                    title: 'New Message',
                    body: message,
                },
                token: token, // Ensure this is a valid FCM token
            };

            console.log('Sending notification with payload:', messagePayload);

            const response = await admin.messaging().send(messagePayload);
            console.log('Notification sent successfully:', response);
        } else {
            console.log(`No FCM token found for user: ${userId}`);
        }
    } catch (error) {
        console.error('Error sending notification:', error);
    }
};

module.exports = { sendNotification };
