package tn.demo.project.controller;

import java.util.UUID;

public record TaskViewDto(UUID id, String title, String description, boolean isCompleted, TimeEstimation timeEstimation) {
}