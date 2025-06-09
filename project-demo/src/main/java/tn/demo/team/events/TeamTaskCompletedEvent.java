package tn.demo.team.events;

import org.springframework.context.ApplicationEvent;
import tn.demo.project.domain.ProjectTaskId;
import tn.demo.team.domain.TeamTaskId;

public class TeamTaskCompletedEvent extends ApplicationEvent {
    private final TeamTaskId taskID;
    private final ProjectTaskId projectTaskId;
    public TeamTaskCompletedEvent(TeamTaskId taskID, ProjectTaskId projectTaskId) {
        super(taskID);
        this.taskID = taskID;
        this.projectTaskId = projectTaskId;
    }
    public ProjectTaskId getProjectTaskId() {
        return projectTaskId;
    }
}
