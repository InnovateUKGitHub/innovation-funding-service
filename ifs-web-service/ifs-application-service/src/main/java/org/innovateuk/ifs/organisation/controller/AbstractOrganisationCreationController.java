package org.innovateuk.ifs.organisation.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResultPageResource;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.service.ProjectPartnerInviteRestService;
import org.innovateuk.ifs.registration.form.InviteAndIdCookie;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.service.OrganisationJourneyEnd;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Provides a base class for each of the organisation registration controllers.
 */
public abstract class AbstractOrganisationCreationController {

    protected static final String BASE_URL = "/organisation/create";
    protected static final String ORGANISATION_TYPE = "organisation-type";
    protected static final String FIND_ORGANISATION = "find-organisation";
    protected static final String CONFIRM_ORGANISATION = "confirm-organisation";
    protected static final String INTERNATIONAL_CONFIRM_ORGANISATION = "international-confirm-organisation";
    protected static final String KNOWLEDGE_BASE_CONFIRM_ORGANISATION = "knowledge-base-confirm-organisation";
    protected static final String INTERNATIONAL_ORGANISATION = "international-organisation";
    protected static final String INTERNATIONAL_ORGANISATION_DETAILS = "international-organisation-details";
    protected static final String EXISTING_ORGANISATION = "existing-organisation";
    protected static final String ORGANISATION_FORM = "organisationForm";
    protected static final String TEMPLATE_PATH = "registration/organisation";

    private static final String BINDING_RESULT_ORGANISATION_FORM = "org.springframework.validation.BindingResult.organisationForm";
    protected static final int  SEARCH_ITEMS_MAX = 10;
    protected static final int DEFAULT_PAGE_NUMBER_VALUE = 1;
    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    protected RegistrationCookieService registrationCookieService;

    @Autowired
    protected OrganisationTypeRestService organisationTypeRestService;

    @Autowired
    protected OrganisationSearchRestService organisationSearchRestService;

    @Autowired
    protected AddressRestService addressRestService;

    @Autowired
    protected OrganisationRestService organisationRestService;

    @Autowired
    protected OrganisationJourneyEnd organisationJourneyEnd;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private ProjectPartnerInviteRestService projectPartnerInviteRestService;

    protected Validator validator;

    @Autowired
    @Qualifier("mvcValidator")
    protected void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Value("${ifs.new.organisation.search.enabled:false}")
    protected Boolean isNewOrganisationSearchEnabled;

    protected OrganisationCreationForm getFormDataFromCookie(OrganisationCreationForm organisationForm, Model model, HttpServletRequest request) {
          return processedOrganisationCreationFormFromCookie(model, request).
                orElseGet(() -> processedOrganisationCreationFormFromRequest(organisationForm, request));
    }

    protected OrganisationCreationForm getImprovedSearchFormDataFromCookie(OrganisationCreationForm organisationForm, Model model, HttpServletRequest request, int pageNumber, boolean isOrganisationSearching) {
       if (isNewOrganisationSearchEnabled) {
           return processedImprovedOrganisationCreationFormFromCookie(model, request, pageNumber, isOrganisationSearching).
                   orElseGet(() -> processedOrganisationCreationFormFromRequest(organisationForm, request));
       }
       else {
          return getFormDataFromCookie(organisationForm, model, request);
       }
    }

    private OrganisationCreationForm processedOrganisationCreationFormFromRequest(OrganisationCreationForm organisationForm, HttpServletRequest request){
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        return organisationForm;
    }

    private Optional<OrganisationCreationForm> processedOrganisationCreationFormFromCookie(Model model, HttpServletRequest request) {
        Optional<OrganisationCreationForm> organisationCreationFormFromCookie = registrationCookieService.getOrganisationCreationCookieValue(request);
        organisationCreationFormFromCookie.ifPresent(organisationCreationForm -> {
            populateOrganisationCreationForm(request, organisationCreationForm);

            BindingResult bindingResult = new BeanPropertyBindingResult(organisationCreationForm, ORGANISATION_FORM);
            organisationFormValidate(organisationCreationForm, bindingResult);
            model.addAttribute(BINDING_RESULT_ORGANISATION_FORM, bindingResult);
        });
        return organisationCreationFormFromCookie;
    }

