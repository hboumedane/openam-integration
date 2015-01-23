package org.spend.openam.integration.controller;

import play.libs.F.Promise;
import play.mvc.Result;

/**
 * Main controller with two actions: export report and list all reports
 */
public interface WelcomeController {

    Result pageIndex();

    Promise<Result> authenticate();

    Promise<Result> register();

    Result getUsername();
}
