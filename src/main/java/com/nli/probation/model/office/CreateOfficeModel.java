package com.nli.probation.model.office;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class CreateOfficeModel {

    @NotNull(message = "{office_location.null}")
    @Length(message = "{office_name.length}")
    private String name;

    @NotNull(message = "{office_location.null}")
    @Length(message = "{office_location.length}")
    private String location;

}
