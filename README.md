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

## Price Backend

The app is powered by a Node.js Express backend deployed as a Serverless Function on **Vercel**. 
The backend connects to the **SerpApi Google Shopping API** (`https://serpapi.com/search.json`) to fetch real-time, live prices across UK supermarkets and online suppliers. 

The backend returns the top 3 cheapest live options for each item, including the exact product description (quantity/size) and a direct link to the retailer's product page. 

If the backend fails to connect to SerpApi (or the API key is missing), it loudly fails with an error payload for easy debugging.

### Environment Setup

For the backend to function, you must set the `SERPAPI_KEY` environment variable in your Vercel Project settings (make sure it's applied to the Production environment).
