package store.products;

public interface StockManageable {
    int getStock();
    boolean increaseStock(int amount);
    boolean decreaseStock(int amount);
}
