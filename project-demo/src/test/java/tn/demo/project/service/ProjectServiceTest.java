package tn.demo.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import tn.demo.common.DateService;
import tn.demo.common.IDService;
import tn.demo.project.controller.ContactPersonInput;
import tn.demo.project.domain.*;
import tn.demo.project.events.TaskAddedToProjectEvent;
import tn.demo.project.repository.ProjectRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projects;
    @Mock
    private DateService dateService;
    @Mock
    private IDService IDService;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private ProjectService underTest;

    @BeforeEach
    void setup(){
        underTest = new ProjectService(projects, dateService, IDService, applicationEventPublisher);
    }

    @Test
    void createsProject(){
        ProjectId id = new ProjectId(UUID.randomUUID());
        LocalDate endDate = LocalDate.of(2026,12,31);
        LocalDateTime now = LocalDateTime.of(2026,6,1,18,0,0);
        tn.demo.project.controller.TimeEstimation timeEstimation = new tn.demo.project.controller.TimeEstimation(100,0);
        when(IDService.newProjectId()).thenReturn(id);
        when(dateService.now()).thenReturn(now);
        Project mockProject = Mockito.mock(Project.class);
        when(mockProject.getId()).thenReturn(id.value());
        when(projects.save(argThat(newProjectWithId(id)))).thenReturn(mockProject);
        ContactPersonId contactPersonId = new ContactPersonId(UUID.randomUUID());
        when(IDService.newContactPersonId()).thenReturn(contactPersonId);
        ProjectId actual = underTest.createProject( "test project", "test description", endDate, timeEstimation, new ContactPersonInput("name", "email"));
        assertEquals(id, actual);
    }

    @Test
    void addsTaskTo(){
        ProjectId id = new ProjectId(UUID.randomUUID());
        Project project = mock(Project.class);
        String taskName = "taskName";
        String description = "task description";
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());
        when(IDService.newProjectTaskId()).thenReturn(taskId);
        when(projects.findById(id.value())).thenReturn(Optional.of(project));
        when(projects.save(project)).thenReturn(project);
        when(project.addTask(taskId, taskName, description, TimeEstimation.fromMinutes(5))).thenReturn(project);

        ProjectTaskId actualTaskId = underTest.addTaskTo(id, taskName, description, new tn.demo.project.controller.TimeEstimation(0,5));
        assertEquals(taskId, actualTaskId);

        verify(applicationEventPublisher).publishEvent(new TaskAddedToProjectEvent(id, taskId));
    }

    @Test
    void throwsExceptionWhenUnknownProjectId(){
        ProjectId id = new ProjectId(UUID.randomUUID());
        String taskName = "taskName";
        String description = "task description";
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());
        when(IDService.newProjectTaskId()).thenReturn(taskId);
        when(projects.findById(id.value())).thenReturn(Optional.empty());

        assertThrows(UnknownProjectIdException.class, () -> underTest.addTaskTo(id, taskName, description, new tn.demo.project.controller.TimeEstimation(0,5)));
    }

    private ArgumentMatcher<Project> newProjectWithId(ProjectId id) {
        return project -> project.isNew() && project.hasId(id);
    }

}