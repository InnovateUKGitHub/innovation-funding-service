package org.innovateuk.ifs.testdata.services;

import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.testdata.builders.MonitoringOfficerDataBuilder;
import org.innovateuk.ifs.testdata.builders.ServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

import static org.innovateuk.ifs.testdata.builders.MonitoringOfficerDataBuilder.newMonitoringOfficerData;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.COMP_ADMIN_EMAIL;
import static org.innovateuk.ifs.testdata.services.BaseDataBuilderService.PROJECT_FINANCE_EMAIL;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

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

    public void buildMonitoringOfficersWithProject(List<ProjectResource> projects, List<CsvUtils.MonitoringOfficerUserLine> monitoringOfficers) {
        projects.forEach(project -> {
            List<CsvUtils.MonitoringOfficerUserLine> monitoringOfficerUserLines = simpleFilter(monitoringOfficers, l ->
                    Objects.equals(l.applicationNumber, project.getApplication()));

            monitoringOfficerUserLines.forEach(mo -> {
                monitoringOfficerDataBuilder.assignProject(mo.emailAddress, project.getApplication());
            });
        });
    }

//    public void buildMonitoringOfficersWithProject(CsvUtils.ExternalUserLine monitoringOfficer, List<CsvUtils.MonitoringOfficerUserLine> monitoringOfficerUserLines) {
//
//        monitoringOfficerUserLines.forEach(mo -> {
//            if(mo.emailAddress.equals(monitoringOfficer.emailAddress)) {
//                monitoringOfficerDataBuilder.assignProject(mo.email, mo.applicationNumber);
//            }});
//    }
}
