package org.spend.openam.integration.controller;

import org.spend.openam.integration.security.OpenAmPolicyAuthenticated;
import play.libs.F.Promise;
import play.mvc.Result;
import play.mvc.Security.Authenticated;


@Authenticated(OpenAmPolicyAuthenticated.class)
public interface ProfileController {

    Result pageProfile();

    Promise<Result> userProfile();

    Promise<Result> logout();

    Promise<Result> update();
}
