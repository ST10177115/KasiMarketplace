# Kasi Marketplace

An Android marketplace app for township (kasi) buyers and sellers, backed by a Node.js REST API hosted on Microsoft Azure.

> \*\*Student:\*\* Banathi Maholwana (ST10177115) · \*\*Module:\*\* OPSC6312 · \*\*Submission:\*\* POE Part 3

## Demo Video

https://youtube.com/shorts/Dz0M_AwSyIo

## Live API

The API is deployed on Azure App Service (Spain Central):

**https://kasi-marketplace-api-gxeredfpgef6dqgn.spaincentral-01.azurewebsites.net/**

Opening that address should return:

```json
{"message":"Kasi Marketplace API is running."}
```

> The API runs on the Azure Free (F1) plan, which sleeps when idle. The first request after a quiet period can take 30 to 60 seconds to respond.

## Features

* Register and log in with email and password (JWT authentication)
* Sign in with Google (Firebase)
* Browse products and view product details
* List a product for sale (logged-in users)
* Place orders and view your order history
* View and update your profile, and change your password

## Tech stack

|Layer|Technology|
|-|-|
|Mobile app|Android (Gradle Kotlin DSL)|
|API|Node.js, Express|
|Database|MongoDB Atlas|
|Authentication|JWT, Firebase Authentication (Google sign-in)|
|Hosting|Azure App Service (Linux, Node 22 LTS)|
|CI/CD|GitHub Actions|

## Repository structure

```
KasiMarketplace/
├── app/                      Android application
├── api/                      Node.js REST API
│   ├── models/
│   ├── routes/
│   ├── middleware/
│   ├── tests/
│   ├── server.js
│   └── .env.example          Names of the environment variables the API needs
├── .github/workflows/        CI (Android build and test) and API deployment to Azure
├── AI\_USAGE.md               Declaration of AI tool usage
└── README.md
```

## API overview

Base path: `/api/v1`

|Method|Endpoint|Description|
|-|-|-|
|POST|`/auth/register`|Create an account|
|POST|`/auth/login`|Log in and receive a JWT|
|POST|`/auth/google`|Sign in with a Google (Firebase) token|
| GET | `/products` | List products |
| GET | `/products/:id` | Get one product |
| POST | `/products` | Create a product (requires login) |
| GET | `/orders` | List the user's orders (requires login) |
| POST | `/orders` | Place an order (requires login) |
| GET | `/users/me` | Get the current user's profile (requires login) |
| PATCH | `/users/me` | Update the profile (requires login) |
| PATCH | `/users/me/password` | Change password (requires login) |

## Running the API locally

1. Install Node.js 22 LTS.
2. In the `api` folder, copy `.env.example` to `.env` and fill in your own values.
3. Install and start:

```bash
cd api
npm install
npm start
```

### Environment variables

|Name|Purpose|
|-|-|
|`MONGODB\_URI`|MongoDB Atlas connection string|
|`JWT\_SECRET`|Secret used to sign JWTs|
|`JWT\_EXPIRES\_IN`|Token lifetime, for example `7d`|
|`FIREBASE\_SERVICE\_ACCOUNT`|Firebase service account JSON (as a single value)|

**No secrets are stored in this repository.** On Azure these values are set under the web app's *Environment variables*.

## Running the Android app

1. Open the project root in Android Studio.
2. Let Gradle sync finish.
3. The API address is set in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE\_URL", "\\"https://kasi-marketplace-api-gxeredfpgef6dqgn.spaincentral-01.azurewebsites.net/\\"")
```

4. Choose an emulator or a USB-connected phone and click **Run**.

## Deployment

* **API:** the workflow `.github/workflows/azure-api.yml` deploys the `api` folder to Azure App Service on every push to `main` that changes files in `api/`. It uses a publish-profile secret stored in the repository's GitHub Actions secrets.
* **Android:** the *Android CI Build and Test* workflow builds and tests the app on every push.

## Testing

* Android CI runs automatically on GitHub Actions.
* Run the API tests with `npm test` in the `api` folder (uses Jest)

## AI usage

See [AI\_USAGE.md](AI_USAGE.md).

## Author

Banathi, Rosebank College

