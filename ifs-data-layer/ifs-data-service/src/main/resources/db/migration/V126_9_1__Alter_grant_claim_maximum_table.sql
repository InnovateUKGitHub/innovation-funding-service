-- IFS-3622 change the unique constraint on the grant_claim_maximum table
ALTER TABLE grant_claim_maximum DROP FOREIGN KEY grant_claim_maximum_category_fk;
ALTER TABLE grant_claim_maximum DROP FOREIGN KEY grant_claim_maximum_competition_type_fk;
ALTER TABLE grant_claim_maximum DROP FOREIGN KEY grant_claim_maximum_organisation_size_fk;
ALTER TABLE grant_claim_maximum DROP FOREIGN KEY grant_claim_maximum_organisation_type_fk;

DROP INDEX unique_grant_claim_maximum ON grant_claim_maximum;
-- CREATE UNIQUE INDEX unique_grant_claim_maximum ON grant_claim_maximum (`category_id`,`organisation_size_id`,`organisation_type_id`,`competition_type_id`, `maximum`);

ALTER TABLE grant_claim_maximum ADD CONSTRAINT `grant_claim_maximum_category_fk` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`);
ALTER TABLE grant_claim_maximum ADD CONSTRAINT `grant_claim_maximum_competition_type_fk` FOREIGN KEY (`competition_type_id`) REFERENCES `competition_type` (`id`);
ALTER TABLE grant_claim_maximum ADD CONSTRAINT `grant_claim_maximum_organisation_size_fk` FOREIGN KEY (`organisation_size_id`) REFERENCES `organisation_size` (`id`);
ALTER TABLE grant_claim_maximum ADD CONSTRAINT `grant_claim_maximum_organisation_type_fk` FOREIGN KEY (`organisation_type_id`) REFERENCES `organisation_type` (`id`);
