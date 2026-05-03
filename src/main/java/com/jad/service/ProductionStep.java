package com.jad.service;

import com.jad.entity.MachineTool;
import com.jad.entity.OperationType;
import com.jad.entity.Product;

import java.util.List;

public record ProductionStep(
        Product product,
        OperationType operation,       // null si matière première (nœud atomique)
        MachineTool machine,           // la machine sélectionnée
        float quantityNeeded,          // quantité à produire / à approvisionner
        List<Ingredient> ingredients   // ingrédients nécessaires à cette étape (vide si matière première)
) {

    public boolean isRawMaterial() {
        return this.operation == null;
    }

    @Override
    public String toString() {
        if (this.isRawMaterial()) {
            return String.format("[Matière première] %-30s → %.3f unités à approvisionner",
                    this.product.getLabel(),
                    this.quantityNeeded);
        }

        StringBuilder sb = new StringBuilder();
        String machineLabel = this.machine != null ? this.machine.getLabel() : "AUCUNE";
        sb.append(String.format("[Opération : %-10s] %-30s → %.3f unités à produire | Machine: %s%n",
                this.operation.getLabel(),
                this.product.getLabel(),
                this.quantityNeeded,
                machineLabel));

        for (Ingredient ing : this.ingredients) {
            sb.append(String.format("    - %s%n", ing));
        }

        return sb.toString().stripTrailing();
    }

    public record Ingredient(
            Product product,
            float quantityNeeded
    ) {
        @Override
        public String toString() {
            return String.format("%-30s : %.3f unités", this.product.getLabel(), this.quantityNeeded);
        }
    }
}
