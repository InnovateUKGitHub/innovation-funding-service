package org.innovateuk.ifs.applicant.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.function.BiConsumer;

abstract class AbstractApplicantResourceBuilder<R extends AbstractApplicantResource, B> extends BaseBuilder<R, B> {

    AbstractApplicantResourceBuilder(List<BiConsumer<Integer, R>> newActions) {
        super(newActions);
    }

    public B withApplication(ApplicationResource... application) {
        return withArraySetFieldByReflection("application", application);
    }

    public B withCompetition(CompetitionResource... competition) {
        return withArraySetFieldByReflection("competition", competition);
    }

    public B withCurrentApplicant(ApplicantResource... currentApplicant) {
        return withArraySetFieldByReflection("currentApplicant", currentApplicant);
    }

    public B withCurrentUser(UserResource... currentUser) {
        return withArraySetFieldByReflection("currentUser", currentUser);
    }

    public B withApplicants(List<ApplicantResource>... applicants) {
        return withArraySetFieldByReflection("applicants", applicants);
    }
}
