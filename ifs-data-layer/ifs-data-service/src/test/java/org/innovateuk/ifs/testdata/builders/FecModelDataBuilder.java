package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.testdata.builders.data.FecModelData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Generates FEC model for an Organisation on an Application
 */
public class FecModelDataBuilder extends BaseDataBuilder<FecModelData, FecModelDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(FecModelDataBuilder.class);

    public FecModelDataBuilder withApplicationFinance(ApplicationFinanceResource applicationFinance) {
        return with(data -> data.setApplicationFinance(applicationFinance));
    }

    public FecModelDataBuilder withCompetition(CompetitionResource competitionResource) {
        return with(data -> data.setCompetition(competitionResource));
    }

    public FecModelDataBuilder withUploadedFecFile() {
        return with(data -> {
            FileEntry fileEntry = fileEntryRepository.save(
                    new FileEntry(null, "fec-file" + data.getApplicationFinance().getId() + ".pdf", "application/pdf", 7945));

            ApplicationFinance finance = applicationFinanceRepository.findById(data.getApplicationFinance().getId()).get();
            //finance.setFecFileEntry(fileEntry);
            applicationFinanceRepository.save(finance);
        });
    }

    public FecModelDataBuilder withEnabled(Boolean enabled) {
        return with(data -> {
            ApplicationFinanceResource applicationFinance =
                    financeService.getApplicationFinanceById(data.getApplicationFinance().getId()).
                            getSuccess();

            //applicationFinance.setFecModelEnabled(enabled);

            financeService.updateApplicationFinance(applicationFinance.getId(), applicationFinance);
        });
    }

    public static FecModelDataBuilder newFecModel(ServiceLocator serviceLocator) {
        return new FecModelDataBuilder(emptyList(), serviceLocator);
    }

    private FecModelDataBuilder(List<BiConsumer<Integer, FecModelData>> multiActions, ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected FecModelDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FecModelData>> actions) {
        return new FecModelDataBuilder(actions, serviceLocator);
    }

    @Override
    protected FecModelData createInitial() {
        return new FecModelData();
    }

    @Override
    protected void postProcess(int index, FecModelData instance) {
        super.postProcess(index, instance);
        OrganisationResource organisation = organisationService.findById(instance.getApplicationFinance().getOrganisation()).getSuccess();
        ApplicationResource application = applicationService.getApplicationById(instance.getApplicationFinance().getApplication()).getSuccess();
        LOG.info("Created FEC model for Application '{}', Organisation '{}'", application.getName(), organisation.getName());
    }
}
