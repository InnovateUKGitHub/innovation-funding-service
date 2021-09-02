package org.innovateuk.ifs.testdata.services;

import org.innovateuk.ifs.testdata.builders.MonitoringOfficerDataBuilder;
import org.innovateuk.ifs.testdata.builders.ServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
    public void readCsvs() {
        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);
        monitoringOfficerDataBuilder = newMonitoringOfficerData(serviceLocator);
    }

//   public void createMonitoringOfficers(List<ProjectData> projects, List<CsvUtils.MonitoringOfficerUserLine> monitoringOfficerUserLineList) {
//        projects.forEach(project -> {
//            List<CsvUtils.MonitoringOfficerUserLine> monitoringOfficerLinesForProject = simpleFilter(monitoringOfficerUserLineList, mo ->
//                    Objects.equals(mo.projectTitle, project.getProject().getName()));
//
//            monitoringOfficerLinesForProject.forEach(moLine -> createMonitoringOfficers(moLine, ));
//            )
//        });
//   }
//
//    private void createMonitoringOfficer(CsvUtils.MonitoringOfficerUserLine line, List<CsvUtils.InviteLine> inviteLines) {
//
//    }


    }
