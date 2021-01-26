package org.innovateuk.ifs.organisation.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.domain.SicCode;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.stereotype.Service;
import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;

import java.time.LocalDate;
import java.util.List;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contains static matching logic used in @{OrganisationMatchingServiceImpl}.
 */
@Service
public class OrganisationPatternMatcher {

    private static final Log LOG = LogFactory.getLog(OrganisationPatternMatcher.class);

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean organisationTypeMatches(Organisation organisation, OrganisationResource organisationResource) {
        try {
            return organisation.getOrganisationType().getId().equals(organisationResource.getOrganisationType());
        }
        catch(NullPointerException e) {
            LOG.trace("NPE when checking organisation type match", e);
            return false;
        }
    }

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean organisationTypeIsResearch(Organisation organisation) {
        try {
            return OrganisationTypeEnum.isResearch(organisation.getOrganisationType().getId());
        }
        catch(NullPointerException e) {
            LOG.trace("NPE when checking organisation type is research", e);
            return false;
        }
    }

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean sicCodesMatch(OrganisationResource submittedOrganisationResource, Organisation organisation) {
        try {
            List<String> sics = submittedOrganisationResource.getSicCodes().stream().map(s -> s.getSicCode()).sorted().collect(Collectors.toList());
            List<String> sicResource= organisation.getSicCodes().stream().map(s -> s.getSicCode()).sorted().collect(Collectors.toList());
            return  isEqualCollection(sics,sicResource);
        }
        catch(NullPointerException e) {
            LOG.trace("NPE when checking organisation sic codes match", e);
            return false;
        }
    }

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean stringsMatch(String name, String name1) {

        try {
            return  name.equals(name1);
        }
        catch(NullPointerException e) {
            LOG.trace("NPE when checking organisation names codes match", e);
            return false;
        }
    }

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean executiveOfficersMatch(OrganisationResource submittedOrganisationResource, Organisation foundOrg) {

        try {
            List<String> executiveOfficers = submittedOrganisationResource.getExecutiveOfficers().stream().map(s -> s.getName()).collect(Collectors.toList());
            List<String> executiveOfficersResource= foundOrg.getExecutiveOfficers().stream().map(s -> s.getName()).collect(Collectors.toList());
            return  isEqualCollection(executiveOfficers,executiveOfficersResource);
        }
        catch(NullPointerException e) {
            LOG.trace("NPE when checking organisation executive offciers match", e);
            return false;
        }
    }

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean addressesMatch(final OrganisationResource submittedOrganisationResource, final Organisation foundOrg) {
        List<Address> addresses = submittedOrganisationResource.getAddresses().stream().map( address -> buildAddressFromAddressResource(address.getAddress())).collect(Collectors.toList());
        List<Address> addresses2 = foundOrg.getAddresses().stream().map(OrganisationAddress::getAddress).collect(Collectors.toList());
        return isEqualCollection(addresses,addresses2);
    }

    private Address buildAddressFromAddressResource(final AddressResource addressResource){
        Address address = new Address();
        address.setAddressLine1(addressResource.getAddressLine1());
        address.setAddressLine2(addressResource.getAddressLine2());
        address.setAddressLine3(addressResource.getAddressLine3());
        address.setTown(addressResource.getTown());
        address.setCounty(addressResource.getCounty());
        address.setCountry(addressResource.getCountry());
        address.setPostcode(addressResource.getPostcode());
        return  address;
    }

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean matchLocalDate(LocalDate dateOfIncorporation, LocalDate dateOfIncorporation1) {
     return dateOfIncorporation.equals(dateOfIncorporation1);
    }
}
