package org.innovateuk.ifs.testdata.services;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.testdata.builders.SupporterDataBuilder;
import org.innovateuk.ifs.testdata.builders.ServiceLocator;
import org.innovateuk.ifs.testdata.services.CsvUtils.ExternalUserLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.innovateuk.ifs.testdata.builders.SupporterDataBuilder.newSupporterData;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.COMP_ADMIN_EMAIL;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.PROJECT_FINANCE_EMAIL;

@Component
@Lazy
public class SupporterDataService {

    @Autowired
    private GenericApplicationContext applicationContext;


    private SupporterDataBuilder supporterDataBuilder;

    @PostConstruct
    public void postConstruct() {
        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);
        supporterDataBuilder = newSupporterData(serviceLocator);
    }

    public void buildSupporters(List<ApplicationResource> applications, List<ExternalUserLine> supporters) {
        applications.forEach(app -> {
            String appName = app.getName();
            supporters.forEach(funder -> {
                String funderEmail = funder.emailAddress;
                int mod = (appName + funderEmail).length() % 4;
                SupporterState decision = null;
                switch (mod) {
                    case(0):
                        decision = SupporterState.CREATED;
                        break;
                    case(1):
                        decision = SupporterState.ACCEPTED;
                        break;
                    case(2):
                        decision = SupporterState.REJECTED;
                        break;
                }
                if (decision != null) {
                    supporterDataBuilder.withDecision(funderEmail, app.getId(), decision).build();
                }
            });
        });


    }
}
