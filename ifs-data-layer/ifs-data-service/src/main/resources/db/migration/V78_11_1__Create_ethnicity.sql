CREATE TABLE ethnicity (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL UNIQUE,
  description VARCHAR(255) NOT NULL,
  priority INT NOT NULL UNIQUE,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id)
);

INSERT INTO ethnicity (id,  name,             description,                    priority)
VALUES                (1,   'WHITE',          'White',                        1),
                      (2,   'MIXED',          'Mixed/Multiple ethnic groups', 2),
                      (3,   'ASIAN',          'Asian/Asian British',          3),
                      (4,   'BLACK',          'Black/African/Caribbean',      4),
                      (5,   'BLACK_BRITISH',  'Black British',                5),
                      (6,   'OTHER',          'Other ethnic group',           6),
                      (7,   'NOT_STATED',     'Prefer not to say',            7);
