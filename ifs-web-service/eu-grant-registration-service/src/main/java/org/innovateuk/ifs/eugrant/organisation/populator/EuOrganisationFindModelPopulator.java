package org.innovateuk.ifs.eugrant.organisation.populator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.organisation.form.EuOrganisationForm;
import org.innovateuk.ifs.eugrant.organisation.viewmodel.EuOrganisationFindViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Component
public class EuOrganisationFindModelPopulator {

    private static final Log LOG = LogFactory.getLog(EuOrganisationFindModelPopulator.class);

    @Autowired
    private OrganisationSearchRestService searchRestService;

    @Autowired
    private MessageSource messageSource;

    public EuOrganisationFindViewModel populate(EuOrganisationType type, EuOrganisationForm organisationForm, HttpServletRequest request) {
        List<OrganisationSearchResult> results;
        if (organisationForm.isOrganisationSearching()) {
            results = searchOrganisation(organisationForm, type);
        } else {
            results = emptyList();
        }
        String searchLabel = getMessageByOrganisationType(type, "SearchLabel",  request.getLocale());
        String searchHint = getMessageByOrganisationType(type, "SearchHint",  request.getLocale());

        return new EuOrganisationFindViewModel(searchLabel, searchHint, type, results);
    }

    private String getMessageByOrganisationType(EuOrganisationType orgTypeEnum, String textKey, Locale locale) {
        try {
            return messageSource.getMessage(String.format("registration.%s.%s", orgTypeEnum.name(), textKey), null, locale);
        } catch (NoSuchMessageException e) {
            LOG.error("unable to get message", e);
            return messageSource.getMessage(String.format("registration.DEFAULT.%s", textKey), null, locale);
        }
    }

    private List<OrganisationSearchResult> searchOrganisation(EuOrganisationForm form, EuOrganisationType type) {
        if (isNotBlank(form.getOrganisationSearchName())) {
            String trimmedSearchString = StringUtils.normalizeSpace(form.getOrganisationSearchName());
            return searchRestService.searchOrganisation(type, trimmedSearchString)
                    .getOptionalSuccessObject()
                    .orElse(emptyList());
        } else {
            return emptyList();
        }
    }


}
