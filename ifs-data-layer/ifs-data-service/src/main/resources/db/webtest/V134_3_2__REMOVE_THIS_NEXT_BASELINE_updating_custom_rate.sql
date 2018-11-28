-- CUSTOM_RATE is no longer supported. It only existed for the beta competition 1.
-- The generator has been updated so next time the webtest is baselined this patch can be removed.
update finance_row set item = 'TOTAL' where item = 'CUSTOM_RATE';