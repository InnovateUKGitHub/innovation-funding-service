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
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.finance.resource.cost.LabourCost;
import com.worth.ifs.finance.resource.cost.Materials;
import com.worth.ifs.finance.resource.cost.OtherFunding;
import com.worth.ifs.user.domain.OrganisationSize;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.mapper.UserMapper;
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
import static org.junit.Assert.*;

@Rollback
public class CostControllerIntegrationTest extends BaseControllerIntegrationTest<CostController> {

    private GrantClaim grantClaim;
    private Materials materials;
    private LabourCost labourCost;
    private LabourCost labourCostDaysPerYear;
    private OtherFunding otherFunding;

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
        grandClaimCost = costRepository.findOne(48L);
        applicationFinance = grandClaimCost.getApplicationFinance();

        grantClaim = (GrantClaim) controller.get(48L).getSuccessObject();
        materials = (Materials) controller.get(12L).getSuccessObject();
        labourCost = (LabourCost) controller.get(4L).getSuccessObject();
        labourCostDaysPerYear = (LabourCost) controller.get(1L).getSuccessObject();
        otherFunding = (OtherFunding) controller.get(54L).getSuccessObject();

        leadApplicantId = 1L;
        leadApplicantProcessRole = 1L;
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

    @Rollback
    @Test
    public void testValidationLabour(){
        RestResult<ValidationMessages> validationMessages = controller.update(labourCost.getId(), labourCost);
        assertTrue(validationMessages.isSuccess());
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
    }

    @Rollback
    @Test
    public void testValidationLabourUpdate(){
        labourCost.setRole("");
        labourCost.setLabourDays(-50);
        labourCost.setGrossAnnualSalary(new BigDecimal(-500000));
        RestResult<ValidationMessages> validationMessages = controller.update(labourCost.getId(), labourCost);
        assertTrue(validationMessages.isSuccess());
        assertNotNull(validationMessages.getOptionalSuccessObject().get());

        ValidationMessages messages = validationMessages.getSuccessObject();
        assertEquals(3, messages.getErrors().size());
        assertEquals(labourCost.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);


        assertTrue(messages.getErrors().stream()
                .filter(e -> "labourDays".equals(e.getErrorKey()))
                .filter(e -> "must be greater than or equal to 0".equals(e.getErrorMessage()))
                .findAny().isPresent());

        assertTrue(messages.getErrors().stream()
                .filter(e -> "role".equals(e.getErrorKey()))
                .filter(e -> "may not be empty".equals(e.getErrorMessage()))
                .findAny().isPresent());

        assertTrue(messages.getErrors().stream()
                .filter(e -> "grossAnnualSalary".equals(e.getErrorKey()))
                .filter(e -> "must be greater than or equal to 0".equals(e.getErrorMessage()))
                .findAny().isPresent());
    }

    @Rollback
    @Test
    public void testValidationMaterial(){
        RestResult<ValidationMessages> validationMessages = controller.update(materials.getId(), materials);
        assertTrue(validationMessages.isSuccess());
        assertFalse(validationMessages.getOptionalSuccessObject().isPresent());
    }

    @Rollback
    @Test
    public void testValidationOtherFundingUpdate(){
        RestResult<ValidationMessages> validationMessages = controller.update(otherFunding.getId(), otherFunding);
        ValidationMessages messages = validationMessages.getSuccessObject();
        assertEquals(null, messages);
        //assertEquals(otherFunding.getId(), messages.getObjectId());
        //assertEquals("costItem", messages.getObjectName());
//        assertTrue(messages.getErrors().stream()
//                .filter(e -> "".equals(e.getErrorKey()))
//                .filter(e -> "You should provide at least one Source of funding".equals(e.getErrorMessage()))
//                .findAny().isPresent());
    }

    @Rollback
    @Test
    public void testValidationMaterialUpdate(){
        materials.setCost(new BigDecimal(-5));
        materials.setItem("");
        materials.setQuantity(-5);


        RestResult<ValidationMessages> validationMessages = controller.update(materials.getId(), materials);
        ValidationMessages messages = validationMessages.getSuccessObject();
        assertEquals(3, messages.getErrors().size());
        assertEquals(materials.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);


        assertTrue(messages.getErrors().stream()
                .filter(e -> "item".equals(e.getErrorKey()))
                .filter(e -> "may not be empty".equals(e.getErrorMessage()))
                .findAny().isPresent());

        assertTrue(messages.getErrors().stream()
                .filter(e -> "quantity".equals(e.getErrorKey()))
                .filter(e -> "must be greater than or equal to 1".equals(e.getErrorMessage()))
                .findAny().isPresent());

        assertTrue(messages.getErrors().stream()
                .filter(e -> "cost".equals(e.getErrorKey()))
                .filter(e -> "must be greater than or equal to 1".equals(e.getErrorMessage()))
                .findAny().isPresent());
    }

    @Rollback
    @Test
    public void testValidationGrantClaimUpdate(){
        assertEquals(OrganisationSize.SMALL, applicationFinance.getOrganisationSize());
        grantClaim.setGrantClaimPercentage(80);

        RestResult<ValidationMessages> validationMessages = controller.update(grantClaim.getId(), grantClaim);
        ValidationMessages messages = validationMessages.getSuccessObject();
        assertEquals(1, messages.getErrors().size());
        assertEquals(grantClaim.getId(), messages.getObjectId());
        assertEquals("costItem", messages.getObjectName());
        messages.getErrors().get(0);

        assertTrue(messages.getErrors().stream()
                .filter(e -> "grantClaimPercentage".equals(e.getErrorKey()))
                .filter(e -> "This field should be 70% or lower".equals(e.getErrorMessage()))
                .findAny().isPresent());
    }
}
