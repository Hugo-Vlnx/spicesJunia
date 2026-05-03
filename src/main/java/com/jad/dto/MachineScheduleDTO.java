package com.jad.dto;
import java.util.ArrayList;
import java.util.List;

// emploi du temps d'une machine
public class MachineScheduleDTO {
    public int idMachineTool;
    public List<OrderDTO> orders;

    public MachineScheduleDTO(int idMachineTool) {
        this.idMachineTool = idMachineTool;
        this.orders = new ArrayList<>();
    }
}
