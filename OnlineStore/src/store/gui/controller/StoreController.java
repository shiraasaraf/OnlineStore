package store.gui.controller;
import java.util.List;
import store.engine.*;
import store.core.*;
import store.products.*;


public class StoreController {

    private StoreEngine engine;
    private Customer customer;


    public StoreController(StoreEngine engine, Customer customer) {
        this.engine = engine;
        this.customer = customer;
    }

    public List<Product> getAvailableProducts() {
        return engine.getAvailableProducts();
    }

    public boolean addToCart(Product p, int quantity) {
        return customer.addToCart(p, quantity);
    }

   //TODO public List<Product> getCartProducts() {}

    //בהמשך לשקול אם נהיה עמוס לפצל-  להוסיף מחלקות נוספות בcontroller:
    //StoreController  אירועים, הפעלת מודל, עדכון וויוז
    //
    //CatalogController (אופציונלי) – אם נהיה גדול מדי
    //
    //IOController או FileActionsController (אופציונלי) – רק לשמור על סדר

}
