package com.worth.ifs.competitiontemplate.domain;

import com.worth.ifs.competition.domain.CompetitionType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Template for creating new competitions.
 */
@Entity
public class CompetitionTemplate {

    @Id
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="competitionTypeId", referencedColumnName="id")
    private CompetitionType competitionType;
    
    @OneToMany(mappedBy="competitionTemplate")
    @OrderBy("priority ASC")
    private List<SectionTemplate> sectionTemplates = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CompetitionType getCompetitionType() {
		return competitionType;
	}

	public void setCompetitionType(CompetitionType competitionType) {
		this.competitionType = competitionType;
	}

	public List<SectionTemplate> getSectionTemplates() {
		return sectionTemplates;
	}

	public void setSectionTemplates(List<SectionTemplate> sectionTemplates) {
		this.sectionTemplates = sectionTemplates;
	}
    
}
