package tn.demo.project.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tn.demo.project.controller.ProjectViewDto;
import tn.demo.project.controller.ProjectsViewDto;
import tn.demo.project.controller.TaskViewDto;
import tn.demo.project.controller.TimeEstimation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcProjectViewDtoRepository implements ProjectViewDtoRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public JdbcProjectViewDtoRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<ProjectViewDto> findDtoById(UUID id) {
        String sqlProject = "SELECT id, name, description, status, initial_estimated_time_hours, initial_estimated_time_minutes FROM projects WHERE id = :id";
        Map<String, Object> params = Map.of("id", id);

        List<ProjectViewDtoBuilder> projects = jdbc.query(sqlProject, params, (rs, rowNum) -> {
            ProjectViewDtoBuilder builder = new ProjectViewDtoBuilder();
            builder.id = UUID.fromString(rs.getString("id"));
            builder.name = rs.getString("name");
            builder.isCompleted = rs.getString("status").equals("COMPLETED") ? true : false;
            builder.description = rs.getString("description");
            builder.timeEstimation = new TimeEstimation(rs.getInt("initial_estimated_time_hours"), rs.getInt("initial_estimated_time_minutes"));
            return builder;
        });

        if (projects.isEmpty()) return Optional.empty();
        ProjectViewDtoBuilder builder = projects.get(0);

        String sqlTasks = "SELECT id, title, description, estimated_time_hours, estimated_time_minutes, task_status FROM project_tasks WHERE project_id = :id";
        List<TaskViewDto> taskViewDtos = jdbc.query(sqlTasks, params, (rs, rowNum) -> {
            TaskViewDto t = new TaskViewDto(UUID.fromString(rs.getString("id")), rs.getString("title"), rs.getString("description"),
                    rs.getString("task_status").equals("COMPLETE") ? true : false,
                    new TimeEstimation(rs.getInt("estimated_time_hours"), rs.getInt("estimated_time_minutes")));
            return t;
        });

        builder.tasks = taskViewDtos;

        String contactEmailSql = "SELECT email from contact_persons WHERE project_id = :id";
        List<String> emails = jdbc.query(contactEmailSql, params, (rs, rowNum) -> {
            return  rs.getString("email");
        });

        builder.contactPersonEmail = emails.get(0);
        return Optional.of(builder.build());
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

    class ProjectViewDtoBuilder{
        UUID id;
        String name;
        String description;
        boolean isCompleted;
        String contactPersonEmail;
        List<TaskViewDto> tasks;
        TimeEstimation timeEstimation;

        ProjectViewDto build(){
            return new ProjectViewDto(id, name, description, isCompleted, contactPersonEmail, timeEstimation, tasks);
        }

    }

}
