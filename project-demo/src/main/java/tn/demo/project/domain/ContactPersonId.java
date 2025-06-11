package tn.demo.project.domain;

import tn.demo.common.domain.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject(description ="Represents id of contact person")
public record ContactPersonId(UUID value) {
    public ContactPersonId {
        Objects.requireNonNull(value, "ContactPersonId value cannot be null");
    }
}
