package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.competition.domain.Competition;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.application.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.inOrder;

public class SectionTemplatePersistorImplTest extends BaseServiceUnitTest<SectionTemplatePersistorImpl> {
    public SectionTemplatePersistorImpl supplyServiceUnderTest() {
        return new SectionTemplatePersistorImpl();
    }

    @Mock
    private EntityManager entityManagerMock;

    @Mock
    private QuestionTemplatePersistorImpl questionTemplatePersistorMock;

    @Test
    public void persistByParentEntity_noSectionsShouldReturnNull() throws Exception {
        Competition template = newCompetition().withSections(null).build();

        List<Section> sections = service.persistByParentEntity(template);

        assertNull(sections);
    }

    @Test
    public void persistByParentEntity_nestedSectionsShouldResultInCorrectOrderCalls() throws Exception {
        List<Section> childSections = newSection().build(2);
        Section parentSection = newSection().withChildSections(childSections).build();
        childSections.stream().forEach(childSection -> childSection.setParentSection(parentSection));

        Competition template = newCompetition().withSections(Arrays.asList(parentSection)).build();

        List<Section> sections = service.persistByParentEntity(template);

        InOrder inOrder = inOrder(entityManagerMock, sectionRepositoryMock, questionTemplatePersistorMock);

        Section expectedParentSection = parentSection;
        expectedParentSection.setId(null);
        expectedParentSection.setCompetition(template);
        expectedParentSection.setParentSection(null);

        Section expectedChildSection1 = childSections.get(0);
        expectedParentSection.setId(null);
        expectedParentSection.setCompetition(template);
        expectedParentSection.setParentSection(expectedParentSection);

        Section expectedChildSection2 = childSections.get(1);
        expectedParentSection.setId(null);
        expectedParentSection.setCompetition(template);
        expectedParentSection.setParentSection(expectedParentSection);

        inOrder.verify(entityManagerMock).detach(parentSection);
        inOrder.verify(sectionRepositoryMock).save(refEq(parentSection));
        inOrder.verify(questionTemplatePersistorMock).persistByParentEntity(refEq(parentSection));

        inOrder.verify(entityManagerMock).detach(childSections.get(0));
        inOrder.verify(sectionRepositoryMock).save(refEq(expectedChildSection1));
        inOrder.verify(questionTemplatePersistorMock).persistByParentEntity(refEq(expectedChildSection1));

        inOrder.verify(entityManagerMock).detach(childSections.get(1));
        inOrder.verify(sectionRepositoryMock).save(refEq(expectedChildSection2));
        inOrder.verify(questionTemplatePersistorMock).persistByParentEntity(refEq(expectedChildSection2));
    }

    @Test
    public void cleanForParentEntity_nestedSectionsShouldBeDeletedInReverseOrder() throws Exception {
        List<Section> childSections = newSection().build(2);
        Section parentSection = newSection().withChildSections(childSections).build();
        childSections.stream().forEach(childSection -> childSection.setParentSection(parentSection));

        Competition template = newCompetition().withSections(Arrays.asList(parentSection)).build();

        service.cleanForParentEntity(template);

        InOrder inOrder = inOrder(entityManagerMock, sectionRepositoryMock, questionTemplatePersistorMock);

        inOrder.verify(questionTemplatePersistorMock).cleanForParentEntity(childSections.get(0));
        inOrder.verify(entityManagerMock).detach(childSections.get(0));
        inOrder.verify(sectionRepositoryMock).delete(childSections.get(0));

        inOrder.verify(questionTemplatePersistorMock).cleanForParentEntity(childSections.get(1));
        inOrder.verify(entityManagerMock).detach(childSections.get(1));
        inOrder.verify(sectionRepositoryMock).delete(childSections.get(1));

        inOrder.verify(questionTemplatePersistorMock).cleanForParentEntity(parentSection);
        inOrder.verify(entityManagerMock).detach(parentSection);
        inOrder.verify(sectionRepositoryMock).delete(parentSection);
    }
}