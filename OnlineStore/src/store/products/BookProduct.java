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
        this.author = author;
        this.pages = pages;

    }



    @Override
    public String toString() {
        final String YELLOW = "\u001B[33m";
        final String RESET = "\u001B[0m";

        return YELLOW + "A Book Product " + RESET + super.toString() + ", Author: " + author + ", Pages: " + pages;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o instanceof BookProduct) {
            BookProduct bookProduct = (BookProduct) o;
            return super.equals(bookProduct) && author.equals(bookProduct.getAuthor())
                    && pages == bookProduct.pages;
        }
         return false;
    }




}
