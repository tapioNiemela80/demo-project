package tn.demo.team.controller;

import java.util.UUID;

public record TeamMemberDto(UUID id, String name, String profession) {
}
