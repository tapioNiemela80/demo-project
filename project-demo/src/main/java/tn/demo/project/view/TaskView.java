package tn.demo.project.view;

import tn.demo.project.controller.TimeEstimation;

import java.util.UUID;

public record TaskView(UUID id, String title, String description, boolean isCompleted, TimeEstimation timeEstimation, ActualTimeSpent actualTimeSpent) { }
