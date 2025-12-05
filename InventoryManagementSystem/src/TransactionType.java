public enum TransactionType {
    PURCHASE("Purchase"),
    SALE("Sale"),
    RESTOCK("Restock"),
    ADJUSTMENT("Adjustment");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
