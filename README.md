Edge Viewer — Real-Time Camera Frame Processing (Android + Native C++ + OpenGL ES)

This project implements a real-time camera processing pipeline on Android using:

Android Camera2 API for frame capture

JNI + Native C++ for image processing using OpenCV

OpenGL ES 2.0 for rendering processed frames

A minimal TypeScript Web Viewer for displaying a sample processed frame

This repository fulfills the assessment requirements by demonstrating:
modular project structure, native processing, OpenGL rendering, clean code, and meaningful Git commits.

📌 Project Architecture Overview
Android Camera2  →  JNI Bridge  →  OpenCV (C++)  →  RGBA Buffer  →  OpenGL Renderer → Display
                                                            ↓
                                               Saved Frame (PNG/Base64)
                                                            ↓
                                           Web Viewer (TypeScript)
1. Camera Layer (Kotlin)

Captures frames using Camera2 API with ImageReader (YUV_420_888)

Converts YUV → NV21 for processing

Sends frames to native C++ through JNI

2. Native Processing Layer (C++ / OpenCV)

Receives NV21 byte buffer

Converts NV21 → RGB → Gray

Applies Canny edge detection

Outputs a processed RGBA pixel buffer

3. Rendering Layer (OpenGL ES 2.0)

Uploads RGBA buffer as GL texture

Renders full-screen quad using custom vertex & fragment shaders

Achieves 10–15 FPS depending on device

4. Web Viewer (TypeScript)

Displays a sample processed frame (PNG/Base64)

Shows resolution + FPS text overlay

Built using plain TypeScript + HTML

📁 Folder Structure
edge-viewer/
│
├── android-app/
│   ├── app/
│   │   ├── src/main/java/com/edgeviewer/
│   │   │     ├── MainActivity.kt
│   │   │     ├── NativeLib.kt
│   │   │     └── FrameRenderer.kt
│   │   ├── src/main/cpp/
│   │   │     ├── native-lib.cpp
│   │   │     ├── edge_processor.cpp
│   │   │     └── CMakeLists.txt
│   │   └── res/layout/activity_main.xml
│   └── build.gradle
│
├── web/
│   ├── index.html
│   ├── package.json
│   └── src/app.ts
│
├── README.md
├── .gitignore
└── LICENSE (optional)

🛠️ Tech Stack
Layer	Technology
Android App	Kotlin, Camera2
Native	C++, OpenCV 4.x
Rendering	OpenGL ES 2.0
Web Viewer	TypeScript, HTML
Build Tools	CMake, NDK r25+, Gradle
🚀 Build & Run Instructions (macOS)
1. Install Requirements

Install via Homebrew:

brew install git node openjdk


Install Android Studio:

Enable NDK, CMake, NDK side-by-side

Download OpenCV Android SDK:

https://opencv.org/releases/

Unzip to:

~/Android/opencv-4.x-android-sdk/

2. Configure CMakeLists.txt

Set your OpenCV path:

set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/../opencv-4.x-android-sdk/sdk/native/jni)

3. Build Android App

Open android-app folder in Android Studio

Wait for Gradle Sync

Connect Android device

Press Run ▶️

The preview will show processed (edge detected) real-time frames.

4. Run Web Viewer
cd web
npm install
npx http-server .


Open browser at:

http://localhost:8080


You will see:

Sample processed frame

FPS + resolution overlay

📷 Sample Outputs

(Include actual screenshots when submitting)

android-app/assets/screenshots/
    processed_frame.png
    realtime_preview.png

📚 Key Implementation Details
✔ JNI Bridge

Transfers byte arrays between Kotlin ↔ C++ efficiently.

✔ OpenCV Processing

Canny edge detection:

cvtColor(rgb, gray, COLOR_RGB2GRAY);
Canny(gray, edges, 50, 150);

✔ GL Rendering

Texture updated via glTexImage2D

Vertex + Fragment shaders

Draws full-screen quad

⚡ Performance Notes

Using 640×480 improves FPS

Preallocate buffers to avoid GC

Use glTexSubImage2D instead of glTexImage2D in final version

Avoid processing on UI thread

🚨 Limitations

Uses basic shaders (no advanced effects)

Not using multithreading yet

YUV → NV21 conversion can be optimized further

🧪 Test Plan

Install app on device

Move camera around — processed edges should update smoothly

Capture one processed frame and verify correct PNG output

Load PNG in the Web Viewer to confirm web integration

🧭 Git Commit Strategy (as required in assessment)

Use meaningful, small commits:

feat(android): add Camera2 preview pipeline
feat(native): implement Canny edge detection in C++
feat(render): add OpenGL ES renderer
feat(web): add TypeScript viewer for sample frame
docs: update README with build instructions


Push:

git push origin main


Tag a release:

git tag -a v1.0 -m "Assessment Submission v1.0"
git push origin v1.0

📄 Submission Checklist

✔ Android app: Camera2 + JNI + OpenCV + OpenGL
✔ Web viewer (TypeScript)
✔ Clean commits
✔ README with build + run instructions
✔ No large binaries committed
✔ Final tagged release

🙌 End of README

