-- Create backing states for eoi evidence upload, to be used for state machine project processes: IFS-12517
INSERT INTO `activity_state` (activity_type, state) VALUES ('APPLICATION_EOI_EVIDENCE_UPLOAD', 'NOT_SUBMITTED');
INSERT INTO `activity_state` (activity_type, state) VALUES ('APPLICATION_EOI_EVIDENCE_UPLOAD', 'SUBMITTED');