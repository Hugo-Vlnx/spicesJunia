package com.jad.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jad.dto.MachineScheduleDTO;
import com.jad.dto.OrderDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleGenerator {

    public static String generateScheduleJson(List<ProductionStep> steps) {
        // groupe les étapes par machine
        Map<Integer, MachineScheduleDTO> scheduleByMachine = new HashMap<>();

        for (ProductionStep step : steps) {
            // saute les matières premières
            if (step.isRawMaterial() || step.machine() == null) {
                continue;
            }

            int machineId = step.machine().getId();

            // ajoute la machine au dico si elle n'y est pas encore
            scheduleByMachine.putIfAbsent(machineId, new MachineScheduleDTO(machineId));

            //  var pour le découpage en lots
            float remainingQuantity = step.quantityNeeded();
            float maxCapacity = step.machine().getMaxQuantity();


            while (remainingQuantity > 0) {
                // on prend soit le reste exact soit la capa max de la machine
                float orderQuantity = Math.min(remainingQuantity, maxCapacity);

                // on crée l'ordre pour ce lot spécifique
                OrderDTO order = new OrderDTO(
                        0,
                        step.product().getId(),
                        orderQuantity
                );

                // on l'ajoute à la liste d'ordre de cette machine
                scheduleByMachine.get(machineId).orders.add(order);

                // on deduit la quantite qu'on vient de mettre dans cet ordre
                remainingQuantity -= orderQuantity;
            }
        }

        // numéroter les ordres dans chaque machine chronologiquement
        for (MachineScheduleDTO schedule : scheduleByMachine.values()) {
            for (int i = 0; i < schedule.orders.size(); i++) {
                schedule.orders.get(i).numOrder = i;
            }
        }

        // Convertit en JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<MachineScheduleDTO> schedules = new ArrayList<>(scheduleByMachine.values());
        return gson.toJson(schedules);
    }
}