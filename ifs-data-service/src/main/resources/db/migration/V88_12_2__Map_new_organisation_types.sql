/* Organisation updates */
UPDATE organisation o,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research and technology organisations (RTO\'s)') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Public sector organisation or charity') newot
SET o.organisation_type_id=newot.id
WHERE o.organisation_type_id=oldot.id;

UPDATE organisation o,
(SELECT `id` FROM `organisation_type` WHERE `name`='University (HEI)') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research') newot
SET o.organisation_type_id=newot.id
WHERE o.organisation_type_id=oldot.id;

UPDATE organisation o,
(SELECT `id` FROM `organisation_type` WHERE `name`='Non profit distributing Research & Technology Organisation (RTO)') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research and technology organisations (RTO\'s)') newot
SET o.organisation_type_id=newot.id
WHERE o.organisation_type_id=oldot.id;

UPDATE organisation o,
(SELECT `id` FROM `organisation_type` WHERE `name`='Catapult') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research and technology organisations (RTO\'s)') newot
SET o.organisation_type_id=newot.id
WHERE o.organisation_type_id=oldot.id;

UPDATE organisation o,
(SELECT `id` FROM `organisation_type` WHERE `name`='Public sector research establishment') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Public sector organisation or charity') newot
SET o.organisation_type_id=newot.id
WHERE o.organisation_type_id=oldot.id;

UPDATE grant_claim_maximum o,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research council institute') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research') newot
SET o.organisation_type_id=newot.id
WHERE o.organisation_type_id=oldot.id;

/* Now the grantclaim update */
UPDATE grant_claim_maximum g,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research and technology organisations (RTO\'s)') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Public sector organisation or charity') newot
SET g.organisation_type_id=newot.id
WHERE g.organisation_type_id=oldot.id;

UPDATE grant_claim_maximum g,
(SELECT `id` FROM `organisation_type` WHERE `name`='University (HEI)') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research') newot
SET g.organisation_type_id=newot.id
WHERE g.organisation_type_id=oldot.id;

UPDATE grant_claim_maximum g,
(SELECT `id` FROM `organisation_type` WHERE `name`='Non profit distributing Research & Technology Organisation (RTO)') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research and technology organisations (RTO\'s)') newot
SET g.organisation_type_id=newot.id
WHERE g.organisation_type_id=oldot.id;

UPDATE grant_claim_maximum g,
(SELECT `id` FROM `organisation_type` WHERE `name`='Catapult') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research and technology organisations (RTO\'s)') newot
SET g.organisation_type_id=newot.id
WHERE g.organisation_type_id=oldot.id;

UPDATE grant_claim_maximum g,
(SELECT `id` FROM `organisation_type` WHERE `name`='Public sector research establishment') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Public sector organisation or charity') newot
SET g.organisation_type_id=newot.id
WHERE g.organisation_type_id=oldot.id;

UPDATE grant_claim_maximum g,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research council institute') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research') newot
SET g.organisation_type_id=newot.id
WHERE g.organisation_type_id=oldot.id;