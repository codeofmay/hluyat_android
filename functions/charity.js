const admin = require('firebase-admin');
exports.handler = (event) => {
    const message = event.data.current.val();
           const senderUid = message.from;
           const receiverUid = message.to;
           const promises = [];

           if (senderUid == receiverUid) {
               //if sender is receiver, don't send notification
               promises.push(event.data.current.ref.remove());
               return Promise.all(promises);
           }

           const getInstanceIdPromise = admin.database().ref(`/charity/${receiverUid}/instance_id`).once('value');
           const getSenderUidPromise = admin.auth().getUser(senderUid);

           return Promise.all([getInstanceIdPromise, getSenderUidPromise]).then(results => {
               const instanceId = results[0].val();
               const sender = results[1];
               const receiver="charity"
               console.log('notifying ' + sender.displayName + ' about ' + message.item_amount + ' from ' + sender.photoURL);

               const payload = {
                   notification: {
                       title: "DC",
                       body: sender.displayName+ " accept to donate. \n" +message.item_category+" : " +message.item_amount,
                       tag: receiver,
                       icon: sender.photoURL
                   }
               };

               admin.messaging().sendToDevice(instanceId, payload)
                   .then(function (response) {
                       console.log("Successfully sent message:", response);
                       console.log(response.results[0].error);
                   })
                   .catch(function (error) {
                       console.log("Error sending message:", error);
                   });
           });
};