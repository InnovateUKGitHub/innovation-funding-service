package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.ExecutiveOfficer;
import org.innovateuk.ifs.organisation.domain.ExecutiveOfficer;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.domain.SicCode;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.repository.ExecutiveOfficerRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.repository.SicCodeRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationExecutiveOfficerResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSicCodeResource;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationSicCodeBuilder.newOrganisationSicCode;
import static org.innovateuk.ifs.organisation.builder.OrganisationExecutiveOfficerBuilder.newOrganisationExecutiveOfficer;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressTypeBuilder.newAddressType;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationSicCodeResourceBuilder.newOrganisationSicCodeResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationExecutiveOfficerResourceBuilder.newOrganisationExecutiveOfficerResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static org.innovateuk.ifs.address.builder.AddressTypeResourceBuilder.newAddressTypeResource;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class OrganisationServiceImplTest extends BaseServiceUnitTest<OrganisationService> {

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private OrganisationMapper organisationMapper;

    @Mock
    private SicCodeRepository sicCodeRepository;

    @Mock
    private ExecutiveOfficerRepository executiveOfficerRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private OrganisationAddressRepository organisationAddressRepository;

    protected OrganisationService supplyServiceUnderTest() {
        return new OrganisationServiceImpl();
    }

    @Test
    public void syncCompaniesHouseDetailsAddsDetails() {
        LocalDate date = LocalDate.now();
        OrganisationResource organisationResource = newOrganisationResource().build();
        Organisation organisation = newOrganisation().build();
        List<SicCode> sicCodes = newOrganisationSicCode()
                .withSicCode("12345", "67890")
                .build(2);
        List<ExecutiveOfficer> executiveOfficers = newOrganisationExecutiveOfficer()
                .withName("Name-1", "Name-2")
                .build(2);
        Address address = newAddress()
                .withAddressLine1("address line 1")
                .withAddressLine2("address line 2")
                .withAddressLine3("address line 3")
                .withTown("town")
                .withCounty("county")
                .withCountry("country")
                .withPostcode("postcode")
                .build();
        AddressType addressType = newAddressType()
                .withId(OrganisationAddressType.REGISTERED.getId())
                .withName(OrganisationAddressType.REGISTERED.name())
                .build();
        OrganisationAddress organisationAddress = newOrganisationAddress()
                .withAddress(address)
                .withAddressType(addressType)
                .build();
        Organisation organisationData = newOrganisation()
                .withDateOfIncorporation(date)
                .withSicCodes(sicCodes)
                .withExecutiveOfficers(executiveOfficers)
                .withAddresses(Collections.singletonList(organisationAddress))
                .build();

        List<OrganisationSicCodeResource> sicCodeResources = newOrganisationSicCodeResource()
                .withSicCode("12345", "67890")
                .build(2);
        List<OrganisationExecutiveOfficerResource> executiveOfficerResources = newOrganisationExecutiveOfficerResource()
                .withName("Name-1", "Name-2")
                .build(2);
        AddressResource addressResource = newAddressResource()
                .withAddressLine1("address line 1")
                .withAddressLine2("address line 2")
                .withAddressLine3("address line 3")
                .withTown("town")
                .withCounty("county")
                .withCountry("country")
                .build();
        AddressTypeResource addressTypeResource = newAddressTypeResource()
                .withId(OrganisationAddressType.REGISTERED.getId())
                .withName(OrganisationAddressType.REGISTERED.name())
                .build();
        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource()
                .withAddress(addressResource)
                .withAddressType(addressTypeResource)
                .build();
        OrganisationResource organisationDataResource = newOrganisationResource()
                .withSicCodes(sicCodeResources)
                .withExecutiveOfficers(executiveOfficerResources)
                .withDateOfIncorporation(date)
                .withAddresses(Collections.singletonList(organisationAddressResource))
                .build();

        when(organisationRepository.findById(anyLong())).thenReturn(Optional.of(organisation));
        when(organisationMapper.mapToDomain(any(OrganisationResource.class))).thenReturn(organisationData);
        when(sicCodeRepository.findByOrganisationId(anyLong())).thenReturn(Collections.emptyList());
        when(executiveOfficerRepository.findByOrganisationId(anyLong())).thenReturn(Collections.emptyList());
        when(organisationAddressRepository.findByOrganisationIdAndAddressType(anyLong(), any(AddressType.class)))
                .thenReturn(Collections.emptyList());
        when(organisationRepository.save(any(Organisation.class))).thenReturn(organisationData);
        when(organisationMapper.mapToResource(any(Organisation.class))).thenReturn(organisationDataResource);

        ServiceResult<OrganisationResource> result = service.syncCompaniesHouseDetails(organisationResource);

        assertTrue(result.isSuccess());

        OrganisationResource resultOrganisation = result.getSuccess();

        assertEquals(organisationDataResource.getDateOfIncorporation(), resultOrganisation.getDateOfIncorporation());
        assertEquals(organisationDataResource.getSicCodes(), resultOrganisation.getSicCodes());
        assertEquals(organisationDataResource.getExecutiveOfficers(), resultOrganisation.getExecutiveOfficers());
        assertEquals(organisationDataResource.getAddresses(), resultOrganisation.getAddresses());

        verify(sicCodeRepository, times(0)).deleteAll();
        verify(executiveOfficerRepository, times(0)).deleteAll();
        verify(addressRepository, times(0)).delete(any(Address.class));
        verify(organisationAddressRepository, times(0)).delete(any(OrganisationAddress.class));
    }

    @Test
    public void syncCompaniesHouseDetailsRemovesDetails() {
        LocalDate date = LocalDate.now();
        OrganisationResource organisationResource = newOrganisationResource().build();
        List<SicCode> sicCodes = newOrganisationSicCode()
                .withSicCode("12345", "67890")
                .build(2);
        List<ExecutiveOfficer> executiveOfficers = newOrganisationExecutiveOfficer()
                .withName("Name-1", "Name-2")
                .build(2);
        Address address = newAddress()
                .withAddressLine1("address line 1")
                .withAddressLine2("address line 2")
                .withAddressLine3("address line 3")
                .withTown("town")
                .withCounty("county")
                .withCountry("country")
                .withPostcode("postcode")
                .build();
        AddressType addressType = newAddressType()
                .withId(OrganisationAddressType.REGISTERED.getId())
                .withName(OrganisationAddressType.REGISTERED.name())
                .build();
        OrganisationAddress organisationAddress = newOrganisationAddress()
                .withAddress(address)
                .withAddressType(addressType)
                .build();
        Organisation organisation = newOrganisation()
                .withDateOfIncorporation(date)
                .withSicCodes(sicCodes)
                .withExecutiveOfficers(executiveOfficers)
                .withAddresses(Collections.singletonList(organisationAddress))
                .build();

        Organisation organisationData = newOrganisation().build();
        OrganisationResource organisationDataResource = newOrganisationResource().build();

        when(organisationRepository.findById(anyLong())).thenReturn(Optional.of(organisation));
        when(organisationMapper.mapToDomain(any(OrganisationResource.class))).thenReturn(organisationData);
        when(sicCodeRepository.findByOrganisationId(anyLong())).thenReturn(sicCodes);
        when(executiveOfficerRepository.findByOrganisationId(anyLong())).thenReturn(executiveOfficers);
        when(organisationAddressRepository.findByOrganisationIdAndAddressType(anyLong(), any(AddressType.class)))
                .thenReturn(Collections.singletonList(organisationAddress));
        when(organisationRepository.save(any(Organisation.class))).thenReturn(organisationData);
        when(organisationMapper.mapToResource(any(Organisation.class))).thenReturn(organisationDataResource);

        ServiceResult<OrganisationResource> result = service.syncCompaniesHouseDetails(organisationResource);

        assertTrue(result.isSuccess());

        OrganisationResource resultOrganisation = result.getSuccess();

        assertEquals(organisationDataResource.getDateOfIncorporation(), resultOrganisation.getDateOfIncorporation());
        assertEquals(organisationDataResource.getSicCodes(), resultOrganisation.getSicCodes());
        assertEquals(organisationDataResource.getExecutiveOfficers(), resultOrganisation.getExecutiveOfficers());
        assertEquals(organisationDataResource.getAddresses(), resultOrganisation.getAddresses());

        verify(sicCodeRepository, times(1)).deleteAll(sicCodes);
        verify(executiveOfficerRepository, times(1)).deleteAll(executiveOfficers);
        verify(addressRepository, times(1)).delete(any(Address.class));
        verify(organisationAddressRepository, times(1)).delete(any(OrganisationAddress.class));
    }
}