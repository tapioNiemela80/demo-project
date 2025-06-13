package tn.demo.project.controller;

import java.util.UUID;

public record ProjectsView(UUID id, String name, String description) {
}