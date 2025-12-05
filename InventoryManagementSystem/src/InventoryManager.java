import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryManager {
    private Map<String, Product> products;
    private ActivityLogger activityLogger;
    private TransactionManager transactionManager;
    private static final String INVENTORY_FILE = "inventory.dat";

    public InventoryManager(ActivityLogger activityLogger,
                            TransactionManager transactionManager) {
        this.products = new HashMap<>();
        this.activityLogger = activityLogger;
        this.transactionManager = transactionManager;
        loadFromFile();
    }

    public boolean addProduct(Product product) {
        if (products.containsKey(product.getProductId())) {
            System.out.println("Product with ID " + product.getProductId() +
                    " already exists!");
            return false;
        }

        products.put(product.getProductId(), product);
        activityLogger.logActivity("ADD_PRODUCT",
                "Added product: " + product.getName() + " (ID: " +
                        product.getProductId() + ")");
        saveToFile();
        return true;
    }

    public boolean removeProduct(String productId) {
        Product product = products.remove(productId);
        if (product == null) {
            System.out.println("Product not found!");
            return false;
        }

        activityLogger.logActivity("REMOVE_PRODUCT",
                "Removed product: " + product.getName() + " (ID: " + productId + ")");
        saveToFile();
        return true;
    }

    public boolean updateProduct(Product updatedProduct) {
        if (!products.containsKey(updatedProduct.getProductId())) {
            System.out.println("Product not found!");
            return false;
        }

        products.put(updatedProduct.getProductId(), updatedProduct);
        activityLogger.logActivity("UPDATE_PRODUCT",
                "Updated product: " + updatedProduct.getName() +
                        " (ID: " + updatedProduct.getProductId() + ")");
        saveToFile();
        return true;
    }

    public Product getProduct(String productId) {
        return products.get(productId);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public List<Product> searchProducts(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return products.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerKeyword) ||
                        p.getDescription().toLowerCase().contains(lowerKeyword) ||
                        p.getCategory().toLowerCase().contains(lowerKeyword) ||
                        p.getProductId().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByCategory(String category) {
        return products.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Product> getLowStockProducts(int threshold) {
        return products.values().stream()
                .filter(p -> p.getQuantity() <= threshold)
                .collect(Collectors.toList());
    }

    public Set<String> getAllCategories() {
        return products.values().stream()
                .map(Product::getCategory)
                .collect(Collectors.toSet());
    }

    public boolean purchaseProduct(String productId, int quantity, String supplier) {
        Product product = products.get(productId);
        if (product == null) {
            System.out.println("Product not found!");
            return false;
        }

        product.addQuantity(quantity);
        Transaction transaction = new Transaction(productId, product.getName(),
                TransactionType.PURCHASE, quantity, product.getPrice());
        transactionManager.recordTransaction(transaction);

        activityLogger.logActivity("PURCHASE_PRODUCT",
                "Purchased " + quantity + " units of " + product.getName() +
                        " from " + supplier);
        saveToFile();
        return true;
    }

    public boolean sellProduct(String productId, int quantity) {
        Product product = products.get(productId);
        if (product == null) {
            System.out.println("Product not found!");
            return false;
        }

        if (product.getQuantity() < quantity) {
            System.out.println("Insufficient stock! Available: " +
                    product.getQuantity());
            return false;
        }

        product.reduceQuantity(quantity);
        Transaction transaction = new Transaction(productId, product.getName(),
                TransactionType.SALE, quantity, product.getPrice());
        transactionManager.recordTransaction(transaction);

        activityLogger.logActivity("SELL_PRODUCT",
                "Sold " + quantity + " units of " + product.getName());
        saveToFile();
        return true;
    }

    public boolean restockProduct(String productId, int quantity) {
        Product product = products.get(productId);
        if (product == null) {
            System.out.println("Product not found!");
            return false;
        }

        product.addQuantity(quantity);
        Transaction transaction = new Transaction(productId, product.getName(),
                TransactionType.RESTOCK, quantity, product.getPrice());
        transactionManager.recordTransaction(transaction);

        activityLogger.logActivity("RESTOCK_PRODUCT",
                "Restocked " + quantity + " units of " + product.getName());
        saveToFile();
        return true;
    }

    public void displayInventory() {
        if (products.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("         CURRENT INVENTORY");
        System.out.println("========================================");
        System.out.printf("%-10s %-25s %-15s %-12s %-10s%n",
                "ID", "Name", "Category", "Price", "Quantity");
        System.out.println("----------------------------------------");

        for (Product product : products.values()) {
            System.out.printf("%-10s %-25s %-15s $%-11.2f %-10d%n",
                    product.getProductId(),
                    truncate(product.getName(), 25),
                    truncate(product.getCategory(), 15),
                    product.getPrice(),
                    product.getQuantity());
        }
        System.out.println("========================================");
    }

    private String truncate(String str, int length) {
        if (str.length() > length) {
            return str.substring(0, length - 3) + "...";
        }
        return str;
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(INVENTORY_FILE))) {
            oos.writeObject(products);
        } catch (IOException e) {
            System.err.println("Error saving inventory: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        File file = new File(INVENTORY_FILE);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(INVENTORY_FILE))) {
            products = (Map<String, Product>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
            products = new HashMap<>();
        }
    }

    public double getTotalInventoryValue() {
        return products.values().stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
    }

    public int getTotalProductCount() {
        return products.size();
    }

    public int getTotalStockCount() {
        return products.values().stream()
                .mapToInt(Product::getQuantity)
                .sum();
    }
}
