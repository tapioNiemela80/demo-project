package tn.demo.team.repository;

import tn.demo.team.controller.TeamViewDto;
import tn.demo.team.controller.TeamsViewDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamViewDtoRepository {
    List<TeamsViewDto> findAll();
    Optional<TeamViewDto> findById(UUID id);
}
