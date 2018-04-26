package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

/**
 * Builder for {@link ProfileResource}.
 */
public class ProfileResourceBuilder extends BaseBuilder<ProfileResource, ProfileResourceBuilder> {

    public static ProfileResourceBuilder newProfileResource() {
        return new ProfileResourceBuilder(emptyList());
    }

    public ProfileResourceBuilder(List<BiConsumer<Integer, ProfileResource>> newActions) {
        super(newActions);
    }

    public ProfileResourceBuilder withInnovationAreas(List<InnovationAreaResource>... innovationAreas) {
        return withArraySetFieldByReflection("innovationAreas", innovationAreas);
    }

    public ProfileResourceBuilder withBusinessType(BusinessType... businessTypes) {
        return withArraySetFieldByReflection("businessType", businessTypes);
    }

    public ProfileResourceBuilder withSkillsAreas(String... skillsAreas) {
        return withArraySetFieldByReflection("skillsAreas", skillsAreas);
    }

    public ProfileResourceBuilder withAffiliations(List<AffiliationResource>... affiliations) {
        return withArraySetFieldByReflection("affiliations", affiliations);
    }

    public ProfileResourceBuilder withAddress(AddressResource... addresses) {
        return withArraySetFieldByReflection("address", addresses);
    }

    public ProfileResourceBuilder withSiteTermsAndConditionsAccepted(Boolean... siteTermsAndConditionsAcceptedDates) {
        return withArray((siteTermsAndConditionsAccepted, user) -> setField("siteTermsAndConditionsAccepted",
                siteTermsAndConditionsAccepted, user), siteTermsAndConditionsAcceptedDates);
    }

    @Override
    protected ProfileResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProfileResource>> actions) {
        return new ProfileResourceBuilder(actions);
    }

    @Override
    protected ProfileResource createInitial() {
        return new ProfileResource();
    }
}
