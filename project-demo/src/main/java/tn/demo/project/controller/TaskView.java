package tn.demo.project.controller;

import tn.demo.team.controller.ActualSpentTime;

import java.util.UUID;

public record TaskView(UUID id, String title, String description, boolean isCompleted, TimeEstimation timeEstimation, ActualSpentTime actualSpentTime) { }
