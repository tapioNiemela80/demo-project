package tn.demo.project.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.demo.project.domain.ProjectId;
import tn.demo.project.service.ProjectReadService;
import tn.demo.project.service.ProjectService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    private final ProjectReadService projectReadService;

    public ProjectController(ProjectService service, ProjectReadService projectReadService) {
        this.service = service;
        this.projectReadService = projectReadService;
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody ProjectInput projectInput) {
        UUID projectId = service.createProject(projectInput.name(), projectInput.description(), projectInput.estimatedEndDate(), projectInput.estimation(), projectInput.contactPersonInput()).value();
        return ResponseEntity.ok(projectId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectViewDto> getOne(@PathVariable UUID id) {
        return projectReadService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProjectsViewDto>> findAll() {
        return ResponseEntity.ok(projectReadService.findAll());
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<UUID> addTask(@PathVariable UUID id, @RequestBody TaskInput taskInput) {
        return ResponseEntity.ok(service.addTaskTo(new ProjectId(id), taskInput.name(), taskInput.description(), taskInput.estimation()).value());
    }

}