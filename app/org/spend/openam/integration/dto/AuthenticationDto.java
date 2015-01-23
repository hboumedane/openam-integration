package org.spend.openam.integration.dto;

import lombok.Data;
import play.data.validation.Constraints;

@Data
public class AuthenticationDto {
    @Constraints.Required
    private String username;
    @Constraints.Required
    private String password;
}
