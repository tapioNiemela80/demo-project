package tn.demo.team.controller;

import java.util.List;
import java.util.UUID;

public record TeamViewDto(UUID id, String name, List<TeamTaskViewDto> tasks, List<TeamMemberDto> members) {
}
