package store.engine;

import store.cart.Cart;
import store.products.Product;
import store.order.Order;
import store.core.Customer;
import java.util.List;

public class StoreEngine {


    private List<Product> products;
    private List<Order> allOrders;
    private List<Customer> customers;

    private static int nextOrderId = 0; //uniq ID number for every order

    public void addProduct(Product p) {

    }

    public List<Product> getAvailableProducts(){
        return products;
    }


    public boolean registerCustomer(Customer c) {
        return false;
    }

    public Order createOrderFromCart(Cart cart) {
        return null;
    }
}
