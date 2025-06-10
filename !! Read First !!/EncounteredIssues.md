06-01-2025 | 11 PM | Encoutered Issues of the Day

## Key Development Challenges and Decisions (06-01-2025)

Today, as we progressed on the Tiger Motor Parts Inventory Management System, we encountered and addressed several notable technical challenges and made important decisions, particularly concerning the user interface and data handling.

### GUI Framework Transition: JavaFX vs. Swing

Our initial plan was to develop the Graphical User Interface (GUI) using JavaFX, a modern Java UI toolkit. However, this presented significant setup difficulties:

*   **Dependency Resolution:** A primary obstacle was correctly resolving and including the necessary JavaFX libraries in the project. Standard Java installations do not bundle JavaFX by default.
*   **Build Tool Integration (Maven):** We attempted to use Maven for dependency management. This introduced issues such as the build tool executable not being recognized (`mvn is not recognized`), indicating a system PATH or installation problem. Furthermore, the IDE (VS Code) maintained a cached project configuration based on the `pom.xml` even after its removal, leading to persistent build errors until the IDE's Java workspace was explicitly cleaned.
*   **Setup Complexity:** The combined effort of configuring JavaFX dependencies and resolving build tool integration issues consumed significant development time.

Given these challenges and the need to make timely progress on the GUI, we opted to switch to Java Swing. Swing is included in the standard JDK, simplifying setup as it requires no external libraries or complex build configurations.

### Data Persistence Format: CSV vs. XLSX

A key decision point involved selecting a data format for persistent storage (inventory, categories, transaction logs), evaluating CSV and XLSX.

*   **Consideration for XLSX:** XLSX offers features like multiple sheets and rich formatting, which could be convenient for manual data viewing and organization in spreadsheet software.
*   **Rationale for Choosing CSV:** We decided to continue using CSV due to its advantages for programmatic handling:
    *   **Simplicity and Parsability:** CSV is a plain text format that is easy to read and write using standard Java I/O streams. Parsing its delimited structure is straightforward compared to the complex binary format of XLSX, which necessitates external libraries (e.g., Apache POI).
    *   **Interoperability:** CSV is universally compatible with virtually all spreadsheet programs, text editors, and data processing tools.
    *   **Performance:** For our application's data volume, CSV provides efficient read/write performance with minimal overhead.
*   **Implemented CSV Structure:** To consolidate data while retaining CSV's simplicity, we implemented a single structured `inventory_data.csv` file. This file stores categories, items, and logs using distinct prefixes (`CATEGORY,`, `ITEM,`, `LOG,`) on each line, allowing the `InventoryMgt` class to correctly parse the combined data from a single source. Transaction logs are also maintained in a separate `transaction_log.csv` file as handled by the backend.

### Adhering to Backend Separation

Throughout the GUI development, we maintained a strict separation between the presentation layer (`InventorySwingGUI.java`) and the core business logic (`InventoryMgt.java`, `Item.java`, etc.). The Swing GUI interacts with the existing backend methods for data operations rather than embedding or refactoring the core logic within the UI code. This design promotes modularity and maintainability.

## Path Forward: Implementing JavaFX with Visual Design Tools

While Swing provides a functional GUI, the benefit of using a visual design tool like JavaFX Scene Builder for UI development is recognized. Should we decide to transition to JavaFX in the future, the following steps would be necessary:

1.  **JavaFX SDK/Module Configuration:** Proper setup of the JavaFX SDK and ensuring its modules are accessible to the Java runtime is fundamental.
2.  **Build Tool Integration:** Configuring a build tool (Maven or Gradle) to correctly manage JavaFX dependencies and utilize JavaFX-specific plugins for execution and packaging is crucial for a streamlined development workflow.
3.  **IDE Environment Setup:** Configuring the IDE (VS Code or a dedicated Java IDE) to correctly recognize and support JavaFX projects and integrate with the chosen build tool is essential for development productivity.
4.  **Adopting FXML and Scene Builder:** This involves using Scene Builder to visually design the UI and save it as FXML files. Java code then loads these FXML files using `FXMLLoader`. A separate Java controller class is linked to the FXML to handle UI events and logic.
5.  **Connecting UI Controllers to Backend:** JavaFX controller classes would call methods on the existing `InventoryMgt` instance to perform inventory operations, maintaining the separation between UI control and business logic.

Implementing JavaFX with FXML and Scene Builder offers a model-view-controller (MVC) like separation, distinguishing UI layout (FXML) from UI logic (Java controllers) and business logic (backend classes). While it requires initial setup and adherence to this pattern, it significantly aids visual design workflows.

