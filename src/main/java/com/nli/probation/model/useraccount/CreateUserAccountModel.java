package com.nli.probation.model.useraccount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class CreateUserAccountModel {

    private String role;

    private String name;

    private String email;

    private String phone;

    private int teamId;

    private int officeId;
}
