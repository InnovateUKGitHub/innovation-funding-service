--IFS-2533 - Capture GOL Rejection Reason
ALTER TABLE project ADD COLUMN grant_offer_letter_rejection_reason VARCHAR(255) AFTER offer_submitted_date;