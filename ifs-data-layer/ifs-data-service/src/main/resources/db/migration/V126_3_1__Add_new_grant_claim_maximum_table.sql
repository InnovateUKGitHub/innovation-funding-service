-- IFS-3818 Add the new Grant Claim Maximum table
CREATE TABLE grant_claim_maximum_new
(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
	`competition_id` bigint(20) NOT NULL,
	`category_id` bigint(20) NOT NULL,
	`organisation_type_id` bigint(20) NOT NULL,
	`def` tinyint(1) DEFAULT NULL,
	`small` tinyint(1) DEFAULT NULL,
	`medium` tinyint(1) DEFAULT NULL,
	`large` tinyint(1) DEFAULT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT unique_grant_claim_maximum_new UNIQUE (competition_id, category_id, organisation_type_id),
	CONSTRAINT grant_claim_maximum_new_competition_fk FOREIGN KEY (competition_id) REFERENCES competition (id),
	CONSTRAINT grant_claim_maximum_new_category_fk FOREIGN KEY (category_id) REFERENCES category (id),
	CONSTRAINT grant_claim_maximum_new_organisation_type_fk FOREIGN KEY (organisation_type_id) REFERENCES
	organisation_type(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET @org_size_small_id = (SELECT id
                 FROM organisation_size
                 WHERE description = 'Micro or small');

SET @org_size_med_id = (SELECT id
                 FROM organisation_size
                 WHERE description = 'Medium');

SET @org_size_large_id = (SELECT id
                 FROM organisation_size
                 WHERE description = 'Large');

-- Insert grant claim maximum values for all of the template competitions
INSERT INTO grant_claim_maximum_new (competition_id, category_id, organisation_type_id, def, small, medium, large)
SELECT  ct.template_competition_id, gcm.category_id, gcm.organisation_type_id,
  MAX(CASE WHEN organisation_size_id IS NULL THEN maximum ELSE NULL END) AS def,
  MAX(CASE WHEN organisation_size_id = @org_size_small_id THEN maximum ELSE NULL END) AS small,
  MAX(CASE WHEN organisation_size_id = @org_size_med_id THEN maximum ELSE NULL END) AS medium,
  MAX(CASE WHEN organisation_size_id = @org_size_large_id THEN maximum ELSE NULL END) AS large
FROM
   grant_claim_maximum gcm
INNER JOIN competition_type ct ON gcm.competition_type_id = ct.id
GROUP BY ct.id, gcm.category_id, gcm.organisation_type_id;

-- Insert grant claim maximum values for all of the non-template competitions
INSERT INTO grant_claim_maximum_new (competition_id, category_id, organisation_type_id, def, small, medium, large)
SELECT  c.id , gcm.category_id, gcm.organisation_type_id,
        MAX(CASE WHEN organisation_size_id IS NULL THEN maximum ELSE NULL END) AS def,
        MAX(CASE WHEN organisation_size_id = @org_size_small_id THEN maximum ELSE NULL END) AS small,
        MAX(CASE WHEN organisation_size_id = @org_size_med_id THEN maximum ELSE NULL END) AS medium,
        MAX(CASE WHEN organisation_size_id = @org_size_large_id THEN maximum ELSE NULL END) AS large
FROM
competition c
INNER JOIN grant_claim_maximum gcm ON c.competition_type_id = gcm.competition_type_id
GROUP BY c.id, gcm.category_id, gcm.organisation_type_id;