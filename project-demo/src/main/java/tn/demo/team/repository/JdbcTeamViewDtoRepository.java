package tn.demo.team.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tn.demo.team.controller.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbcTeamViewDtoRepository implements TeamViewDtoRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public JdbcTeamViewDtoRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<TeamViewDto> findById(UUID id) {
        String sql = """
                SELECT
                    teams.id as team_id,
                    teams.name as team_name,
                    members.id as member_id,
                    members.name as member_name,
                    members.profession as member_profession,
                    tasks.id as task_id,
                    tasks.name as task_title,
                    tasks.description as task_description,
                    tasks.status as task_status,
                    tasks.project_task_id as task_project_task_id,
                    tasks.actual_time_spent_hours as task_actual_time_spent_hours,
                    tasks.actual_time_spent_minutes as task_actual_time_spent_minutes
                    FROM project_demo.teams teams
                    LEFT JOIN project_demo.team_members members ON teams.id = members.team_id
                    LEFT JOIN project_demo.team_tasks tasks ON teams.id = tasks.team_id
                    where teams.id = :taskId
                    """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("taskId", id);

        List<Map<String, Object>> rows = jdbc.query(sql, params, (rs, rowNum) -> {
            Map<String, Object> row = new HashMap<>();
            row.put("team_id", rs.getString("team_id"));
            row.put("team_name", rs.getString("team_name"));
            row.put("member_id", rs.getString("member_id"));
            row.put("member_name", rs.getString("member_name"));
            row.put("member_profession", rs.getString("member_profession"));
            row.put("task_id", rs.getString("task_id"));
            row.put("task_title", rs.getString("task_title"));
            row.put("task_description", rs.getString("task_description"));
            row.put("task_status", rs.getString("task_status"));
            row.put("task_project_task_id", rs.getString("task_project_task_id"));
            addTaskActualTime(row, rs, "task_actual_time_spent_hours");
            addTaskActualTime(row, rs, "task_actual_time_spent_minutes");
            return row;
        });
        return mapToTeamDetailsDto(rows);
    }

    private Optional<TeamViewDto> mapToTeamDetailsDto(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        TeamViewDtoBuilder builder = new TeamViewDtoBuilder();
        boolean teamSet = false;

        for (Map<String, Object> row : rows) {
            if (!teamSet) {
                builder.id = (String) row.get("team_id");
                builder.name = (String) row.get("team_name");
                teamSet = true;
            }
            if(row.get("member_id")!=null) {
                builder.members.add(toMember(row));
            }
            if(row.get("task_id")!=null) {
                builder.tasks.add(toTask(row));
            }
        }
        return Optional.of(builder.build());
    }

    private TeamTaskViewDto toTask(Map<String, Object> row) {
        UUID id = UUID.fromString((String)row.get("task_id"));
        String title = (String)row.get("task_title");
        String desc = (String)row.get("task_description");
        String status = (String)row.get("task_status");
        UUID projectTaskId = UUID.fromString((String)row.get("task_project_task_id"));
        return new TeamTaskViewDto(id, title, desc, projectTaskId, status, actualSpentTime(row));
    }

    private ActualSpentTime actualSpentTime(Map<String, Object> row) {
        Integer hours = (Integer) row.get("task_actual_time_spent_hours");
        Integer minutes = (Integer) row.get("task_actual_time_spent_minutes");
        if (hours == null) {
            return ActualSpentTime.zero();
        }
        return new ActualSpentTime(hours, minutes);
    }

    private TeamMemberDto toMember(Map<String, Object> row) {
        UUID id = UUID.fromString((String)row.get("member_id"));
        return new TeamMemberDto(id, (String)row.get("member_name"), (String)row.get("member_profession"));
    }

    private void addTaskActualTime(Map<String, Object> row, ResultSet rs, String key) {
        try {
            Integer value = rs.getObject(key, Integer.class);
            if (value != null) {
                row.put(key, value);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<TeamsViewDto> findAll() {
        String sqlProject = "SELECT id, name FROM teams";
        Map<String, Object> params = Map.of();

        List<TeamsViewDto> teams = jdbc.query(sqlProject, params, (rs, rowNum) -> {
            TeamsViewDto dto = new TeamsViewDto(UUID.fromString(rs.getString("id")), rs.getString("name"));
            return dto;
        });
        return teams;
    }

    class TeamViewDtoBuilder {
        String id;
        String name;
        List<TeamTaskViewDto> tasks = new ArrayList<>();
        List<TeamMemberDto> members = new ArrayList<>();

        TeamViewDto build() {
            return new TeamViewDto(UUID.fromString(id), name, tasks, members);
        }
    }
}
