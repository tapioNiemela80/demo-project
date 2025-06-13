package tn.demo.team.service;

import org.springframework.stereotype.Service;
import tn.demo.team.controller.TeamViewDto;
import tn.demo.team.controller.TeamsViewDto;
import tn.demo.team.repository.TeamViewDtoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamReadService {
    private final TeamViewDtoRepository teamViewDtoRepository;

    public TeamReadService(TeamViewDtoRepository teamViewDtoRepository) {
        this.teamViewDtoRepository = teamViewDtoRepository;
    }

    public List<TeamsViewDto> findAll() {
        return teamViewDtoRepository.findAll();
    }



    public Optional<TeamViewDto> findById(UUID id) {
        return teamViewDtoRepository.findById(id);
    }

}
