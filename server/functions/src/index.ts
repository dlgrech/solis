import functions = require("firebase-functions");
import admin = require("firebase-admin");
import fetch = require("node-fetch");
import {defineSecret} from "firebase-functions/v2/params";

const heliusApiKey = defineSecret("HELIUS_API_KEY");
const heliusWebhookId = defineSecret("HELIUS_WEBHOOK_ID");

admin.initializeApp();

/**
 * @return {Promise<Array<string>>} the list of account public keys
 * which should be part of the Helius Webhook
 */
function getAccountsForHeliusWebhook(): Promise<Array<string>> {
  return admin.database()
      .ref("account_to_user_count")
      .once("value")
      .then((snapshot) => {
        const accountsToObserve: string[] = [];
        snapshot.forEach((child) => {
          const account = child.key;
          const count = child.val();

          if (account && count > 0) {
            accountsToObserve.push(account);
          }
        });

        return accountsToObserve;
      });
}

/**
 *  @return {Promise<boolean>} indicating if the webhook has been updated or not
 */
function updateHeliusWebhookAddresses(): Promise<boolean> {
  const webhookUrl = "https://us-central1-solis-cdf84.cloudfunctions.net/onHeliusWebhook";
  const webhookId = heliusWebhookId.value();
  const apiKey = heliusApiKey.value();

  return getAccountsForHeliusWebhook()
      .then((accounts) => {
        const url = "https://api.helius.xyz/v0/webhooks/" + webhookId + "?";
        const params = new URLSearchParams({"api-key": apiKey});
        const payload = {
          "webhookUrl": webhookUrl,
          "accountAddresses": accounts,
          "webhookType": "enhanced",
          "transactionTypes": ["Any"],
        };

        return fetch.default(url + params, {
          method: "PUT",
          body: JSON.stringify(payload),
        }).then((result) => {
          functions.logger.info(
              "Update webhook: " + result.status + " " + result.statusText
          );
          return result.status == 200;
        }).catch((err) =>{
          return Promise.reject(err);
        });
      });
}

/**
 * @param account The account responsible for causing this push.
 * @param signature The transaction signature responsible for causing this push
 * @param type The type from the helius webhook.
 * @param description: The (nullable) description passed from the helis webhook.
 * @return {{[key: string]: string;}}  payload to be sent as the data element
 * of a push notification.
 */
function createNotificationPayload(
    account: string,
    signature: string,
    type: string,
    description: string
): {[key: string]: string;} {
  return {
    account: account,
    transaction: signature,
    type: type ?? "UNKNOWN",
    description: description,
  };
}

exports.onAccountSaved = functions
    .runWith({secrets: [heliusApiKey.name, heliusWebhookId.name]})
    .database
    .ref("{uid}/saved_accounts/{account}")
    .onCreate(async (snapshot, context) => {
      const uid = context.params.uid;
      const account = context.params.account;
      functions.logger.info(
          "User " + uid + " saved account " + account
      );

      /**
       * - Get the current user_count for this account
       * - Increment count by 1
       * - If first user saving this account, add it to our Helius Webook
       */

      return snapshot.ref.root
          .child("account_to_user_count")
          .child(account)
          .get()
          .then((res) => {
            return res.exists() ? res.val() : 0;
          }).then((currentCount) => {
            const newCount = currentCount + 1;
            const updateCountPromise = snapshot.ref.root
                .child("account_to_user_count")
                .child(account)
                .set(newCount)
                .then(() => newCount);
            const updateAccountToUserMapPromise = snapshot.ref.root
                .child("account_to_users")
                .child(account)
                .child(uid)
                .set(true);

            return Promise.all(
                [updateCountPromise, updateAccountToUserMapPromise]
            );
          }).then(([newCount]) => {
            if (newCount != 1) {
              return true;
            } else {
              // First time a user is saving this account. Add it to our webhook
              return updateHeliusWebhookAddresses();
            }
          }).catch((err) => {
            return Promise.reject(err);
          });
    });

