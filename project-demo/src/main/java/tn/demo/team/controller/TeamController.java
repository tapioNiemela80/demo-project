package tn.demo.team.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.demo.project.domain.ProjectTaskId;
import tn.demo.team.domain.TeamId;
import tn.demo.team.domain.TeamMemberId;
import tn.demo.team.domain.TeamTaskId;
import tn.demo.team.view.TeamView;
import tn.demo.team.view.TeamViewService;
import tn.demo.team.service.TeamService;
import tn.demo.team.view.TeamsView;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;
    private final TeamViewService teamViewService;

    public TeamController(TeamService teamService, TeamViewService teamViewService) {
        this.teamService = teamService;
        this.teamViewService = teamViewService;
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody TeamInput teamInput) {
        UUID teamId = teamService.createNew(teamInput.name()).value();
        return ResponseEntity.ok(teamId);
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<UUID> addMember(@PathVariable UUID teamId, @RequestBody MemberInput memberInput) {
        return ResponseEntity.ok(teamService.addMember(new TeamId(teamId), memberInput.name(), memberInput.profession()).value());
    }

    @PostMapping("/{teamId}/tasks/by-project-id/{projectTaskId}")
    public ResponseEntity<UUID> addTask(@PathVariable UUID teamId, @PathVariable UUID projectTaskId) {
        return ResponseEntity.ok(teamService.addTask(new TeamId(teamId), new ProjectTaskId(projectTaskId)).value());
    }

    @PatchMapping("/{teamId}/tasks/{taskId}/assignee")
    public ResponseEntity<Void> assignTask(@PathVariable UUID teamId, @PathVariable UUID taskId, @RequestBody AssignTaskInput assignTaskInput) {
        teamService.assignTask(new TeamId(teamId), new TeamTaskId(taskId), new TeamMemberId(assignTaskInput.assigneeId()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/tasks/{taskId}/mark-in-progress")
    public ResponseEntity<Void> markTaskInProgress(@PathVariable UUID teamId, @PathVariable UUID taskId) {
        teamService.markTaskInProgress(new TeamId(teamId), new TeamTaskId(taskId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/tasks/{taskId}/unassign")
    public ResponseEntity<Void> markTaskUnAssigned(@PathVariable UUID teamId, @PathVariable UUID taskId) {
        teamService.unassignTask(new TeamId(teamId), new TeamTaskId(taskId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/tasks/{taskId}/complete")
    public ResponseEntity<Void> markTaskCompleted(@PathVariable UUID teamId, @PathVariable UUID taskId, @RequestBody ActualSpentTime actualSpentTime) {
        teamService.completeTask(new TeamId(teamId), new TeamTaskId(taskId), actualSpentTime);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{teamId}/tasks/{taskId}")
    public ResponseEntity<Void> removeTask(@PathVariable UUID teamId, @PathVariable UUID taskId) {
        teamService.removeTask(new TeamId(teamId), new TeamTaskId(taskId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable UUID teamId, @PathVariable UUID memberId) {
        teamService.removeMember(new TeamId(teamId), new TeamMemberId(memberId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TeamsView>> findAll() {
        return ResponseEntity.ok(teamViewService.findAll());
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamView> findById(@PathVariable UUID teamId) {
        return teamViewService.findById(teamId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
