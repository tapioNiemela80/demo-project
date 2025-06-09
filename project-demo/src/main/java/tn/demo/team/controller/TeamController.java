package tn.demo.team.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.demo.project.domain.ProjectTaskId;
import tn.demo.team.domain.TeamId;
import tn.demo.team.domain.TeamMemberId;
import tn.demo.team.domain.TeamTaskId;
import tn.demo.team.service.TeamService;

import java.util.UUID;

@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
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

    @PostMapping("/{teamId}/tasks/{taskId}")
    public ResponseEntity<UUID> addTask(@PathVariable UUID teamId, @PathVariable UUID taskId) {
        return ResponseEntity.ok(teamService.addTask(new TeamId(teamId), new ProjectTaskId(taskId)).value());
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
    public ResponseEntity<Void> markTaskCompleted(@PathVariable UUID teamId, @PathVariable UUID taskId) {
        teamService.completeTask(new TeamId(teamId), new TeamTaskId(taskId));
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{teamId}/tasks/{taskId}")
    public ResponseEntity<Void> removeTask(@PathVariable UUID teamId, @PathVariable UUID taskId) {
        teamService.removeTask(new TeamId(teamId), new TeamTaskId(taskId));
        return ResponseEntity.noContent().build();
    }

}
