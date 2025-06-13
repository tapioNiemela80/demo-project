package tn.demo.team.repository;

import java.util.UUID;

public record TeamsViewRow(
        UUID teamId,
        String teamName
) {}