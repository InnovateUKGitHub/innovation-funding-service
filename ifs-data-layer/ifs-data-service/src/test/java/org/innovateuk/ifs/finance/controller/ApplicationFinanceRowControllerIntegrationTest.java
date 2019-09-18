package org.innovateuk.ifs.finance.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.security.SecuritySetter.swapOutForUser;
import static org.innovateuk.ifs.finance.resource.OrganisationSize.*;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.*;

@Rollback
public class ApplicationFinanceRowControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationFinanceRowController> {

    private GrantClaimPercentage grantClaim;
    private Materials materials;
    private LabourCost labourCost;
    private LabourCost labourCostDaysPerYear;

    private CapitalUsage capitalUsage;
    private SubContractingCost subContractingCost;
    private TravelCost travelCost;
    private OtherCost otherCost;
    private OtherFunding otherFunding;
    private OtherFunding otherFundingCost;
    private Overhead overhead;

    private String overMaxAllowedTextSize;

    @Mock
    private BindingResult bindingResult;

    @Autowired
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    private FinanceRow grandClaimCost;
    private ApplicationFinance applicationFinance;
    private long leadApplicantId;
    private long leadApplicantProcessRole;
    public static final long APPLICATION_ID = 1L;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Autowired
    protected void setControllerUnderTest(ApplicationFinanceRowController controller) {
        this.controller = controller;
    }

    @Before
    public void prepare(){
        loginSteveSmith();
        grandClaimCost = applicationFinanceRowRepository.findById(48L).get();
        applicationFinance = ((ApplicationFinanceRow) grandClaimCost).getTarget();

        grantClaim = (GrantClaimPercentage) controller.get(48L).getSuccess();
        materials = (Materials) controller.get(12L).getSuccess();
        labourCost = (LabourCost) controller.get(4L).getSuccess();
        labourCostDaysPerYear = (LabourCost) controller.get(1L).getSuccess();

        otherFunding = (OtherFunding) controller.get(54L).getSuccess();

        overhead =  (Overhead) controller.get(51L).getSuccess();
        CapitalUsage capitalUsageResult = (CapitalUsage) controller.create(new CapitalUsage(applicationFinance.getId())).getSuccess();
        capitalUsage = (CapitalUsage) controller.get(capitalUsageResult.getId()).getSuccess();
        SubContractingCost subContractingCostResult = (SubContractingCost) controller.create(new SubContractingCost(applicationFinance.getId())).getSuccess();
        subContractingCost = (SubContractingCost) controller.get(subContractingCostResult.getId()).getSuccess();
        TravelCost travelCostResult = (TravelCost) controller.create(new TravelCost(applicationFinance.getId())).getSuccess();
        travelCost = (TravelCost) controller.get(travelCostResult.getId()).getSuccess();
        OtherCost otherCostResult = (OtherCost) controller.create(new OtherCost(applicationFinance.getId())).getSuccess();
        otherCost = (OtherCost) controller.get(otherCostResult.getId()).getSuccess();
        OtherFunding otherFundingResult = (OtherFunding) controller.create(new OtherFunding(applicationFinance.getId())).getSuccess();
        otherFundingCost = (OtherFunding) controller.get(otherFundingResult.getId()).getSuccess();

        leadApplicantId = 1L;
        leadApplicantProcessRole = 1L;

        overMaxAllowedTextSize = StringUtils.repeat("<ifs_test>", 30);

        List<ProcessRole> proccessRoles = new ArrayList<>();
        Application application = new Application("");
        application.setId(APPLICATION_ID);
        proccessRoles.add(newProcessRole().withId(leadApplicantProcessRole).withApplication(application).build());
        User user = new User(leadApplicantId, "steve", "smith", "steve.smith@empire.com", "", "123abc");
        proccessRoles.get(0).setUser(user);
        swapOutForUser(userMapper.mapToResource(user));

    }

    /* Labour Section Tests */

