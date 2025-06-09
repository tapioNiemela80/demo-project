package tn.demo.project.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.UUID;

@Table("contact_persons")
class ContactPerson {
    @Id
    private UUID id;

    private String name;

    private String email;

    @PersistenceCreator
    private ContactPerson(UUID id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;
    }

    static ContactPerson createNew(ContactPersonId personId, String name, String email){
        return new ContactPerson(personId.value(), name, email);
    }

    String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactPerson other = (ContactPerson) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
