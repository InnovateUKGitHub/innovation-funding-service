CREATE TABLE `gluster_migration_status` (
  `id` bigint(20) NOT NULL,
  `file_entry_id` bigint(20) NOT NULL,
  `status` varchar(255) COLLATE utf8_bin NOT NULL,
  `error_message` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;