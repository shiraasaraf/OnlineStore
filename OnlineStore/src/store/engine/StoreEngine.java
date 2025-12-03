/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

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

    private static StoreEngine instance = new StoreEngine();

    private StoreEngine() { } // private ctor — אי אפשר new מבחוץ

    //singleton
    //todo: check implementaion
    public static StoreEngine getInstance() {
        return instance;
    }

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
