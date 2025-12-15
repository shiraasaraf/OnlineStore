/**
 * Submitted by:
 * Tamar Nahum, ID 021983812
 * Shira Asaraf, ID 322218439
 *
 * This file is part of Assignment 1 in the course
 * Advanced Object-Oriented Programming - Java.
 */
package store.io;

import store.cart.*;
import store.core.*;
import store.engine.*;
import store.products.*;
import store.order.*;
import store.io.*;

import java.awt.Color;


public class Main {
    public static void main(String[] args) {

        // ---------- Creating sample products ----------

        Product book1 = new BookProduct(
                "Harry Potter",
                79.90,
                10,
                "Fantasy book",
                Category.BOOKS,
                Color.BLUE,
                "J. K. Rowling",
                500
        );

        //The exact same book – to check equals
        Product book2 = new BookProduct(
                "Harry Potter",
                79.90,
                10,
                "Fantasy book",
                Category.BOOKS,
                Color.BLUE,
                "J. K. Rowling",
                500
        );

        Product shirt = new ClothingProduct(
                "T-Shirt",
                49.90,
                20,
                "Cotton shirt",
                Category.CLOTHING,
                Color.RED,
                "M"
        );

        Product phone = new ElectronicsProduct(
                "Smartphone",
                1999.0,
                5,
                "Android phone",
                Category.ELECTRONICS,
                Color.BLACK,
                2,
                "Samsung"
        );

        // ---------- Print products (toString) ----------

        System.out.println("=== Products ===");

        System.out.println(book1);
        System.out.println(shirt);
        System.out.println(phone);

        // ----------  equals checking ----------

        System.out.println("\n=== equals tests ===");
        System.out.println("book1.equals(book2) = " + book1.equals(book2)); // supposed to be true
        System.out.println("book1.equals(shirt)  = " + book1.equals(shirt)); // supposed to be false


        // ---------- work with Cart ----------

        Cart cart = new Cart();

        System.out.println("\n=== Empty cart ===");
        System.out.println(cart);

        // add products to cart
        cart.addItem(book1, 1);
        cart.addItem(shirt, 2);
        cart.addItem(phone, 1);

        System.out.println("\n=== Cart after adding 3 products ===");
        System.out.println(cart);

        // Adding the same book again – needs to increase quantity in the same CartItem
        cart.addItem(book1, 3);
        System.out.println("\n=== Cart after adding book1 again (quantity should grow) ===");
        System.out.println(cart);

        // clear product from cart
        boolean removedShirt = cart.removeItem(shirt);
        System.out.println("\nRemoved shirt from cart? " + removedShirt);
        System.out.println("=== Cart after removing shirt ===");
        System.out.println(cart);

    }


}
