const admin = require('firebase-admin');
exports.handler = (event) => {
    const message = event.data.current.val();
           const senderUid = message.from;
           const receiverUid = message.to;
           const requestId=message.request_id
           const notiType=message.noti_type
           const promises = [];

           if (senderUid == receiverUid) {
               //if sender is receiver, don't send notification
               promises.push(event.data.current.ref.remove());
               return Promise.all(promises);
           }

           const getInstanceIdPromise = admin.database().ref(`/donor/${receiverUid}/instance_id`).once('value');
           const getSenderNamePromise = admin.database().ref(`/charity/${senderUid}/charity_name`).once('value');
           const getSenderImagePromise = admin.database().ref(`/charity/${senderUid}/charity_image`).once('value');
           const getRequestName = admin.database().ref(`/request_post/${senderUid}/${requestId}/request_place`).once('value');

           return Promise.all([getInstanceIdPromise, getSenderNamePromise,getSenderImagePromise,getRequestName]).then(results => {
               const instanceId = results[0].val();
               const senderName = results[1].val();
               const senderImage= results[2].val();
               const requestPlace= results[3].val();
               const receiver="donor"
               console.log('notifying ' + senderName + ' about ' + message.item_amount + ' from ' + senderImage);


               if(notiType == "request"){
                     const payload = {
                                       notification: {
                                           title: "Hlu Yat",
                                           body: senderName+" request your donating item. \n"+message.item_category+" : "+message.item_amount,
                                           tag: receiver,
                                           icon: senderImage
                                       }

                                   };
                     admin.messaging().sendToDevice(instanceId, payload)
                                        .then(function (response) {
                                            console.log("Successfully sent request message:", response);
                                            console.log(response.results[0].error);
                                        })
                                        .catch(function (error) {
                                            console.log("Error sending message:", error);
                                        });
                }
                 if(notiType =="done"){
                     const payload = {
                              notification: {
                                  title: "Hlu Yat",
                                  body: senderName+" had been donated to "+requestPlace,
                                  tag: receiver,
                                  icon: senderImage
                              }

                          };
                     admin.messaging().sendToDevice(instanceId, payload)
                                        .then(function (response) {
                                            console.log("Successfully sent done message:", response);
                                            console.log(response.results[0].error);
                                        })
                                        .catch(function (error) {
                                            console.log("Error sending message:", error);
                                        });
                 }

           });
};