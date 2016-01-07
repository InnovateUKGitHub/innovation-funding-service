package com.worth.ifs.finance.resource;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.Organisation;

public class ApplicationFinanceResource {
        Long id;
        private Long organisation;
        private Long application;

        public ApplicationFinanceResource() {
        }

        public ApplicationFinanceResource(Application application, Organisation organisation) {
            this.application = application.getId();
            this.organisation = organisation.getId();
        }

        public ApplicationFinanceResource(long id, Application application, Organisation organisation) {
            this.id = id;
            this.application = application.getId();
            this.organisation = organisation.getId();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getOrganisation() {
            return organisation;
        }

        public Long getApplication() {
            return application;
        }
}
