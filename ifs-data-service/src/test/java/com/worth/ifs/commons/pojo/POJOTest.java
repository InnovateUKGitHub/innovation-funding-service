package com.worth.ifs.commons.pojo;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.filters.FilterEnum;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.*;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import com.worth.ifs.address.domain.*;
import com.worth.ifs.address.resource.*;
import com.worth.ifs.alert.domain.*;
import com.worth.ifs.alert.resource.*;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.application.resource.*;
import com.worth.ifs.assessment.domain.*;
import com.worth.ifs.assessment.resource.*;
import com.worth.ifs.authentication.resource.*;
import com.worth.ifs.commons.rest.*;
import com.worth.ifs.competition.domain.*;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.file.domain.*;
import com.worth.ifs.finance.domain.*;
import com.worth.ifs.form.domain.*;
import com.worth.ifs.invite.domain.*;
import com.worth.ifs.invite.resource.*;
import com.worth.ifs.organisation.domain.*;
import com.worth.ifs.project.bankdetails.domain.*;
import com.worth.ifs.project.bankdetails.resource.*;
import com.worth.ifs.project.domain.*;
import com.worth.ifs.project.finance.resource.*;
import com.worth.ifs.project.finance.workflow.financechecks.resource.*;
import com.worth.ifs.project.resource.*;
import com.worth.ifs.project.status.resource.*;
import com.worth.ifs.registration.resource.*;
import com.worth.ifs.token.domain.*;
import com.worth.ifs.token.resource.*;
import com.worth.ifs.user.domain.*;
import com.worth.ifs.user.resource.*;
import com.worth.ifs.workflow.resource.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class POJOTest {
    // Configured for expectation, so we know when a class gets added or removed.
    private static final int EXPECTED_RESOURCES = 38;

    // The package to test
    private static final String POJO_PACKAGE = "com.worth.ifs";

    private List<PojoClass> classes;
    private Validator validator;
    private List<Class<?>> classesToTest = Arrays.asList(
            Affiliation.class,
            AffiliationResource.class,
            Alert.class,
            AlertResource.class,
            ApplicationRejectionResource.class,
            AssessmentFundingDecisionResource.class,
            AssessorFormInputResponse.class,
            AssessorFormInputResponseResource.class,
            Address.class,
            OrganisationType.class,
            Application.class,
            ApplicationStatus.class,
            FileEntry.class,
            FinanceRowMetaField.class,
            FormValidator.class,
            FormInputType.class,
            OrganisationAddress.class,
            FinanceRowMetaValue.class,
            Token.class,
            InviteOrganisation.class,
            Section.class,
            ApplicationFinance.class,
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
            CompetitionSetupQuestionResource.class,
            GuidanceRowResource.class
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
                    && !pojoClass.getClazz().equals(Competition.class)
                    && (classes.stream().anyMatch(pojoClass.getClazz()::equals) || pojoClass.getClazz().getName().endsWith("Resource"))
                    && !Modifier.isAbstract(pojoClass.getClazz().getModifiers());
        }
    }
}
