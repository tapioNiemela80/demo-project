package tn.demo.team.events;

import org.springframework.context.ApplicationEvent;
import tn.demo.common.domain.ActualSpentTime;
import tn.demo.project.domain.ProjectTaskId;
import tn.demo.team.domain.TeamTaskId;

public class TeamTaskCompletedEvent extends ApplicationEvent {
    private final TeamTaskId taskID;
    private final ProjectTaskId projectTaskId;
    private final ActualSpentTime actualSpentTime;
    public TeamTaskCompletedEvent(TeamTaskId taskID, ProjectTaskId projectTaskId, ActualSpentTime actualSpentTime) {
        super(taskID);
        this.taskID = taskID;
        this.projectTaskId = projectTaskId;
        this.actualSpentTime = actualSpentTime;
    }
    public ProjectTaskId getProjectTaskId() {
        return projectTaskId;
    }

    public ActualSpentTime getActualSpentTime() {
        return actualSpentTime;
    }
}
