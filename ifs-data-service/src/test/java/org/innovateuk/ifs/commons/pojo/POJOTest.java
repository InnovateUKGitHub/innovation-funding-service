package org.innovateuk.ifs.commons.pojo;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.filters.FilterEnum;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.*;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.alert.domain.Alert;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationStatus;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.resource.PageResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.authentication.resource.CreateUserResource;
import org.innovateuk.ifs.authentication.resource.UpdateUserResource;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsStatusResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.resource.TokenResource;
import org.innovateuk.ifs.user.domain.Affiliation;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class POJOTest {
    // Configured for expectation, so we know when a class gets added or removed.
    private static final int EXPECTED_RESOURCES = 37;

    // The package to test
    private static final String POJO_PACKAGE = "org.innovateuk.ifs";

    private List<PojoClass> classes;
    private Validator validator;
    private List<Class<?>> classesToTest = Arrays.asList(
            Affiliation.class,
            AffiliationResource.class,
            Alert.class,
            AlertResource.class,
            ApplicationAssessmentSummaryResource.class,
            ApplicationRejectionResource.class,
            AssessmentFundingDecisionResource.class,
            AssessorFormInputResponse.class,
            AssessorFormInputResponseResource.class,
            Assessment.class,
            AssessmentTotalScoreResource.class,
            AvailableAssessorResource.class,
            AssessorCreatedInviteResource.class,
            AssessorInviteOverviewResource.class,
            AssessorInviteToSendResource.class,
            Address.class,
            OrganisationType.class,
            Application.class,
            ApplicationStatus.class,
            FileEntry.class,
            FormValidator.class,
            FormInputType.class,
            OrganisationAddress.class,
            Token.class,
            InviteOrganisation.class,
            Section.class,
            ApplicationFinance.class,
            ProjectFinanceResource.class,
            Competition.class,
            CreateUserResource.class,
            UpdateUserResource.class,
            CompetitionSummaryResource.class,
            Project.class,
            ProjectResource.class,
            InviteProjectResource.class,
            ProjectUserResource.class,
            AddressType.class,
            AddressTypeResource.class,
            BankDetails.class,
            BankDetailsResource.class,
            MonitoringOfficerResource.class,
            CostCategoryGroupResource.class,
            CostCategoryResource.class,
            CostGroupResource.class,
            CostResource.class,
            CostTimePeriodResource.class,
            CostCategoryTypeResource.class,
            SpendProfileResource.class,
            SpendProfileTableResource.class,
            ViabilityResource.class,
            CompetitionFunderResource.class,
            CompetitionCountResource.class,
            LocalDateResource.class,
            RejectionReasonResource.class,
            ProjectLeadStatusResource.class,
            ProjectPartnerStatusResource.class,
            ProjectTeamStatusResource.class,
            ProjectBankDetailsStatusSummary.class,
            BankDetailsStatusResource.class,
            ProjectStatusResource.class,
            CompetitionProjectsStatusResource.class,
            EthnicityResource.class,
            UserRegistrationResource.class,
            FinanceCheckResource.class,
            FinanceCheckProcessResource.class,
            PartnerOrganisationResource.class,
            SpendProfileCSVResource.class,
            FinanceCheckSummaryResource.class,
            FinanceCheckPartnerStatusResource.class,
            SpendProfileCSVResource.class,
            ContractResource.class,
            ProfileContractResource.class,
            ProfileSkillsResource.class,
            UserProfileResource.class,
            UserProfileStatusResource.class,
            AssessorCountOptionResource.class,
            CompetitionSetupQuestionResource.class,
            GuidanceRowResource.class,
            AssessmentSubmissionsResource.class,
            NewUserStagedInviteResource.class,
            ExistingUserStagedInviteResource.class,
            NewUserStagedInviteListResource.class,
            ApplicationCountSummaryResource.class
            );

    @Before
    public void setup() {
        classes = PojoClassFactory.getPojoClassesRecursively(POJO_PACKAGE, new FilterPackages(classesToTest));

        validator = ValidatorBuilder.create()
            .with(
                new GetterMustExistRule(),
                new SetterMustExistRule(),
                new NoNestedClassRule(),
                new NoStaticExceptFinalRule(),
                new SerializableMustHaveSerialVersionUIDRule(),
                new NoFieldShadowingRule(),
                new NoPublicFieldsExceptStaticFinalRule(),
                new TestClassMustBeProperlyNamedRule()
            )
            .with(
                new SetterTester(),
                new GetterTester()
            )
            .build();
    }

    @Test
    public void ensureExpectedPojoCount() {
        Assert.assertEquals(String.format("Classes added / removed? %s => %s ", classesToTest.size()+EXPECTED_RESOURCES, classes.size()), classesToTest.size()+EXPECTED_RESOURCES, classes.size());
    }

    @Test
    public void testPojoStructureAndBehavior() {
        validator.validateRecursively(POJO_PACKAGE, new FilterPackages(classesToTest), new FilterEnum());
    }

    private static class FilterPackages implements  PojoClassFilter {
        private final List<Class<?>> classes;

        FilterPackages(List<Class<?>> classes){
            this.classes = classes;
        }

        @Override
        public boolean include(PojoClass pojoClass) {
            return !pojoClass.getClazz().equals(ProcessOutcomeResource.class)
                    && !pojoClass.getClazz().equals(Token.class)
                    && !pojoClass.getClazz().equals(TokenResource.class)
                    && !pojoClass.getClazz().equals(PageResource.class)
                    && !pojoClass.getClazz().equals(FinanceRowMetaField.class)
                    && !pojoClass.getClazz().equals(FinanceRowMetaValue.class)
                    && !pojoClass.getClazz().equals(Competition.class)
                    && (classes.stream().anyMatch(pojoClass.getClazz()::equals) || pojoClass.getClazz().getName().endsWith("Resource"))
                    && !Modifier.isAbstract(pojoClass.getClazz().getModifiers());
        }
    }
}
