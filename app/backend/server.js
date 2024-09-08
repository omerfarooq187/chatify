const express = require('express');
const { sendNotification } = require('./sendNotification.js'); // Ensure correct export/import

const app = express();
app.use(express.json());

app.post('/sendNotification', async (req, res) => {
    const { userId, message } = req.body; // Destructure as object

    if (!userId || !message) {
        return res.status(400).send('Missing userId or Message');
    }

    try {
        await sendNotification(userId, message);
        res.status(200).send('Notification Sent');
    } catch (error) {
        console.error('Error sending notification:', error); // Log the error for debugging
        res.status(500).send('Error sending notification');
    }
});

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
    console.log(`Server running on ${PORT}`); // Use backticks for template literals
});
