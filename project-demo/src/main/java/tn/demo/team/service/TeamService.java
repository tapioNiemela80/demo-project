package tn.demo.team.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.demo.common.IDService;
import tn.demo.project.domain.ProjectTaskId;
import tn.demo.project.domain.ProjectTaskSnapshot;
import tn.demo.project.domain.UnknownProjectTaskIdException;
import tn.demo.project.repository.ProjectRepository;
import tn.demo.team.domain.*;
import tn.demo.team.events.TeamTaskCompletedEvent;
import tn.demo.team.repository.TeamRepository;

@Service
public class TeamService {
    private final TeamRepository teams;
    private final ProjectRepository projects;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final IDService IDService;

    public TeamService(TeamRepository teams, ProjectRepository projects, ApplicationEventPublisher applicationEventPublisher, IDService IDService) {
        this.teams = teams;
        this.projects = projects;
        this.applicationEventPublisher = applicationEventPublisher;
        this.IDService = IDService;
    }

    @Transactional
    public TeamId createNew(String name){
        TeamId teamId = IDService.newTeamId();
        teams.save(Team.createNew(teamId, name));
        return teamId;
    }

    @Transactional
    public TeamMemberId addMember(TeamId teamId, String name, String profession){
        TeamMemberId memberId = IDService.newTeamMemberId();
        return teams.findById(teamId.value())
                .map(team -> team.addMember(memberId, name, profession))
                .map(team -> teams.save(team))
                .map(ignored -> memberId)
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
    }

    @Transactional
    public TeamTaskId addTask(TeamId teamId, ProjectTaskId projectTaskId){
        boolean alreadyBelongsToSomeTeam = checkIfAlreadyBelongsToSomeTeam(projectTaskId);
        if(alreadyBelongsToSomeTeam){
            throw new TaskAlreadyAssignedException("Task is already assigned to some team");
        }
        ProjectTaskSnapshot projectTaskSnapshot = projects.findByTaskId(projectTaskId.value())
                .flatMap(project -> project.getTask(projectTaskId))
                .orElseThrow(() -> new UnknownProjectTaskIdException(projectTaskId));
        Team team = teams.findById(teamId.value()).orElseThrow(() -> new UnknownTeamIdException(teamId));
        TeamTaskId teamTaskId = IDService.newTeamTaskId();
        Team teamWithAddedTask = team.addTask(teamTaskId, projectTaskSnapshot.projectTaskId(), projectTaskSnapshot.title(), projectTaskSnapshot.description());
        teams.save(teamWithAddedTask);
        return teamTaskId;
    }

    private boolean checkIfAlreadyBelongsToSomeTeam(ProjectTaskId originalTaskId) {
        return teams.findByOriginalProjectTaskId(originalTaskId.value())
                .map(team -> true)
                .orElse(false);
    }
    @Transactional
    public void assignTask(TeamId teamId, TeamTaskId taskID, TeamMemberId toMemberId){
        teams.findById(teamId.value())
                .map(team -> team.assignTask(taskID, toMemberId))
                .map(team -> teams.save(team))
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
    }

    @Transactional
    public void markTaskInProgress(TeamId teamId, TeamTaskId taskID){
        teams.findById(teamId.value())
                .map(team -> team.markTaskInProgress(taskID))
                .map(team -> teams.save(team))
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
    }

    @Transactional
    public void unassignTask(TeamId teamId, TeamTaskId taskID){
        teams.findById(teamId.value())
                .map(team -> team.markTaskUnassigned(taskID))
                .map(team -> teams.save(team))
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
    }

    @Transactional
    public void removeTask(TeamId teamId, TeamTaskId taskID){
        teams.findById(teamId.value())
                .map(team -> team.removeTask(taskID))
                .map(team -> teams.save(team))
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
    }

    @Transactional
    public void completeTask(TeamId teamId, TeamTaskId taskID){
        teams.findById(teamId.value())
                .map(team -> team.markTaskCompleted(taskID))
                .map(team -> teams.save(team))
                .map(team -> publishTaskCompletedEvent(taskID, team))
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
    }

    private Team publishTaskCompletedEvent(TeamTaskId taskID, Team team) {
        team.getOriginalTaskId(taskID)
                .ifPresent(projectTaskId -> applicationEventPublisher.publishEvent(new TeamTaskCompletedEvent(taskID, projectTaskId)));
        return team;
    }

}
