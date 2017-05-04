package org.innovateuk.ifs.application.viewmodel.finance;


import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

public abstract class AbstractCostViewModel extends AbstractFormInputViewModel {

    private String viewmode;
    private ApplicantSectionResource applicantSection;
    private FinanceRowCostCategory costCategory;

    public abstract FinanceRowType rowType();

    public SectionResource getSection() {
        return applicantSection.getSection();
    }
}
