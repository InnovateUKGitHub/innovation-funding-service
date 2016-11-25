package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.assessment.resource.ApplicationRejectionResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class ApplicationRejectionResourceBuilder extends BaseBuilder<ApplicationRejectionResource, ApplicationRejectionResourceBuilder> {

    private ApplicationRejectionResourceBuilder(List<BiConsumer<Integer, ApplicationRejectionResource>> multiActions) {
        super(multiActions);
    }

    public static ApplicationRejectionResourceBuilder newApplicationRejectionResource() {
        return new ApplicationRejectionResourceBuilder(emptyList());
    }

    @Override
    protected ApplicationRejectionResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationRejectionResource>> actions) {
        return new ApplicationRejectionResourceBuilder(actions);
    }

    @Override
    protected ApplicationRejectionResource createInitial() {
        return new ApplicationRejectionResource();
    }

    public ApplicationRejectionResourceBuilder withRejectReason(String... rejectReasons) {
        return withArray((rejectReason, applicationRejectionResource) -> setField("rejectReason", rejectReason, applicationRejectionResource), rejectReasons);
    }

    public ApplicationRejectionResourceBuilder withRejectComment(String... rejectComments) {
        return withArray((rejectComment, applicationRejectionResource) -> setField("rejectComment", rejectComment, applicationRejectionResource), rejectComments);
    }
}