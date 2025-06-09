package tn.demo.project.repository;

import tn.demo.project.controller.ProjectViewDto;
import tn.demo.project.controller.ProjectsViewDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectViewDtoRepository {
    Optional<ProjectViewDto> findDtoById(UUID id);

    List<ProjectsViewDto> findAll();

}
