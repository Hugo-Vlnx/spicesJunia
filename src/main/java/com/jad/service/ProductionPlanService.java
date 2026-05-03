package com.jad.service;

import com.jad.connector.DBConnector;
import com.jad.entity.OperationType;
import com.jad.entity.ProductTreeNode;
import com.jad.entity.MachineTool;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductionPlanService {

    // service pour récupérer les machines
    private OperationTypeService operationTypeService;

    public ProductionPlanService(DBConnector dbConnector) {
        this.operationTypeService = new OperationTypeService(dbConnector);
    }

    public List<ProductionStep> computePlan(final ProductTreeNode root, final float quantiteVoulue) throws SQLException {
        List<ProductionStep> steps = new ArrayList<>();
        this.compute(root, quantiteVoulue, steps);
        return steps;
    }

    private void compute(final ProductTreeNode node,
                         final float quantiteVoulue,
                         final List<ProductionStep> steps) throws SQLException {

        final OperationType op = node.getOperation();

        // On va considérer les pertes
        float quantiteAProduire = quantiteVoulue;
        if (op != null
                && op.getLossOfQuantity() != null
                && op.getLossOfQuantity() > 0) {
            // on évite une division par zéro si lossOfQuantity vaut 100
            if (op.getLossOfQuantity() >= 100) {
                throw new IllegalStateException(
                        "Perte de 100% ou plus sur l'opération \"" + op.getLabel() + "\" — impossible de produire.");
            }
            quantiteAProduire = quantiteVoulue / (1f - op.getLossOfQuantity() / 100f);
        }

        // Descente récursive sur les composants
        List<ProductionStep.Ingredient> ingredients = new ArrayList<>();
        for (final ProductTreeNode enfant : node.getComponents()) {
            final float quantiteComposant = quantiteAProduire * (enfant.getPercentage() / 100f);
            this.compute(enfant, quantiteComposant, steps);
            ingredients.add(new ProductionStep.Ingredient(enfant.getProduct(), quantiteComposant));
        }

        // sélectionne la meilleure machine si opé existe
        MachineTool machine = null;
        if (op != null && operationTypeService != null) {
            try {
                List<com.jad.entity.MachineTool> machines = operationTypeService.getMachineToolsForOperationTypeId(op.getId());
                machine = MachineSelector.selectBestMachine(machines, op, quantiteAProduire);
            } catch (Exception e) {
                System.err.println("Erreur lors de la sélection de machine: " + e.getMessage());
            }
        }


        // ajout de l'étape après ses enfants
        steps.add(new ProductionStep(node.getProduct(), op, machine, quantiteAProduire, ingredients));
    }
}