    private Optional<OrganisationCreationForm> processedImprovedOrganisationCreationFormFromCookie(Model model, HttpServletRequest request, int pageNumber, boolean isOrganisationSearching) {
        Optional<OrganisationCreationForm> organisationCreationFormFromCookie = registrationCookieService.getOrganisationCreationCookieValue(request);
        organisationCreationFormFromCookie.ifPresent(organisationCreationForm -> {
            organisationCreationForm.setOrganisationSearching(isOrganisationSearching);
            populateOrganisationCreationForm(request, organisationCreationForm, pageNumber);

            BindingResult bindingResult = new BeanPropertyBindingResult(organisationCreationForm, ORGANISATION_FORM);
            organisationFormValidate(organisationCreationForm, bindingResult);
            model.addAttribute(BINDING_RESULT_ORGANISATION_FORM, bindingResult);
        });
        return organisationCreationFormFromCookie;
    }

    private void populateOrganisationCreationForm(HttpServletRequest request, OrganisationCreationForm organisationCreationForm) {
           searchOrganisation(organisationCreationForm);
           addOrganisationType(organisationCreationForm, organisationTypeIdFromCookie(request));
    }

    private void populateOrganisationCreationForm(HttpServletRequest request, OrganisationCreationForm organisationCreationForm, int pageNumber) {
        if (organisationCreationForm.isOrganisationSearching()) {
            addOrganisationSearchIndex(organisationCreationForm, pageNumber);
            improvedSearchOrganisation(organisationCreationForm);
            addOrganisationType(organisationCreationForm, organisationTypeIdFromCookie(request));
        }
    }

    protected void addOrganisationType(OrganisationCreationForm organisationForm, Optional<Long> organisationTypeId) {
        organisationTypeId.ifPresent(organisationForm::setOrganisationTypeId);
    }

    protected Optional<Long> organisationTypeIdFromCookie(HttpServletRequest request) {
        Optional<OrganisationTypeForm> organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request);

