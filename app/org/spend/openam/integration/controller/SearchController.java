package org.spend.openam.integration.controller;

import org.spend.openam.integration.security.FunnyUserAuthenticated;
import org.spend.openam.integration.security.OpenAmPolicyAuthenticated;
import play.libs.F.Promise;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

@Authenticated(FunnyUserAuthenticated.class)
public interface SearchController {
    Result pageUsers();

    Promise<Result> listAllUsers();
}
