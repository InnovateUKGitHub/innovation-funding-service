package org.innovateuk.ifs.testdata.services;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.innovateuk.ifs.testdata.builders.CofunderDataBuilder;
import org.innovateuk.ifs.testdata.builders.ServiceLocator;
import org.innovateuk.ifs.testdata.services.CsvUtils.ExternalUserLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.innovateuk.ifs.testdata.builders.CofunderDataBuilder.newCofunderData;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.COMP_ADMIN_EMAIL;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.PROJECT_FINANCE_EMAIL;

@Service
public class CofunderDataService {

    @Autowired
    private GenericApplicationContext applicationContext;


    private CofunderDataBuilder cofunderDataBuilder;

    @PostConstruct
    public void postConstruct() {
        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);
        cofunderDataBuilder = newCofunderData(serviceLocator);
    }

    public void buildCofunders(List<ApplicationResource> applications, List<ExternalUserLine> cofunders) {
        applications.forEach(app -> {
            String appName = app.getName();
            cofunders.forEach(funder -> {
                String funderEmail = funder.emailAddress;
                int mod = (appName + funderEmail).length() % 4;
                CofunderState decision = null;
                switch (mod) {
                    case(0):
                        decision = CofunderState.CREATED;
                        break;
                    case(1):
                        decision = CofunderState.ACCEPTED;
                        break;
                    case(2):
                        decision = CofunderState.REJECTED;
                        break;
                }
                if (decision != null) {
                    cofunderDataBuilder.withDecision(funderEmail, app.getId(), decision).build();
                }
            });
        });


    }
}
