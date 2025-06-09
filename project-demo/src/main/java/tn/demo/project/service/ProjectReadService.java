package tn.demo.project.service;

import org.springframework.stereotype.Service;
import tn.demo.project.controller.ProjectViewDto;
import tn.demo.project.controller.ProjectsViewDto;
import tn.demo.project.domain.UnknownProjectIdException;
import tn.demo.project.repository.ProjectViewDtoRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectReadService {
    private final ProjectViewDtoRepository projects;

    public ProjectReadService(ProjectViewDtoRepository projects) {
        this.projects = projects;
    }

    public ProjectViewDto findById(UUID id){
        return projects.findDtoById(id)
                .orElseThrow(() -> new UnknownProjectIdException(id));
    }


    public List<ProjectsViewDto> findAll() {
        return projects.findAll();
    }
}