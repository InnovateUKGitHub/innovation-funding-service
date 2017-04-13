package org.innovateuk.ifs.profile;

import org.hamcrest.Matchers;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.service.EthnicityRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.user.resource.Disability;
import org.innovateuk.ifs.user.resource.Gender;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.HttpServletRequest;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.builder.AddressTypeResourceBuilder.newAddressTypeResource;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.OPERATING;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.REGISTERED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Title.Mrs;
import static org.innovateuk.ifs.user.resource.Title.Ms;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProfileControllerTest extends BaseControllerMockMVCTest<ProfileController> {

    @Override
    protected ProfileController supplyControllerUnderTest() {
        return new ProfileController();
    }

    @Mock EthnicityRestService ethnicityRestService;

    private UserResource user;

    private OrganisationResource organisation;

    @Before
    public void setUp() {
        super.setUp();

        user = newUserResource()
                .withTitle(Ms)
                .withFirstName("firstname")
                .withLastName("lastname")
                .withPhoneNumber("1234567890")
                .withEmail("email@provider.com")
                .withDisability(Disability.YES)
                .withGender(Gender.FEMALE)
                .withEthnicity(2L)
                .build();
        setLoggedInUser(user);

        when(ethnicityRestService.findAllActive()).thenReturn(restSuccess(newEthnicityResource().build(4)));
    }

    @SuppressWarnings("unchecked")
    private void setupOrganisation(OrganisationAddressResource...addressResources) {
        organisation = newOrganisationResource()
                .withName("orgname")
                .withCompanyHouseNumber("companyhousenumber")
                .withAddress(asList(addressResources))
                .build();
        when(organisationService.getOrganisationById(6L)).thenReturn(organisation);
        when(organisationService.getOrganisationForUser(user.getId())).thenReturn(organisation);
    }

    private OrganisationAddressResource organisationAddress(OrganisationAddressType addressType) {
        AddressTypeResource addressTypeResource = newAddressTypeResource().withId((long)addressType.getOrdinal()).withName(addressType.name()).build();
        return newOrganisationAddressResource()
                .withAddressType(addressTypeResource)
                .withAddress(newAddressResource()
                        .withAddressLine1("line1" + addressType.name())
                        .withAddressLine2("line2" + addressType.name())
                        .withAddressLine3("line3" + addressType.name())
                        .withTown("town" + addressType.name())
                        .withCounty("county" + addressType.name())
                        .withPostcode("postcode" + addressType.name())
                        .build())
                .build();
    }

    @Test
    public void userProfileDetailsAndOrganisationDetailsAreAddedToModelWhenViewingDetails() throws Exception {

        OrganisationAddressResource operatingOrgAddress = organisationAddress(OPERATING);
        setupOrganisation(operatingOrgAddress);

        ResultActions result = mockMvc.perform(get("/profile/view"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("model", Matchers.hasProperty("name", Matchers.equalTo(user.getTitle() + " " +
                        user.getFirstName() + " " +
                        user.getLastName()))))
                .andExpect(model().attribute("model", Matchers.hasProperty("phoneNumber", Matchers.equalTo(user.getPhoneNumber()))))
                .andExpect(model().attribute("model", Matchers.hasProperty("emailAddress", Matchers.equalTo(user.getEmail()))))
                .andExpect(model().attribute("model", Matchers.hasProperty("organisationName", Matchers.equalTo(organisation.getName()))))
                .andExpect(model().attribute("model", Matchers.hasProperty("registrationNumber", Matchers.equalTo(organisation.getCompanyHouseNumber()))))
                .andExpect(model().attribute("model", Matchers.hasProperty("gender", Matchers.equalTo(user.getGender().getDisplayName()))))
                .andExpect(model().attribute("model", Matchers.hasProperty("disability", Matchers.equalTo(user.getDisability().getDisplayName()))));

        verifyOrganisationAddress(result, operatingOrgAddress, "model");
    }

    @Test
    public void operationAddressForOrganisationIsFavouredWhenRegisteredAddressIsAlsoPresent() throws Exception {

        OrganisationAddressResource registeredOrgAddress = organisationAddress(REGISTERED);
        OrganisationAddressResource operatingOrgAddress = organisationAddress(OPERATING);
        setupOrganisation(registeredOrgAddress, operatingOrgAddress);

        ResultActions result = mockMvc.perform(get("/profile/view"))
                .andExpect(status().is2xxSuccessful());

        verifyOrganisationAddress(result, operatingOrgAddress, "model");
    }

    @Test
    public void registeredAddressIsUsedWhenOperatingAddressIsAbsent() throws Exception {

        OrganisationAddressResource registeredOrgAddress = organisationAddress(REGISTERED);
        setupOrganisation(registeredOrgAddress);

        ResultActions result = mockMvc.perform(get("/profile/view"))
                .andExpect(status().is2xxSuccessful());


        verifyOrganisationAddress(result, registeredOrgAddress, "model");
    }

    @Test
    public void nullValuesUsedWhenNoAddressPresent() throws Exception {

        setupOrganisation();

        mockMvc.perform(get("/profile/view"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("model", Matchers.hasProperty("addressLine1", Matchers.isEmptyOrNullString())))
                .andExpect(model().attribute("model", Matchers.hasProperty("addressLine2", Matchers.isEmptyOrNullString())))
                .andExpect(model().attribute("model", Matchers.hasProperty("addressLine3", Matchers.isEmptyOrNullString())))
                .andExpect(model().attribute("model", Matchers.hasProperty("town", Matchers.isEmptyOrNullString())))
                .andExpect(model().attribute("model", Matchers.hasProperty("county", Matchers.isEmptyOrNullString())))
                .andExpect(model().attribute("model", Matchers.hasProperty("postcode", Matchers.isEmptyOrNullString())));
    }

    private void verifyOrganisationAddress(ResultActions result, OrganisationAddressResource registeredOrgAddress, String modelName) throws Exception {
        result
                .andExpect(model().attribute(modelName, Matchers.hasProperty("addressLine1", Matchers.equalTo(registeredOrgAddress.getAddress().getAddressLine1()))))
                .andExpect(model().attribute(modelName, Matchers.hasProperty("addressLine2", Matchers.equalTo(registeredOrgAddress.getAddress().getAddressLine2()))))
                .andExpect(model().attribute(modelName, Matchers.hasProperty("addressLine3", Matchers.equalTo(registeredOrgAddress.getAddress().getAddressLine3()))))
                .andExpect(model().attribute(modelName, Matchers.hasProperty("town", Matchers.equalTo(registeredOrgAddress.getAddress().getTown()))))
                .andExpect(model().attribute(modelName, Matchers.hasProperty("county", Matchers.equalTo(registeredOrgAddress.getAddress().getCounty()))))
                .andExpect(model().attribute(modelName, Matchers.hasProperty("postcode", Matchers.equalTo(registeredOrgAddress.getAddress().getPostcode()))));
    }

    @Test
    public void userProfileDetailsAreAddedToModelWhenViewingDetailsForm() throws Exception {
        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("firstName", Matchers.equalTo(user.getFirstName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("lastName", Matchers.equalTo(user.getLastName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("phoneNumber", Matchers.equalTo(user.getPhoneNumber()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("email", Matchers.equalTo(user.getEmail()))));
    }

    @Test
    public void userServiceSaveMethodIsCalledWhenSubmittingValidDetailsForm() throws Exception {

        when(userService.updateDetails(user.getId(), user.getEmail(), "newfirstname", "newlastname",
                "Mrs", "0987654321", "MALE", 2L,"NO"))
                .thenReturn(ServiceResult.serviceSuccess(newUserResource().build()));
        mockMvc.perform(post("/profile/edit")
                .param("title", Mrs.toString())
                .param("firstName", "newfirstname")
                .param("lastName", "newlastname")
                .param("phoneNumber", "0987654321")
                .param("gender", Gender.MALE.toString())
                .param("ethnicity", "2")
                .param("disability", Disability.NO.toString())

        );

        verify(userService, times(1)).updateDetails(
                eq(user.getId()),
                eq(user.getEmail()),
                eq("newfirstname"),
                eq("newlastname"),
                any(),
                eq("0987654321"),
                any(),
                any(),
                any());
    }

    @Test
    public void userServiceSaveMethodIsNotCalledWhenSubmittingInvalidDetailsForm() throws Exception {
        mockMvc.perform(post("/profile/edit")
                .param("title", "illegalcharacters:!@#$%^&*()")
                .param("firstName", "illegalcharacters:!@#$%^&*()")
                .param("lastName", "illegalcharacters:!@#$%^&*()")
                .param("phoneNumber", "illegalcharacters:!@#$%^&*()")
                .param("gender", "illegalcharacters:!@#$%^&*()")
                .param("ethnicity", "illegalcharacters:!@#$%^&*()")
                .param("disability", "illegalcharacters:!@#$%^&*()")
        );

        verify(userService, times(0)).updateDetails(
                isA(Long.class),
                isA(String.class),
                isA(String.class),
                isA(String.class),
                isA(String.class),
                isA(String.class),
                isA(String.class),
                isA(Long.class),
                isA(String.class));
    }

    @Test
    public void whenSubmittingAValidFormTheUserProfileDetailsViewIsReturned() throws Exception {


        when(userService.updateDetails(eq(user.getId()), eq(user.getEmail()), eq(user.getFirstName()), eq(user.getLastName()), anyString(),
                eq(user.getPhoneNumber()),
                anyString(),
                anyLong(),
                anyString()))
                .thenReturn(ServiceResult.serviceSuccess(newUserResource().build()));
        mockMvc.perform(post("/profile/edit")
                .param("title", user.getTitle().name())
                .param("firstName", user.getFirstName())
                .param("lastName", user.getLastName())
                .param("phoneNumber", user.getPhoneNumber())
                .param("gender", user.getGender().name())
                .param("ethnicity", user.getEthnicity().toString())
                .param("disability", user.getDisability().name())

        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile/user-profile"));
    }

    @Test
    public void whenSubmittingAnInvalidFormTheUserProfileDetailsFormIsReturned() throws Exception {
        mockMvc.perform(post("/profile/edit")
                .param("firstName", "illegalcharacters:!@#$%^&*()")
                .param("lastName", "illegalcharacters:!@#$%^&*()")
                .param("phoneNumber", "illegalcharacters:!@#$%^&*()")
                .param("gender", "")
                .param("ethnicity", "")
                .param("disability", "")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile/edit-user-profile"));
    }

    @Test
    public void userServiceResponseErrorsAreAddedTheModel() throws Exception {

        Error error = new Error("objectName", singletonList("fieldName"), BAD_REQUEST);
        when(userService.updateDetails(eq(user.getId()), eq(user.getEmail()), eq(user.getFirstName()), eq(user.getLastName()), anyString(),
                eq(user.getPhoneNumber()), anyString(), anyLong(), anyString()))
                .thenReturn(ServiceResult.serviceFailure(error));

        mockMvc.perform(post("/profile/edit")
                .param("title", user.getTitle().name())
                .param("firstName", user.getFirstName())
                .param("lastName", user.getLastName())
                .param("phoneNumber", user.getPhoneNumber())
                .param("gender", user.getGender().name())
                .param("ethnicity", user.getEthnicity().toString())
                .param("disability", user.getDisability().name())

        )
                .andExpect(model().hasErrors());
    }
}
