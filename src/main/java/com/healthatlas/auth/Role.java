package com.healthatlas.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role {
    private long id;
    private String name;
    private String description;
}
