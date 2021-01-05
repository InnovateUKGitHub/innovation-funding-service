package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
 * @see <a href="https://developer.companieshouse.gov.uk/api/docs/">Companies House API site</a>
 */

@Service
@ConditionalOnProperty(name = "ifs.data.companies.house.lookup.enabled", havingValue = "false")
public class CompaniesHouseApiServiceStub implements CompaniesHouseApiService {
    protected static final int  INDEX_POSITION = 0;
    private static final String TOTAL_SEARCH_RESULTS = "11";

    @Value("${ifs.new.organisation.search.enabled}")
    private boolean isImprovedSearchEnabled = false;

    @Override
    public ServiceResult<List<OrganisationSearchResult>> searchOrganisations(String encodedSearchText, int indexPos) {
       if (isImprovedSearchEnabled) {
           return improvedSearchOrganisations(encodedSearchText, indexPos);
       }
        return encodedSearchText.equals("innoavte") ?
                serviceSuccess(emptyList()) :
                serviceSuccess(asList(getHiveIt(), getWorthIt(), getNomensa(), getInnovate(), getUniversityOfLiverpool()));
    }


    public ServiceResult<List<OrganisationSearchResult>> improvedSearchOrganisations(String encodedSearchText, int indexPos) {
        if (indexPos == 0) {
            return encodedSearchText.equals("innoavte") ?
                    serviceSuccess(emptyList()) :
                    serviceSuccess(getFirstPageSearchResults());
        } else {
            return encodedSearchText.equals("innoavte") ?
                    serviceSuccess(emptyList()) :
                    serviceSuccess(getSecondPageSearchResults());
        }
    }

    private List<OrganisationSearchResult> getFirstPageSearchResults() {
        return asList(getAmadeus(), getASOS(), getAVIVA(), getBBC(), getCineWorld(), getFirstGroupPlc(),
                getITV(), getRoyalMail(), getSAGA(), getTesco());
    }

    private List<OrganisationSearchResult> getSecondPageSearchResults() {
        return asList(getVirginMoney());

    }

