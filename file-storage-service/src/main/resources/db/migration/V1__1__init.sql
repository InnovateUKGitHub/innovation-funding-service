CREATE TABLE `file_storage_record` (
  `file_uuid` varchar(255) COLLATE utf8_bin NOT NULL,
  `file_name` varchar(255) COLLATE utf8_bin NOT NULL,
  `file_size_bytes` bigint(20) NOT NULL,
  `md5checksum` varchar(255) COLLATE utf8_bin NOT NULL,
  `mime_type` varchar(255) COLLATE utf8_bin NOT NULL,
  `storage_location` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `storage_provider` varchar(255) COLLATE utf8_bin NOT NULL,
  `system_id` varchar(255) COLLATE utf8_bin NOT NULL,
  `user_id` varchar(255) COLLATE utf8_bin NOT NULL,
  `virus_scan_message` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `virus_scan_status` int(11) DEFAULT NULL,
  PRIMARY KEY (`file_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;