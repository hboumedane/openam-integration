package org.spend.openam.integration.controller;

import org.spend.openam.integration.service.OpenAmIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import play.libs.F.Promise;
import play.mvc.Result;

import static play.libs.Json.toJson;

@Controller
public class SearchControllerImpl extends play.mvc.Controller implements SearchController {

    @Autowired
    private OpenAmIntegrationService openAmIntegrationService;

    @Override
    public Result pageUsers() {
        return ok(org.spend.openam.integration.view.html.users.render());
    }

    @Override
    public Promise<Result> listAllUsers() {
        return openAmIntegrationService.listAllUsers().map((users) -> {
            users.forEach((user) -> {
                user.setGroup(null);
                user.setPassword(null);
            });
            return users;
        }).map(users -> ok(toJson(users)));
    }
}
