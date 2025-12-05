import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private String transactionId;
    private String productId;
    private String productName;
    private TransactionType type;
    private int quantity;
    private double pricePerUnit;
    private double totalAmount;
    private LocalDateTime timestamp;

    public Transaction(String productId, String productName, TransactionType type,
                       int quantity, double pricePerUnit) {
        this.transactionId = UUID.randomUUID().toString().substring(0, 8);
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.totalAmount = quantity * pricePerUnit;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public TransactionType getType() { return type; }
    public int getQuantity() { return quantity; }
    public double getPricePerUnit() { return pricePerUnit; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
                "Transaction ID: %s | Type: %s | Product: %s (ID: %s) | " +
                        "Quantity: %d | Price/Unit: $%.2f | Total: $%.2f | Time: %s",
                transactionId, type.getDisplayName(), productName, productId,
                quantity, pricePerUnit, totalAmount, timestamp.format(formatter)
        );
    }
}
