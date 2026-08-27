<div align="center">

</div>

# Run and deploy your ImageKit app

This contains everything you need to build and run ImageKit locally.

# ImageKit — offline image editor

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. No API keys are needed — the app runs fully offline.
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
7. 
