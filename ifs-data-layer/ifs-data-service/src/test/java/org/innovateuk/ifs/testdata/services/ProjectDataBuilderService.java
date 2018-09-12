package org.innovateuk.ifs.testdata.services;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.testdata.builders.ProjectDataBuilder;
import org.innovateuk.ifs.testdata.builders.ServiceLocator;
import org.innovateuk.ifs.testdata.builders.data.ApplicationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.testdata.builders.ProjectDataBuilder.newProjectData;
import static org.innovateuk.ifs.testdata.services.CsvUtils.readProjects;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * A service that {@link org.innovateuk.ifs.testdata.BaseGenerateTestData} uses to generate Project data.  While
 * {@link org.innovateuk.ifs.testdata.BaseGenerateTestData} is responsible for gathering CSV information and
 * orchestarting the building of it, this service is responsible for taking the CSV data passed to it and using
 * the appropriate builders to generate and update entities.
 */
@Component
@Lazy
public class ProjectDataBuilderService extends BaseDataBuilderService {

    private List<CsvUtils.ProjectLine> projectLines;

    @Autowired
    private TestService testService;

    @Autowired
    private GenericApplicationContext applicationContext;

    private ProjectDataBuilder projectDataBuilder;

    @PostConstruct
    public void setup() {
        projectLines = readProjects();

        ServiceLocator serviceLocator = new ServiceLocator(applicationContext, COMP_ADMIN_EMAIL, PROJECT_FINANCE_EMAIL);

        projectDataBuilder = newProjectData(serviceLocator);
    }

    public void createProjects(List<ApplicationData> applications) {

        applications.forEach(application -> {

            Optional<CsvUtils.ProjectLine> projectForApplication = simpleFindFirst(projectLines, l ->
                    l.name.equals(application.getApplication().getName()));

            projectForApplication.ifPresent(this::createProject);
        });
    }

    private void createProject(CsvUtils.ProjectLine line) {

        ProjectDataBuilder baseBuilder = this.projectDataBuilder.
                withExistingProject(line.name).
                withStartDate(line.startDate);

        UnaryOperator<ProjectDataBuilder> assignProjectManagerIfNecessary =
                builder -> !isBlank(line.projectManager) ? builder.withProjectManager(line.projectManager) : builder;

        UnaryOperator<ProjectDataBuilder> setProjectAddressIfNecessary =
                builder -> line.projectAddressAdded ? builder.withProjectAddressOrganisationAddress() : builder;

        UnaryOperator<ProjectDataBuilder> setMonitoringOfficerIfNecessary =
                builder -> !isBlank(line.moFirstName) ?
                        builder.withMonitoringOfficer(line.moFirstName, line.moLastName, line.moEmail, line.moPhoneNumber) : builder;

        UnaryOperator<ProjectDataBuilder> selectFinanceContactsIfNecessary = builder -> {

            ProjectDataBuilder currentBuilder = builder;

            for (Pair<String, String> fc : line.financeContactsForOrganisations) {
                currentBuilder = currentBuilder.withFinanceContact(fc.getLeft(), fc.getRight());
            }

            return currentBuilder;
        };

        UnaryOperator<ProjectDataBuilder> submitBankDetailsIfNecessary = builder -> {

            ProjectDataBuilder currentBuilder = builder;

            for (Triple<String, String, String> bd : line.bankDetailsForOrganisations) {
                currentBuilder = currentBuilder.withBankDetails(bd.getLeft(), bd.getMiddle(), bd.getRight());
            }

            return currentBuilder;
        };

        UnaryOperator<ProjectDataBuilder> withdrawIfNecessary = builder -> {
            if (ProjectState.WITHDRAWN.equals(line.projectState)) {
                return builder.withAmendedStatus(ProjectState.WITHDRAWN);
            } else {
                return builder;
            }
        };

        testService.doWithinTransaction(() ->
                assignProjectManagerIfNecessary.
                        andThen(setProjectAddressIfNecessary).
                        andThen(setMonitoringOfficerIfNecessary).
                        andThen(selectFinanceContactsIfNecessary).
                        andThen(submitBankDetailsIfNecessary).
                        andThen(withdrawIfNecessary).
                        apply(baseBuilder).
                        build());

    }
}
