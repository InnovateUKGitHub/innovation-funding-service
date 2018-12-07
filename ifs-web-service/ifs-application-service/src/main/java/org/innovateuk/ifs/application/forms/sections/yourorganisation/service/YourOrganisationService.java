package org.innovateuk.ifs.application.forms.sections.yourorganisation.service;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.GrowthTableRow;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import java.time.LocalDate;
import java.util.List;

/**
 * TODO DW - document this class
 */
public interface YourOrganisationService {

    ServiceResult<Long> getTurnover(long applicationId, long competitionId, long organisationId);

    ServiceResult<Long> getHeadCount(long applicationId, long competitionId, long organisationId);

    ServiceResult<Boolean> getStateAidAgreed(long applicationId);

    ServiceResult<OrganisationSize> getOrganisationSize(long applicationId, long organisationId);

    ServiceResult<Void> updateTurnover(long applicationId, long competitionId, long userId, Long value);

    ServiceResult<Void> updateHeadCount(long applicationId, long competitionId, long userId, Long value);

    ServiceResult<Void> updateStateAidAgreed(long applicationId, boolean stateAidAgreed);

    ServiceResult<Void> updateOrganisationSize(long applicationId, long organisationId, OrganisationSize organisationSize);

    ServiceResult<Boolean> isShowStateAidAgreement(long applicationId, long organisationId);

    ServiceResult<Boolean> isIncludingGrowthTable(long competitionId);

    ServiceResult<LocalDate> getFinancialYearEnd(long applicationId, long competitionId, long organisationId);

    ServiceResult<List<GrowthTableRow>> getGrowthTableRows(long applicationId, long competitionId, long organisationId);

    ServiceResult<Long> getHeadCountAtLastFinancialYear(long applicationId, long competitionId, long organisationId);

    ServiceResult<Void> updateFinancialYearEnd(long applicationId, long competitionId, long userId, LocalDate financialYearEnd);
}