    @Override
    public ServiceResult<OrganisationSearchResult> getOrganisationById(String id) {
        if (isImprovedSearchEnabled) {
            return serviceSuccess(getImprovedResultById(id));
        } else {
            return serviceSuccess(getDummyResultById(id));
        }
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

    private OrganisationSearchResult getImprovedResultById(String id) {
        switch(id) {
            case "02276684" : return getAmadeus();
            case "04006623" : return getASOS();
            case "02468686" : return getAVIVA();
            case "07520089" : return getBBC();
            case "04081830" : return getCineWorld();
            case "SC157176" : return getFirstGroupPlc();
            case "04967001" : return getITV();
            case "08680755" : return getRoyalMail();
            case "08804263" : return getSAGA();
            case "00445790" : return getTesco();
            case "09595911" : return getVirginMoney();
            default : return getRoyalMail();
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

    private OrganisationSearchResult getAmadeus() {
        return buildDummyOrganisationSearchResult("3rd Floor First Point",
                "Buckingham Gate London Gatwick Airport",
                "",
                "Gatwick",
                "West Sussex",
                "RH6 0NT",
                "02276684",
                "AMADEUS IT SERVICES UK LIMITED",
                "Business",
                "1988-07-13",
                "02276684 - Incorporated on 13 July 1988",
                asList("62020","63990","79909"),
                asList("BOUSQUET, Christophe","KRAFT ANTELYES, Diana","MAGESH, Champa Hariharan","SANCHEZ QUINONES, Arturo"));
    }
    private OrganisationSearchResult getASOS() {
        return buildDummyOrganisationSearchResult("Greater London House",
                "Hampstead Road",
                " ",
                "London",
                "",
                "NW1 7FB",
                "04006623",
                "ASOS PLC",
                "plc",
                "2000-06-02",
                "04006623 - Incorporated on 2 June 2000",
                 asList("70100"),
                 asList("BEIGHTON, Nicholas Timothy", "CROZIER, Adam Alexander", "DUNN, Mathew James", "DYSON, Ian","FYFIELD, Rowenna Mai",
                        "GEARY, Karen Mary", "JENSEN, Luke Giles William", "ROBERTSON, Nicholas John", "ULASEWICZ, Eugenia Marie"));
    }
    private OrganisationSearchResult getAVIVA() {
        return buildDummyOrganisationSearchResult("St Helen's",
                "1 Undershaft",
                " ",
                "London",
                "",
                "EC3P 3DQ",
                "02468686",
                "Aviva Plc",
                "plc",
                "1990-02-09",
                "02468686 - Incorporated on 9 February 1990",
                asList("70100"), asList("BLANC, Amanda Jayne", "CROSS, Patricia Anne","CULMER, Mark George","FLYNN, Patrick Gerard","JOSHI, Mohit",
                                        "MCCONVILLE, James","Michael Philip","ROMANA GARCIA, Belen","WINDSOR, Jason Michael"));

    }
    private OrganisationSearchResult getBBC() {
           return buildDummyOrganisationSearchResult("Unit 2 Restormel Estate",
                "Liddicoat Road",
                " ",
                "Lostwithiel",
                "Cornwall",
                "PL22 0HG",
                "07520089",
                "BBC AND CO LIMITED",
                "ltd",
                "2011-02-07",
                "07520089 - Incorporated on 7 February 2011",
                asList("69201","69202","69203"),
                asList("BATE, Philip Henry"));
    }

    private OrganisationSearchResult getCineWorld() {
        return buildDummyOrganisationSearchResult("778 Rivington St",
                "",
                " ",
                "London",
                " ",
                "EC2A 3FF",
                "04081830",
                "CINEWORLD LIMITED",
                "ltd",
                "1995-03-16",
                "04081830 - Incorporated on 16 March 1995",
                asList("74909"),
                asList("LANGEMANN, Cordula"));
    }
    private OrganisationSearchResult getFirstGroupPlc() {
            return buildDummyOrganisationSearchResult("395 King Street",
                "",
                " ",
                "Aberdeen",
                " ",
                "AB24 5RP",
                "SC157176",
                "FIRSTGROUP PLC",
                "plc",
                "1995-03-31",
                "SC157176 - Incorporated on 31 March 1995",
                asList("49100", "49100"),
                asList("GREEN, Anthony Charles","GREGORY, Matthew","GUNNING, Stephen William Lawrence","MANGOLD, Ryan Dirk",
                        "MARTIN, David Robert","ROBBIE, David Andrew"));
       }

    private OrganisationSearchResult getITV() {
        return buildDummyOrganisationSearchResult("Waterhouse Square",
                "",
                "",
                "London",
                "",
                "EC1N 2AE",
                "04967001",
                "ITV PLC",
                "plc",
                "2003-11-18",
                "04967001 - Incorporated on 18 November 2003",
                asList("82990"),
                asList("AMIN, Salman","BAZALGETTE, Sir Peter Lytton","BONHAM CARTER, Edward Henry","COOKE, " +
                        "Graham Christopher","EWING, Margaret","HARRIS, Mary Elaine","KENNEDY, Christopher John",
                        "MANZ, Anna Olive Magdelene","MCCALL, Carolyn Julia, Dame"));

    }
    private OrganisationSearchResult getRoyalMail() {
        return buildDummyOrganisationSearchResult("100 Victoria Embankment",
                "",
                "",
                "London",
                "",
                "EC4Y 0HQ",
                "08680755",
                "ROYAL MAIL PLC",
                "plc",
                "2013-09-06",
                "08680755 - Incorporated on 6 September 2013",
                 asList("64209"),
                 asList("HOGG, Sarah Elizabeth Mary, Baroness","PEACOCK, Lynne","SILVA, Maria Juana Da Cunha Da",
                         "SIMPSON, Stuart Campbell","THOMPSON, Simon","WILLIAMS, Keith"));
    }
    private OrganisationSearchResult getSAGA() {
        return buildDummyOrganisationSearchResult("Enbrook Park",
                "Sandgate",
                "",
                "Folkestone",
                "Kent",
                "CT20 3SE",
                "08804263",
                "SAGA PLC",
                "plc",
                "2013-12-05",
                "08804263 - Incorporated on 5 December 2013",
                 asList("70100"),
                asList("EISENSCHIMMEL, Eva Kristina","HOPES, Julie","HOSKIN, Gareth John","NI-CHIONNA, Orna Gabrielle",
                        "QUIN, James","SUTHERLAND, Euan Angus","WILLIAMS, Gareth"));
    }


    private OrganisationSearchResult getTesco() {
        return buildDummyOrganisationSearchResult("Kestrel Way",
                "Tesco House, Shire Park",
                "",
                "Welwyn Garden City",
                "Hertfordshire",
                "AL7 1GA",
                "00445790",
                "TESCO PLC",
                "plc",
                "1947-11-27",
                "00445790 - Incorporated on 27 November 1947",
                asList("47110"),
                asList("BETHELL, Melissa","GILLILAND, Stewart Charles","GOLSBY, Stephen William","GROTE, Byron Elmer","MURPHY, Ken",
                        "OLSSON, Anders Bertil Mikael","OPPENHEIMER, Deanna Watson"));

    }

    private OrganisationSearchResult getVirginMoney() {
          return buildDummyOrganisationSearchResult("Gosforth",
                "",
                "",
                "Gosforth",
                "Newcastle Upon Tyne",
                "NE3 4PL",
                "09595911",
                "VIRGIN MONEY UK PLC",
                "plc",
                "2015-05-18",
                "09595911 - Incorporated on 18 May 2015",
                asList("70100"),
                asList("COBY, Paul Jonathan","DUFFY, David Joseph","GOPALAN, Geeta","POPE, Darren Scott","STIRLING, Amy Elizabeth",
                          "WADE, Timothy Cardwell"));
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
    private OrganisationSearchResult buildDummyOrganisationSearchResult(String addressLine1,
                                                                        String addressLine2,
                                                                        String addressLine3,
                                                                        String town,
                                                                        String county,
                                                                        String postcode,
                                                                        String id,
                                                                        String name,
                                                                        String companyType,
                                                                        String dateOfCreation,
                                                                        String description,
                                                                        List<String> sicCodes,
                                                                        List<String> directors) {

        AddressResource address = new AddressResource(addressLine1,
                addressLine2,
                addressLine3,
                town,
                county,
                postcode);

        OrganisationSearchResult org = new OrganisationSearchResult(id, name);
        org.setOrganisationSicCodes(getSicCodes(sicCodes));
        org.setOrganisationAddress(address);
        org.setOrganisationExecutiveOfficers(getDirectors(directors));

        Map<String, Object> extras = new HashMap<>();
        extras.put("company_type", companyType);
        extras.put("date_of_creation", dateOfCreation);
        extras.put("description", description);
        extras.put("total_results", TOTAL_SEARCH_RESULTS);
        org.setExtraAttributes(extras);
        return org;
    }

   private List<OrganisationSicCodeResource> getSicCodes(List<String> sicCodes) {
        List<OrganisationSicCodeResource> sicCodeResources = new ArrayList<>();
         sicCodes.forEach(sicCode -> {
            sicCodeResources.add(new OrganisationSicCodeResource(null,sicCode));
        });
        return sicCodeResources;
    }
    private List<OrganisationExecutiveOfficerResource> getDirectors(List<String> directors) {
        List<OrganisationExecutiveOfficerResource> directorResources = new ArrayList<>();
        directors.forEach(director -> {
            directorResources.add(new OrganisationExecutiveOfficerResource(null,director));
        });
        return directorResources;
    }
}
