CREATE TABLE `spend_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cost_category_type_id` bigint(20) NOT NULL,
  `eligible_costs_cost_group_id` bigint(20) NOT NULL,
  `organisation_id` bigint(20) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `spend_profile_figures_cost_group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_260indgab3foqj3wd1br5ppsx` (`cost_category_type_id`),
  KEY `FK_gwv7qqsmomu0vf3aihpchw4ya` (`eligible_costs_cost_group_id`),
  KEY `FK_t09amptdpq28to3ndm4sbj0pr` (`organisation_id`),
  KEY `FK_dgtosuo14i9xovfh7ja16io9l` (`project_id`),
  KEY `FK_tp4phg304cqs0f8sfpjqt5cvh` (`spend_profile_figures_cost_group_id`),
  CONSTRAINT `FK_260indgab3foqj3wd1br5ppsx` FOREIGN KEY (`cost_category_type_id`) REFERENCES `cost_category_type` (`id`),
  CONSTRAINT `FK_dgtosuo14i9xovfh7ja16io9l` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_gwv7qqsmomu0vf3aihpchw4ya` FOREIGN KEY (`eligible_costs_cost_group_id`) REFERENCES `cost_group` (`id`),
  CONSTRAINT `FK_t09amptdpq28to3ndm4sbj0pr` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `FK_tp4phg304cqs0f8sfpjqt5cvh` FOREIGN KEY (`spend_profile_figures_cost_group_id`) REFERENCES `cost_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;