exports.onAccountRemoved = functions
    .runWith({secrets: [heliusApiKey.name, heliusWebhookId.name]})
    .database
    .ref("{uid}/saved_accounts/{account}")
    .onDelete((snapshot, context) => {
      const uid = context.params.uid;
      const account = context.params.account;
      functions.logger.info(
          "User " + uid + " deleted account " + account
      );

      /**
       * - Get the current user_count for this account
       * - Decrement count by 1 (or remove the record completely)
       * - If no users observing the account, remove it from our Helius Webook
       */

      return snapshot.ref.root
          .child("account_to_user_count")
          .child(account)
          .get()
          .then((res) => {
            return res.exists() ? res.val() : 0;
          }).then((currentCount) => {
            const newCount = currentCount == 0 ? 0 : (currentCount - 1);
            const updateCountPromise = newCount == 0 ?
              snapshot.ref.root
                  .child("account_to_user_count")
                  .child(account)
                  .remove()
                  .then(() => 0) :
              snapshot.ref.root
                  .child("account_to_user_count")
                  .child(account)
                  .set(newCount)
                  .then(() => newCount);
            const updateAccountToUserMapPromise = snapshot.ref.root
                .child("account_to_users")
                .child(account)
                .child(uid)
                .remove();

            return Promise.all(
                [updateCountPromise, updateAccountToUserMapPromise]
            );
          }).then(([newCount]) => {
            if (newCount != 1) {
              return true;
            } else {
              // No more users observing this account. Remove from webhook
              return updateHeliusWebhookAddresses();
            }
          }).catch((err) => {
            return Promise.reject(err);
          });
    });

exports.onHeliusWebhook = functions.https.onRequest((request, response) => {
  // - Get accounts in txn
  // - Get all users interested in account
  // - Get push token for those users
  // - Build push payload
  // - Send push
  // - Cleanup unused push tokens
  functions.logger.info(
      JSON.stringify(request.body), {structuredData: true}
  );

  const transaction = request.body[0];
  if (!transaction) {
    return functions.logger.warn("Got Webhook without transaction");
  }

  functions.logger.info("Got transaction: " + transaction["signature"]);

  const accountData = transaction["accountData"];
  if (!accountData || accountData.length === 0) {
    return functions.logger.warn("Helius: Got Webhook without account data");
  }

  const relevantAccounts: string[] = accountData.filter((value) => {
    return value && (
      value["nativeBalanceChange"] != 0 ||
        value["tokenBalanceChanges"].size > 0
    );
  }).map((account) => {
    return account["account"];
  });

  functions.logger.debug(
      "Relevant accounts in transaction: " + JSON.stringify(relevantAccounts)
  );

  relevantAccounts.map((account) => {
    admin.database()
        .ref("account_to_users")
        .child(account)
        .once("value")
        .then((snapshot) => {
          if (snapshot.numChildren() == 0) {
            return {account: account, users: []};
          } else {
            const users: string[] = [];
            snapshot.forEach((userRef) => {
              if (userRef.key) {
                users.push(userRef.key);
              }
            });

            return {account: account, users: users};
          }
        }).then((accountAndUsers) => {
          const account = accountAndUsers.account;
          const users = accountAndUsers.users;

          functions.logger.info(
              "Account: " + account + " users: " + JSON.stringify(users)
          );

          users.map((user) => {
            admin.database()
                .ref(user)
                .child("push_token")
                .once("value")
                .then((pushToken) => {
                  return pushToken.val();
                }).then((token) => {
                  if (token == null) {
                    functions.logger.debug(
                        "No push token found for " + user
                    );
                    Promise.resolve("");
                  } else {
                    const payload = {
                      token: token,
                      data: createNotificationPayload(
                          account,
                          transaction["signature"],
                          transaction["type"],
                          transaction["description"]
                      ),
                    };

                    functions.logger.debug(
                        "Sending push payload: " + JSON.stringify(payload)
                    );
                    admin.messaging().send(payload);
                  }
                });
          });
        });
  });

  response.sendStatus(200);
});
