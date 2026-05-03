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
        // Groupe les étapes par machine
        Map<Integer, MachineScheduleDTO> scheduleByMachine = new HashMap<>();

        int orderNumber = 0;
        for (ProductionStep step : steps) {
            // Saute les matières premières
            if (step.isRawMaterial()) {
                continue;
            }

            // Récupère l'ID de la machine
            int machineId = step.machine() != null ? step.machine().getId() : -1;

            // Crée l'ordre
            OrderDTO order = new OrderDTO(
                    orderNumber,
                    step.product().getId(),
                    step.quantityNeeded()
            );

            // Ajoute à la machine correspondante
            scheduleByMachine.putIfAbsent(machineId, new MachineScheduleDTO(machineId));
            scheduleByMachine.get(machineId).orders.add(order);

            orderNumber++;
        }

        // Numéroter les ordres dans chaque machine à partir de 0
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
