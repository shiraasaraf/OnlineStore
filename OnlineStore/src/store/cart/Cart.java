/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.cart;
import store.products.Product;
import java.util.List;


public class Cart {

    private List<CartItem> items;

    public boolean addItem(Product p, int quantity){
        //TODO implements
        return false;

    }

    public boolean removeItem(Product p){
        //TODO implements
        return false;
    }

    public double calculateTotal(){
        //TODO implements
        return 0;
    }
    public void clear(){
        //TODO implements
    }

    //Checking if there are items in the cart. used for check-out in Customer
    public boolean isEmpty(){
        return items.isEmpty();
    }
}
