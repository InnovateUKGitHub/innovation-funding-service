package org.innovateuk.ifs.registration;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.registration.AbstractAcceptInviteController.ORGANISATION_TYPE;
import static org.innovateuk.ifs.util.ExceptionFunctions.getOrRethrow;
import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;
import static org.springframework.web.util.UriUtils.encodeQueryParam;

/**
 * Provides a base class for each of the organisation registration controllers.
 */
@Controller
public class AbstractOrganisationCreationController {

    protected static final String BASE_URL = "/organisation/create";
    protected static final String LEAD_ORGANISATION_TYPE = "lead-organisation-type";
    protected static final String FIND_ORGANISATION = "find-organisation";
    protected static final String CONFIRM_ORGANISATION = "confirm-organisation";

    public static final String ORGANISATION_FORM = "organisationForm";
    public static final String ORGANISATION_ID = "organisationId";
    protected static final String SELECTED_POSTCODE = "selectedPostcode";
    protected static final String USE_SEARCH_RESULT_ADDRESS = "useSearchResultAddress";
    protected static final String MANUAL_ADDRESS = "manual-address";

    protected static final String TEMPLATE_PATH = "registration/organisation";

    private static final String BINDING_RESULT_ORGANISATION_FORM = "org.springframework.validation.BindingResult.organisationForm";

    @Autowired
    protected CookieUtil cookieUtil;

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;

    @Autowired
    protected OrganisationSearchRestService organisationSearchRestService;

    @Autowired
    protected AddressRestService addressRestService;

    protected Validator validator;

    @Autowired
    @Qualifier("mvcValidator")
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    protected OrganisationTypeForm saveToTypeCookie(HttpServletResponse response, Long organisationTypeId) {
        OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(organisationTypeId);
        organisationTypeForm.setLeadApplicant(true);
        String orgTypeForm = JsonUtil.getSerializedObject(organisationTypeForm);

        cookieUtil.saveToCookie(response, ORGANISATION_TYPE, orgTypeForm);

        return organisationTypeForm;
    }

    protected OrganisationCreationForm getFormDataFromCookie(OrganisationCreationForm organisationForm, Model model, HttpServletRequest request) {
        return processedOrganisationCreationFormFromCookie(model, request).
                orElseGet(() -> processedOrganisationCreationFormFromRequest(organisationForm, request));
    }

    private OrganisationCreationForm processedOrganisationCreationFormFromRequest(OrganisationCreationForm organisationForm, HttpServletRequest request){
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        return organisationForm;
    }

    private Optional<OrganisationCreationForm> processedOrganisationCreationFormFromCookie(Model model, HttpServletRequest request) {
        Optional<OrganisationCreationForm> organisationCreationFormFromCookie = organisationCreationFormFromCookie(request);
        organisationCreationFormFromCookie.ifPresent(organisationCreationForm -> {

            populateOrganisationCreationForm(request, organisationCreationForm);

            BindingResult bindingResult = new BeanPropertyBindingResult(organisationCreationForm, ORGANISATION_FORM);
            organisationFormValidate(organisationCreationForm, bindingResult);
            model.addAttribute(BINDING_RESULT_ORGANISATION_FORM, bindingResult);

            BindingResult addressBindingResult = new BeanPropertyBindingResult(organisationCreationForm.getAddressForm().getSelectedPostcode(), SELECTED_POSTCODE);
            organisationFormAddressFormValidate(organisationCreationForm, bindingResult, addressBindingResult);
        });
        return organisationCreationFormFromCookie;
    }

    protected void addOrganisationType(OrganisationCreationForm organisationForm, Optional<Long> organisationTypeId) {
        organisationTypeId.ifPresent(id -> {
            organisationTypeRestService.findOne(id).ifSuccessful(organisationType ->
                    organisationForm.setOrganisationType(organisationType));
        });
    }

    protected Optional<Long> organisationTypeIdFromCookie(HttpServletRequest request) {
        String organisationTypeJson = cookieUtil.getCookieValue(request, ORGANISATION_TYPE);
        if (isNotBlank(organisationTypeJson)) {
            return Optional.ofNullable(getObjectFromJson(organisationTypeJson, OrganisationTypeForm.class).getOrganisationType());
        } else {
            return Optional.empty();
        }
    }

    private void populateOrganisationCreationForm(HttpServletRequest request, OrganisationCreationForm organisationCreationForm) {
        searchOrganisation(organisationCreationForm);
        addOrganisationType(organisationCreationForm, organisationTypeIdFromCookie(request));
    }

