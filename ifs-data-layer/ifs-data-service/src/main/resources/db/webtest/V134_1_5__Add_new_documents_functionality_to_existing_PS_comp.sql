-- IFS 4629 Add new documents functionality to an existing webtest competition to aid with acceptance tests

INSERT INTO `document_config`
(`competition_id`, `title`, `guidance`, `editable`, `enabled`, `type`)
VALUES
(
(SELECT `id` FROM `competition` WHERE `name` = 'Integrated delivery programme - solar vehicles'),
'Exploitation plan',
'Enter guidance for Exploitation plan',
false,
true,
'ProjectDocument');

INSERT INTO `document_config`
(`competition_id`, `title`, `guidance`, `editable`, `enabled`, `type`)
VALUES
(
(SELECT `id` FROM `competition` WHERE `name` = 'Integrated delivery programme - solar vehicles'),
'Collaboration agreement',
'Enter guidance for Collaboration agreement',
false,
true,
'ProjectDocument');