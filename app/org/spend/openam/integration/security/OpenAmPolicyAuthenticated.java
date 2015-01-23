package org.spend.openam.integration.security;

import org.spend.openam.integration.service.OpenAmIntegrationService;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import static java.time.Duration.ofMinutes;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.spend.openam.integration.Global.context;
import static org.spend.openam.integration.controller.ControllersConstants.COOKIE_NAME_TOKEN;
import static org.spend.openam.integration.controller.ControllersConstants.COOKIE_NAME_USERNAME;
import static play.mvc.Controller.request;
import static play.mvc.Controller.session;

public class OpenAmPolicyAuthenticated extends Security.Authenticator {
    private static OpenAmIntegrationService openAmIntegrationService;

    static {
        openAmIntegrationService = context.getBean(OpenAmIntegrationService.class);
    }

    @Override
    public String getUsername(Http.Context ctx) {
        final String username = session(COOKIE_NAME_USERNAME);
        final String token = session(COOKIE_NAME_TOKEN);
        if (isBlank(username) || isBlank(token)) {
            session().clear();
            return null;
        }
        if (openAmIntegrationService.hasAccess(token, request().path(), request().method()).get(ofMinutes(1).toMillis())) {
            return username;
        } else {
            return null;
        }
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        if (isNotBlank(session(COOKIE_NAME_USERNAME))) {
            redirect(org.spend.openam.integration.controller.routes.ProfileController.pageProfile());
        }
        return redirect(org.spend.openam.integration.controller.routes.WelcomeController.pageIndex());
    }
}
