package com.jad.service;

import com.jad.entity.MachineTool;
import com.jad.entity.OperationType;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineSelector {

    //Dico pour mémoriser quand chaque machine a fini son travail
    private static Map<Integer, Long> machineAvailability = new HashMap<>();

    private static long getTotalTime(MachineTool machine, OperationType operation, float quantity) {
        long installTime = timeToSeconds(machine.getInstallationDuration());
        long cleaningTime = timeToSeconds(machine.getCleaningDuration());
        long operationTime = timeToSeconds(operation.getDuration());

        // utilisation  Math.ceil pour arrondir au lot sup
        int numberOfBatches = (int) Math.ceil(quantity / machine.getMaxQuantity());

        // Le temps total inclut l'installation, le nettoyage et le temps d'opération pour chaque lot
        return (installTime + operationTime + cleaningTime) * numberOfBatches;
    }

    private static long timeToSeconds(LocalTime time) {
        return time.toSecondOfDay();
    }

    // singe pour trouver la machine la plus rapide globalement
    public static MachineTool selectBestMachine(List<MachineTool> machines, OperationType operation, float quantity) {
        if (machines == null || machines.isEmpty() || operation == null) {
            return null;
        }

        MachineTool bestMachine = null;
        long earliestFinishTime = Long.MAX_VALUE;

        for (MachineTool machine : machines) {
            //calcul du temps que prendra cette tâche spécifique
            long taskDuration = getTotalTime(machine, operation, quantity);

            // récupére du temps déjà occupé par la machine
            long availableFrom = machineAvailability.getOrDefault(machine.getId(), 0L);

            //heure de fin réelle = heure à laquelle elle est libre + durée de la tâche
            long finishTime = availableFrom + taskDuration;

            //  cherche la machine qui terminera le plus tôt au global
            if (finishTime < earliestFinishTime) {
                earliestFinishTime = finishTime;
                bestMachine = machine;
            }
        }

        // MAJ  le calendrier de la machine
        if (bestMachine != null) {
            machineAvailability.put(bestMachine.getId(), earliestFinishTime);
        }

        return bestMachine;
    }

    // méthode  pour remettre les compteurs à zéro
    public static void resetAvailability() {
        machineAvailability.clear();
    }
}
