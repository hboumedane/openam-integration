package org.spend.openam.integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.spend.openam.integration.converter.OpenAmUserToDtoConverter;
import org.spend.openam.integration.dto.UserProfileDto;
import org.spend.openam.integration.exception.BadRequestException;
import org.spend.openam.integration.exception.UnauthorizedException;
import org.spend.openam.integration.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.libs.ws.WSResponse;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.time.Duration.ofMinutes;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static play.libs.Json.toJson;
import static play.libs.ws.WS.url;
import static play.mvc.Http.MimeTypes.JSON;
import static play.mvc.Http.Status.*;

@Service
public class OpenAmIntegrationServiceImpl implements OpenAmIntegrationService {
    private OpenAmUserToDtoConverter openAmUserToDtoConverter;
    private String adminToken;

    @Autowired
    public OpenAmIntegrationServiceImpl(OpenAmUserToDtoConverter openAmUserToDtoConverter) {
        this.openAmUserToDtoConverter = openAmUserToDtoConverter;
    }

    @PostConstruct
    public void authenticateAdmin() {
        final Promise<String> adminAuthenticationPromise = url(ADMIN_AUTHENTICATION).setContentType(JSON)
                .setHeader(X_OPEN_AM_USERNAME, ADMIN_USERNAME)
                .setHeader(X_OPEN_AM_PASSWORD, ADMIN_PASSWORD)
                .post("{}").map((response) -> {
                    if (response.getStatus() == OK) {
                        return response.asJson().get("tokenId").textValue();
                    } else if (response.getStatus() == UNAUTHORIZED) {
                        throw new UnauthorizedException();
                    } else {
                        throw new BadRequestException(response.getBody());
                    }
                });
        adminAuthenticationPromise.onFailure(RuntimeException::new);
        adminToken = adminAuthenticationPromise.get(ofMinutes(1).toMillis());
    }

    @Override
    public Promise<Tuple<String, String>> authenticate(String username, String password) {
        return url(URL_AUTHENTICATION).setContentType(JSON)
                .setHeader(X_OPEN_AM_USERNAME, username)
                .setHeader(X_OPEN_AM_PASSWORD, password)
                .post("{}").map((response) -> {
                    if (response.getStatus() == UNAUTHORIZED) {
                        final JsonNode message = response.asJson().get("message");
                        if (message != null) {
                            return new Tuple<>("error", message.textValue());
                        }
                        return new Tuple<>("error", response.getStatusText());
                    } else if (response.getStatus() == OK) {
                        return new Tuple<>(username, response.asJson().get("tokenId").textValue());
                    } else {
                        throw new BadRequestException(response.getBody());
                    }
                });
    }

    @Override
    public Promise<UserProfileDto> userProfile(String username, String token) {
        return url(URL_USERS_PROFILE + username).setContentType(JSON)
                .setHeader(IPLANET_DIRECTORY_PRO_HEADER_NAME, token)
                .get().map((wsResponse) -> {
                    JsonNode response = wsResponse.asJson();
                    if (response.get("code") != null) {
                        throw new UnauthorizedException(response.get("reason").textValue() + ':' + response.get("message").textValue());
                    }
                    return openAmUserToDtoConverter.toUserProfileDto(response);
                });
    }

    @Override
    public Promise<Tuple<String, String>> register(UserProfileDto userProfileDto) {
        return url(URL_REGISTRATION).setContentType(JSON)
                .setHeader(IPLANET_DIRECTORY_PRO_HEADER_NAME, adminToken)
                .post(toJson(userProfileDto))
                .map((response) -> {
                    if (response.getStatus() == CREATED) {
                        return true;
                    }
                    final JsonNode messageNode = response.asJson().get("message");
                    final String message = messageNode != null ? messageNode.textValue() : response.getStatusText();
                    switch (response.getStatus()) {
                        case CONFLICT:
                            throw new UserAlreadyExistsException(message);
                        case BAD_REQUEST:
                            throw new BadRequestException(message);
                        default:
                            throw new RuntimeException(message);
                    }
                }).flatMap((everythingAllRight) -> authenticate(userProfileDto.getUsername(), userProfileDto.getPassword()));
    }

    @Override
    public Promise<Boolean> logout(String token) {
        return url(URL_SESSIONS_LOGOUT).setContentType(JSON)
                .setHeader(IPLANET_DIRECTORY_PRO_HEADER_NAME, token)
                .post("{}")
                .map((response) -> {
                    final JsonNode result = response.asJson().get("result");
                    return result != null && "Successfully Logged Out".equals(result.textValue());
                });
    }

    @Override
    public Promise<Boolean> hasAccess(String token, String resource, String method) {
        return url(URL_POLICY_EVALUATE).setContentType(JSON)
                .setHeader(IPLANET_DIRECTORY_PRO_HEADER_NAME, token)
                .post(format(PATTERN_POLICY_EVALUATE_BODY, resource)).map((response) -> {
                    if (response.getStatus() == OK) {
                        final JsonNode action = response.asJson().get(0).get("actions").get(method);
                        return action != null && action.asBoolean();
                    }
                    return false;
                });
    }

    @Override
    public Promise<Boolean> hasAccessGet(String token, String resource) {
        return hasAccess(token, resource, GET.getName());
    }

    @Override
    public Promise<List<UserProfileDto>> listAllUsers() {
        return url(URL_USERS_PROFILES).setContentType(JSON)
                .setHeader(IPLANET_DIRECTORY_PRO_HEADER_NAME, adminToken)
                .get().map(WSResponse::asJson).map(response -> {
                    final int resultCount = response.get("resultCount").intValue();
                    List<UserProfileDto> users = new ArrayList<>();
                    response.get("result").elements().forEachRemaining(username -> {
                        users.add(userProfile(username.textValue(), adminToken).get(ofMinutes(1L).toMillis()));
                    });
                    return users;
                });
    }

    @Override
    public Promise<Boolean> update(JsonNode patch, String username, String token) {
        return url(URL_USERS_PROFILE + username).setContentType(JSON)
                .setHeader(IPLANET_DIRECTORY_PRO_HEADER_NAME, adminToken)
                .put(patch).map((wsResponse) -> {
                    return wsResponse.getStatus() == OK;
                });
    }
}
