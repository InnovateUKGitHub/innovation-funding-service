package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

public class MonitoringDashboardSectionsViewModel {

    private MonitoringOfficerDashboardDocumentSectionViewModel  documentSectionViewModel;
    private MonitoringOfficerDashboardSpendProfileSectionViewModel spendProfileSectionViewModel;

    public MonitoringDashboardSectionsViewModel(MonitoringOfficerDashboardDocumentSectionViewModel documentSectionViewModel,
                                                MonitoringOfficerDashboardSpendProfileSectionViewModel spendProfileSectionViewModel) {
        this.documentSectionViewModel = documentSectionViewModel;
        this.spendProfileSectionViewModel = spendProfileSectionViewModel;
    }

    public MonitoringOfficerDashboardDocumentSectionViewModel getDocumentSectionViewModel() {
        return documentSectionViewModel;
    }

    public MonitoringOfficerDashboardSpendProfileSectionViewModel getSpendProfileSectionViewModel() {
        return spendProfileSectionViewModel;
    }
}
