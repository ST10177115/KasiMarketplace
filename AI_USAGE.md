# AI Tool Usage Write-Up — OPSC6312 POE Part 2

**Tool used:** Claude (Anthropic), accessed via claude.ai.

For Part 2, I used Claude to help design and generate the initial Android prototype and REST API for Kasi Marketplace, building on the app concept, requirements, and UML/API design I produced in Part 1.

**How it was used:**

1. **Project scaffolding.** I asked Claude to set up a Kotlin/Jetpack Compose Android project structure (Gradle files, manifest, package layout) and a matching Node.js/Express/MongoDB REST API, based on the requirements and data model from my Part 1 Planning and Design document.

2. **Core feature implementation.** Claude generated the register/login flow with bcrypt password hashing on the API side and JWT-based authentication, the settings screen and its update endpoint, and the product browsing/cart/checkout flow connected to the REST API. I reviewed each file to understand the logic before accepting it, and adjusted scope so that only Part 2's required features (excluding the PoE-only items: SSO, full offline sync, push notifications, multi-language) were fully implemented, with one deferred feature — a lightweight offline product cache using Room — included as a partial nod to the fuller design.

3. **Debugging and consistency checks.** During generation, Claude caught and fixed a mismatch between the Android app's expected API response fields (e.g. `id` vs MongoDB's `_id`, a flattened `vendorName` field) and the actual API output, and fixed a stale test assertion (`res.body._id` → `res.body.id`) after that schema change. This is the kind of integration bug that is easy to miss when building the client and server separately.

4. **GitHub Actions and testing.** Claude wrote the `.github/workflows/android-build.yml` file that runs the Android unit tests and builds a debug APK on every push, and wrote JUnit unit tests (for input validation logic and cart calculations) and Jest/Supertest integration tests (for the auth, products, and settings endpoints).

5. **Documentation.** Claude drafted this README and this write-up as a starting point, which I then reviewed and edited into my own words before submission.

**What I did myself:** I made the actual product decisions (app concept, feature scope, which Part 1 features to include now versus defer), reviewed all generated code for correctness before accepting it, configured and deployed the REST API to my own hosting provider with my own MongoDB Atlas cluster and secrets, ran the app on my own device, recorded the demonstration video myself, and committed and pushed all code to my own GitHub repository incrementally as I completed and understood each part.

**Why I used it this way:** Given the scope of Part 2 — a working app, a hosted API, automated CI, and unit tests, all due together — using Claude to generate a correct starting implementation let me spend my own time understanding the architecture, testing it on a real device, and fixing issues in context, rather than typing out boilerplate from scratch. I take responsibility for understanding and being able to explain every part of this submission.

*(Word count: approx. 430 words)*
