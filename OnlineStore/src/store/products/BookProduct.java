package store.products;

import java.awt.*;

public class BookProduct extends Product {

    //data members
    private String author;
    private int pages;

    //c'tor //todo: to write
    public BookProduct(String name, double price, int stock, String description, Category category,
                       Color color, String author, int pages){

        super(name, price, stock, description, category, color);

    }

    //todo: add toString method - adding the fact it is a book and it's parameters in addition to father information
}
