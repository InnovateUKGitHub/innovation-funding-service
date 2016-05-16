package com.worth.ifs.finance.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.cost.*;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.resource.OrganisationSize;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.security.SecuritySetter.swapOutForUser;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@Rollback
public class CostControllerIntegrationTest extends BaseControllerIntegrationTest<CostController> {

    private GrantClaim grantClaim;
    private Materials materials;
    private LabourCost labourCost;
    private LabourCost labourCostDaysPerYear;

    private CapitalUsage capitalUsage;
    private SubContractingCost subContractingCost;
    private TravelCost travelCost;
    private OtherCost otherCost;
    private OtherFunding otherFunding;
    private OtherFunding otherFundingCost;
    private OtherFunding otherFundingCost2;
    private Overhead overhead;


    private String overMaxAllowedTextSize;

    @Mock
    BindingResult bindingResult;

    @Autowired
    CostRepository costRepository;
    private Cost grandClaimCost;
    private ApplicationFinance applicationFinance;
    private long leadApplicantId;
    private long leadApplicantProcessRole;
    public static final long APPLICATION_ID = 1L;

    @Autowired

    UserMapper userMapper;

    @Override
    @Autowired
    protected void setControllerUnderTest(CostController controller) {
        this.controller = controller;
    }

    @Before
    public void prepare(){
        loginSteveSmith();
        grandClaimCost = costRepository.findOne(48L);
        applicationFinance = grandClaimCost.getApplicationFinance();

        grantClaim = (GrantClaim) controller.get(48L).getSuccessObject();
        materials = (Materials) controller.get(12L).getSuccessObject();
        labourCost = (LabourCost) controller.get(4L).getSuccessObject();
        labourCostDaysPerYear = (LabourCost) controller.get(1L).getSuccessObject();

        otherFunding = (OtherFunding) controller.get(54L).getSuccessObject();

        overhead =  (Overhead) controller.get(51L).getSuccessObject();
        capitalUsage = (CapitalUsage) controller.add(applicationFinance.getId(), 31L, null).getSuccessObject();
        subContractingCost = (SubContractingCost) controller.add(applicationFinance.getId(), 32L, new SubContractingCost()).getSuccessObject();
        travelCost = (TravelCost) controller.add(applicationFinance.getId(), 33L, new TravelCost()).getSuccessObject();
        otherCost = (OtherCost) controller.add(applicationFinance.getId(), 34L, null).getSuccessObject();

        otherFundingCost = (OtherFunding) controller.add(applicationFinance.getId(), 35L, null).getSuccessObject();
        otherFundingCost2 = (OtherFunding) controller.get(55L).getSuccessObject();

        leadApplicantId = 1L;
        leadApplicantProcessRole = 1L;

        overMaxAllowedTextSize = StringUtils.repeat("<ifs_test>", 30);

        List<ProcessRole> proccessRoles = new ArrayList<>();
        proccessRoles.add(
                new ProcessRole(
                        leadApplicantProcessRole,
                        null,
                        new Application(
                                APPLICATION_ID,
                                "",
                                new ApplicationStatus(
                                        ApplicationStatusConstants.CREATED.getId(),
                                        ApplicationStatusConstants.CREATED.getName()
                                )
                        ),
                        null,
                        null
                )
        );
        User user = new User(leadApplicantId, "steve", "smith", "steve.smith@empire.com", "", proccessRoles, "123abc");
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
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
    }

    @Rollback
    @Test
    public void testValidationLabourUpdateIncorrectValues(){
        labourCost.setRole("");
        labourCost.setLabourDays(-50);
        labourCost.setGrossAnnualSalary(new BigDecimal("-500000"));

        RestResult<ValidationMessages> validationMessages = controller.update(labourCost.getId(), labourCost);
        assertTrue(validationMessages.isSuccess());
        assertNotNull(validationMessages.getOptionalSuccessObject().get());

        ValidationMessages messages = validationMessages.getSuccessObject();
        assertEquals(3, messages.getErrors().size());
        assertEquals(labourCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);


        assertThat(messages.getErrors(),
                containsInAnyOrder(
                        allOf(
                                hasProperty("errorKey", is("labourDays")),
                                hasProperty("errorMessage", is("This field should be 1 or higher"))
                        ),
                        allOf(
                                hasProperty("errorKey", is("grossAnnualSalary")),
                                hasProperty("errorMessage", is("This field should be 1 or higher"))
                        ),
                        allOf(
                                hasProperty("errorKey", is("role")),
                                hasProperty("errorMessage", is("This field cannot be left blank"))
                        )
                )
        );
    }

