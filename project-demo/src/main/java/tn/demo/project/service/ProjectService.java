package tn.demo.project.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.demo.common.DateService;
import tn.demo.common.IDService;
import tn.demo.project.controller.ContactPersonInput;
import tn.demo.project.domain.*;
import tn.demo.project.events.TaskAddedToProjectEvent;
import tn.demo.project.repository.ProjectRepository;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projects;
    private final DateService dateService;
    private final IDService IDService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ProjectService(ProjectRepository projects, DateService dateService, tn.demo.common.IDService IDService, ApplicationEventPublisher applicationEventPublisher) {
        this.projects = projects;
        this.dateService = dateService;
        this.IDService = IDService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public ProjectId createProject(String name, String description, LocalDate estimatedEndDate, tn.demo.project.controller.TimeEstimation estimation, ContactPersonInput contactPerson) {
        Project project = Project.createNew(IDService.newProjectId(), name, description, dateService.now(), estimatedEndDate, toEstimation(estimation), IDService.newContactPersonId(), contactPerson.name(), contactPerson.email());
        return new ProjectId(projects.save(project).getId());
    }

    @Transactional
    public ProjectTaskId addTaskTo(ProjectId projectId, String taskName, String description, tn.demo.project.controller.TimeEstimation estimation){
        ProjectTaskId taskId = IDService.newProjectTaskId();
        return projects.findById(projectId.value())
                .map(project -> project.addTask(taskId, taskName, description, toEstimation(estimation)))
                .map(projects::save)
                .map(project -> publishNewTaskAddedToProjectEvent(projectId, taskId))
                .orElseThrow(() -> new UnknownProjectIdException(projectId.value()));
    }

    private ProjectTaskId publishNewTaskAddedToProjectEvent(ProjectId projectId, ProjectTaskId taskId) {
        applicationEventPublisher.publishEvent(new TaskAddedToProjectEvent(projectId, taskId));
        return taskId;
    }

    private TimeEstimation toEstimation(tn.demo.project.controller.TimeEstimation estimation) {
        return new TimeEstimation(estimation.hours(), estimation.minutes());
    }

}