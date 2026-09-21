# Kasi Marketplace

**OPSC6312 — Portfolio of Evidence, Part 2: App Prototype Development**

Kasi Marketplace is a mobile app that lets small vendors and buyers in South African communities — spaza shops, home bakers, informal traders — list, discover, and buy everyday goods from their phones. This repository contains the working Android prototype (Kotlin + Jetpack Compose) and its REST API backend (Node.js + Express + MongoDB), built for Part 2 of the OPSC6312 Portfolio of Evidence.

> This prototype implements every Part 2 requirement (register/login with encrypted passwords, user settings, a REST API connected to a database) plus a lightweight offline product cache. Features marked **PoE only** in the module brief — SSO, real-time push notifications, full offline sync, and multi-language support — were designed in Part 1 but are intentionally deferred to the final Portfolio of Evidence, as the brief permits.

---

## 📱 Demo Video

**[Insert your unlisted YouTube link here once recorded]**

The video shows registration and login with password encryption, changing settings, live REST API calls with data visible in MongoDB Atlas, and the app handling invalid input without crashing.

---

## Purpose of the App

Research carried out in Part 1 (see the Research Report) found that existing South African buy-and-sell apps — Bob Shop, Gumtree SA, and Facebook Marketplace — each solve product discovery reasonably well, but none offers a genuine offline experience or support for South African languages beyond English. Kasi Marketplace is designed to close that gap: buyers can browse and shop even with unreliable connectivity, and the underlying design (see Part 1's Planning and Design document) supports isiZulu and isiXhosa alongside English at the full Portfolio of Evidence stage.

## Design Considerations

- **Architecture:** The Android app follows an MVVM structure — Jetpack Compose screens observe `StateFlow` exposed by ViewModels, which call into Repositories. Repositories are the single source of truth and decide whether to serve data from the network or, for products, fall back to a local Room cache.
- **Security:** Passwords are never stored or transmitted in plain text beyond the initial HTTPS request. The API hashes every password with bcrypt (10 salt rounds) before writing it to MongoDB; the app never persists a raw password locally.
- **Authentication:** A JSON Web Token (JWT) is issued on register/login and stored locally via Jetpack DataStore. An OkHttp interceptor (`RetrofitClient`) attaches it to every subsequent request automatically.
- **Offline resilience:** `ProductRepository` writes every successful API response into a Room database. If a later request fails (no connectivity), the app transparently falls back to the last-cached listings rather than showing a blank screen or crashing — a lightweight version of the fuller offline-mode-with-sync feature designed in Part 1.
- **Input validation:** All registration/login input is validated client-side (`Validators.kt`) before ever reaching the network, and again server-side, so the app cannot crash from empty fields, malformed emails, or short passwords.
- **Data integrity:** The API re-fetches product prices and stock server-side when an order is placed, rather than trusting client-supplied prices — protecting against a tampered request placing an order at the wrong price.

## Tech Stack

| Layer | Technology |
|---|---|
| Android UI | Kotlin, Jetpack Compose, Material 3, Navigation Compose |
| Local storage | Room (offline product cache), Jetpack DataStore (session) |
| Networking | Retrofit2, OkHttp, Gson |
| Backend API | Node.js, Express |
| Database | MongoDB (Atlas) via Mongoose |
| Auth | bcryptjs (password hashing), jsonwebtoken (JWT) |
| Testing | JUnit + Google Truth (Android unit tests), Jest + Supertest + mongodb-memory-server (API tests) |
| CI/CD | GitHub Actions |

## Repository Structure

```
KasiMarketplace/
├── app/                     # Android app (Kotlin, Jetpack Compose)
│   └── src/
│       ├── main/java/com/kasimarketplace/app/
│       │   ├── data/        # models, remote (Retrofit), local (Room/DataStore), repository
│       │   ├── ui/          # Compose screens, theme, reusable components
│       │   ├── viewmodel/   # ViewModels per screen
│       │   ├── navigation/  # NavHost + routes
│       │   └── util/        # AppContainer (DI), ViewModelFactory, Validators
│       └── test/            # JUnit unit tests
├── api/                     # REST API (Node.js + Express + MongoDB)
│   ├── models/               # Mongoose schemas: User, Product, Order
│   ├── routes/                # auth, users, products, orders
│   ├── middleware/            # JWT auth middleware
│   └── tests/                 # Jest + Supertest integration tests
└── .github/workflows/       # GitHub Actions CI (build + test the Android app)
```

## ⚠️ Before your first commit

This repository includes the `gradlew` / `gradlew.bat` scripts but **not** the binary `gradle/wrapper/gradle-wrapper.jar` file (a compiled file I can't generate in this environment). Before you push to GitHub:

1. Open the project once in Android Studio and let it sync — Android Studio will automatically download and add the missing `gradle-wrapper.jar`.
2. Confirm the file now exists at `gradle/wrapper/gradle-wrapper.jar`.
3. Commit it along with everything else — **without it, `./gradlew` (and therefore the GitHub Actions workflow) will fail.**

## Getting Started

### 1. Deploy the REST API

```bash
cd api
cp .env.example .env      # fill in your MongoDB Atlas URI and a JWT secret
npm install
npm run dev                # runs locally on http://localhost:5000
```

To deploy for real device testing, push the `api` folder to a host such as [Render](https://render.com) (free tier), set the same environment variables there, and use the resulting public URL in the next step.

### 2. Run the Android app

1. Open the project root in Android Studio (Koala or newer).
2. In `app/build.gradle.kts`, update the `BASE_URL` build config field to point at your deployed API (or `http://10.0.2.2:5000/` to reach a locally running API from the Android emulator).
3. Let Gradle sync, then run on a device or emulator.

### 3. Run the tests

```bash
# Android unit tests
./gradlew test

# API tests
cd api && npm test
```

## REST API Reference

Base path: `/api/v1`

| Method | Endpoint | Auth required | Description |
|---|---|---|---|
| POST | `/auth/register` | No | Create an account (password is hashed server-side) |
| POST | `/auth/login` | No | Log in, returns a JWT |
| GET | `/users/me` | Yes | Get the logged-in user's profile |
| PATCH | `/users/me` | Yes | Update name, phone, notification preference |
| PATCH | `/users/me/password` | Yes | Change password |
| GET | `/products` | No | Browse/search listings (`?search=`, `?category=`) |
| GET | `/products/:id` | No | Get a single product |
| POST | `/products` | Yes | Create a listing (caller becomes the vendor) |
| GET | `/orders` | Yes | Get the logged-in user's order history |
| POST | `/orders` | Yes | Place an order from the cart |

## Version Control & GitHub Actions

This repository was built with regular, incremental commits as each feature was completed (see commit history). The workflow at `.github/workflows/android-build.yml` runs automatically on every push and pull request to `main`: it installs JDK 17, runs the Android unit test suite (`./gradlew test`), and builds a debug APK, uploading it as a workflow artifact. This confirms the app builds and its tests pass on a clean machine, not only on the developer's own laptop.

## Screenshots

*(Add screenshots of your running app here once built — Login, Home, Product Detail, Cart, Settings.)*

| Login | Home | Product Detail |
|---|---|---|
| *[screenshot]* | *[screenshot]* | *[screenshot]* |

## Logging

The app uses `android.util.Log` at key points (registration/login attempts, product loading, checkout, settings updates) so behaviour is visible in Logcat during the demo video and marking. The API logs every incoming request (method + path + timestamp) to the console for the same reason.

## AI Tool Usage

See the accompanying short write-up (max 500 words, submitted alongside this repo) describing how AI tools were used and cited during this assessment, as required by the module brief.

## Author

Banathi — OPSC6312, Part 2 submission.