    @Rollback
    @Test
    //@Todo no validation for WorkingDaysPerYear (0-365), role limitation (255 characters), salary (0-8), but form validation works
    public void testValidationLabourUpdateIncorrectMaxValues() {

        labourCost.setRole(overMaxAllowedTextSize);
        labourCost.setLabourDays(400);
        labourCost.setGrossAnnualSalary(new BigDecimal("100000"));

        RestResult<ValidationMessages> validationMessages = controller.update(labourCost.getId(), labourCost);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertTrue(validationMessages.isSuccess());
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());

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
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
    }

    @Rollback
    @Test
    public void testValidationOverheadUpdateMinRate(){

        overhead.setRate(-10);
        assertEquals(-100, overhead.getRate() * 1000/100);

        RestResult<ValidationMessages> validationMessages = controller.update(overhead.getId(), overhead);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(1, messages.getErrors().size());
        assertEquals(overhead.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), contains(
                allOf(
                        hasProperty("errorKey", is("rate")),
                        hasProperty("errorMessage", is("This field should be 1 or higher"))
                )
        ));
    }

    @Rollback
    @Test
    public void testValidationOverheadUpdateMaxRate(){
        overhead.setRate(150);
        assertEquals(1500, overhead.getRate() * 1000/100);

        RestResult<ValidationMessages> validationMessages = controller.update(overhead.getId(), overhead);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(1, messages.getErrors().size());
        assertEquals(overhead.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), contains(
                allOf(
                        hasProperty("errorKey", is("rate")),
                        hasProperty("errorMessage", is("This field should be 100 or lower"))
                )
        ));
    }


    /* Material Section Tests */

    @Rollback
    @Test
    public void testValidationMaterial(){
        assertEquals(new BigDecimal("2000"), materials.getTotal());

        RestResult<ValidationMessages> validationMessages = controller.update(materials.getId(), materials);
        assertTrue(validationMessages.isSuccess());
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
    }

    @Rollback
    @Test
    public void testValidationMaterialUpdateInvalidValues(){
        materials.setCost(new BigDecimal("-5"));

        materials.setItem("");
        materials.setQuantity(-5);

        RestResult<ValidationMessages> validationMessages = controller.update(materials.getId(), materials);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(3, messages.getErrors().size());
        assertEquals(materials.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("errorKey", is("item")),
                        hasProperty("errorMessage", is("This field cannot be left blank"))
                ),
                allOf(
                        hasProperty("errorKey", is("quantity")),
                        hasProperty("errorMessage", is("This field should be 1 or higher"))
                ),
                allOf(
                        hasProperty("errorKey", is("cost")),
                        hasProperty("errorMessage", is("This field should be 1 or higher"))
                )
        ));
    }

    @Rollback
    @Test
    public void testValidationMaterialUpdateMaxValues() {

        materials.setCost(new BigDecimal("1000"));
        materials.setItem(overMaxAllowedTextSize);
        materials.setQuantity(1000);

        RestResult<ValidationMessages> validationMessages = controller.update(materials.getId(), materials);
        assertTrue(validationMessages.isSuccess());
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());

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
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
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
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(6, messages.getErrors().size());
        assertEquals(capitalUsage.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("errorKey", is("description")),
                        hasProperty("errorMessage", is("This field cannot be left blank"))
                ),
                allOf(
                        hasProperty("errorKey", is("existing")),
                        hasProperty("errorMessage", is("This field cannot be left blank"))
                ),
                allOf(
                        hasProperty("errorKey", is("deprecation")),
                        hasProperty("errorMessage", is("This field should be 1 or higher"))
                ),
                allOf(
                        hasProperty("errorKey", is("residualValue")),
                        hasProperty("errorMessage", is("This field should be 0 or higher"))
                ),
                allOf(
                        hasProperty("errorKey", is("npv")),
                        hasProperty("errorMessage", is("This field should be 1 or higher"))
                ),
                allOf(
                        hasProperty("errorKey", is("utilisation")),
                        hasProperty("errorMessage", is("This field should be 0 or higher"))
                )
        ));
    }

    @Rollback
    @Test
    public void testValidationCapitalUsageUpdateOverMaxAllowedValues(){
        capitalUsage.setDescription(overMaxAllowedTextSize);
        capitalUsage.setExisting(overMaxAllowedTextSize);
        capitalUsage.setDeprecation(1000);
        capitalUsage.setResidualValue(new BigDecimal("1000000"));
        capitalUsage.setNpv(new BigDecimal("0"));
        capitalUsage.setUtilisation(200);

        RestResult<ValidationMessages> validationMessages = controller.update(capitalUsage.getId(), capitalUsage);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(2, messages.getErrors().size());
        assertEquals(capitalUsage.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("errorKey", is("existing")),
                        hasProperty("errorMessage", is("This field cannot contain more than 255 characters"))
                ),
                allOf(
                        hasProperty("errorKey", is("utilisation")),
                        hasProperty("errorMessage", is("This field should be 100 or lower"))
                )
        ));
    }

    /* SubContracting Section Tests */

    @Rollback
    @Test
    public void testValidationSubContractingCostUpdateSuccess() {
        assertNotNull(subContractingCost.getName());
        assertNotNull(subContractingCost.getCountry());
        assertNotNull(subContractingCost.getRole());
        assertNotNull(subContractingCost.getCost());

        subContractingCost.setName("Tom Bloggs");
        subContractingCost.setCountry("UK");
        subContractingCost.setRole("Business Analyst");
        subContractingCost.setCost(new BigDecimal("10000"));

        RestResult<ValidationMessages> validationMessages = controller.update(subContractingCost.getId(), subContractingCost);
        assertTrue(validationMessages.isSuccess());
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
    }

    @Rollback
    @Test
    //@TODO Country can be saved as empty string
    public void testValidationSubContractingCostUpdateIncorrectValues(){
        subContractingCost.setName("");
        subContractingCost.setCountry("");
        subContractingCost.setRole("");
        subContractingCost.setCost(new BigDecimal("-5000"));

        RestResult<ValidationMessages> validationMessages = controller.update(travelCost.getId(), travelCost);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(3, messages.getErrors().size());
        assertEquals(travelCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("errorKey", is("item")),
                        hasProperty("errorMessage", is("This field cannot be left blank"))
                ),
                allOf(
                        hasProperty("errorKey", is("cost")),
                        hasProperty("errorMessage", is("may not be null"))
                ),
                allOf(
                        hasProperty("errorKey", is("quantity")),
                        hasProperty("errorMessage", is("may not be null"))
                )
        ));
    }

    @Rollback
    @Test
    //@TODO Text values are allowed over 255 characters
    public void testValidationSubContractingCostUpdateOverMaxAllowedValues(){
        subContractingCost.setName(overMaxAllowedTextSize);
        subContractingCost.setCountry(overMaxAllowedTextSize);
        subContractingCost.setRole(overMaxAllowedTextSize);
        subContractingCost.setCost(new BigDecimal("1000000"));

        RestResult<ValidationMessages> validationMessages = controller.update(subContractingCost.getId(), subContractingCost);
        assertTrue(validationMessages.isSuccess());
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
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
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
    }

    @Rollback
    @Test
    public void testValidationTravelCostUpdateIncorrectMinValues(){
        travelCost.setItem("");
        travelCost.setCost(new BigDecimal("-1000"));
        travelCost.setQuantity(-500);

        RestResult<ValidationMessages> validationMessages = controller.update(travelCost.getId(), travelCost);
        ValidationMessages messages = validationMessages.getSuccessObject();
        assertEquals(3, messages.getErrors().size());
        assertEquals(travelCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("errorKey", is("item")),
                        hasProperty("errorMessage", is("This field cannot be left blank"))
                ),
                allOf(
                        hasProperty("errorKey", is("cost")),
                        hasProperty("errorMessage", is("This field should be 1 or higher"))
                ),
                allOf(
                        hasProperty("errorKey", is("quantity")),
                        hasProperty("errorMessage", is("This field should be 1 or higher"))
                )
        ));
    }

    @Rollback
    @Test
    //@TODO Text value (item) can be over 255 characters; quantity and cost can be 0 if updated through the form
    public void testValidationTravelCostUpdateIncorrectMaxAndZeroValues(){

        travelCost.setItem(overMaxAllowedTextSize);
        travelCost.setCost(new BigDecimal("0"));
        travelCost.setQuantity(0);

        RestResult<ValidationMessages> validationMessages = controller.update(travelCost.getId(), travelCost);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(300, overMaxAllowedTextSize.length());

        assertEquals(2, messages.getErrors().size());
        assertEquals(travelCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("errorKey", is("cost")),
                        hasProperty("errorMessage", is("This field should be 1 or higher"))
                ),
                allOf(
                        hasProperty("errorKey", is("quantity")),
                        hasProperty("errorMessage", is("This field should be 1 or higher"))

                )
        ));
    }

    /* Other Cost Section Tests */

    @Rollback
    @Test
    public void testValidationOtherCostUpdateSuccess() {
        otherCost.setCost(new BigDecimal("1000"));
        otherCost.setDescription("Additional Test Cost");

        RestResult<ValidationMessages> validationMessages = controller.update(otherCost.getId(), otherCost);
        assertTrue(validationMessages.isSuccess());
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
    }

    @Rollback
    @Test
    public void testValidationOtherCostUpdateIncorrectCostValue() {

        otherCost.setCost(new BigDecimal("0"));
        otherCost.setDescription(overMaxAllowedTextSize);

        RestResult<ValidationMessages> validationMessages = controller.update(otherCost.getId(), otherCost);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(1, messages.getErrors().size());
        assertEquals(otherCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), contains(
                allOf(
                        hasProperty("errorKey", is("cost")),
                        hasProperty("errorMessage", is("This field should be 1 or higher"))
                )
        ));
    }

    @Rollback
    @Test
    public void testValidationOtherCostUpdateMinIncorrectValues() {

        otherCost.setCost(new BigDecimal("-1000"));
        otherCost.setDescription("");

        RestResult<ValidationMessages> validationMessages = controller.update(otherCost.getId(), otherCost);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(2, messages.getErrors().size());
        assertEquals(otherCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());

        messages.getErrors().get(0);

        assertThat(messages.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("errorKey", is("description")),
                        hasProperty("errorMessage", is("This field cannot be left blank"))
                ),
                allOf(
                        hasProperty("errorKey", is("cost")),
                        hasProperty("errorMessage", is("This field should be 1 or higher"))
                )
        ));
    }

     /* Other funding section Tests */

    @Rollback
    @Test
    public void testValidationOtherFundingUpdate(){

        assertEquals(new BigDecimal("0"), otherFunding.getTotal());
        assertEquals("Yes", otherFunding.getOtherPublicFunding());

        RestResult<ValidationMessages> validationMessages = controller.update(otherFunding.getId(), otherFunding);
        ValidationMessages messages = validationMessages.getSuccessObject();
        assertEquals(null, messages);
    }

    //@Rollback
    //@Test
    //TODO Something wrong here??? otherPublicFunding and secureDate contain the same variable?
    public void testValidationOtherFundingUpdateIncorrectValues() {

        assertEquals("Yes", otherFunding.getOtherPublicFunding());
        otherFundingCost.setOtherPublicFunding("Yes");
        otherFundingCost.setFundingSource("SomethingWrongHere");
        otherFundingCost.setSecuredDate("15-1000");
        otherFundingCost.setFundingAmount(new BigDecimal("0"));

        //otherFunding.getTotal();

        RestResult<ValidationMessages> validationMessages = controller.update(otherFundingCost.getId(), otherFundingCost);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(2, messages.getErrors().size());
        assertEquals(otherFundingCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("errorKey", is("securedDate")),
                        hasProperty("errorMessage", is("Invalid secured date.  Please use MM-YYYY format."))
                ),
                allOf(
                        hasProperty("errorKey", is("fundingSource")),
                        hasProperty("errorMessage", is("Funding source cannot be blank."))
                ),
                allOf(
                        hasProperty("errorKey", is("fundingAmount")),
                        hasProperty("errorMessage", is("Funding source cannot be blank"))
                )
        ));
    }

    /* Grant Claim Section Tests - Small Organisation Size */

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateSmallOrganisationSize(){

        assertEquals(OrganisationSize.SMALL, applicationFinance.getOrganisationSize());
        grantClaim.setGrantClaimPercentage(55);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        assertTrue(validationMessages.isSuccess());
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
    }

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateSmallOrganisationSizeHigherValue(){

        assertEquals(OrganisationSize.SMALL, applicationFinance.getOrganisationSize());
        grantClaim.setGrantClaimPercentage(71);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(1, messages.getErrors().size());
        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), contains(
                allOf(
                        hasProperty("errorKey", is("grantClaimPercentage")),
                        hasProperty("errorMessage", is("This field should be 70% or lower"))
                )
        ));
    }

    @Rollback
    @Test
    //@TODO No error message "This field should be 1 or higher" OR "This field cannot be left blank"
    public void testValidationGrantClaimUpdateSmallOrganisationSizeZeroValue() {

        assertEquals(OrganisationSize.SMALL, applicationFinance.getOrganisationSize());

        grantClaim.setGrantClaimPercentage(0);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(1, messages.getErrors().size());
        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), contains(
                allOf(
                        hasProperty("errorKey", is("grantClaimPercentage")),
                        hasProperty("errorMessage", is(""))
                )
        ));
    }

     /* Grant Claim Section Tests - Medium Organisation Size */

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateMediumOrganisationSize(){

        applicationFinance.setOrganisationSize(OrganisationSize.MEDIUM);

        assertEquals(OrganisationSize.MEDIUM, applicationFinance.getOrganisationSize());
        grantClaim.setGrantClaimPercentage(45);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        assertTrue(validationMessages.isSuccess());
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
    }

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateMediumOrganisationSizeHigherValue(){

        applicationFinance.setOrganisationSize(OrganisationSize.MEDIUM);

        assertEquals(OrganisationSize.MEDIUM, applicationFinance.getOrganisationSize());
        grantClaim.setGrantClaimPercentage(61);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(1, messages.getErrors().size());
        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), contains(
                allOf(
                        hasProperty("errorKey", is("grantClaimPercentage")),
                        hasProperty("errorMessage", is("This field should be 60% or lower"))
                )
        ));
    }

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateMediumOrganisationSizeZeroValue() {

        applicationFinance.setOrganisationSize(OrganisationSize.MEDIUM);

        assertEquals(OrganisationSize.MEDIUM, applicationFinance.getOrganisationSize());
        grantClaim.setGrantClaimPercentage(0);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(1, messages.getErrors().size());
        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), contains(
                allOf(
                        hasProperty("errorKey", is("grantClaimPercentage")),
                        hasProperty("errorMessage", is(""))
                )
        ));
    }

    /* Grant Claim Section Tests - Large Organisation Size */

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateLargeOrganisationSize(){

        applicationFinance.setOrganisationSize(OrganisationSize.LARGE);

        assertEquals(OrganisationSize.LARGE, applicationFinance.getOrganisationSize());
        grantClaim.setGrantClaimPercentage(45);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        assertTrue(validationMessages.isSuccess());
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
    }

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateLargeOrganisationSizeHigherValue(){

        applicationFinance.setOrganisationSize(OrganisationSize.LARGE);

        assertEquals(OrganisationSize.LARGE, applicationFinance.getOrganisationSize());
        grantClaim.setGrantClaimPercentage(51);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(1, messages.getErrors().size());
        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), contains(
                allOf(
                        hasProperty("errorKey", is("grantClaimPercentage")),
                        hasProperty("errorMessage", is("This field should be 50% or lower"))
                )
        ));
    }

    @Rollback
    @Test
    public void testValidationGrantClaimUpdateLargeOrganisationSizeZeroValue() {

        applicationFinance.setOrganisationSize(OrganisationSize.LARGE);

        assertEquals(OrganisationSize.LARGE, applicationFinance.getOrganisationSize());
        grantClaim.setGrantClaimPercentage(0);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccessObject();

        assertEquals(1, messages.getErrors().size());
        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertThat(messages.getErrors(), contains(
                allOf(
                        hasProperty("errorKey", is("grantClaimPercentage")),
                        hasProperty("errorMessage", is(""))
                )
        ));
    }
}
