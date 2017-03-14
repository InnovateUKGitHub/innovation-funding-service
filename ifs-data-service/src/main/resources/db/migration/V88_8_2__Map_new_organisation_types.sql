/* Old 3 (Public sector) to 4 (Public sector or org) */
UPDATE organisation o,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research and technology organisations (RTO\'s)') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Public sector organisation or charity') newot
SET o.organisation_type_id=newot.id
WHERE o.organisation_type_id=oldot.id;

/* Old 5 to 2 */
UPDATE organisation o,
(SELECT `id` FROM `organisation_type` WHERE `name`='University (HEI)') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research') newot
SET o.organisation_type_id=newot.id
WHERE o.organisation_type_id=oldot.id;

/* Old 6 to 3 */
UPDATE organisation o,
(SELECT `id` FROM `organisation_type` WHERE `name`='Non profit distributing Research & Technology Organisation (RTO)') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research and technology organisations (RTO\'s)') newot
SET o.organisation_type_id=newot.id
WHERE o.organisation_type_id=oldot.id;

/* Old 7 to 3 */
UPDATE organisation o,
(SELECT `id` FROM `organisation_type` WHERE `name`='Catapult') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research and technology organisations (RTO\'s)') newot
SET o.organisation_type_id=newot.id
WHERE o.organisation_type_id=oldot.id;

/* Old 8 to 4 */
UPDATE organisation o,
(SELECT `id` FROM `organisation_type` WHERE `name`='Public sector research establishment') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Public sector organisation or charity') newot
SET o.organisation_type_id=newot.id
WHERE o.organisation_type_id=oldot.id;

/* Old 9 to 2 */
UPDATE organisation o,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research council institute') oldot,
(SELECT `id` FROM `organisation_type` WHERE `name`='Research') newot
SET o.organisation_type_id=newot.id
WHERE o.organisation_type_id=oldot.id;