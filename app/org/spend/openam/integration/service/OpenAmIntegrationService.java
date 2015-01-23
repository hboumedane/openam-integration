package org.spend.openam.integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.spend.openam.integration.dto.UserProfileDto;
import play.Configuration;
import play.libs.F.Promise;
import play.libs.F.Tuple;

import java.util.List;

import static play.Play.application;

public interface OpenAmIntegrationService {
    Configuration CONF = application().configuration();
    String ADMIN_USERNAME = CONF.getString("external-ws.openam.username");
    String ADMIN_PASSWORD = CONF.getString("external-ws.openam.password");
    String IPLANET_DIRECTORY_PRO_HEADER_NAME = "iplanetDirectoryPro";

    String URL = CONF.getString("external-ws.openam.url");
    String ADMIN_AUTHENTICATION = URL + "/authenticate";
    String APPLICATION = CONF.getString("external-ws.openam.application");
    String REALM = CONF.getString("external-ws.openam.realm");
    String URL_AUTHENTICATION = URL + '/' + REALM + "/authenticate";
    String URL_SESSIONS_LOGOUT = URL + '/' + REALM + "/sessions/?_action=logout";
    String URL_REGISTRATION = URL + '/' + REALM + "/users?_action=create";
    String URL_USERS_PROFILE = URL + '/' + REALM + "/users/";
    String URL_USERS_PROFILES = URL_USERS_PROFILE + "?_queryID=*";
    String URL_POLICY_EVALUATE = URL + '/' + REALM + "/policies?_action=evaluateTree";

    String X_OPEN_AM_USERNAME = "X-OpenAM-Username";
    String X_OPEN_AM_PASSWORD = "X-OpenAM-Password";

    String PATTERN_POLICY_EVALUATE_BODY = "{\"application\":\"" + APPLICATION + "\"," +
            "\"resource\": \"%s\"}";

    /**
     * @return username (_1) and token (_2)
     */
    Promise<Tuple<String, String>> authenticate(String username, String password);

    Promise<UserProfileDto> userProfile(String username, String token);

    /**
     * @return username (_1) and token (_2)
     */
    Promise<Tuple<String, String>> register(UserProfileDto userProfileDto);

    Promise<Boolean> logout(String token);

    Promise<Boolean> hasAccess(String token, String resource, String method);

    Promise<Boolean> hasAccessGet(String token, String resource);

    Promise<List<UserProfileDto>> listAllUsers();

    Promise<Boolean> update(JsonNode patch, String username, String token);
}
