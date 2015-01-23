package org.spend.openam.integration.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.spend.openam.integration.dto.UserProfileDto;
import org.springframework.stereotype.Component;

public interface OpenAmUserToDtoConverter {
    UserProfileDto toUserProfileDto(JsonNode openAmResponse);
}
