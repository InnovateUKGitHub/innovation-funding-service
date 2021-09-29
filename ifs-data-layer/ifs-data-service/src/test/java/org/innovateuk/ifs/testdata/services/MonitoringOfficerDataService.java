package org.innovateuk.ifs.testdata.services;

import org.innovateuk.ifs.testdata.builders.MonitoringOfficerDataBuilder;
import org.innovateuk.ifs.testdata.builders.ServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.innovateuk.ifs.testdata.builders.MonitoringOfficerDataBuilder.newMonitoringOfficerData;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.COMP_ADMIN_EMAIL;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.PROJECT_FINANCE_EMAIL;

@Component
@Lazy
public class MonitoringOfficerDataService {

    @Autowired
    private GenericApplicationContext applicationContext;

    private MonitoringOfficerDataBuilder monitoringOfficerDataBuilder;

    @PostConstruct
    public void postConstruct() {
        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);
        monitoringOfficerDataBuilder = newMonitoringOfficerData(serviceLocator);
    }

    public void buildMonitoringOfficersWithProject(CsvUtils.ExternalUserLine monitoringOfficer, List<CsvUtils.MonitoringOfficerUserLine> monitoringOfficerUserLines) {

        monitoringOfficerUserLines.forEach(mo -> {
                if(mo.emailAddress.equals(monitoringOfficer.emailAddress)) {
                    monitoringOfficerDataBuilder.assignProject(mo.email, mo.applicationNumber);
                }});
    }
}
