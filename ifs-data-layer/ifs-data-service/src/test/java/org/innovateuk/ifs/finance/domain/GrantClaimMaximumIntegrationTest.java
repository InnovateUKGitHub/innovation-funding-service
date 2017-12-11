package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.testdata.builders.ServiceLocator;
import org.innovateuk.ifs.testdata.builders.data.ApplicationData;
import org.innovateuk.ifs.testdata.builders.data.CompetitionData;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.testdata.builders.ApplicationDataBuilder.newApplicationData;
import static org.innovateuk.ifs.testdata.builders.CompetitionDataBuilder.newCompetitionData;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;

/**
 * TODO DW - document this class
 */
@Rollback
public class GrantClaimMaximumIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private GenericApplicationContext applicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private FinanceRowService financeRowService;

    @Test
    @Rollback
    public void test() {

        RoleResource adminRole = newRoleResource().withType(UserRoleType.IFS_ADMINISTRATOR).build();

        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(adminRole)).build());

//        User compAdmin = new User("John", "Doe", "john.doe@innovateuk.test", "", "1234");
//        compAdmin.addRole(roleRepository.findOneByName(UserRoleType.COMP_ADMIN.getName()));
//        userRepository.save(compAdmin);

        ServiceLocator serviceLocator = new ServiceLocator(applicationContext);

        CompetitionData competitionData = newCompetitionData(serviceLocator).
                createCompetition().
                withBasicData("APC Competition", "Advanced Propulsion Centre",
                        singletonList("Digital manufacturing"), "Materials and manufacturing",
                        "Feasibility studies", "ian.cooper@innovateuk.test",
                        "john.doe@innovateuk.test", "DET1536/1537", "875",
                        "CCCC", "16014", 1, BigDecimal.valueOf(100L),
                        false, false, false,
                        "single-or-collaborative", singletonList(OrganisationTypeEnum.BUSINESS),
                        50, false, "").
                withApplicationFormFromTemplate().
                withNewMilestones().
                withOpenDate(ZonedDateTime.now().minus(1, ChronoUnit.DAYS)).
                withBriefingDate(addDays(1)).
                withSubmissionDate(addDays(2)).
                withAllocateAssesorsDate(addDays(3)).
                withAssessorBriefingDate(addDays(4)).
                withAssessorAcceptsDate(addDays(5)).
                withAssessorsNotifiedDate(addDays(6)).
                withAssessorEndDate(addDays(7)).
                withAssessmentClosedDate(addDays(8)).
                withLineDrawDate(addDays(9)).
                withAsessmentPanelDate(addDays(10)).
                withPanelDate(addDays(11)).
                withFundersPanelDate(addDays(12)).
                withFundersPanelEndDate(addDays(13)).
                withReleaseFeedbackDate(addDays(14)).
                withFeedbackReleasedDate(addDays(15)).
                withPublicContent(true, "blah", "blah", "blah",
                        "blah", FundingType.GRANT, "blah", singletonList("blah"), false).
                withSetupComplete().
                build();

        UserResource applicant = userService.findByEmail("steve.smith@empire.com").getSuccessObjectOrThrowException();

        ApplicationData applicationData = newApplicationData(serviceLocator).
                withCompetition(competitionData.getCompetition()).
                withBasicDetails(applicant, "APC Application", "Feasibility studies", false).
                beginApplication().
                withFinances(financeBuilder -> financeBuilder.
                    withOrganisation("Empire Ltd").
                    withUser("steve.smith@empire.com").
                    withIndustrialCosts(costBuilder -> costBuilder.
                        withOrganisationSize(1L).
                        withGrantClaim(0))).
                build();

        Organisation applicantOrganisation = organisationRepository.findOneByName("Empire Ltd");

        setLoggedInUser(applicant);

        ApplicationFinanceResource financeDetails = financeRowService.financeDetails(applicationData.getApplication().getId(), applicantOrganisation.getId()).getSuccessObjectOrThrowException();
        System.out.println(financeDetails.getMaximumFundingLevel());
    }

    private ZonedDateTime addDays(int amount) {
        return ZonedDateTime.now().plus(amount, ChronoUnit.DAYS);
    }
}
