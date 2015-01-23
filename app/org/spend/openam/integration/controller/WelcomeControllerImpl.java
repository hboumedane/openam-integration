package org.spend.openam.integration.controller;


import org.spend.openam.integration.dto.AuthenticationDto;
import org.spend.openam.integration.dto.UserProfileDto;
import org.spend.openam.integration.service.OpenAmIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import play.data.Form;
import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.mvc.Result;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.spend.openam.integration.controller.ControllersConstants.COOKIE_NAME_TOKEN;
import static org.spend.openam.integration.controller.ControllersConstants.COOKIE_NAME_USERNAME;
import static org.spend.openam.integration.dto.UserProfileGroup.USER;
import static org.spend.openam.integration.dto.UserProfileStatus.ACTIVE;
import static play.data.Form.form;
import static play.libs.F.Promise.pure;

@Controller
public class WelcomeControllerImpl extends play.mvc.Controller implements WelcomeController {

    @Autowired
    private OpenAmIntegrationService openAmIntegrationService;

    @Override
    public Result pageIndex() {
        return ok(org.spend.openam.integration.view.html.welcome.render());
    }

    //  Actions
    @Override
    public Promise<Result> authenticate() {
        Form<AuthenticationDto> authenticationForm = form(AuthenticationDto.class).bindFromRequest(request());
        if (authenticationForm.hasErrors()) {
            return pure(badRequest(authenticationForm.errorsAsJson()));
        }
        final AuthenticationDto authentication = authenticationForm.get();
        return openAmIntegrationService.authenticate(
                authentication.getUsername(), authentication.getPassword()).map((tuple) -> {
            if (!tuple._1.equals("error")) {
                saveUsernameAndTokenCookie(tuple);
                return ok();
            } else {
                return badRequest(tuple._2);
            }
        });
    }

    @Override
    public Promise<Result> register() {
        final Form<UserProfileDto> userProfile = form(UserProfileDto.class).bindFromRequest(request());
        if (userProfile.hasErrors()) {
            return pure(badRequest(userProfile.errorsAsJson()));
        }
        final UserProfileDto profileDto = userProfile.get();
        profileDto.setGroup(USER);
        profileDto.setStatus(ACTIVE);
        return openAmIntegrationService.register(profileDto).map((tuple) -> {
            if (!tuple._1.equals("error")) {
                saveUsernameAndTokenCookie(tuple);
                return ok();
            } else {
                return badRequest(tuple._2);
            }
        });
    }

    @Override
    public Result getUsername() {
        final String username = session(COOKIE_NAME_USERNAME);
        if (isNotBlank(username)) {
            return ok(username);
        } else {
            return ok();
        }
    }

    private void saveUsernameAndTokenCookie(Tuple<String, String> usernameAndToken) {
        if (isBlank(usernameAndToken._1)) {
            throw new RuntimeException(format("Can't set username cookie, it's blank (%s)", usernameAndToken._1));
        } else if (isBlank(usernameAndToken._2)) {
            throw new RuntimeException(format("Can't set token cookie, it's blank (%s)", usernameAndToken._2));
        }
        session(COOKIE_NAME_USERNAME, usernameAndToken._1);
        session(COOKIE_NAME_TOKEN, usernameAndToken._2);
    }
}
