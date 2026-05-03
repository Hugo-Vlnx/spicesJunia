package com.jad.dto;

// Ordre avec un numéro d'ordre, un id de produit et une quantité
public class OrderDTO {
    public int numOrder;
    public int idProduct;
    public float quantity;

    public OrderDTO(int numOrder, int idProduct, float quantity) {
        this.numOrder = numOrder;
        this.idProduct = idProduct;
        this.quantity = quantity;
    }
}
