package tn.demo.team.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.UUID;

@Table("team_members")
class TeamMember {
    @Id
    private UUID id;

    private String name;

    private String profession;

    @PersistenceCreator
    private TeamMember(UUID id, String name, String profession){
        this.id = id;
        this.name = name;
        this.profession = profession;
    }

    static TeamMember createNew(TeamMemberId memberId, String name, String profession){
        return new TeamMember(memberId.value(), name, profession);
    }

    boolean hasId(TeamMemberId expected) {
        return id.equals(expected.value());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMember other = (TeamMember) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
