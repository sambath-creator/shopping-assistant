# Baker Basket

AI powered Android shopping assistant for bakers. The app keeps a reusable weekly ingredient list, schedules a recurring price-check agent, and shows the top three store options per item across UK supermarkets and online baking suppliers.

## Features

- Kotlin Android app using Jetpack Compose and Material 3.
- Modern glassmorphic interface with editable ingredient cards.
- Weekly agent schedule controls for frequency, day, hour, and minute.
- Background scheduling via WorkManager.
- Item image loading with Coil.
- Price-results model with top three options per ingredient.
- GitHub Actions workflow to build and upload a debug APK.

## Build Locally

Install Android Studio or a local Android SDK/JDK 17 setup, then run:

```bash
gradle assembleDebug
```

The CI workflow in `.github/workflows/android.yml` installs Gradle on GitHub Actions and builds `app/build/outputs/apk/debug/app-debug.apk`.

## Price Agent

The current `PriceAgent` is a replaceable prototype that generates ranked sample results and attaches store search links for Sainsbury's, Asda, Aldi, Lidl, Morrisons, Amazon, and Vanilla Valley. Replace `app/src/main/java/com/sambath/shoppingassistant/data/PriceAgent.kt` with a real backend/API-backed implementation when production price data access is available.
