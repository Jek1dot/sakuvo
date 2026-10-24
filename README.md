# Sakuvo

Sakuvo is an offline-first personal finance Android app. This repository is intended for learning, experimentation, and community contributions.

## Current release
- Version: **v0.4.0-beta**
- Platform: Android
- UI: HTML, CSS and vanilla JavaScript
- Android host: Java and WebView
- Storage: on-device local storage; no cloud backend required

## Source layout
The original beta source archive contains:
- `src/id/sakuvo/app/MainActivity.java` — Android activity and WebView integration
- `assets/index.html` — application UI and logic
- `res/` — Android resources and icons
- `AndroidManifest.xml` — Android app manifest

Basic Gradle project configuration is included (`settings.gradle`, `build.gradle`, and `app/build.gradle`). A Gradle wrapper is not included yet. Build has **not been verified**; open with Android Studio and install a compatible Gradle/JDK toolchain before building.

## Contributing
Feel free to fork the repository and propose improvements using pull requests. Please do not commit real financial data, credentials, signing keys or keystores.

## License
MIT — see [LICENSE](LICENSE).