-- IFS-7197 update business organisation description
UPDATE `organisation_type` SET `description` = 'A person or organisation that provides goods or services in exchange for something of value, usually money.' WHERE (`id` = '1');
UPDATE `organisation_type` SET `description`='A not-for-profit organisation focusing on innovation.' WHERE `id`='4';
