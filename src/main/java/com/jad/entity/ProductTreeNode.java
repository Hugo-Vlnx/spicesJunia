package com.jad.entity;
import com.jad.entity.OperationType;
import com.jad.entity.Product;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ProductTreeNode {
    @Setter
    private Product product;
    @Setter
    private OperationType operation;
    @Setter
    private Float percentage; //ca c la variable du pourcentage du produit parent
    private List<ProductTreeNode> components; // ici liste avec ingredients les enfants en gros


    public ProductTreeNode() {
        this.components = new ArrayList<>();
    }



    public Product getProduct() {
        return this.product;
    }

    public OperationType getOperation() {
        return this.operation;
    }

    public Float getPercentage() {
        return this.percentage;
    }

    public List<ProductTreeNode> getComponents() { return
            this.components;
    }


    public void addComponent(ProductTreeNode child) {
        this.components.add(child);
    }
}