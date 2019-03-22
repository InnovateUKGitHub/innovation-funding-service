-- IFS-5312 Update eu action types

-- Update existing priorities
UPDATE `eu_action_type` SET `priority` = 33 WHERE `name` = 'SME-2';
UPDATE `eu_action_type` SET `priority` = 32 WHERE `name` = 'SME-1';
UPDATE `eu_action_type` SET `priority` = 29 WHERE `name` = 'RPr';
UPDATE `eu_action_type` SET `priority` = 28 WHERE `name` = 'RIA';
UPDATE `eu_action_type` SET `priority` = 27 WHERE `name` = 'RFCS';
UPDATE `eu_action_type` SET `priority` = 26 WHERE `name` = 'PPI';
UPDATE `eu_action_type` SET `priority` = 25 WHERE `name` = 'PCP';
UPDATE `eu_action_type` SET `priority` = 24 WHERE `name` = 'MSCA-RISE';
UPDATE `eu_action_type` SET `priority` = 23 WHERE `name` = 'MSCA-ITN';
UPDATE `eu_action_type` SET `priority` = 22 WHERE `name` = 'MSCA-IF';
UPDATE `eu_action_type` SET `priority` = 21 WHERE `name` = 'MSCA-Cofund';
UPDATE `eu_action_type` SET `priority` = 19 WHERE `name` = 'KICS';
UPDATE `eu_action_type` SET `priority` = 16 WHERE `name` = 'IPr';
UPDATE `eu_action_type` SET `priority` = 15 WHERE `name` = 'IA';
UPDATE `eu_action_type` SET `priority` = 14 WHERE `name` = 'FTI';
UPDATE `eu_action_type` SET `priority` = 13 WHERE `name` = 'FPA';
UPDATE `eu_action_type` SET `priority` = 12 WHERE `name` = 'FP7';
UPDATE `eu_action_type` SET `priority` = 11 WHERE `name` = 'ERC-SyG';
UPDATE `eu_action_type` SET `priority` = 10 WHERE `name` = 'ERC-STG';
UPDATE `eu_action_type` SET `priority` = 9 WHERE `name` = 'ERC-POC';
UPDATE `eu_action_type` SET `priority` = 8 WHERE `name` = 'ERC-COG';
UPDATE `eu_action_type` SET `priority` = 7 WHERE `name` = 'ERC-ADG';
UPDATE `eu_action_type` SET `priority` = 6 WHERE `name` = 'ERA-NET-Cofund';
UPDATE `eu_action_type` SET `priority` = 3 WHERE `name` = 'CSA';


--Update existing records
UPDATE `eu_action_type` SET `description` = 'Coordination and Support Actions' WHERE `name` = 'CSA';
UPDATE `eu_action_type` SET `description` = 'European Research Agency-NET Cofund' WHERE `name` = 'ERA-NET-Cofund';
UPDATE `eu_action_type` SET `description` = 'Fast Track to Innovation' WHERE `name` = 'FTI';
UPDATE `eu_action_type` SET `description` = 'Research and Innovation Actions' WHERE `name` = 'RIA';
UPDATE `eu_action_type` SET `description` = 'Marie Sklodowska-Curie actions Cofund' WHERE `name` = 'MSCA-Cofund';
UPDATE `eu_action_type` SET `description` = 'Marie Sklodowska-Curie actions Individual Fellowships' WHERE `name` = 'MSCA-IF';
UPDATE `eu_action_type` SET `description` = 'Marie Sklodowska-Curie actions Innovative Training Networks' WHERE `name` = 'MSCA-ITN';
UPDATE `eu_action_type` SET `description` = 'Marie Sklodowska-Curie actions Research and Innovation Staff Exchange' WHERE `name` = 'MSCA-RISE';


--Include new action types
INSERT INTO `eu_action_type` (`id`, `name`, `description`, `priority`) VALUES
(26, 'CS',    'Clean Sky',                                                   2),
(27, 'EDCTP', 'European & Developing Countries Clinical Trials Partnership', 4),
(28, 'EMPIR', 'European Metrology Programme for Innovation and Research',    5),
(29, 'ISIB',  'Innovative, Sustainable & Inclusive Bio-economy',            17),
(30, 'JTI',  'Joint Technology Initiatives',                                18),
(31, 'MSCA-Night',  'Marie Sklodowska-Curie actions',                       20),
(32, 'SFS',   'Sustainable Food Security',                                  30);