    @Rollback
    @Test
    public void testValidationLabour(){

        assertEquals(new BigDecimal("129.31034"), labourCost.getRate(labourCostDaysPerYear.getLabourDays()).setScale(5, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(new BigDecimal("90000"), (labourCost.getTotal(labourCostDaysPerYear.getLabourDays())).setScale(0, BigDecimal.ROUND_HALF_EVEN) );

        RestResult<ValidationMessages> validationMessages = controller.update(labourCost.getId(), labourCost);
        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    @Rollback
    @Test
    public void testValidationLabourUpdateIncorrectValues(){
        labourCost.setRole("");
        labourCost.setLabourDays(-50);
        labourCost.setGrossEmployeeCost(new BigDecimal("-500000"));

        RestResult<ValidationMessages> validationMessages = controller.update(labourCost.getId(), labourCost);
        assertTrue(validationMessages.isSuccess());
        assertNotNull(validationMessages.getOptionalSuccessObject().get());

        ValidationMessages messages = validationMessages.getSuccess();
        assertEquals(3, messages.getErrors().size());
        assertEquals(labourCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = asList(
                fieldError("labourDays", -50, "validation.field.max.value.or.higher", 1),
                fieldError("grossEmployeeCost", new BigDecimal("-500000"), "validation.field.max.value.or.higher", 1),
                fieldError("role", "", "validation.field.must.not.be.blank"));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    @Rollback
    @Test
    public void testValidationLabourUpdateIncorrectMaxValues() {

        labourCost.setRole(overMaxAllowedTextSize);
        labourCost.setLabourDays(400);
        labourCost.setGrossEmployeeCost(new BigDecimal("100000"));

        RestResult<ValidationMessages> validationMessages = controller.update(labourCost.getId(), labourCost);

        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    /* Overhead Section Tests */

    @Rollback
    @Test
    public void testValidationOverhead() {

        BigDecimal overheadTotal =  overhead.getTotal();
        assertEquals(0, overheadTotal.intValue());
        overheadTotal = new BigDecimal("50000");
        assertEquals(11500, overhead.getRate() * overheadTotal.intValue() / 100);

        RestResult<ValidationMessages> validationMessages = controller.update(overhead.getId(), overhead);
        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    @Rollback
    @Test
    public void testValidationOverheadUpdateMinRate(){

        overhead.setRate(-10);
        assertEquals(-100, overhead.getRate() * 1000/100);

        RestResult<ValidationMessages> validationMessages = controller.update(overhead.getId(), overhead);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(1, messages.getErrors().size());
        assertEquals(overhead.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = singletonList(
                fieldError("rate", -10, "validation.field.max.value.or.higher", 1));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    @Rollback
    @Test
    public void testValidationOverheadUpdateMaxRate(){
        overhead.setRate(150);
        assertEquals(1500, overhead.getRate() * 1000/100);

        RestResult<ValidationMessages> validationMessages = controller.update(overhead.getId(), overhead);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(1, messages.getErrors().size());
        assertEquals(overhead.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = singletonList(
                fieldError("rate", 150, "validation.field.max.value.or.lower", 100));

        assertErrorsAsExpected(messages, expectedErrors);
    }


    /* Material Section Tests */

    @Rollback
    @Test
    public void testValidationMaterial(){
        assertEquals(new BigDecimal("2000"), materials.getTotal());

        RestResult<ValidationMessages> validationMessages = controller.update(materials.getId(), materials);
        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    @Rollback
    @Test
    public void testValidationMaterialUpdateInvalidValues(){
        materials.setCost(new BigDecimal("-5"));

        materials.setItem("");
        materials.setQuantity(-5);

        RestResult<ValidationMessages> validationMessages = controller.update(materials.getId(), materials);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(materials.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = asList(
                fieldError("item", "", "validation.field.must.not.be.blank"),
                fieldError("quantity", -5, "validation.field.max.value.or.higher", 1),
                fieldError("cost", new BigDecimal("-5"), "validation.field.max.value.or.higher", 1));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    @Rollback
    @Test
    public void testValidationMaterialUpdateMaxValues() {

        materials.setCost(new BigDecimal("1000"));
        materials.setItem(overMaxAllowedTextSize);
        materials.setQuantity(1000);

        RestResult<ValidationMessages> validationMessages = controller.update(materials.getId(), materials);
        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());

    }

    /* Capital Usage Section Tests */

    @Rollback
    @Test
    public void testValidationCapitalUsageUpdateSuccess() {

        capitalUsage.setDescription(overMaxAllowedTextSize);
        capitalUsage.setExisting("New");
        capitalUsage.setDeprecation(5);
        capitalUsage.setResidualValue(new BigDecimal("1000"));
        capitalUsage.setNpv(new BigDecimal("10000"));
        capitalUsage.setUtilisation(99);

        assertEquals(new BigDecimal("8910.00"), capitalUsage.getTotal());

        RestResult<ValidationMessages> validationMessages = controller.update(capitalUsage.getId(), capitalUsage);
        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    @Rollback
    @Test
    public void testValidationCapitalUsageUpdateIncorrectValues(){
        capitalUsage.setDescription("");
        capitalUsage.setExisting("");
        capitalUsage.setDeprecation(-5);
        capitalUsage.setResidualValue(new BigDecimal("-100000"));
        capitalUsage.setNpv(new BigDecimal("-10000"));
        capitalUsage.setUtilisation(-5);

        RestResult<ValidationMessages> validationMessages = controller.update(capitalUsage.getId(), capitalUsage);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(capitalUsage.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = asList(
                fieldError("description", "", "validation.field.must.not.be.blank"),
                fieldError("existing", "", "validation.field.must.not.be.blank"),
                fieldError("deprecation", -5, "validation.field.max.value.or.higher", 1),
                fieldError("residualValue", new BigDecimal("-100000"), "validation.field.max.value.or.higher", 1),
                fieldError("npv", new BigDecimal("-10000"), "validation.field.max.value.or.higher", 1),
                fieldError("utilisation", -5, "validation.field.max.value.or.higher", 1));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    @Rollback
    @Test
    public void testValidationCapitalUsageUpdateOverMaxAllowedValues(){
        capitalUsage.setDescription("Desc");
        capitalUsage.setExisting("Existing");
        capitalUsage.setDeprecation(1000);
        capitalUsage.setResidualValue(new BigDecimal("1000000"));
        capitalUsage.setNpv(new BigDecimal("1000"));
        capitalUsage.setUtilisation(200);

        RestResult<ValidationMessages> validationMessages = controller.update(capitalUsage.getId(), capitalUsage);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(capitalUsage.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = singletonList(
                fieldError("utilisation", 200, "validation.field.max.value.or.lower", 100));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    @Rollback
    @Test
    @Ignore("The original test that this came from could not have been actually flushing the session and forcing the " +
            "save of the finance_row and finance_row_meta_values, because validation only occurs AFTER a successful " +
            "save occurs in FinanceRowController - these values are over the max MySQL limit for the columns and so could " +
            "never have resulted in a successful save")
    public void testValidationCapitalUsageUpdateExistingAndDescriptionOverMaxAllowedValues(){
        capitalUsage.setDescription(overMaxAllowedTextSize);
        capitalUsage.setExisting(overMaxAllowedTextSize);
        capitalUsage.setDeprecation(1000);
        capitalUsage.setResidualValue(new BigDecimal("1000000"));
        capitalUsage.setNpv(new BigDecimal("1000"));
        capitalUsage.setUtilisation(200);

        RestResult<ValidationMessages> validationMessages = controller.update(capitalUsage.getId(), capitalUsage);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(capitalUsage.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = asList(
                fieldError("existing", overMaxAllowedTextSize, "validation.field.too.many.characters", 0, 255),
                fieldError("utilisation", 200, "validation.field.max.value.or.lower", 100));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    /* SubContracting Section Tests */

    @Rollback
    @Test
    public void testValidationSubContractingCostUpdateSuccess() {

        subContractingCost.setName("Tom Bloggs");
        subContractingCost.setCountry("UK");
        subContractingCost.setRole("Business Analyst");
        subContractingCost.setCost(new BigDecimal("10000"));

        RestResult<ValidationMessages> validationMessages = controller.update(subContractingCost.getId(), subContractingCost);
        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    @Rollback
    @Test
    public void testValidationSubContractingCostUpdateIncorrectValues(){
        subContractingCost.setName("");
        subContractingCost.setCountry("");
        subContractingCost.setRole("");
        subContractingCost.setCost(new BigDecimal("-5000"));

        RestResult<ValidationMessages> validationMessages = controller.update(travelCost.getId(), travelCost);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(travelCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = asList(
                fieldError("item", null, "validation.field.must.not.be.blank"),
                fieldError("cost", null, "validation.field.must.not.be.blank"),
                fieldError("quantity", null, "validation.field.must.not.be.blank"));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    @Rollback
    @Test
    public void testValidationSubContractingCostUpdateOverMaxAllowedValues(){
        subContractingCost.setName(overMaxAllowedTextSize);
        subContractingCost.setCountry(overMaxAllowedTextSize);
        subContractingCost.setRole(overMaxAllowedTextSize);
        subContractingCost.setCost(new BigDecimal("1000000"));

        RestResult<ValidationMessages> validationMessages = controller.update(subContractingCost.getId(), subContractingCost);
        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    /* TravelCost Section Tests */

    @Rollback
    @Test
    public void testValidationTravelCostUpdateSuccess() {
        travelCost.setItem("Travel To Australia for research consultancy");
        travelCost.setCost(new BigDecimal("1000"));
        travelCost.setQuantity(100);

        assertEquals(new BigDecimal("100000"), travelCost.getTotal());

        RestResult<ValidationMessages> validationMessages = controller.update(travelCost.getId(), travelCost);
        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    @Rollback
    @Test
    public void testValidationTravelCostUpdateIncorrectMinValues(){
        travelCost.setItem("");
        travelCost.setCost(new BigDecimal("-1000"));
        travelCost.setQuantity(-500);

        RestResult<ValidationMessages> validationMessages = controller.update(travelCost.getId(), travelCost);
        ValidationMessages messages = validationMessages.getSuccess();
        assertEquals(travelCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = asList(
                fieldError("item", "", "validation.field.must.not.be.blank"),
                fieldError("cost", new BigDecimal("-1000"), "validation.field.max.value.or.higher", 1),
                fieldError("quantity", -500, "validation.field.max.value.or.higher", 1));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    @Rollback
    @Test
    public void testValidationTravelCostUpdateIncorrectMaxAndZeroValues(){

        travelCost.setItem(overMaxAllowedTextSize);
        travelCost.setCost(new BigDecimal("0"));
        travelCost.setQuantity(0);

        RestResult<ValidationMessages> validationMessages = controller.update(travelCost.getId(), travelCost);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(300, overMaxAllowedTextSize.length());

        assertEquals(travelCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = asList(
                fieldError("cost", new BigDecimal("0"), "validation.field.max.value.or.higher", 1),
                fieldError("quantity", 0, "validation.field.max.value.or.higher", 1));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    /* Other FinanceRow Section Tests */

    @Rollback
    @Test
    public void testValidationOtherCostUpdateSuccess() {
        otherCost.setCost(new BigDecimal("1000"));
        otherCost.setDescription("Additional Test FinanceRow");

        RestResult<ValidationMessages> validationMessages = controller.update(otherCost.getId(), otherCost);
        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    @Rollback
    @Test
    public void testValidationOtherCostUpdateIncorrectCostValue() {

        otherCost.setCost(new BigDecimal("0"));
        otherCost.setDescription(overMaxAllowedTextSize);

        RestResult<ValidationMessages> validationMessages = controller.update(otherCost.getId(), otherCost);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(otherCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = singletonList(
                fieldError("cost", new BigDecimal("0"), "validation.field.max.value.or.higher", 1));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    @Rollback
    @Test
    public void testValidationOtherCostUpdateMinIncorrectValues() {

        otherCost.setCost(new BigDecimal("-1000"));
        otherCost.setDescription("");

        RestResult<ValidationMessages> validationMessages = controller.update(otherCost.getId(), otherCost);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(otherCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = asList(
                fieldError("description", "", "validation.field.must.not.be.blank"),
                fieldError("cost", new BigDecimal("-1000"), "validation.field.max.value.or.higher", 1));

        assertErrorsAsExpected(messages, expectedErrors);
    }

     /* Other funding section Tests */

    @Rollback
    @Test
    public void testValidationOtherFundingUpdate(){

        assertEquals(new BigDecimal("0"), otherFunding.getTotal());
        assertEquals("Yes", otherFunding.getOtherPublicFunding());

        RestResult<ValidationMessages> validationMessages = controller.update(otherFunding.getId(), otherFunding);
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    /* Grant Claim Section Tests - Small Organisation Size */

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateSmallOrganisationSize(){

        assertEquals(SMALL, applicationFinance.getOrganisationSize());
        grantClaim.setPercentage(55);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateSmallOrganisationSizeHigherValue(){

        assertEquals(SMALL, applicationFinance.getOrganisationSize());
        grantClaim.setPercentage(71);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = singletonList(
                fieldError("percentage", 71, "validation.finance.grant.claim.percentage.max", 70));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateSmallOrganisationSizeNegativeValue() {

        assertEquals(SMALL, applicationFinance.getOrganisationSize());
        grantClaim.setPercentage(-1);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = singletonList(
                fieldError("percentage", -1, "validation.field.percentage.max.value.or.higher", 0));

        assertErrorsAsExpected(messages, expectedErrors);
    }

     /* Grant Claim Section Tests - Medium Organisation Size */

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateMediumOrganisationSize(){

        applicationFinance.setOrganisationSize(MEDIUM);
        assertEquals(MEDIUM, applicationFinance.getOrganisationSize());

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateMediumOrganisationSizeHigherValue(){

        applicationFinance.setOrganisationSize(MEDIUM);
        assertEquals(MEDIUM, applicationFinance.getOrganisationSize());
        grantClaim.setPercentage(61);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = singletonList(
                fieldError("percentage", 61, "validation.finance.grant.claim.percentage.max", 60));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateMediumOrganisationSizeNegativeValue() {

        applicationFinance.setOrganisationSize(MEDIUM);
        assertEquals(MEDIUM, applicationFinance.getOrganisationSize());
        grantClaim.setPercentage(-1);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = singletonList(
                fieldError("percentage", -1, "validation.field.percentage.max.value.or.higher", 0));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    /* Grant Claim Section Tests - Large Organisation Size */

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateLargeOrganisationSize(){

        applicationFinance.setOrganisationSize(LARGE);
        assertEquals(LARGE, applicationFinance.getOrganisationSize());
        grantClaim.setPercentage(45);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        assertTrue(validationMessages.isSuccess());
        assertTrue(validationMessages.getSuccess().getErrors().isEmpty());
    }

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateLargeOrganisationSizeHigherValue() {

        applicationFinance.setOrganisationSize(LARGE);
        assertEquals(LARGE, applicationFinance.getOrganisationSize());
        grantClaim.setPercentage(51);
        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = singletonList(
                fieldError("percentage", 51, "validation.finance.grant.claim.percentage.max", 50));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateLargeOrganisationSizeNegativeValue() {

        applicationFinance.setOrganisationSize(LARGE);
        assertEquals(LARGE, applicationFinance.getOrganisationSize());
        grantClaim.setPercentage(-1);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccess();

        assertEquals(1, messages.getErrors().size());
        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        List<Error> expectedErrors = singletonList(
                fieldError("percentage", -1, "validation.field.percentage.max.value.or.higher", 0));

        assertErrorsAsExpected(messages, expectedErrors);
    }

    private void assertErrorsAsExpected(ValidationMessages messages, List<Error> expectedErrors) {
        assertEquals(expectedErrors.size(), messages.getErrors().size());
        expectedErrors.forEach(error -> assertTrue("Expected to find " + error.getErrorKey(), messages.getErrors().contains(error)));
    }
}
