# Install Guide for Tiger Motorhub App

This guide will help you set up and run the `TigerMotorhubApp.exe` on your Windows system.

## Prerequisites:
To run this application, you need to have a **Java Runtime Environment (JRE) installed on your system, version 8 or newer.**

If you don't have Java installed, or if you have an older version, please download and install a compatible JRE (e.g., OpenJDK 8 or a later LTS version) from a trusted source like Oracle or Adoptium. You can usually find the latest distributions by searching for "Download OpenJDK 8" or "Download Oracle JRE 8+".

## How to Run the Application:

1.  **Locate the Executable:** Navigate to the `exe_output` folder (where this `InstallGuide.md` file is located). You will find the executable file named `TigerMotorhubApp.exe`.

2.  **Double-Click to Run:** Simply double-click `TigerMotorhubApp.exe`.
    *   If a security warning appears (e.g., Windows SmartScreen or your antivirus), this is a common occurrence for unsigned executables. You can usually click "More info" and then "Run anyway" to proceed. This warning does NOT indicate an issue with the application itself.

3.  **Data File Location:**
    *   The application will automatically look for its data file (`inventory_data.csv`) in the same directory where the `TigerMotorhubApp.exe` is located (`exe_output`).
    *   If `inventory_data.csv` is not found, the application will start with an empty inventory and create a new `inventory_data.csv` file in the same `exe_output` directory upon the first save operation.
    *   You can change the data folder path from within the application via the `Settings -> Set Data Folder Path` menu option if you wish to store your data elsewhere.

## Troubleshooting:

*   **"A JNI error has occurred" or similar Java errors:** Ensure you have Java Runtime Environment (JRE) version 8 or newer installed on your system. If you have multiple Java versions, ensure the correct one is prioritized in your system's PATH environment variables.
*   **Antivirus blocking the application:** As mentioned, unsigned executables can sometimes be flagged. If your antivirus blocks it, you may need to add an exception for `TigerMotorhubApp.exe` in your antivirus software settings. 