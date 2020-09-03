ALTER table knowledge_base
    ADD COLUMN type enum(
    'RTO',
    'UNIVERSITY',
    'CATAPULT'
    );

ALTER table knowledge_base
    ADD COLUMN registration_number VARCHAR(10);

ALTER table knowledge_base
    ADD COLUMN address_id BIGINT(20);
