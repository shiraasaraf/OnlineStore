package store.core;
import store.products.Product;
import store.cart.Cart;
import store.order.Order;
import java.util.List;


public class Costomer extends User{
    private Cart cart;
    private List<Order> orderHistory;

    public boolean addToCart(Product p, int quantity){
        //TODO return if we can add the product to the cart
        return false;
    }

    public boolean removeFromCart(Product p) {
        //TODO implement
        return false;
    }

    public boolean checkout(){
        //TODO implement
        return false;
    }

}
