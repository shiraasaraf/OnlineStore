/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.engine;
import java.util.ArrayList;
import store.cart.Cart;
import store.cart.CartItem;
import store.products.Product;
import store.order.Order;
import store.core.Customer;
import java.util.List;

public class StoreEngine {


    private static StoreEngine instance = null;

    private List<Product> products;
    private List<Order> allOrders;
    private List<Customer> customers;

    private static int nextOrderId = 0; //uniq ID number for every order

    private StoreEngine() {
        this.products = new ArrayList<>();
        this.allOrders = new ArrayList<>();
        this.customers = new ArrayList<>();

    }

    public static StoreEngine getInstance() {
        if (instance == null) {
            instance = new StoreEngine();
        }
        return instance;
    }


    public void addProduct(Product p) {

        products.add(p);
    }

    public List<Product> getAvailableProducts() {

        List<Product> available = new ArrayList<>();
        for (Product p : products) {
            if (p.getStock() > 0) {  // מניח ש-Product מכיל שדה stock
                available.add(p);
            }
        }
            return products;


    }




    public boolean registerCustomer(Customer c) {


        for (Customer customer : customers) {
            if (c.getUsername().equals(customer.getUsername())){
                return false;
            }
        }

        customers.add(c);
        return true;
    }

    public Order createOrderFromCart(Cart cart) {

        //Order newOrder = new Order(nextOrderId+1, )
        return null;
    }


}