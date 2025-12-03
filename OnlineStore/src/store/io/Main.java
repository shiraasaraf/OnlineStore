/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 *
 * This file is part of Assignment 1 in the course
 * Advanced Object-Oriented Programming - Java.
 */


package store.io;

import store.cart.Cart;
import store.products.BookProduct;
import store.products.Category;
import store.products.*;
import store.order.Order;
import store.core.Customer;

import java.awt.*;
import java.awt.print.Book;

public class Main {
    public static void main(String[] args) {

        Product book = new BookProduct("Book", 179.0, 9, " description", Category.BOOKS,
                Color.darkGray, " author", 332);

        Product book2 = new BookProduct("Book", 179.0, 9, " description", Category.BOOKS,
                Color.darkGray, " author", 332);

        System.out.println(book);
        System.out.println(book2.equals(book));
        System.out.println(book2.equals(book2));


        ClothingProduct clothingProduct  = new ClothingProduct("Book", 179.0, 9, " description", Category.BOOKS,
                Color.darkGray, "M");

        ClothingProduct clothingProduct2  = new ClothingProduct("Book", 179.0, 9, " description", Category.BOOKS,
                Color.darkGray, "M");

        System.out.println(clothingProduct);
        System.out.println(clothingProduct.equals(clothingProduct));
        System.out.println(clothingProduct2.equals(clothingProduct));
    }


}
