package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.competition.domain.SiteTermsAndConditions;
import org.innovateuk.ifs.competition.repository.SiteTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsBuilder.newSiteTermsAndConditions;
import static org.junit.Assert.assertEquals;

public class TermsAndConditionsControllerIntegrationTest extends
        BaseControllerIntegrationTest<TermsAndConditionsController> {

    @Autowired
    private SiteTermsAndConditionsRepository siteTermsAndConditionsRepository;

    @Autowired
    @Override
    protected void setControllerUnderTest(final TermsAndConditionsController controller) {
        this.controller = controller;
    }

    @Test
    public void getLatestSiteTermsAndConditions() throws Exception {
        SiteTermsAndConditions siteTermsAndConditions = setUpSiteTermsAndConditions();

        SiteTermsAndConditionsResource result = controller.getLatestSiteTermsAndConditions().getSuccess();

        assertEquals(siteTermsAndConditions.getId(), result.getId());
        assertEquals(siteTermsAndConditions.getName(), result.getName());
        assertEquals(siteTermsAndConditions.getTemplate(), result.getTemplate());
        assertEquals(siteTermsAndConditions.getVersion(), result.getVersion());
    }

    private SiteTermsAndConditions setUpSiteTermsAndConditions() {
        loginCompAdmin();

        Optional<SiteTermsAndConditions> latest = Optional.of(siteTermsAndConditionsRepository
                .findTopByOrderByVersionDesc());

        int nextVersion = latest.map(siteTermsAndConditions -> siteTermsAndConditions.getVersion() + 1).orElse(1);

        SiteTermsAndConditions siteTermsAndConditions = newSiteTermsAndConditions()
                .with(id(null))
                .withName("test terms and conditions")
                .withTemplate("test-terms-and-conditions")
                .withVersion(nextVersion)
                .build();

        return siteTermsAndConditionsRepository.save(siteTermsAndConditions);
    }
}