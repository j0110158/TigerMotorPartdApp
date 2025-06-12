# Tiger Motor Parts App Installation Guide

## System Requirements
- Windows 10 or later
- Java 18 or later (JDK 18+ recommended, JDK 24.0.1 or later preferred)

## Quick Start Guide

### 1. Install Java
1. Download and install Java from Oracle's website:
   - [Download JDK 24.0.1](https://download.oracle.com/java/24/latest/jdk-24_windows-x64_bin.exe)
   - Follow the installation wizard's instructions
   - Accept the default installation settings

### 2. Verify Java Installation
1. Open Command Prompt (Press Windows + R, type `cmd`, press Enter)
2. Type `java -version` and press Enter
3. You should see something like:
   ```
   java version "24.0.1" 2024-04-16
   Java(TM) SE Runtime Environment (build 24.0.1+9)
   Java HotSpot(TM) 64-Bit Server VM (build 24.0.1+9, mixed mode, sharing)
   ```

### 3. Install Tiger Motor Parts App
1. Download the application files:
   - `TigerMotorhubApp.exe`
   - `inventory_data.csv`
2. Create a new folder on your computer (e.g., `C:\TigerMotorParts`)
3. Copy both files into this folder

### 4. Running the Application
1. Navigate to the folder where you saved the files
2. Double-click `TigerMotorhubApp.exe`
3. The application should start and show "Data loaded successfully"

## Troubleshooting

### If the application doesn't start:

1. **Check Java Installation**
   - Open Command Prompt
   - Type `java -version`
   - If you see "'java' is not recognized", you need to install Java

2. **Common Issues**
   - "Java not found": Install Java from the link above
   - "Data file not found": Make sure `inventory_data.csv` is in the same folder as the EXE
   - "Application failed to start": Try restarting your computer after Java installation

3. **Still Having Issues?**
   - Make sure Windows is up to date
   - Try running the application as administrator
   - Check if your antivirus is blocking the application

## Support
If you need help:
1. Check the troubleshooting section above
2. Make sure Java is properly installed
3. Verify all files are in the same folder
4. Try restarting your computer

## Notes
- The application requires Java 18 or later
- Keep all files in the same folder
- The application is designed for 64-bit Windows
- Your inventory data is automatically saved in `inventory_data.csv` 