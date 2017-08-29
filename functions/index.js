const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const toDonorModule = require('./donor');
const toCharityModule = require('./charity');


exports.donor = functions.database.ref('/notification/to_donor/{pushId}').onWrite(toDonorModule.handler);
exports.charity = functions.database.ref('/notification/to_charity/{pushId}').onWrite(toCharityModule.handler);


