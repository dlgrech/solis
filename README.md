<p align="center">
  <img width="150px" height="150px" src="./android/ic_launcher-playstore.png">
</p>

<h1 align="center">Solis Inspector</h1>

<p align="center">
  Android Solana Explorer & Debugging tool
</p>

Android app build on top of [Helius](https://helius.xyz/) & Firebase that provides:

- Detailed view of Solana transactions, accounts and programs
- Real-time push notifications for saved accounts
- SolPay QR scanning inspection tools

The client itself uses the [khelius](https://github.com/dlgrech/khelius) library for displaying transaction/account information & [ksol](https://github.com/dlgrech/ksol) for interacting with SolPay.

### Building the app

You'll need to provide your own Helius API key and make it available to gradle under the variable name `HELIUS_API_KEY`.

You'll also need to provide your own `google-services.json` entry that you can obtain from your own firebase account.

Other than that, the app can be built just as any other Android app using the command `./gradlew assembleDebug`