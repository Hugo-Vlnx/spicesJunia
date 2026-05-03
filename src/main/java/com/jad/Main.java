package com.jad;

import com.jad.connector.DBConnector;
import com.jad.entity.ProductTreeNode;
import com.jad.service.FabricatingDataService;
import com.jad.service.ProductionPlanService;
import com.jad.service.ProductionStep;
import com.jad.service.ScheduleGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {

        FabricatingDataService fab = new FabricatingDataService(DBConnector.getInstance());
        ProductTreeNode arbre = fab.getCompleteProductTree(98); // changer l'id (ici test avec la harissa)

        ProductionPlanService planService = new ProductionPlanService(DBConnector.getInstance());
        List<ProductionStep> plan = planService.computePlan(arbre, 100000f);// la c la quanrite

        for (ProductionStep step : plan) {
            System.out.println(step);
            System.out.println();
        }
        String json = ScheduleGenerator.generateScheduleJson(plan);
        System.out.println(json); // génère le json puis l'affiche dans la console
        try (FileWriter writer = new FileWriter("plan.json")) {
            writer.write(json);
            System.out.println("Le fichier plan.json a été généré");                    // crée un fichier plan.json et y écrit le json généré
        } catch (IOException e) {
            System.err.println("Erreur ecriture fichier : " + e.getMessage());
        }
        DBConnector.getInstance().disconnect();
    }
}


//        MachineToolService machineToolService = new MachineToolService(DBConnector.getInstance());
//        machineToolService.getAll().forEach(System.out::println);
//
//        OperationTypeService operationTypeService = new OperationTypeService(DBConnector.getInstance());
//        operationTypeService.getAll().forEach(System.out::println);
//        operationTypeService.getMachineToolsForOperationTypeId(1).forEach(System.out::println);
//
//        ProductService productService = new ProductService(DBConnector.getInstance());
//        productService.getAll().forEach(System.out::println);
//
//        ProductRecipeService productReportService = new ProductRecipeService(DBConnector.getInstance());
//        productReportService.getAll().forEach(System.out::println);
//
//        System.out.println(machineToolService.getById(1).toPrettyJson());
//        System.out.println(operationTypeService.getById(1).toPrettyJson());
//        System.out.println(productService.getById(1).toPrettyJson());
//        System.out.println(productReportService.getByIdProduct(200).toPrettyJson());





//        arbre.getProduct().getLabel()                       : nom de la recette
//        step.quantityNeeded(),                              : quantité à produire
//        step.ingredients()                                  : liste des ingrédients
//        step.ingredients().get(index).product().getLabel()  : ingrédient
//        step.ingredients().get(index).quantityNeeded()      : quantité ingrédient
//        step.operation().getLabel()                         : opération