        if (organisationTypeForm.isPresent()) {
            return Optional.ofNullable(organisationTypeForm.get().getOrganisationType());
        } else {
            return Optional.empty();
        }
    }

    private void organisationFormValidate(OrganisationCreationForm organisationForm, BindingResult bindingResult) {
        validator.validate(organisationForm, bindingResult);
    }

    private void searchOrganisation(OrganisationCreationForm organisationForm) {
        if (organisationForm.isOrganisationSearching()) {
            if (isNotBlank(organisationForm.getOrganisationSearchName())) {
                String trimmedSearchString = StringUtils.normalizeSpace(organisationForm.getOrganisationSearchName());
                List<OrganisationSearchResult> searchResults;
                searchResults = organisationSearchRestService.searchOrganisation(organisationForm.getOrganisationTypeId(), trimmedSearchString, 0)
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


    private void improvedSearchOrganisation(OrganisationCreationForm organisationForm) {
        if (organisationForm.isOrganisationSearching()) {
            if (isNotBlank(organisationForm.getOrganisationSearchName())) {
                String trimmedSearchString = StringUtils.normalizeSpace(organisationForm.getOrganisationSearchName());
                int indexPosition = organisationForm.getSearchPageIndexPosition();
                List<OrganisationSearchResult> searchResults;
                searchResults = organisationSearchRestService.searchOrganisation(organisationForm.getOrganisationTypeId(), trimmedSearchString, indexPosition)
                        .handleSuccessOrFailure(
                                f -> new ArrayList<>(),
                                s -> s
                        );
                organisationForm.setOrganisationSearchResults(searchResults);
                organisationForm.setTotalSearchResults(setTotalSearchResults(searchResults));
            } else {
                organisationForm.setOrganisationSearchResults(new ArrayList<>());
            }
        }
    }

    private int setTotalSearchResults(List<OrganisationSearchResult> searchResults) {
       Optional<OrganisationSearchResult> searchResult = searchResults.stream().findFirst();
        if(searchResult.isPresent()) {
            String totalSearch = "";
            totalSearch = (String)searchResult.get().getExtraAttributes().get("total_results");
           return totalSearch.isEmpty() ? 0 : Integer.parseInt(totalSearch);
        }
        return 0;
    }

    /**
     * after user has selected a organisation, get the details and add it to the form and the model.
     */
    protected OrganisationSearchResult addSelectedOrganisation(OrganisationCreationForm organisationForm, Model model) {
        if (!organisationForm.isManualEntry() && isNotBlank(organisationForm.getSearchOrganisationId())) {
            OrganisationSearchResult organisationSearchResult = organisationSearchRestService.getOrganisation(organisationForm.getOrganisationTypeId(), organisationForm.getSearchOrganisationId()).getSuccess();
            organisationForm.setOrganisationName(organisationSearchResult.getName());
            if(isNewOrganisationSearchEnabled && !organisationForm.isResearch()) {
                String localDateString = (String) organisationSearchResult.getExtraAttributes().get("date_of_creation");
                if (localDateString != null) {
                    organisationForm.setDateOfIncorporation(LocalDate.parse(localDateString, DATE_PATTERN));
                }
                organisationForm.setOrganisationAddress(organisationSearchResult.getOrganisationAddress());
                organisationForm.setSicCodes(organisationSearchResult.getOrganisationSicCodes());
                organisationForm.setExecutiveOfficers(organisationSearchResult.getOrganisationExecutiveOfficers());
            }
            model.addAttribute("selectedOrganisation", organisationSearchResult);
            return organisationSearchResult;
        }
        return null;
    }

    protected void addPageSubtitleToModel(HttpServletRequest request, UserResource user, Model model) {
        if (user != null) {
            if (registrationCookieService.getProjectInviteHashCookieValue(request).isPresent()) {
                model.addAttribute("subtitle", "Join project");
            } else if (registrationCookieService.isCollaboratorJourney(request)) {
                model.addAttribute("subtitle", "Join application");
            } else if (!model.containsAttribute("subtitle")) {
                model.addAttribute("subtitle", "Create new application");
            }
        } else {
            model.addAttribute("subtitle", "Create an account");
        }
    }

    protected long getCompetitionIdFromInviteOrCookie(HttpServletRequest request) {
        if (registrationCookieService.isLeadJourney(request)) {
            return registrationCookieService.getCompetitionIdCookieValue(request).orElseThrow(ObjectNotFoundException::new);
        } else if (registrationCookieService.getProjectInviteHashCookieValue(request).isPresent()) {
            InviteAndIdCookie projectInvite = registrationCookieService.getProjectInviteHashCookieValue(request).get();
            SentProjectPartnerInviteResource invite = projectPartnerInviteRestService.getInviteByHash(projectInvite.getId(), projectInvite.getHash()).getSuccess();
            return invite.getCompetitionId();
        } else {
            String applicationInviteHash = registrationCookieService.getInviteHashCookieValue(request).orElseThrow(ObjectNotFoundException::new);
            ApplicationInviteResource invite = inviteRestService.getInviteByHash(applicationInviteHash).getSuccess();
            return invite.getCompetitionId();
        }
    }

    protected void addPageResourceToModel(OrganisationCreationForm organisationForm, Model model, int pageNumber) {
        long totalElements = organisationForm.getTotalSearchResults();
        int reminder = (int) (totalElements % SEARCH_ITEMS_MAX);
        int pages = (int) (totalElements / SEARCH_ITEMS_MAX);
        int totalPages = reminder > 0 ? pages + 1  : pages;
        List<OrganisationSearchResult> content = organisationForm.getOrganisationSearchResults();
        int number = pageNumber - 1;
        int size = SEARCH_ITEMS_MAX;
        model.addAttribute("pagination", new PaginationViewModel(new OrganisationSearchResultPageResource(totalElements, totalPages, content, number,size)));
    }

    private void addOrganisationSearchIndex(OrganisationCreationForm organisationForm, int pageNumber) {
        int searchPageIndexPosition = pageNumber == 1 ? 0 : (pageNumber -1) * SEARCH_ITEMS_MAX;
        organisationForm.setSearchPageIndexPosition(searchPageIndexPosition);
    }

}
