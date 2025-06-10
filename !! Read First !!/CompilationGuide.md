# Compilation Guide for Tiger Motorhub App

This document summarizes the steps taken to compile the Java source code and produce the `TigerMotorhubApp.exe` executable.

## 1. Initial Java Compilation
All Java source files (`.java`) were compiled into Java bytecode (`.class`) files using the `javac` compiler.

```bash
javac -d . *.java
```

## 2. Creating the Executable JAR File
After compilation, all `.class` files were packaged into a single executable Java Archive (JAR) file. This JAR specifies `InventorySwingGUI` as the main entry point.

```bash
jar cfe TigerMotorhubApp.jar InventorySwingGUI *.class
```

## 3. Native Executable Creation Attempts (jpackage)
Initially, `jpackage` (bundled with JDK 14+) was attempted for creating a self-contained native executable. This method bundles a Java Runtime Environment (JRE) with the application, eliminating the need for a pre-installed JRE on the user's machine.

*   **PowerShell Parsing Issues:** The `jpackage` command encountered parsing errors in PowerShell due to the presence of spaces in the executable path and hyphenated arguments. This was attempted to be resolved using the `--%` stop-parsing symbol and the `&` call operator.
*   **WiX Toolset Dependency:** `jpackage` for Windows requires the WiX Toolset to create `.exe` installers. This toolset is free and open-source, used internally by `jpackage` to generate installer definitions (in XML) and compile them into the final `.exe`. The user was guided to download and install WiX Toolset from `https://wixtoolset.org` and ensure it's in the system's PATH.

## 4. Switching to Launch4j
Due to complexities with WiX Toolset installation, the process pivoted to using Launch4j, a third-party tool known for simpler executable wrapping, particularly for GUI applications.

### Launch4j Overview:
Launch4j creates a wrapper `.exe` around an existing `.jar` file. Users running a Launch4j-generated `.exe` **still require a compatible Java Runtime Environment (JRE) installed on their system** (minimum JRE 8+ for this application).

### Launch4j Setup Steps:

1.  **Download:** Downloaded Launch4j from `https://sourceforge.net/projects/launch4j/`.
2.  **Extraction:** Extracted the downloaded ZIP file to a convenient location (e.g., `C:\Launch4j`).
3.  **GUI Configuration:** The `launch4j.exe` GUI was used to configure the executable settings:
    *   **Basic Tab:**
        *   `Output file`: Set to `[Project_Root_Directory]\exe_output\TigerMotorhubApp.exe`.
        *   `Jar`: Set to `[Project_Root_Directory]\TigerMotorhubApp.jar`.
    *   **JRE Tab:**
        *   `Min JRE version`: Set to `1.8.0`.
        *   `GUI Application`: Ensured this was selected in the `Header` tab.
    *   **Version Info Tab:** Filled in various metadata fields (File version, Product version, Company name, Product name, File description, Copyright, Original filename, Internal name, Trademarks) as required by Launch4j for `Add version information` checkbox.

### Troubleshooting during Launch4j Build:

*   **`cannot open output file ... No such file or directory`:** This error occurred because the `exe_output` directory was not created prior to the build. The solution was to manually create the directory:
    ```bash
    mkdir exe_output
    ```
*   **Antivirus False Positives Warning:** A warning regarding antivirus false positives for unsigned executables was noted. This is expected behavior for custom-built executables not digitally signed by a Certificate Authority. For personal use, it can be ignored or an antivirus exception can be added. For wider distribution, digital signing is recommended.

## 5. Final Executable Production
After successfully creating the `exe_output` directory and configuring Launch4j, the `TigerMotorhubApp.exe` was successfully produced.

```
Successfully created C:\Users\Carlos\OneDrive - San Beda University\Desktop\TIGER MOTORHUB\TigerMotorPartdApp\exe_output\TigerMotorhubApp.exe
``` 