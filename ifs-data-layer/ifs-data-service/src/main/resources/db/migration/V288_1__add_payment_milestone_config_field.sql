-- Adding new column for requirement to allow configurable finances for payment milestones.
-- If flag is set then the payment milestones question will be enabled on the application form.
ALTER TABLE competition
    ADD include_payment_milestone BIT(1) NOT NULL DEFAULT FALSE;

-- Setting false for all production competitions.
UPDATE competition
SET include_payment_milestone = FALSE;