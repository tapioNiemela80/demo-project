package tn.demo.team.controller;

import java.util.UUID;

public record TeamTaskViewDto(UUID id, String title, String description, UUID projectTaskId, String status, ActualSpentTime actualSpentTime) {
}