    private void organisationFormValidate(OrganisationCreationForm organisationForm, BindingResult bindingResult) {
        if (organisationForm.getAddressForm().isTriedToSearch() && isBlank(organisationForm.getAddressForm().getPostcodeInput())) {
            ValidationMessages.rejectValue(bindingResult, "addressForm.postcodeInput", "EMPTY_POSTCODE_SEARCH");
        }
        validator.validate(organisationForm, bindingResult);
    }

    private void searchOrganisation(OrganisationCreationForm organisationForm) {
        if (organisationForm.isOrganisationSearching()) {
            if (isNotBlank(organisationForm.getOrganisationSearchName())) {
                String trimmedSearchString = StringUtils.normalizeSpace(organisationForm.getOrganisationSearchName());
                List<OrganisationSearchResult> searchResults;
                searchResults = organisationSearchRestService.searchOrganisation(organisationForm.getOrganisationType().getId(), trimmedSearchString)
                        .handleSuccessOrFailure(
                                f -> new ArrayList<>(),
                                s -> s
                        );
                organisationForm.setOrganisationSearchResults(searchResults);
            } else {
                organisationForm.setOrganisationSearchResults(new ArrayList<>());
            }
        }
    }

    protected void organisationFormAddressFormValidate(OrganisationCreationForm organisationForm, BindingResult bindingResult, BindingResult addressBindingResult) {
        if (organisationForm.isTriedToSave() && !organisationForm.isUseSearchResultAddress()) {
            AddressForm addressForm = organisationForm.getAddressForm();
            if (addressForm.getSelectedPostcode() != null) {
                validator.validate(addressForm.getSelectedPostcode(), addressBindingResult);
            } else if (!addressForm.isManualAddress()) {
                bindingResult.rejectValue(USE_SEARCH_RESULT_ADDRESS, "NotEmpty", "You should either fill in your address, or use the registered address as your operating address.");
            }
        }
    }

    private Optional<OrganisationCreationForm> organisationCreationFormFromCookie(HttpServletRequest request) {
        String organisationFormJson = cookieUtil.getCookieValue(request, ORGANISATION_FORM);
        if (isNotBlank(organisationFormJson)) {
            return Optional.ofNullable(getObjectFromJson(organisationFormJson, OrganisationCreationForm.class));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get the list of postcode options, with the entered postcode. Add those results to the form.
     */
    protected void addAddressOptions(AddressForm addressForm) {
        if (isNotBlank(addressForm.getPostcodeInput())) {
            addressForm.setPostcodeOptions(searchPostcode(addressForm.getPostcodeInput()));
        }
    }

    /**
     * If user has selected a address from the dropdown, get it from the list, and set it as selected.
     */
    protected void addSelectedAddress(AddressForm addressForm) {
        if (isNotBlank(addressForm.getSelectedPostcodeIndex()) && addressForm.getSelectedPostcode() == null) {
            addressForm.setSelectedPostcode(addressForm.getPostcodeOptions().get(Integer.parseInt(addressForm.getSelectedPostcodeIndex())));
        }
    }

    private List<AddressResource> searchPostcode(String postcodeInput) {
        return addressRestService.doLookup(postcodeInput).getOrElse(new ArrayList<>());
    }

    protected boolean checkOrganisationIsLead(HttpServletRequest request) {
        String organisationTypeJson = cookieUtil.getCookieValue(request, ORGANISATION_TYPE);
        if(isNotBlank(organisationTypeJson)){
            OrganisationTypeForm organisationTypeForm = JsonUtil.getObjectFromJson(organisationTypeJson, OrganisationTypeForm.class);
            return organisationTypeForm.isLeadApplicant();

        }
        return false;
    }

    protected String escapePathVariable(final String input) {
        return getOrRethrow(() -> encodeQueryParam(input, "UTF-8"));
    }

    /**
     * +
     * after user has selected a organisation, get the details and add it to the form and the model.
     */
    protected OrganisationSearchResult addSelectedOrganisation(OrganisationCreationForm organisationForm, Model model) {
        if (!organisationForm.isManualEntry() && isNotBlank(organisationForm.getSearchOrganisationId())) {
            OrganisationSearchResult organisationSearchResult = organisationSearchRestService.getOrganisation(organisationForm.getOrganisationType().getId(), organisationForm.getSearchOrganisationId()).getSuccessObject();
            organisationForm.setOrganisationName(organisationSearchResult.getName());
            model.addAttribute("selectedOrganisation", organisationSearchResult);
            return organisationSearchResult;
        }
        return null;
    }

    protected OrganisationCreationForm getOrganisationCreationFormFromCookie(HttpServletRequest request) {
        String organisationFormJson = cookieUtil.getCookieValue(request, ORGANISATION_FORM);
        OrganisationCreationForm organisationCreationForm = JsonUtil.getObjectFromJson(organisationFormJson, OrganisationCreationForm.class);

        return organisationCreationForm;
    }
}
