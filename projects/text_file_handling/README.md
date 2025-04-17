# Simple Text File Handling App

This is a simple Android application that reads text from a `.txt` file and displays it in the app's user interface.

## Features

- File Handling in android app using Kotlin

## Screenshots

![Interface](app/src/main/res/drawable/interface_img.png)

## How It Works

1. The app uses the `assets` folder to store the `.txt` file.
2. If the file is already there, it reads content from that file otherwise creates a new one.
3. The content of the file is displayed in a `TextView` on the main screen.

## Project Structure

- **Languages**: Kotlin, Java
- **Build System**: Gradle
- **UI**: XML-based layouts

## How to Run

1. Clone the repository:
   ```bash
   git clone <repository-url>
    cd <repository-directory>
    ```
2. Open the project in Android Studio.
3. Build and run the app on an Android device or emulator.
4. Make sure to have the necessary permissions set in `AndroidManifest.xml` if you are accessing external storage.


## Wanna see the txt file? 
Most people don't know where app's files are stored. But I got you covered !
1. Open Android Studio.
2. Go to `View` > `Tool Windows` > `Device File Explorer`.
3. Select your device or emulator.
4. Navigate to `data` > `data` > `com.learning.text_file_handling` > `files`.
5. You will find the `my_data_file.txt` file there.

Happy coding! 😊

Author: [M. Yousuf]
Date: [Apr 17, 2025]

