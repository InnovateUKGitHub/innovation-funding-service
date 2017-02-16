ALTER TABLE competition
ADD COLUMN `non_ifs` bit(1) NOT NULL DEFAULT 0,
ADD COLUMN `non_ifs_url` varchar(255) NULL
