import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productId;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;

    public Product(String productId, String name, String description,
                   double price, int quantity, String category) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    // Getters
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getCategory() { return category; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastModified() { return lastModified; }

    // Setters
    public void setName(String name) {
        this.name = name;
        updateModifiedTime();
    }

    public void setDescription(String description) {
        this.description = description;
        updateModifiedTime();
    }

    public void setPrice(double price) {
        this.price = price;
        updateModifiedTime();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateModifiedTime();
    }

    public void setCategory(String category) {
        this.category = category;
        updateModifiedTime();
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
        updateModifiedTime();
    }

    public void reduceQuantity(int amount) {
        this.quantity -= amount;
        updateModifiedTime();
    }

    private void updateModifiedTime() {
        this.lastModified = LocalDateTime.now();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
                "Product ID: %s\n" +
                        "Name: %s\n" +
                        "Description: %s\n" +
                        "Price: $%.2f\n" +
                        "Quantity: %d\n" +
                        "Category: %s\n" +
                        "Created: %s\n" +
                        "Last Modified: %s",
                productId, name, description, price, quantity, category,
                createdAt.format(formatter), lastModified.format(formatter)
        );
    }
}
