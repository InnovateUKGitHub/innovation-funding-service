package org.innovateuk.ifs.eugrant.scheduled;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.euactiontype.domain.EuActionType;
import org.innovateuk.ifs.euactiontype.mapper.EuActionTypeMapper;
import org.innovateuk.ifs.euactiontype.repository.EuActionTypeRepository;
import org.innovateuk.ifs.eugrant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * TODO DW - document this class
 */
@Component
public class GrantResourceBuilder {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private EuActionTypeRepository actionTypeRepository;
    private EuActionTypeMapper actionTypeMapper;

    GrantResourceBuilder(@Autowired EuActionTypeRepository actionTypeRepository, @Autowired EuActionTypeMapper actionTypeMapper) {
        this.actionTypeRepository = actionTypeRepository;
        this.actionTypeMapper = actionTypeMapper;
    }

    ServiceResult<List<ServiceResult<EuGrantResource>>> convertDataRowsToEuGrantResources(List<Map<CsvHeader, String>> dataRows) {

        List<ServiceResult<EuGrantResource>> convertedData = simpleMap(dataRows, this::convertDataRowToEuGrantResource);
        return serviceSuccess(convertedData);
    }

    private ServiceResult<EuGrantResource> convertDataRowToEuGrantResource(Map<CsvHeader, String> dataRow) {

        return createContact(dataRow).andOnSuccess(contact ->
               createFunding(dataRow).andOnSuccess(funding ->
               createOrganisation(dataRow).andOnSuccess(organisation ->
               createGrant(contact, funding, organisation))));
    }

    private ServiceResult<EuGrantResource> createGrant(EuContactResource contact, EuFundingResource funding, EuOrganisationResource organisation) {

        EuGrantResource grant = new EuGrantResource();
        grant.setContact(contact);
        grant.setFunding(funding);
        grant.setOrganisation(organisation);
        return serviceSuccess(grant);
    }

    private ServiceResult<EuOrganisationResource> createOrganisation(Map<CsvHeader, String> dataRow) {

        EuOrganisationResource organisation = new EuOrganisationResource();
        organisation.setName(dataRow.get(CsvHeader.ORGANISATION_NAME));

        String organisationTypeLabel = dataRow.get(CsvHeader.ORGANISATION_TYPE);
        Optional<EuOrganisationType> matchingOrganisationType = findOrganisationType(organisationTypeLabel);

        return matchingOrganisationType.map(organisationType -> {

            organisation.setOrganisationType(organisationType);
            organisation.setCompaniesHouseNumber(dataRow.get(CsvHeader.COMPANIES_HOUSE_REGISTRATION_NUMBER));
            return serviceSuccess(organisation);

        }).orElseGet(() -> serviceFailure(notFoundError(EuOrganisationType.class, organisationTypeLabel)));
    }

    private Optional<EuOrganisationType> findOrganisationType(String organisationTypeLabel) {

        return simpleFindFirst(EuOrganisationType.values(),
                type -> type.getDisplayName().equalsIgnoreCase(organisationTypeLabel));
    }

    private ServiceResult<EuFundingResource> createFunding(Map<CsvHeader, String> dataRow) {

        return getBigDecimal(dataRow.get(CsvHeader.PROJECT_EU_FUNDING_CONTRIBUTION)).andOnSuccess(fundingContribution ->
               getActionType(dataRow).andOnSuccess(actionType ->
               getDate(dataRow.get(CsvHeader.PROJECT_START_DATE)).andOnSuccess(projectStartDate ->
               getDate(dataRow.get(CsvHeader.PROJECT_END_DATE)).andOnSuccessReturn(projectEndDate ->
               createFunding(dataRow, fundingContribution, actionType, projectStartDate, projectEndDate)))));
    }

    private EuFundingResource createFunding(
            Map<CsvHeader, String> dataRow,
            BigDecimal fundingContribution,
            EuActionTypeResource actionType,
            LocalDate projectStartDate,
            LocalDate projectEndDate) {

        EuFundingResource funding = new EuFundingResource();
        funding.setActionType(actionType);
        funding.setFundingContribution(fundingContribution);
        funding.setGrantAgreementNumber(dataRow.get(CsvHeader.GRANT_AGREEMENT_NUMBER));
        funding.setProjectCoordinator("COORDINATOR".equalsIgnoreCase(dataRow.get(CsvHeader.PROJECT_COORDINATOR)));
        funding.setParticipantId(dataRow.get(CsvHeader.PIC));
        funding.setProjectName(dataRow.get(CsvHeader.PROJECT_NAME));
        funding.setProjectStartDate(projectStartDate);
        funding.setProjectEndDate(projectEndDate);
        return funding;
    }

    private ServiceResult<BigDecimal> getBigDecimal(String string) {
        try {
            return serviceSuccess(new BigDecimal(string));
        } catch (NumberFormatException e) {
            return serviceFailure(new Error("Failed to convert string " + string + " to number", BAD_REQUEST));
        }
    }

    private ServiceResult<LocalDate> getDate(String string) {
        try {
            return serviceSuccess(LocalDate.from(DATE_FORMAT.parse(string)));
        } catch (DateTimeException e) {
            return serviceFailure(new Error("Unable to convert string " + string + " into date", BAD_REQUEST));
        }
    }

    private ServiceResult<EuActionTypeResource> getActionType(Map<CsvHeader, String> dataRow) {

        String actionTypeString = dataRow.get(CsvHeader.ACTION_TYPE);

        Supplier<ServiceResult<EuActionTypeResource>> failureToFindActionType = () ->
                serviceFailure(notFoundError(EuActionType.class, actionTypeString));

        Pattern actionTypeNamePattern = Pattern.compile("^\\(([a-zA-Z0-9-]+)\\).*$");

        Matcher matcher = actionTypeNamePattern.matcher(actionTypeString);

        if (!matcher.find()) {
            return failureToFindActionType.get();
        }

        String actionTypeName = matcher.group(1);

        Optional<EuActionType> matchingActionType = actionTypeRepository.findOneByName(actionTypeName);

        return matchingActionType.map(type -> serviceSuccess(actionTypeMapper.mapToResource(type))).
               orElseGet(failureToFindActionType);
    }

    private ServiceResult<EuContactResource> createContact(Map<CsvHeader, String> dataRow) {

        EuContactResource contact = new EuContactResource();
        contact.setEmail(dataRow.get(CsvHeader.CONTACT_EMAIL_ADDRESS));
        contact.setJobTitle(dataRow.get(CsvHeader.CONTACT_JOB_TITLE));
        contact.setName(dataRow.get(CsvHeader.CONTACT_FULL_NAME));
        contact.setTelephone(dataRow.get(CsvHeader.CONTACT_TELEPHONE_NUMBER));

        return serviceSuccess(contact);
    }
}
