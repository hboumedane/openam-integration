package org.spend.openam.integration.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spend.openam.integration.dto.UserProfileDto;
import org.spend.openam.integration.service.OpenAmIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import play.data.Form;
import play.libs.F.Promise;
import play.mvc.Result;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.spend.openam.integration.controller.ControllersConstants.COOKIE_NAME_TOKEN;
import static org.spend.openam.integration.controller.ControllersConstants.COOKIE_NAME_USERNAME;
import static org.spend.openam.integration.dto.UserProfileGroup.FUNNY_USER;
import static org.spend.openam.integration.dto.UserProfileGroup.USER;
import static play.Logger.debug;
import static play.data.Form.form;
import static play.libs.F.Promise.pure;
import static play.libs.Json.newObject;
import static play.libs.Json.toJson;

@Controller
public class ProfileControllerImpl extends play.mvc.Controller implements ProfileController {

    @Autowired
    private OpenAmIntegrationService openAmIntegrationService;

    @Override
    public Result pageProfile() {
        return ok(org.spend.openam.integration.view.html.profile.render());
    }

    @Override
    public Promise<Result> userProfile() {
        final Promise<UserProfileDto> profilePromise = openAmIntegrationService.userProfile(request().username(),
                session(COOKIE_NAME_TOKEN)).recover(UserProfileDto::new);
        return profilePromise.map((profileDto) -> {
            if (profileDto.getThrowable() == null) {
                profileDto.clearPassword();
                return ok(toJson(profileDto));
            } else {
                debug(profileDto.getThrowable().getMessage(), profileDto.getThrowable());
                return unauthorized(profileDto.getThrowable().getMessage());
            }
        });
    }

    @Override
    public Promise<Result> logout() {
        final String token = session(COOKIE_NAME_TOKEN);
        session().clear();
        if (isNotBlank(token)) {
            return openAmIntegrationService.logout(token)
                    .map((o) -> redirect(org.spend.openam.integration.controller.routes.WelcomeController.pageIndex()));
        } else {
            return pure(redirect(org.spend.openam.integration.controller.routes.WelcomeController.pageIndex()));
        }
    }

    @Override
    public Promise<Result> update() {
        final Form<UserProfileDto> profileForm = form(UserProfileDto.class).bindFromRequest(request());
        if (profileForm.hasErrors()) {
            return pure(badRequest(profileForm.errorsAsJson()));
        }
        return openAmIntegrationService.userProfile(request().username(), session(COOKIE_NAME_TOKEN)).flatMap((profile) -> {
            final UserProfileDto updatedProfile = profileForm.get();
            ObjectNode patch = newObject();
            if (isNotBlank(updatedProfile.getEmail()) && !profile.getEmail().equals(updatedProfile.getEmail())) {
                patch.put("email", updatedProfile.getEmail());
            }
            if (isNotBlank(updatedProfile.getFullName()) && !updatedProfile.getFullName().equals(profile.getFullName())) {
                patch.put("full_name", updatedProfile.getFullName());
            }
            if (isNotBlank(updatedProfile.getImageUrl()) && !updatedProfile.getImageUrl().equals(profile.getImageUrl())) {
                if (profile.getGroup() == USER) {
                    patch.put("group_name", FUNNY_USER.toString());
                }
                patch.put("image_url", updatedProfile.getImageUrl());
            }
            if (isNotBlank(updatedProfile.getPassword()) && !updatedProfile.getPassword().equals(profile.getPassword())) {
                patch.put("userpassword", updatedProfile.getPassword());
            }
            return openAmIntegrationService.update(patch, session(COOKIE_NAME_USERNAME), session(COOKIE_NAME_TOKEN));
        }).map((updated) -> ok(updated.toString()));
    }
}