---

## Error: "Error removing item: Amount cannot be negative"

### Description

When attempting to remove an item from the inventory, the application would display an error message stating, "Error removing item: Amount cannot be negative." This issue prevented successful removal of inventory items.

### Root Cause Analysis

The problem stemmed from an incorrect interaction between the `InventoryMgt` class and the `Category` class during item removal.

1.  **`InventoryMgt.removeItemByNumber()`**: This method was designed to remove an item from the `inventoryItems` list. As part of this process, it correctly intended to update the associated category's quantity by decrementing it by the removed item's quantity. It attempted this by calling `updateCategoryQuantity(item.getItemCategory(), -item.getItemQuantity())`.
2.  **`InventoryMgt.updateCategoryQuantity()`**: This method, in turn, called `category.increaseQuantity(quantityChange)`.
3.  **`Category.increaseQuantity()`**: The `increaseQuantity` method in the `Category` class includes a validation check: `if (amount < 0) { throw new IllegalArgumentException("Amount cannot be negative"); }`. Since `removeItemByNumber` passed a *negative* value (e.g., -1 for an item with quantity 1) to `updateCategoryQuantity`, which then passed it to `increaseQuantity`, this validation check was triggered, leading to the `IllegalArgumentException` and the observed error message.

Essentially, the code was trying to "increase" a category's quantity by a negative amount, which was explicitly disallowed by the `Category` class's design, rather than correctly "decreasing" it.

### Resolution

The fix involved modifying the `updateCategoryQuantity` method in `InventoryMgt.java` to use the appropriate method from the `Category` class based on the `quantityChange`:

```java
public void updateCategoryQuantity(String categoryName, int quantityChange) {
    // ... existing code ...
    Category category = findCategoryByName(categoryName);
    // ... existing code ...

    if (quantityChange > 0) {
        category.increaseQuantity(quantityChange);
    } else if (quantityChange < 0) {
        category.decreaseQuantity(Math.abs(quantityChange)); // Use decreaseQuantity for negative changes
    }
}
```

By checking if `quantityChange` is negative, the `updateCategoryQuantity` method now correctly invokes `category.decreaseQuantity(Math.abs(quantityChange))`. The `decreaseQuantity` method in `Category.java` is designed to handle decrements and also ensures that the category quantity does not fall below zero (`this.categoryQuantity = Math.max(0, this.categoryQuantity - amount);`), thus preventing the `IllegalArgumentException` and allowing items to be removed successfully.

### UI Theming Conflicts with Nimbus Look and Feel

**Issue:**

During the implementation of a dark UI theme, directly setting `setBackground()` and `setForeground()` on individual Swing components (`JPanel`, `JTextArea`, `JTable`, `JTextField`, etc.) resulted in an inconsistent and visually unappealing interface. The custom colors clashed with the default rendering logic of the applied "Nimbus" Look and Feel (L&F), leading to visual artifacts such as mismatched borders, inconsistent component appearances, and a general lack of cohesion. For example, some components would display a dark background with light text, but their borders or other graphical elements, still drawn by Nimbus's default dark styling, would create jarring contrasts.

**Resolution:**

The issue was resolved by discontinuing direct color assignments to individual components and instead configuring the "Nimbus" L&F itself through `UIManager.put()` properties. This approach allows Nimbus to internally use the desired dark color palette when rendering all UI elements, ensuring a unified and consistent appearance. By setting properties like `UIManager.put("control", new Color(60, 63, 65))` or `UIManager.put("text", new Color(240, 240, 240))`, the L&F now consistently applies the dark theme across all components, including their borders, highlights, and other graphical attributes, thereby eliminating the visual conflicts experienced previously.

## Absolute Data Path Portability Issue

### Description

Previously, the application stored the absolute file path to `inventory_data.csv` within the `inventory_data.csv` file itself. This caused significant issues when the application was moved or shared with other developers, as their systems would not have the identical file path. This resulted in the application failing to load data and consequently not running correctly on different machines.

### Root Cause Analysis

The `Availability.java` class was designed to write and read a `DATA_PATH` section within `inventory_data.csv`. When `InventoryMgt.java` loaded this path, it would attempt to use it directly. If this absolute path did not exist on a different machine, the file operations would fail.

### Resolution

To resolve this, the application has been refactored to ensure the `inventory_data.csv` file is always located dynamically relative to the `.jar` file's execution directory. This was achieved by:

