package org.innovateuk.ifs.organisation.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public class OrganisationSearchResultBuilder extends BaseBuilder<OrganisationSearchResult, OrganisationSearchResultBuilder> {

    public OrganisationSearchResultBuilder(List<BiConsumer<Integer, OrganisationSearchResult>> newActions) {
        super(newActions);
    }

    public static OrganisationSearchResultBuilder newOrganisationSearchResult(){
        return new OrganisationSearchResultBuilder(emptyList());
    }

    @Override
    protected OrganisationSearchResultBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationSearchResult>> actions) {
        return new OrganisationSearchResultBuilder(actions);
    }

    @Override
    protected OrganisationSearchResult createInitial() {
        return new OrganisationSearchResult();
    }

    public OrganisationSearchResultBuilder withOrganisationSearchId(String... organisationSearchIds){
        return withArray((id, orgId) -> setField("organisationSearchId", id, orgId), organisationSearchIds);
    }

    public OrganisationSearchResultBuilder withName(String... names){
        return withArray((name, orgName) -> setField("name", name, orgName), names);
    }

    public OrganisationSearchResultBuilder withAddressResource(AddressResource... organisationAddress){
        return withArray((address, orgAddress) -> setField("organisationAddress", address, orgAddress), organisationAddress);
    }

    public OrganisationSearchResultBuilder withextraAttributes(Map<String, Object>... extraAttributes){
        return withArray((extraAttribute, orgExtraAttribute) -> setField("extraAttributes", extraAttribute, orgExtraAttribute), extraAttributes);
    }
}
