package com.jad.service;

import com.jad.entity.MachineTool;
import com.jad.entity.OperationType;

import java.time.LocalTime;
import java.util.List;


public class MachineSelector {

    // calcule le temps total pour utiliser une machine en secondes : tps_installation + tps_opé + tps_clean
    private static long getTotalTime(MachineTool machine, OperationType operation, float quantity) {
        long installTime = timeToSeconds(machine.getInstallationDuration());
        long cleaningTime = timeToSeconds(machine.getCleaningDuration());

        // durée de l'opération = (durée de l'opération) * (qté / capa max)
        long operationTime = timeToSeconds(operation.getDuration());
        long scaledOperationTime = (long) (operationTime * (quantity / machine.getMaxQuantity()));

        return installTime + scaledOperationTime + cleaningTime;
    }

    // conversion localTime en secondes car le format était en hh:mm:ss et c'était plus simple pour les comparaisons
    private static long timeToSeconds(LocalTime time) {
        return time.toSecondOfDay();
    }

    // singe pour trouver la machine la plus rapide
    public static MachineTool selectBestMachine(List<MachineTool> machines,
                                                OperationType operation,
                                                float quantity) {
        // Vérif
        if (machines == null || machines.isEmpty() || operation == null) {
            return null;
        }

        MachineTool bestMachine = null;
        long minTime = Long.MAX_VALUE;

        // Parcours toutes les machines
        for (MachineTool machine : machines) {

            // Calcule le temps pour cette machine
            long totalTime = getTotalTime(machine, operation, quantity);

            // Si c'est la meilleure jusqu'à présent on la garde
            if (totalTime < minTime) {
                minTime = totalTime;
                bestMachine = machine;
            }
        }

        return bestMachine;
    }

}
