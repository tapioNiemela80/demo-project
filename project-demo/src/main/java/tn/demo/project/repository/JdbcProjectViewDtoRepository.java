package tn.demo.project.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tn.demo.project.controller.ProjectViewDto;
import tn.demo.project.controller.ProjectsViewDto;
import tn.demo.project.controller.TaskViewDto;
import tn.demo.project.controller.TimeEstimation;
import tn.demo.team.controller.ActualSpentTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbcProjectViewDtoRepository implements ProjectViewDtoRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public JdbcProjectViewDtoRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<ProjectViewDto> findDtoById(UUID id) {

        String sql = """
                    SELECT
                                p.id AS project_id,
                                p.name AS project_name,
                                p.description AS project_description,
                                p.initial_estimated_time_hours as project_initial_estimation_hours,
                                p.initial_estimated_time_minutes as project_initial_estimation_minutes,
                                p.status as project_status,
                                cp.email AS contact_person_email,
                                pt.id AS task_id,
                                pt.title AS task_title,
                                pt.description AS task_description,
                                pt.task_status as task_status,
                                pt.estimated_time_hours as task_estimation_hours,
                                pt.estimated_time_minutes as task_estimation_minutes,
                                pt.actual_time_spent_hours as task_actual_time_hours,
                                pt.actual_time_spent_minutes as task_actual_time_minutes
                            FROM project_demo.projects p
                            LEFT JOIN project_demo.contact_persons cp ON p.id = cp.project_id
                            LEFT JOIN project_demo.project_tasks pt ON p.id = pt.project_id
                            WHERE p.id = :projectId
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("projectId", id);

        List<Map<String, Object>> rows = jdbc.query(sql, params, (rs, rowNum) -> {
            Map<String, Object> row = new HashMap<>();
            row.put("project_id", rs.getString("project_id"));
            row.put("project_name", rs.getString("project_name"));
            row.put("project_description", rs.getString("project_description"));
            row.put("project_initial_estimation_hours", rs.getInt("project_initial_estimation_hours"));
            row.put("project_initial_estimation_minutes", rs.getInt("project_initial_estimation_minutes"));
            row.put("project_status", rs.getString("project_status"));
            row.put("contact_person_email", rs.getString("contact_person_email"));
            row.put("task_id", rs.getString("task_id"));
            row.put("task_title", rs.getString("task_title"));
            row.put("task_description", rs.getString("task_description"));
            row.put("task_status", rs.getString("task_status"));
            row.put("task_estimation_hours", rs.getInt("task_estimation_hours"));
            row.put("task_estimation_minutes", rs.getInt("task_estimation_minutes"));
            addTaskActualTime(row, rs, "task_actual_time_hours");
            addTaskActualTime(row, rs, "task_actual_time_minutes");
            return row;
        });
        return mapToProjectDetailsDto(rows);
    }

    private Optional<ProjectViewDto> mapToProjectDetailsDto(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        ProjectViewDtoBuilder builder = new ProjectViewDtoBuilder();
        boolean projectSet = false;

        for (Map<String, Object> row : rows) {
            if (!projectSet) {
                builder.id = ((String) row.get("project_id"));
                builder.name = ((String) row.get("project_name"));
                builder.description = ((String) row.get("project_description"));
                builder.contactPersonEmail = ((String) row.get("contact_person_email"));
                builder.timeEstimationHours = (Integer) row.get("project_initial_estimation_hours");
                builder.timeEstimationMinutes = (Integer) row.get("project_initial_estimation_minutes");
                builder.isCompleted = isCompleted((String) row.get("project_status"));
                projectSet = true;
            }

            if (row.get("task_id") != null) {
                TaskViewDto task = toTask(row);
                builder.tasks.add(task);
            }
        }
        return Optional.of(builder.build());
    }

    private TaskViewDto toTask(Map<String, Object> row) {
        UUID id = UUID.fromString((String) row.get("task_id"));
        String title = (String) row.get("task_title");
        String desc = (String) row.get("task_description");
        boolean isCompleted = ((String) row.get("task_status")).equals("COMPLETE") ? true : false;
        TimeEstimation estimation = new TimeEstimation((int) row.get("task_estimation_hours"), (int) row.get("task_estimation_minutes"));
        return new TaskViewDto(id, title, desc, isCompleted, estimation, actualSpentTime(row));

    }

    private boolean isCompleted(String projectStatus) {
        return projectStatus.equals("COMPLETED");
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

    private ActualSpentTime actualSpentTime(Map<String, Object> row) {
        Integer hours = (Integer) row.get("task_actual_time_hours");
        Integer minutes = (Integer) row.get("task_actual_time_minutes");
        if (hours == null) {
            return ActualSpentTime.zero();
        }
        return new ActualSpentTime(hours, minutes);
    }

    @Override
    public List<ProjectsViewDto> findAll() {
        String sqlProject = "SELECT id, name, description FROM projects";
        Map<String, Object> params = Map.of();

        List<ProjectsViewDto> projects = jdbc.query(sqlProject, params, (rs, rowNum) -> {
            ProjectsViewDto dto = new ProjectsViewDto(UUID.fromString(rs.getString("id")), rs.getString("name"), rs.getString("description"));
            return dto;
        });
        return projects;
    }

    class ProjectViewDtoBuilder {
        String id;
        String name;
        String description;
        boolean isCompleted;
        String contactPersonEmail;
        List<TaskViewDto> tasks = new ArrayList<>();
        Integer timeEstimationHours;
        Integer timeEstimationMinutes;

        ProjectViewDto build() {
            return new ProjectViewDto(UUID.fromString(id), name, description, isCompleted, contactPersonEmail, new TimeEstimation(timeEstimationHours, timeEstimationMinutes), tasks);
        }
    }

}
