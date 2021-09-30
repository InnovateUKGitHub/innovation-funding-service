//package org.innovateuk.ifs.testdata.builders;
//
//import org.innovateuk.ifs.project.resource.ProjectResource;
//import org.innovateuk.ifs.user.resource.UserResource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.function.BiConsumer;
//
///**
// * Generates data for Monitoring officer on the platform
// */
//public class MonitoringOfficerDataBuilder extends BaseDataBuilder<Void, MonitoringOfficerDataBuilder> {
//
//    private static final Logger LOG = LoggerFactory.getLogger(MonitoringOfficerDataBuilder.class);
//
//    public MonitoringOfficerDataBuilder assignProject(String email, long applicationId) {
//        return with(data ->
//                doAs(ifsAdmin(), () -> {
//                    ProjectResource project = projectService.getByApplicationId(applicationId).getSuccess();
//                    if (project != null) {
//                        UserResource user = userService.findByEmail(email).getSuccess();
//                        monitoringOfficerService.assignProjectToMonitoringOfficer(project.getId(), user.getId()).getSuccess();
//                    }
//                }));
//    }
//
//    public MonitoringOfficerDataBuilder(List<BiConsumer<Integer, Void>> newActions, ServiceLocator serviceLocator) {
//        super(newActions, serviceLocator);
//    }
//
//    public static MonitoringOfficerDataBuilder newMonitoringOfficerData(ServiceLocator serviceLocator) {
//        return new MonitoringOfficerDataBuilder(Collections.emptyList(), serviceLocator);
//    }
//
//    @Override
//    protected MonitoringOfficerDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
//        return new MonitoringOfficerDataBuilder(actions, serviceLocator);
//    }
//
//    @Override
//    protected Void createInitial() {
//        return null;
//    }
//
//    @Override
//    protected void postProcess(int index, Void instance) {
//        super.postProcess(index, instance);
//        LOG.info("Project assigned to MO");
//    }
//}
