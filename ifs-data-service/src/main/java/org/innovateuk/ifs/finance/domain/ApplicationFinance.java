package org.innovateuk.ifs.finance.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.category.domain.ApplicationFinanceResearchCategoryLink;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.user.domain.Organisation;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ApplicationFinance defines database relations and a model to use client side and server side.
 */
@Entity
public class ApplicationFinance extends Finance {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="financeFileEntryId", referencedColumnName="id")
    private FileEntry financeFileEntry;

    @OneToMany(mappedBy = "applicationFinance", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ApplicationFinanceResearchCategoryLink> researchCategories = new HashSet<>();

    public ApplicationFinance() {
    	// no-arg constructor
    }

    public ApplicationFinance(Application application, Organisation organisation) {
        super(organisation);
        this.application = application;
    }

    public ApplicationFinance(long id, Application application, Organisation organisation) {
        super(id, organisation);
        this.application = application;
    }

    @JsonIgnore
    public Application getApplication() {
        return application;
    }

    public FileEntry getFinanceFileEntry() {
        return financeFileEntry;
    }

    public void setFinanceFileEntry(FileEntry financeFileEntry) {
        this.financeFileEntry = financeFileEntry;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Set<ResearchCategory> getResearchCategories() {
        return researchCategories.stream().map(ApplicationFinanceResearchCategoryLink::getCategory).collect(Collectors.toSet());
    }

    public void addResearchCategory(ResearchCategory researchCategory) {
        researchCategories.clear();
        researchCategories.add(new ApplicationFinanceResearchCategoryLink(this, researchCategory));
    }
}
