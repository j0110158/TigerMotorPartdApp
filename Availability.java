import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

public class Availability {
    private String dataFilePath;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Availability() {
        this.dataFilePath = System.getProperty("user.dir") + File.separator + "inventory_data.csv";
        ensureDataFileExists();
    }

    public Availability(String dataFilePath) {
        this.dataFilePath = dataFilePath;
        ensureDataFileExists();
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
        ensureDataFileExists();
    }

    private void ensureDataFileExists() {
        File file = new File(dataFilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (PrintWriter writer = new PrintWriter(new FileWriter(dataFilePath, true))) {
                    writer.println("CATEGORY_DATA");
                    writer.println("ITEM_DATA");
                    writer.println("LOG_DATA");
                    writer.println("LOWSTOCK_DATA");
                    writer.println("DATA_PATH");
                }
            } catch (IOException e) {
                System.err.println("Error creating data file: " + e.getMessage());
            }
        }
    }

    public void writeAllDataToFile(List<Item> items, List<Category> categories, int lowStockThreshold, List<String> transactionLogs, String currentDataPath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dataFilePath, false))) {
            writer.println("CATEGORY_DATA");
            for (Category category : categories) {
                if (category.getCategoryQuantity() > 0 || category.getCategoryName().equals(InventoryMgt.UNCATEGORIZED)) {
                    writer.println("CATEGORY," + category.getCategoryName());
                }
            }

            writer.println("ITEM_DATA");
            for (Item item : items) {
                writer.println("ITEM," + item.getModelNumber() + "," + item.getModelName() + "," + item.getModelPrice() + "," + item.getItemQuantity() + "," + item.getItemCategory());
            }

            writer.println("LOG_DATA");
            for (String logEntry : transactionLogs) {
                writer.println(logEntry);
            }

            writer.println("LOWSTOCK_DATA");
            String timestamp = dateFormat.format(new Date());
            writer.println("LOWSTOCK," + timestamp + ",SET," + lowStockThreshold + ",,");

            writer.println("DATA_PATH");
            writer.println("PATH," + currentDataPath);

            System.out.println("All data exported successfully to " + dataFilePath);
        } catch (IOException e) {
            System.err.println("Error exporting all data to file: " + e.getMessage());
        }
    }

    public Map<String, Object> readAllDataFromFile() {
        List<Item> loadedItems = new ArrayList<>();
        List<Category> loadedCategories = new ArrayList<>();
        List<String> loadedLogs = new ArrayList<>();
        int loadedLowStockThreshold = 5;
        String loadedDataPath = System.getProperty("user.dir") + File.separator + "inventory_data.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFilePath))) {
            String line;
            String currentSection = "";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.endsWith("_DATA")) {
                    currentSection = line;
                    continue;
                }

                switch (currentSection) {
                    case "CATEGORY_DATA":
                        if (line.startsWith("CATEGORY,") && line.split(",").length >= 2) {
                            String categoryName = line.split(",")[1].trim();
                            addCategoryIfNotExist(loadedCategories, categoryName);
                        }
                        break;
                    case "ITEM_DATA":
                        if (line.startsWith("ITEM,") && line.split(",").length >= 6) {
                            try {
                                String[] parts = line.split(",");
                                String modelNumber = parts[1].trim();
                                String modelName = parts[2].trim();
                                double modelPrice = Double.parseDouble(parts[3].trim());
                                int itemQuantity = Integer.parseInt(parts[4].trim());
                                String itemCategory = parts[5].trim();
                                loadedItems.add(new Item(modelPrice, modelName, modelNumber, itemQuantity, itemCategory));
                                addOrUpdateCategory(loadedCategories, itemCategory, itemQuantity);
                            } catch (NumberFormatException e) {
                                System.err.println("Skipping invalid ITEM line (Number format error): " + line);
                            }
                        }
                        break;
                    case "LOG_DATA":
                        loadedLogs.add(line);
                        if (line.startsWith("LOWSTOCK,") && line.split(",").length >= 4) {
                            try {
                                loadedLowStockThreshold = Integer.parseInt(line.split(",")[3].trim());
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing LOWSTOCK threshold from log: " + line);
                            }
                        }
                        break;
                    case "LOWSTOCK_DATA":
                        if (line.startsWith("LOWSTOCK,") && line.split(",").length >= 4) {
                            try {
                                loadedLowStockThreshold = Integer.parseInt(line.split(",")[3].trim());
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing LOWSTOCK threshold: " + line);
                            }
                        }
                        break;
                    case "DATA_PATH":
                        if (line.startsWith("PATH,") && line.split(",").length >= 2) {
                            loadedDataPath = line.split(",")[1].trim();
                        }
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("No existing " + dataFilePath + " found or error reading file. Starting with default values.");
        }

        List<Category> filteredCategories = loadedCategories.stream()
                .filter(c -> c.getCategoryQuantity() > 0 || c.getCategoryName().equals(InventoryMgt.UNCATEGORIZED))
                .collect(Collectors.toList());

        Map<String, Object> allLoadedData = new HashMap<>();
        allLoadedData.put("items", loadedItems);
        allLoadedData.put("categories", filteredCategories);
        allLoadedData.put("logs", loadedLogs);
        allLoadedData.put("lowStockThreshold", loadedLowStockThreshold);
        allLoadedData.put("dataPath", loadedDataPath);

        return allLoadedData;
    }

    private void addOrUpdateCategory(List<Category> categories, String categoryName, int quantity) {
        boolean found = false;
        for (Category cat : categories) {
            if (cat.getCategoryName().equalsIgnoreCase(categoryName)) {
                cat.increaseQuantity(quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            categories.add(new Category(categoryName, quantity));
        }
    }

    private void addCategoryIfNotExist(List<Category> categories, String categoryName) {
        boolean found = false;
        for (Category cat : categories) {
            if (cat.getCategoryName().equalsIgnoreCase(categoryName)) {
                found = true;
                break;
            }
        }
        if (!found) {
            categories.add(new Category(categoryName, 0));
        }
    }
} 