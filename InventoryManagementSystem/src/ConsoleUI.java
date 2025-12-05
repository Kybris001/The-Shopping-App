import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ConsoleUI {
    private final InventoryManager inventoryManager;
    private final TransactionManager transactionManager;
    private final ActivityLogger activityLogger;
    private final Scanner scanner;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.activityLogger = new ActivityLogger();
        this.transactionManager = new TransactionManager();
        this.inventoryManager = new InventoryManager(activityLogger, transactionManager);
    }

    public void start() {
        printHeader();
        activityLogger.logActivity("SYSTEM_START", "Application started");

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> handleProductManagement();
                case 2 -> handleTransactions();
                case 3 -> handleReports();
                case 4 -> handleActivityLogs();
                case 5 -> handleSearch();
                case 0 -> {
                    running = false;
                    shutdown();
                }
                default -> System.out.println("\nInvalid choice! Please try again.");
            }
        }
    }

    private void printHeader() {
        System.out.println("========================================");
        System.out.println("   INVENTORY MANAGEMENT SYSTEM");
        System.out.println("   Version 1.0");
        System.out.println("========================================\n");
    }

    private void displayMainMenu() {
        System.out.println("\n========================================");
        System.out.println("            MAIN MENU");
        System.out.println("========================================");
        System.out.println("1. Product Management");
        System.out.println("2. Transactions");
        System.out.println("3. Reports & Analytics");
        System.out.println("4. Activity Logs");
        System.out.println("5. Search & Filter");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    private void handleProductManagement() {
        boolean back = false;
        while (!back) {
            System.out.println("\n========================================");
            System.out.println("       PRODUCT MANAGEMENT");
            System.out.println("========================================");
            System.out.println("1. Add New Product");
            System.out.println("2. Update Product");
            System.out.println("3. Remove Product");
            System.out.println("4. View All Products");
            System.out.println("5. View Product Details");
            System.out.println("0. Back to Main Menu");
            System.out.println("========================================");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> addNewProduct();
                case 2 -> updateProduct();
                case 3 -> removeProduct();
                case 4 -> inventoryManager.displayInventory();
                case 5 -> viewProductDetails();
                case 0 -> back = true;
                default -> System.out.println("\nInvalid choice!");
            }
        }
    }

    private void addNewProduct() {
        System.out.println("\n========================================");
        System.out.println("         ADD NEW PRODUCT");
        System.out.println("========================================");

        String id = getStringInput("Product ID: ");
        String name = getStringInput("Product Name: ");
        String description = getStringInput("Description: ");
        double price = getDoubleInput("Price: $");
        int quantity = getIntInput("Initial Quantity: ");
        String category = getStringInput("Category: ");

        Product product = new Product(id, name, description, price, quantity, category);

        if (inventoryManager.addProduct(product)) {
            System.out.println("\n[SUCCESS] Product added successfully!");
        } else {
            System.out.println("\n[ERROR] Failed to add product!");
        }
    }

    private void updateProduct() {
        System.out.println("\n========================================");
        System.out.println("          UPDATE PRODUCT");
        System.out.println("========================================");
        String id = getStringInput("Enter Product ID: ");

        Product product = inventoryManager.getProduct(id);
        if (product == null) {
            System.out.println("\n[ERROR] Product not found!");
            return;
        }

        System.out.println("\nCurrent Product Details:");
        System.out.println("----------------------------------------");
        System.out.println(product);
        System.out.println("========================================");
        System.out.println("\nEnter new values (press Enter to keep current value):");

        // Clear buffer before reading nextLine-based optional inputs
        System.out.print(""); // no-op to keep structure
        Scanner lineScanner = new Scanner(System.in);

        System.out.print("Name [" + product.getName() + "]: ");
        String name = lineScanner.nextLine().trim();
        if (!name.isEmpty()) {
            product.setName(name);
        }

        System.out.print("Description [" + product.getDescription() + "]: ");
        String description = lineScanner.nextLine().trim();
        if (!description.isEmpty()) {
            product.setDescription(description);
        }

        System.out.print("Price [" + product.getPrice() + "]: $");
        String priceStr = lineScanner.nextLine().trim();
        if (!priceStr.isEmpty()) {
            try {
                double price = Double.parseDouble(priceStr);
                product.setPrice(price);
            } catch (NumberFormatException e) {
                System.out.println("Invalid price, keeping current value.");
            }
        }

        System.out.print("Quantity [" + product.getQuantity() + "]: ");
        String qtyStr = lineScanner.nextLine().trim();
        if (!qtyStr.isEmpty()) {
            try {
                int quantity = Integer.parseInt(qtyStr);
                product.setQuantity(quantity);
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity, keeping current value.");
            }
        }

        System.out.print("Category [" + product.getCategory() + "]: ");
        String category = lineScanner.nextLine().trim();
        if (!category.isEmpty()) {
            product.setCategory(category);
        }

        if (inventoryManager.updateProduct(product)) {
            System.out.println("\n[SUCCESS] Product updated successfully!");
        }
    }

    private void removeProduct() {
        System.out.println("\n========================================");
        System.out.println("          REMOVE PRODUCT");
        System.out.println("========================================");
        String id = getStringInput("Enter Product ID to remove: ");

        Product product = inventoryManager.getProduct(id);
        if (product == null) {
            System.out.println("\n[ERROR] Product not found!");
            return;
        }

        System.out.println("\nProduct to be removed:");
        System.out.println("----------------------------------------");
        System.out.println(product);
        System.out.println("========================================");

        String confirm = getStringInput("\nAre you sure you want to remove this product? (yes/no): ");

        if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
            if (inventoryManager.removeProduct(id)) {
                System.out.println("\n[SUCCESS] Product removed successfully!");
            }
        } else {
            System.out.println("\n[CANCELLED] Product removal cancelled.");
        }
    }

    private void viewProductDetails() {
        System.out.println("\n========================================");
        System.out.println("        VIEW PRODUCT DETAILS");
        System.out.println("========================================");
        String id = getStringInput("Enter Product ID: ");

        Product product = inventoryManager.getProduct(id);
        if (product == null) {
            System.out.println("\n[ERROR] Product not found!");
            return;
        }

        System.out.println("\n========================================");
        System.out.println(product);
        System.out.println("========================================");

        List<Transaction> transactions = transactionManager.getTransactionsByProduct(id);
        if (!transactions.isEmpty()) {
            System.out.println("\nTransaction History:");
            System.out.println("----------------------------------------");
            for (Transaction t : transactions) {
                System.out.println(t);
            }
        }
    }

    private void handleTransactions() {
        boolean back = false;
        while (!back) {
            System.out.println("\n========================================");
            System.out.println("            TRANSACTIONS");
            System.out.println("========================================");
            System.out.println("1. Purchase (Add Stock)");
            System.out.println("2. Sale (Reduce Stock)");
            System.out.println("3. Restock");
            System.out.println("4. View All Transactions");
            System.out.println("0. Back to Main Menu");
            System.out.println("========================================");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> purchaseProduct();
                case 2 -> sellProduct();
                case 3 -> restockProduct();
                case 4 -> transactionManager.displayAllTransactions();
                case 0 -> back = true;
                default -> System.out.println("\nInvalid choice!");
            }
        }
    }

    private void purchaseProduct() {
        System.out.println("\n=== Purchase Product ===");
        String id = getStringInput("Product ID: ");
        int quantity = getIntInput("Quantity to purchase: ");
        String supplier = getStringInput("Supplier: ");

        if (inventoryManager.purchaseProduct(id, quantity, supplier)) {
            System.out.println("\n[SUCCESS] Purchase recorded.");
        } else {
            System.out.println("\n[ERROR] Purchase failed.");
        }
    }

    private void sellProduct() {
        System.out.println("\n=== Sell Product ===");
        String id = getStringInput("Product ID: ");
        int quantity = getIntInput("Quantity to sell: ");

        if (inventoryManager.sellProduct(id, quantity)) {
            System.out.println("\n[SUCCESS] Sale recorded.");
        } else {
            System.out.println("\n[ERROR] Sale failed.");
        }
    }

    private void restockProduct() {
        System.out.println("\n=== Restock Product ===");
        String id = getStringInput("Product ID: ");
        int quantity = getIntInput("Quantity to restock: ");

        if (inventoryManager.restockProduct(id, quantity)) {
            System.out.println("\n[SUCCESS] Restock recorded.");
        } else {
            System.out.println("\n[ERROR] Restock failed.");
        }
    }

    private void handleReports() {
        boolean back = false;
        while (!back) {
            System.out.println("\n========================================");
            System.out.println("         REPORTS & ANALYTICS");
            System.out.println("========================================");
            System.out.println("1. Inventory Summary");
            System.out.println("2. Low Stock Report");
            System.out.println("3. Financial Summary");
            System.out.println("4. Recent Transactions (Last 10)");
            System.out.println("0. Back to Main Menu");
            System.out.println("========================================");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> showInventorySummary();
                case 2 -> showLowStockReport();
                case 3 -> showFinancialSummary();
                case 4 -> transactionManager.displayRecentTransactions(10);
                case 0 -> back = true;
                default -> System.out.println("\nInvalid choice!");
            }
        }
    }

    private void showInventorySummary() {
        System.out.println("\n=== Inventory Summary ===");
        inventoryManager.displayInventory();
        System.out.printf("Total products: %d%n", inventoryManager.getTotalProductCount());
        System.out.printf("Total stock units: %d%n", inventoryManager.getTotalStockCount());
        System.out.printf("Total inventory value: $%.2f%n", inventoryManager.getTotalInventoryValue());
    }

    private void showLowStockReport() {
        int threshold = getIntInput("Enter low-stock threshold: ");
        List<Product> lowStock = inventoryManager.getLowStockProducts(threshold);
        if (lowStock.isEmpty()) {
            System.out.println("No products at or below the threshold.");
            return;
        }
        System.out.println("\n=== Low Stock Products (<= " + threshold + ") ===");
        for (Product p : lowStock) {
            System.out.printf("%s - %s | Qty: %d%n", p.getProductId(), p.getName(), p.getQuantity());
        }
    }

    private void showFinancialSummary() {
        System.out.println("\n=== Financial Summary ===");
        System.out.printf("Total Purchases: $%.2f%n", transactionManager.getTotalPurchases());
        System.out.printf("Total Sales: $%.2f%n", transactionManager.getTotalRevenue());
    }

    private void handleActivityLogs() {
        boolean back = false;
        while (!back) {
            System.out.println("\n========================================");
            System.out.println("            ACTIVITY LOGS");
            System.out.println("========================================");
            System.out.println("1. View All Logs");
            System.out.println("2. View Recent Logs (Last 10)");
            System.out.println("0. Back to Main Menu");
            System.out.println("========================================");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> activityLogger.displayAllLogs();
                case 2 -> activityLogger.displayRecentLogs(10);
                case 0 -> back = true;
                default -> System.out.println("\nInvalid choice!");
            }
        }
    }

    private void handleSearch() {
        boolean back = false;
        while (!back) {
            System.out.println("\n========================================");
            System.out.println("             SEARCH & FILTER");
            System.out.println("========================================");
            System.out.println("1. Search Products by Keyword");
            System.out.println("2. Filter by Category");
            System.out.println("0. Back to Main Menu");
            System.out.println("========================================");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> {
                    String keyword = getStringInput("Enter keyword: ");
                    List<Product> results = inventoryManager.searchProducts(keyword);
                    if (results.isEmpty()) {
                        System.out.println("No products matched the keyword.");
                    } else {
                        System.out.println("\n=== Search Results ===");
                        for (Product p : results) {
                            System.out.printf("%s - %s | %s | $%.2f | Qty: %d%n",
                                    p.getProductId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity());
                        }
                    }
                }
                case 2 -> {
                    Set<String> categories = inventoryManager.getAllCategories();
                    if (categories.isEmpty()) {
                        System.out.println("No categories available.");
                        break;
                    }
                    System.out.println("Available categories: " + categories);
                    String category = getStringInput("Enter category: ");
                    List<Product> results = inventoryManager.getProductsByCategory(category);
                    if (results.isEmpty()) {
                        System.out.println("No products in this category.");
                    } else {
                        System.out.println("\n=== Category Results ===");
                        for (Product p : results) {
                            System.out.printf("%s - %s | $%.2f | Qty: %d%n",
                                    p.getProductId(), p.getName(), p.getPrice(), p.getQuantity());
                        }
                    }
                }
                case 0 -> back = true;
                default -> System.out.println("\nInvalid choice!");
            }
        }
    }

    // Helpers for safe input

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double val = Double.parseDouble(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        String s = scanner.nextLine();
        while (s == null || s.trim().isEmpty()) {
            System.out.print("Please enter a non-empty value: ");
            s = scanner.nextLine();
        }
        return s.trim();
    }

    private void shutdown() {
        System.out.println("\nShutting down...");
        activityLogger.logActivity("SYSTEM_SHUTDOWN", "Application closed");
        System.out.println("Goodbye!");
    }
}
