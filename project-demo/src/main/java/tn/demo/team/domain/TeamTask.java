package tn.demo.team.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;
import tn.demo.project.domain.ProjectTaskId;

import java.util.Objects;
import java.util.UUID;

@Table("team_tasks")
class TeamTask {
    @Id
    private UUID id;
    private UUID projectTaskId;
    private String name;
    private String description;
    private TeamTaskStatus status;
    private UUID assigneeId;

    @PersistenceCreator
    private TeamTask(UUID id, UUID projectTaskId, String name, String description, TeamTaskStatus status, UUID assigneeId){
        this.id = id;
        this.projectTaskId = projectTaskId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.assigneeId = assigneeId;
    }

    static TeamTask createNew(TeamTaskId id, ProjectTaskId projectTaskId, String name, String description){
        return new TeamTask(id.value(), projectTaskId.value(), name, description, TeamTaskStatus.NOT_ASSIGNED, null);
    }

    boolean canBeDeleted(){
        return status == TeamTaskStatus.NOT_ASSIGNED;
    }

    TeamTask assignTo(TeamMemberId assigneeId){
        if (this.status != TeamTaskStatus.NOT_ASSIGNED) {
            throw new TaskTransitionNotAllowedException("Task already assigned or in progress.");
        }
        return new TeamTask(id, projectTaskId, name, description, TeamTaskStatus.ASSIGNED, assigneeId.value());
    }

    TeamTask markInProgress(){
        if (this.status != TeamTaskStatus.ASSIGNED) {
            throw new TaskTransitionNotAllowedException("Task needs to be assigned before it can be put to in progress.");
        }
        return new TeamTask(id, projectTaskId, name, description, TeamTaskStatus.IN_PROGRESS, assigneeId);
    }

    TeamTask complete() {
        if (this.status != TeamTaskStatus.IN_PROGRESS) {
            throw new TaskTransitionNotAllowedException("task not in progress");
        }
        return new TeamTask(id, projectTaskId, name, description, TeamTaskStatus.COMPLETED, assigneeId);
    }

    TeamTask unassign() {
        if(this.status != TeamTaskStatus.ASSIGNED){
            throw new TaskTransitionNotAllowedException("Task is not assigned");
        }
        return new TeamTask(id, projectTaskId, name, description, TeamTaskStatus.NOT_ASSIGNED, null);
    }

    boolean hasId(TeamTaskId expected) {
        return id.equals(expected.value());
    }

    ProjectTaskId getOriginalTaskId() {
        return new ProjectTaskId(projectTaskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamTask other = (TeamTask) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
