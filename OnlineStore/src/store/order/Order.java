/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 */

package store.order;
import java.io.Serializable;
import java.util.List;
import store.cart.CartItem;
import store.core.Persistable;


public class Order implements Persistable{
    private int orderID;
    private List<CartItem> items;
    private double totalAmount;
    private OrderStatus status;


    public Order(List<CartItem> items, double totalAmount) {
        OrderStatus status = OrderStatus.NEW;
        //TODO
    }
    public void saveToFile(String path){
        //TODO
    }

    public boolean pay() {
        //TODO
        return false;
    }
    public boolean ship(){
        return false;
    }

    public boolean deliver(){
        return false;
    }

    //TODO equals toString
}
