import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionManager {
    private List<Transaction> transactions;
    private static final String TRANSACTION_FILE = "transactions.dat";

    public TransactionManager() {
        this.transactions = new ArrayList<>();
        loadFromFile();
    }

    public void recordTransaction(Transaction transaction) {
        transactions.add(transaction);
        saveToFile();
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getTransactionsByProduct(String productId) {
        return transactions.stream()
                .filter(t -> t.getProductId().equals(productId))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByType(TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactions.stream()
                .filter(t -> !t.getTimestamp().isBefore(start) &&
                        !t.getTimestamp().isAfter(end))
                .collect(Collectors.toList());
    }

    public double getTotalRevenue() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.SALE)
                .mapToDouble(Transaction::getTotalAmount)
                .sum();
    }

    public double getTotalPurchases() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.PURCHASE)
                .mapToDouble(Transaction::getTotalAmount)
                .sum();
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(TRANSACTION_FILE))) {
            oos.writeObject(transactions);
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        File file = new File(TRANSACTION_FILE);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(TRANSACTION_FILE))) {
            transactions = (List<Transaction>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
            transactions = new ArrayList<>();
        }
    }

    public void displayAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("          ALL TRANSACTIONS");
        System.out.println("========================================");
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
        System.out.println("========================================");
    }

    public void displayRecentTransactions(int count) {
        int size = transactions.size();
        int fromIndex = Math.max(0, size - count);
        List<Transaction> recentTransactions = new ArrayList<>(
                transactions.subList(fromIndex, size));

        if (recentTransactions.isEmpty()) {
            System.out.println("No transactions recorded.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("   RECENT TRANSACTIONS (Last " + count + ")");
        System.out.println("========================================");
        for (Transaction transaction : recentTransactions) {
            System.out.println(transaction);
        }
        System.out.println("========================================");
    }
}
