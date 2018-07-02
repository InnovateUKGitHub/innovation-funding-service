package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;


/**
 * This class stubs out the Companies House API.
 * Necessary to remove this dependency from developer and tester machines
 * since that API is unreliable and therefore should not be regularly used for automated regression testing.
 *
 * @see <a href="https://developer.companieshouse.gov.uk/api/docs/">Company House API site</a>
 */

@Service
@ConditionalOnProperty(name = "ifs.data.companies-house.lookup.enabled", havingValue = "false")
public class CompanyHouseApiServiceStub implements CompanyHouseApiService {

    @Override
    public ServiceResult<List<OrganisationSearchResult>> searchOrganisations(String encodedSearchText) {
        return encodedSearchText.equals("innoavte") ?
                serviceSuccess(emptyList()) :
                serviceSuccess(asList(getHiveIt(), getWorthIt(), getNomensa(), getInnovate(), getUniversityOfLiverpool()));
    }

    @Override
    public ServiceResult<OrganisationSearchResult> getOrganisationById(String id) {
        return serviceSuccess(getDummyResultById(id));
    }

    private OrganisationSearchResult getDummyResultById(String id) {
        switch(id) {
            case "08852342" : return getHiveIt();
            case "09872150" : return getWorthIt();
            case "04214477" : return getNomensa();
            case "05493105" : return getInnovate();
            default : return getUniversityOfLiverpool();
        }
    }

    private OrganisationSearchResult getHiveIt() {

        return buildDummyResult("Electric Works",
                                "Sheffield Digital Campus",
                                "Concourse Way",
                                "Sheffield",
                                "South Yorkshire",
                                "S1 2BJ",
                                "08852342",
                                "HIVE IT LIMITED",
                                "ltd",
                                "2014-01-20",
                                "08852342 - Incorporated on 20 January 2014");
    }

    private OrganisationSearchResult getWorthIt() {

        return buildDummyResult("Levens Street",
                                "",
                                "",
                                "Salford",
                                "",
                                "M6 6DY",
                                "09872150",
                                "WORTH IT LTD",
                                "ltd",
                                "2015-11-13",
                                "09872150 - Incorporated on 13 November 2015");
    }

    private OrganisationSearchResult getNomensa() {

        return buildDummyResult("13 Queen Square",
                                "",
                                "",
                                "Bristol",
                                "",
                                "BS1 4NT",
                                "04214477",
                                "NOMENSA LTD",
                                "ltd",
                                "2001-05-10",
                                "04214477 - Incorporated on 10 May 2001");
    }

    private OrganisationSearchResult getInnovate() {

        return buildDummyResult("2 Poole Road",
                                "",
                                "",
                                "Bournemouth",
                                "",
                                "BH2 5QY",
                                "05493105",
                                "INNOVATE LTD",
                                "ltd",
                                "2005-06-28",
                                "05493105 - Incorporated on 28 June 2005");
    }

    private OrganisationSearchResult getUniversityOfLiverpool() {

        return buildDummyResult("",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "RC000660",
                                "UNIVERSITY OF LIVERPOOL",
                                "royal-charter",
                                "",
                                "RC000660");
    }

    private OrganisationSearchResult buildDummyResult(String addressLine1,
                                                      String addressLine2,
                                                      String addressLine3,
                                                      String town,
                                                      String county,
                                                      String postcode,
                                                      String id,
                                                      String name,
                                                      String companyType,
                                                      String dateOfCreation,
                                                      String description) {

        AddressResource address = new AddressResource(addressLine1,
                                                      addressLine2,
                                                      addressLine3,
                                                      town,
                                                      county,
                                                      postcode);
        OrganisationSearchResult org = new OrganisationSearchResult(id, name);
        org.setOrganisationAddress(address);
        Map<String, Object> extras = new HashMap<>();
        extras.put("company_type", companyType);
        extras.put("date_of_creation", dateOfCreation);
        extras.put("description", description);
        org.setExtraAttributes(extras);
        return org;
    }
}
