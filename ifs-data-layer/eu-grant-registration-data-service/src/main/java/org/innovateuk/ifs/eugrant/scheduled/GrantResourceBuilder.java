package org.innovateuk.ifs.eugrant.scheduled;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.euactiontype.domain.EuActionType;
import org.innovateuk.ifs.euactiontype.mapper.EuActionTypeMapper;
import org.innovateuk.ifs.euactiontype.repository.EuActionTypeRepository;
import org.innovateuk.ifs.eugrant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * TODO DW - document this class
 */
@Component
public class GrantResourceBuilder {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private EuActionTypeRepository actionTypeRepository;
    private EuActionTypeMapper actionTypeMapper;

    @Autowired
    GrantResourceBuilder(@Autowired EuActionTypeRepository actionTypeRepository, @Autowired EuActionTypeMapper actionTypeMapper) {
        this.actionTypeRepository = actionTypeRepository;
        this.actionTypeMapper = actionTypeMapper;
    }

    ServiceResult<List<ServiceResult<EuGrantResource>>> convertDataRowsToEuGrantResources(List<Map<CsvHeader, String>> dataRows) {

        List<ServiceResult<EuGrantResource>> convertedData = simpleMap(dataRows, this::convertDataRowToEuGrantResource);
        return serviceSuccess(convertedData);
    }

    private ServiceResult<EuGrantResource> convertDataRowToEuGrantResource(Map<CsvHeader, String> dataRow) {

        EuContactResource contact = createContact(dataRow);
        EuFundingResource funding = createFunding(dataRow);

        EuGrantResource grant = new EuGrantResource();
        grant.setContact(contact);
        grant.setFunding(funding);
//        grant.setContactComplete();
//        grant.setFundingComplete();
        grant.setId(UUID.randomUUID());
        grant.setOrganisation(createOrganisation(dataRow));
//        grant.setShortCode();

        return serviceSuccess(grant);
    }

    private EuOrganisationResource createOrganisation(Map<CsvHeader, String> dataRow) {

        EuOrganisationResource organisation = new EuOrganisationResource();
        organisation.setName(dataRow.get(CsvHeader.ORGANISATION_NAME));

        // TODO DW - possible validation / lookup here
        String organisationTypeLabel = dataRow.get(CsvHeader.ORGANISATION_TYPE);
        organisation.setOrganisationType(findOrganisationType(organisationTypeLabel));
        organisation.setCompaniesHouseNumber(dataRow.get(CsvHeader.COMPANIES_HOUSE_REGISTRATION_NUMBER));
        return organisation;
    }

    // TODO DW - possible validation / lookup here
    private EuOrganisationType findOrganisationType(String organisationTypeLabel) {

        return simpleFindFirst(EuOrganisationType.values(),
                type -> type.getDisplayName().equalsIgnoreCase(organisationTypeLabel)).get();
    }

    private EuFundingResource createFunding(Map<CsvHeader, String> dataRow) {

        EuActionTypeResource actionType = createActionType(dataRow);

        EuFundingResource funding = new EuFundingResource();
        funding.setActionType(actionType);

        // TODO DW - number formatting here
        funding.setFundingContribution(new BigDecimal(dataRow.get(CsvHeader.PROJECT_EU_FUNDING_CONTRIBUTION)));
        funding.setGrantAgreementNumber(dataRow.get(CsvHeader.GRANT_AGREEMENT_NUMBER));

        // TODO DW - possible validation available here
        funding.setProjectCoordinator("COORDINATOR".equalsIgnoreCase(dataRow.get(CsvHeader.PROJECT_COORDINATOR)));
        funding.setParticipantId(dataRow.get(CsvHeader.PIC));
        funding.setProjectName(dataRow.get(CsvHeader.PROJECT_NAME));
        funding.setProjectStartDate(toDate(dataRow, CsvHeader.PROJECT_START_DATE));
        funding.setProjectEndDate(toDate(dataRow, CsvHeader.PROJECT_END_DATE));
        return funding;
    }

    // TODO DW - validation here
    private LocalDate toDate(Map<CsvHeader, String> dataRow, CsvHeader header) {
        return LocalDate.from(DATE_FORMAT.parse(dataRow.get(header)));
    }

    private EuActionTypeResource createActionType(Map<CsvHeader, String> dataRow) {

        String actionTypeString = dataRow.get(CsvHeader.ACTION_TYPE);

        // TODO DW - is it necessary to do regex here, or can we just ask for the action type name?
        Pattern actionTypeNamePattern = Pattern.compile("^\\(([a-zA-Z0-9-]+)\\).*$");

        Matcher matcher = actionTypeNamePattern.matcher(actionTypeString);

        // TODO DW - put failure case in here
        matcher.find();

        String actionTypeName = matcher.group(1);

        // TODO DW - optimise by removing findAll from this method?
        Optional<EuActionType> matchingActionType = actionTypeRepository.findOneByName(actionTypeName);

        // TODO DW - error handling
        return matchingActionType.map(actionTypeMapper::mapToResource).orElse(null);
    }

    private EuContactResource createContact(Map<CsvHeader, String> dataRow) {

        EuContactResource contact = new EuContactResource();
        contact.setEmail(dataRow.get(CsvHeader.CONTACT_EMAIL_ADDRESS));
        contact.setJobTitle(dataRow.get(CsvHeader.CONTACT_JOB_TITLE));
        contact.setName(dataRow.get(CsvHeader.CONTACT_FULL_NAME));
        contact.setTelephone(dataRow.get(CsvHeader.CONTACT_TELEPHONE_NUMBER));
        return contact;
    }
}
