package com.jad.service;

import com.jad.connector.DBConnector;
import com.jad.entity.ProductTreeNode;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RawMaterialsService {

    public Map<String, Float> getRawMaterials(final ProductTreeNode root, final float quantiteVoulue) throws SQLException {
        final Map<String, Float> result = new LinkedHashMap<>();
        //On réutilise ProductionPlanService qui gère les pertes et le parcours
        final ProductionPlanService planService = new ProductionPlanService(DBConnector.getInstance());
        final List<ProductionStep> steps = planService.computePlan(root, quantiteVoulue);

        for (final ProductionStep step : steps) {
            if (step.isRawMaterial()) {
                result.merge(step.product().getLabel(), step.quantityNeeded(), Float::sum);
            }
        }
        return result;
    }

    public void print(final String recipeLabel, final Map<String, Float> rawMaterials) {
        System.out.println("=== Recette : " + recipeLabel + " ===");
        rawMaterials.forEach((label, qty) ->
                System.out.printf("  - %-30s : %.3f unités%n", label, qty));
    }
}