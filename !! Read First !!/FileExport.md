
## `inventory_data.csv` Structure

The `inventory_data.csv` file is a single CSV file that stores all three types of data (Categories, Items, and Transaction Logs) in a structured format. Each line in the file represents a single record, prefixed by a type identifier to distinguish between different data entries.

The structure uses a comma (`,`) as the delimiter.

Here's the format for each record type:

1.  **Categories:**
    -   Prefix: `CATEGORY`
    -   Format: `CATEGORY,categoryName,categoryQuantity`
    -   Example: `CATEGORY,Electronics,15`

2.  **Items:**
    -   Prefix: `ITEM`
    -   Format: `ITEM,modelNumber,modelName,modelPrice,itemQuantity,itemCategory`
    -   Example: `ITEM,ENG-001,Engine Block,5500.00,1,Engines`
    -   *Note: `itemQuantity` is always stored as 1 as per the application's logic.* `modelPrice` is stored as a double.

3.  **Transaction Logs:**
    -   Prefix: `LOG`
    -   Format: `LOG,timestamp,action,modelName,modelNumber,quantity,category`
    -   Example: `LOG,2023-10-27 10:30:01,ADD,Brake Pad Set,BRK-P01,1,Brakes`
    -   *Note: The quantity here refers to the quantity involved in the specific transaction.* The category is included for context.

This combined format allows the `importData` and `exportData` methods in `InventoryMgt.java` to read and write all application data to and from a single, easily parsable file. 