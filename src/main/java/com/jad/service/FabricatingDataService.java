package com.jad.service;

import com.jad.connector.DBConnector;
import com.jad.entity.*;

import java.sql.SQLException;
import java.util.List;

public class FabricatingDataService {
    private ProductService productService;
    private ProductRecipeService recipeService;
    private OperationTypeService operationTypeService;


    public FabricatingDataService(DBConnector dbConnector) {
        this.productService = new ProductService(dbConnector);
        this.recipeService = new ProductRecipeService(dbConnector);
        this.operationTypeService = new OperationTypeService(dbConnector);
    }


    public ProductTreeNode getCompleteProductTree(int productId) throws SQLException {
        // on cherche le produit ds la base de donnée
        Product product = productService.getById(productId);


        if (product == null) {
            return null;
        }

        // on creer le 1er noeud contenant le produit
        ProductTreeNode node = new ProductTreeNode();
        node.setProduct(product);

        //si product est atomique on retourne direct le noeud sinon  on cherche sa recette
        if (product.getIsAtomic() != null && product.getIsAtomic()) {
            // Pas de recette à chercher, on retourne juste le produit
            return node;
        }


        ProductRecipe recipe = recipeService.getByProduct(product);

        if (recipe != null) {
            // On récupère les détails de l'opération temps et % de pertes
            OperationType operation = operationTypeService.getById(recipe.getIdOperationType());
            node.setOperation(operation);

            // on parcours la recette
            List<RecipeLine> lines = recipe.getRecipeLines();
            for (int i = 0; i < lines.size(); i++) {
                RecipeLine currentLine = lines.get(i);

                //on rappelle notre fonction pour trouver les ingredient necessaires a ce produit et en construire leur noued
                ProductTreeNode childNode = this.getCompleteProductTree(currentLine.getIdComponent());

                // On indique le pourcentage de quantite dont il y en a besoin
                childNode.setPercentage(currentLine.getPercentage());

                // on le relie au produit parent
                node.addComponent(childNode);
            }
        }

        // On retourne l'arbre entièrement construit
        return node;
    }
}

// pour la personne qui s'occupe de l'algorithmie il va falloir recuperer le gros arbre que j ai cree a partir de ca
// il va falloir le parcourir cet abre pour creer une liste de l odre chronologique des operation avec les bonnes quantite
// en prenant en compte les qunatite de perte ect

// - Hugo