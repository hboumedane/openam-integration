package org.spend.openam.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import scala.concurrent.ExecutionContext;

import static play.Logger.debug;
import static play.libs.Akka.system;

@Configuration
@ComponentScan
public class Global extends GlobalSettings {
    public static AnnotationConfigApplicationContext context;

    @Override
    public void onStart(Application application) {
        super.onStart(application);
        context = new AnnotationConfigApplicationContext(Global.class);

        for (String bean : context.getBeanDefinitionNames()) {
            debug(bean);
        }
    }

    @Override
    public void onStop(final Application app) {
        context.close();
        super.onStop(app);
    }

    @Override
    public <A> A getControllerInstance(Class<A> clazz) {
        return context.getBean(clazz);
    }

    @Bean
    @Qualifier("wsExecutionContext")
    public ExecutionContext wsExecutionContext() {
        return system().dispatchers().lookup("akka.ws-dispatcher");
    }


    @Bean
    @Qualifier("security$AuthenticatedAction")
    public play.mvc.Security.AuthenticatedAction security$AuthenticatedAction() {
        return new play.mvc.Security.AuthenticatedAction();
    }
}