1.  **Removing `DATA_PATH` from `inventory_data.csv`:** The `writeAllDataToFile` method in `Availability.java` no longer writes the `DATA_PATH` section, ensuring absolute paths are not persisted.
2.  **Defaulting to Current Working Directory:** The `Availability` and `InventoryMgt` classes now default to looking for `inventory_data.csv` in `System.getProperty("user.dir") + File.separator + "inventory_data.csv"`. This means the application will automatically find the data file as long as it's placed in the same directory as the executable `.jar`.
3.  **Simplified Data Path Handling:** The `setDataFilePath` methods have been updated to reflect this dynamic approach, ensuring consistency. The UI's "Set Data Folder Path" option now updates the working directory for data storage, maintaining user control while ensuring portability.

This change significantly improves the application's portability and compatibility across different operating systems and directory structures, resolving the issue for co-developers.

## JNI Error / Generic Java Exception on Startup

### Description

Users have reported a generic "Java Exception has occurred" or "A JNI error has occurred" message from the Java Virtual Machine Launcher when attempting to run the `TigerMotorhubApp.jar` file. This error prevents the application from launching and provides no specific details about the underlying cause.

### Root Cause Analysis

These types of errors are typically very broad and can stem from various sources, often unrelated to direct application code syntax. Common causes include:
*   **Corrupted Java Runtime Environment (JRE) / Java Development Kit (JDK) installation:** Missing or corrupted core Java libraries.
*   **Incompatible Java versions:** Running the JAR with a JVM that is too old or has significant differences from the one it was compiled with.
*   **Incorrect JAR execution:** Issues with how the `java -jar` command is invoked, or problems with the `MANIFEST.MF` file within the JAR (though the current build process should prevent the latter).
*   **Underlying unhandled Java exceptions:** A `NullPointerException` or `FileNotFoundException` (though efforts have been made to address file pathing) occurring very early in the application startup before the GUI is fully initialized and can display a more specific error dialog.

**Note:** Without a full stack trace from the console (obtained by running `java -jar TigerMotorhubApp.jar` in a terminal), it is extremely difficult to diagnose the precise cause of this generic error. The stack trace provides crucial information about where in the code the exception originated.

### Resolution

To mitigate potential code-related causes for unhandled exceptions during startup, the following changes have been implemented:
*   **Removal of "Uncategorized" Category Logic:** Reduced complexity and potential edge cases by eliminating the automatic creation and management of a default "Uncategorized" category. (Detailed below)
*   **Intelligent Data File Handling:** Improved robustness of `inventory_data.csv` loading/creation when setting a new data folder path, reducing potential `FileNotFoundException` scenarios. (Detailed below)

However, if this error persists, the primary troubleshooting step is to obtain the full console output when running the JAR via `java -jar`, as this will provide the necessary stack trace for specific diagnosis.

## Removal of "Uncategorized" Category

### Description

Previously, the application automatically created and assigned items to an "Uncategorized" category if no specific category was provided. This behavior has been removed to simplify category management and give users more explicit control over item categorization.

### Resolution

All references and logic related to the "Uncategorized" category have been removed from `InventoryMgt.java` and `Availability.java`:
*   The `UNCATEGORIZED` constant was removed.
*   Logic that automatically added or filtered items/categories based on "Uncategorized" status was removed.
*   Items without an explicitly assigned category will now simply have an empty string (`""`) as their category, rather than being forced into a default category. Categories are now only managed if they have a quantity greater than zero.

## Intelligent Data File Handling for `inventory_data.csv`

### Description

When a user selects a new data folder path, the application previously might have inadvertently overwritten an existing `inventory_data.csv` or started with a blank inventory even if a data file was present in the chosen directory.

### Resolution

The `showSetDataFilePathDialog()` method in `InventorySwingGUI.java` has been enhanced to provide more intelligent handling of `inventory_data.csv` when a new data folder path is set:
*   **Check for Existing File:** The application now first checks if an `inventory_data.csv` file already exists in the newly selected folder.
*   **Prompt to Load:** If the file exists, the user is prompted with an option to load the existing data from that file. This prevents accidental data loss.
*   **Deferred Creation:** If no `inventory_data.csv` exists in the chosen folder (or if the user opts not to load existing data), the application will display a blank inventory for that path. The `inventory_data.csv` file will then only be created in that location upon the first subsequent save operation (e.g., adding a new item).

This ensures a more user-friendly and robust data management experience, giving users control over their data when changing storage locations.
