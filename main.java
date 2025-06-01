// import java.util.Scanner;

import javax.swing.SwingUtilities;

// public class Main {
//     public static void main(String[] args) {
//         Console.start();
//     }
// }

public class Main {
    public static void main(String[] args) {
        // Launch the Swing GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                InventorySwingGUI gui = new InventorySwingGUI();
                // Initial data load and display is handled within the InventorySwingGUI constructor
                // gui.inventoryManager.importData(InventoryMgt.DATA_FILE); // Redundant if done in constructor
                // gui.updateCategoryTree(); // Redundant if done in constructor
                // gui.updateItemTable(); // Redundant if done in constructor
            }
        });
    }
}


