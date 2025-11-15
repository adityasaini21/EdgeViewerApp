cmake_minimum_required(VERSION 3.10.2)
project("edgeviewer")

set(OpenCV_DIR "/Users/adityasaini/AndroidStudioProjects/OpenCV-android-sdk/sdk/native/jni") # adjust path

add_library( # native lib name
        native-lib
        SHARED
        src/main/cpp/native-lib.cpp
        src/main/cpp/edge_processor.cpp)

# Include OpenCV headers
include_directories(${OpenCV_DIR}/include)

find_library(log-lib log)

# Link OpenCV libs - you will copy/prebuilt libs into jniLibs or link to OpenCV SDK libs
add_library( opencv_java4 SHARED IMPORTED )
set_target_properties( opencv_java4 PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../opencv-4.x-android-sdk/sdk/native/libs/${ANDROID_ABI}/libopencv_java4.so )

target_link_libraries(native-lib
        opencv_java4
        ${log-lib})
