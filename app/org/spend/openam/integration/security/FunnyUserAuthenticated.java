package org.spend.openam.integration.security;

import org.spend.openam.integration.service.OpenAmIntegrationService;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import static java.time.Duration.ofMinutes;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.spend.openam.integration.Global.context;
import static org.spend.openam.integration.controller.ControllersConstants.COOKIE_NAME_TOKEN;
import static org.spend.openam.integration.controller.ControllersConstants.COOKIE_NAME_USERNAME;
import static org.spend.openam.integration.dto.UserProfileGroup.FUNNY_USER;
import static play.mvc.Controller.flash;
import static play.mvc.Controller.request;
import static play.mvc.Controller.session;

public class FunnyUserAuthenticated extends Security.Authenticator {
    private static OpenAmIntegrationService openAmIntegrationService;

    static {
        openAmIntegrationService = context.getBean(OpenAmIntegrationService.class);
    }

    public String getUsername(Http.Context ctx) {
        final String username = session(COOKIE_NAME_USERNAME);
        final String token = session(COOKIE_NAME_TOKEN);
        if (isBlank(username) || isBlank(token)) {
            session().clear();
            return null;
        }
        return openAmIntegrationService.hasAccess(token, request().path(), request().method()).flatMap((hasAccess) -> {
            if (hasAccess) {
                return openAmIntegrationService.userProfile(username, token).map((profile) -> {
                    if (profile.getGroup() == FUNNY_USER) {
                        return username;
                    } else {
                        return null;
                    }
                });
            } else return null;
        }).get(ofMinutes(1).toMillis());
    }

    public Result onUnauthorized(Http.Context ctx) {
        flash("message","To access users list, please add your own photo");
        return redirect(org.spend.openam.integration.controller.routes.ProfileController.pageProfile());
    }
}
