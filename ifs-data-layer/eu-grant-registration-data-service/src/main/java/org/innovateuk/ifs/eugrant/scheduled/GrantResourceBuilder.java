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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.scheduled.CsvHeader.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * A component to create an EuGrantResource given a row of data from the EU Grant csv
 */
@Component
public class GrantResourceBuilder {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern ACTION_TYPE_NAME_PATTERN = Pattern.compile("^\\((.+)\\).*$");

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
        organisation.setName(dataRow.get(ORGANISATION_NAME));

        String organisationTypeLabel = dataRow.get(ORGANISATION_TYPE);
        Optional<EuOrganisationType> matchingOrganisationType = findOrganisationType(organisationTypeLabel);

        return matchingOrganisationType.map(organisationType -> {

            organisation.setOrganisationType(organisationType);
            organisation.setCompaniesHouseNumber(dataRow.get(COMPANIES_HOUSE_REGISTRATION_NUMBER));
            return serviceSuccess(organisation);

        }).orElseGet(() -> serviceFailure(notFoundError(EuOrganisationType.class, organisationTypeLabel)));
    }

    private Optional<EuOrganisationType> findOrganisationType(String organisationTypeLabel) {

        return simpleFindFirst(EuOrganisationType.values(),
                type -> type.getDisplayName().equalsIgnoreCase(organisationTypeLabel));
    }

    private ServiceResult<EuFundingResource> createFunding(Map<CsvHeader, String> dataRow) {

        return getBigDecimal(dataRow.get(PROJECT_EU_FUNDING_CONTRIBUTION)).andOnSuccess(fundingContribution ->
               getActionType(dataRow).andOnSuccess(actionType ->
               getDate(dataRow.get(PROJECT_START_DATE)).andOnSuccess(projectStartDate ->
               getDate(dataRow.get(PROJECT_END_DATE)).andOnSuccessReturn(projectEndDate ->
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
        funding.setGrantAgreementNumber(dataRow.get(GRANT_AGREEMENT_NUMBER));
        funding.setProjectCoordinator("COORDINATOR".equalsIgnoreCase(dataRow.get(PROJECT_COORDINATOR)));
        funding.setParticipantId(dataRow.get(PIC));
        funding.setProjectName(dataRow.get(PROJECT_NAME));
        funding.setProjectStartDate(projectStartDate);
        funding.setProjectEndDate(projectEndDate);
        return funding;
    }

    private ServiceResult<BigDecimal> getBigDecimal(String string) {
        try {
            return serviceSuccess(new BigDecimal(string));
        } catch (NumberFormatException e) {
            return serviceFailure(new Error("Failed to convert string \"" + string + "\" to number", BAD_REQUEST));
        }
    }

    private ServiceResult<LocalDate> getDate(String string) {
        try {
            return serviceSuccess(LocalDate.from(DATE_FORMAT.parse(string)));
        } catch (DateTimeException e) {
            return serviceFailure(new Error("Failed to convert string \"" + string + "\" to date", BAD_REQUEST));
        }
    }

    private ServiceResult<EuActionTypeResource> getActionType(Map<CsvHeader, String> dataRow) {

        String actionTypeString = dataRow.get(ACTION_TYPE);

        Matcher matcher = ACTION_TYPE_NAME_PATTERN.matcher(actionTypeString);

        if (!matcher.find()) {
            Error extractionError = new Error("Unable to extract action type name from string \"" +
                    actionTypeString + "\"", BAD_REQUEST);
            return serviceFailure(extractionError);
        }

        String actionTypeName = matcher.group(1);

        Optional<EuActionType> matchingActionType = actionTypeRepository.findOneByName(actionTypeName);

        return matchingActionType.map(type -> serviceSuccess(actionTypeMapper.mapToResource(type))).
               orElseGet(() -> serviceFailure(new Error("Unable to find an Action Type " +
                       "with name \"" + actionTypeName+ "\"", BAD_REQUEST)));
    }

    private ServiceResult<EuContactResource> createContact(Map<CsvHeader, String> dataRow) {

        EuContactResource contact = new EuContactResource();
        contact.setEmail(dataRow.get(CONTACT_EMAIL_ADDRESS));
        contact.setJobTitle(dataRow.get(CONTACT_JOB_TITLE));
        contact.setName(dataRow.get(CONTACT_FULL_NAME));
        contact.setTelephone(dataRow.get(CONTACT_TELEPHONE_NUMBER));

        return serviceSuccess(contact);
    }
}
