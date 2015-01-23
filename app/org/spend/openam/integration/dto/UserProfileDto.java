package org.spend.openam.integration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static org.spend.openam.integration.dto.UserProfileGroup.USER;
import static org.spend.openam.integration.dto.UserProfileStatus.ACTIVE;
import static play.data.validation.Constraints.*;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileDto {
    private Throwable throwable;
    @Required
    @JsonProperty(required = true)
    private String username;

    @MinLength(8)
    @JsonProperty(value = "userpassword", required = true)
    private String password;

    @Email
    @Required
    @JsonProperty(required = true)
    private String email;

    @JsonProperty(value = "full_name", required = true)
    private String fullName;

    @JsonProperty(value = "image_url", required = false)
    @Pattern("((?:https?\\:\\/\\/)(?:[a-zA-Z]{1}(?:[\\w\\-]+\\.)+(?:[\\w]{2,5}))(?:\\:[\\d]{1,5})?\\/(?:[^\\s\\/]+\\/)*(?:[^\\s]+\\.(?:jpe?g|gif|png|svg|jpg))(?:\\?\\w+=\\w+(?:&\\w+=\\w+)*)?)(\\?.+)?")
    private String imageUrl;

    @JsonFormat(shape = STRING)
    @JsonProperty(value = "group_name")
    private UserProfileGroup group;

    @JsonFormat(shape = STRING)
    private UserProfileStatus status;

    public UserProfileDto clearPassword() {
        password = null;
        return this;
    }

    public UserProfileDto(Throwable throwable) {
        this.throwable = throwable;
    }
}
