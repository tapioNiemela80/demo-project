package tn.demo.team.view;

import tn.demo.team.controller.ActualSpentTime;

import java.util.UUID;

public record TaskView(
        UUID id,
        String name,
        String description,
        UUID projectTaskId,
        String status,
        UUID assigneeId,
        ActualSpentTime actualSpentTime
) {
    public boolean isCompleted() {
        return actualSpentTime != null;
    }
}