package org.spend.openam.integration.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.spend.openam.integration.dto.UserProfileDto;
import org.springframework.stereotype.Component;

import static org.spend.openam.integration.dto.UserProfileGroup.valueOf;

@Component
public class OpenAmUserToDtoConverterImpl implements OpenAmUserToDtoConverter {
    @Override
    public UserProfileDto toUserProfileDto(JsonNode response) {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setEmail(response.get("email").get(0).textValue());
        userProfileDto.setUsername(response.get("username").get(0).textValue());
        {
            final JsonNode imageUrl = response.get("image_url").get(0);
            if (imageUrl != null) {
                userProfileDto.setImageUrl(imageUrl.textValue());
            }
        }
        {
            final JsonNode fullName = response.get("full_name").get(0);
            if (fullName != null) {
                userProfileDto.setFullName(fullName.textValue());
            }
        }
        {
            final JsonNode groupNameListNode = response.get("group_name");
            if (groupNameListNode != null) {
                final JsonNode groupName = response.get("group_name").get(0);
                if (groupName != null) {
                    userProfileDto.setGroup(valueOf(groupName.textValue()));
                }
            }
        }
        return userProfileDto;
    }
}
