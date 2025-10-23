package com.healthatlas.auth;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class Role {
    private long id;
    private String name;
    private String description;
}
