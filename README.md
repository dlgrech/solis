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

### Demo

[![](https://markdown-videos.deta.dev/youtube/ZhXT2oXAMiA)](https://youtu.be/ZhXT2oXAMiA)


### App Preview

![home](https://user-images.githubusercontent.com/111267870/213944437-5b6c5d4c-f0ab-4282-a0df-f13b245f7997.png)![accounts](https://user-images.githubusercontent.com/111267870/213944511-f155f1e6-a280-46dd-9f0a-6580ff3cd81d.png)![transaction](https://user-images.githubusercontent.com/111267870/213944514-c7eb60af-677d-43b8-b534-25b51ca1b13f.png)![recent_transactions](https://user-images.githubusercontent.com/111267870/213944585-d25d700d-76c2-474a-a2f2-89544bd8ab9e.png)![transfer_request](https://user-images.githubusercontent.com/111267870/213944596-4b9cb042-4cdf-4c14-9ff7-e1ac95530274.png)![notification](https://user-images.githubusercontent.com/111267870/213944439-fb0daace-6def-4855-b452-a246dff38dcf.png)
