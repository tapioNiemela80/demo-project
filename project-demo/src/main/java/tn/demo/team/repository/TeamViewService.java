package tn.demo.team.repository;

import org.springframework.stereotype.Service;
import tn.demo.team.controller.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeamViewService {
    private final TeamViewRepository repository;

    public TeamViewService(TeamViewRepository repository) {
        this.repository = repository;
    }

    public List<TeamsViewDto> findAll(){
        return repository.findTeams().stream()
                .map(data -> new TeamsViewDto(data.teamId(), data.teamName()))
                .toList();
    }

    public Optional<TeamView> getTeamView(UUID teamId) {
        var rows = repository.findTeamViewByTeamId(teamId);
        if(rows.isEmpty()){
            return Optional.empty();
        }
        TeamViewRow first = rows.get(0);
        List<MemberView> members = getMembers(rows);
        List<TaskView> tasks = getTasks(rows);
        return Optional.of(new TeamView(
                first.teamId(),
                first.teamName(),
                members,
                tasks
        ));
    }

    private List<TaskView> getTasks(List<TeamViewRow> rows) {
        return rows.stream()
                .filter(row -> row.taskId() != null)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                TeamViewRow::taskId,
                                this::getTaskView,
                                (a, b) -> a
                        ),
                        map -> new ArrayList<>(map.values())
                ));
    }

    private TaskView getTaskView(TeamViewRow row) {
        return new TaskView(
                row.taskId(),
                row.taskName(),
                row.taskDescription(),
                row.projectTaskId(),
                row.taskStatus(),
                row.taskAssigneeId(),
                actualTimeSpent(row.actualTimeSpentHours(), row.actualTimeSpentMinutes())
        );
    }

    private List<MemberView> getMembers(List<TeamViewRow> rows) {
        return rows.stream()
                .filter(row -> row.memberId() != null)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                TeamViewRow::memberId,
                                row -> new MemberView(
                                        row.memberId(),
                                        row.memberName(),
                                        row.memberProfession()
                                ),
                                (a, b) -> a // ignore duplicates
                        ),
                        map -> new ArrayList<>(map.values())
                ));
    }

    private ActualSpentTime actualTimeSpent(Integer actualTimeSpentHours, Integer actualTimeSpentMinutes) {
        if(actualTimeSpentHours == null){
            return null;
        }
        return new ActualSpentTime(actualTimeSpentHours, actualTimeSpentMinutes);
    }
}