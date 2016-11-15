
SET @scored=1;
SET @written_feedback=1;
SET @score_total=10;
SET @word_count=400;
SET @guidance='Your score should be based on the following:';

-- Programme Assessment Questions
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (1, 301, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (2, 302, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (3, 303, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (4, 304, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (5, 305, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (6, 306, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (7, 307, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (8, 308, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (9, 313, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (10, 314, @scored, @written_feedback, @score_total, @word_count, @guidance);

-- Sector Assessment Questions
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (11, 336, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (12, 337, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (13, 338, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (14, 339, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (15, 340, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (16, 341, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (17, 342, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (18, 343, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (19, 348, @scored, @written_feedback, @score_total, @word_count, @guidance);
INSERT INTO `question_assessment` (id, question_id, scored, written_feedback, score_total, word_count, guidance) VALUES (20, 349, @scored, @written_feedback, @score_total, @word_count, @guidance);

SET @row1_start=1;
SET @row1_end=2;
SET @row1_justification='The plan is totally unrealistic or fails to meet the objectives of the project.';

SET @row2_start=3;
SET @row2_end=4;
SET @row2_justification='The plan has serious deficiencies or major missing aspects. The plan has little chance of meeting the objectives of the project.';

SET @row3_start=5;
SET @row3_end=6;
SET @row3_justification='The plan is not completely described or there may be some deficiencies in some aspects. More work will be required before the plan can be said to be realistic.';

SET @row4_start=7;
SET @row4_end=8;
SET @row4_justification='The plan is well described and complete. There is a reasonable chance it will meet the objectives if the project.';

SET @row5_start=9;
SET @row5_end=10;
SET @row5_justification='The project is fully described with milestones and timeframes. The plan is realistic and should meet the objectives of the project.';


-- Programme Assessment Score Rows
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (1, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (1, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (1, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (1, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (1, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (2, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (2, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (2, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (2, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (2, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (3, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (3, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (3, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (3, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (3, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (4, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (4, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (4, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (4, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (4, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (5, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (5, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (5, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (5, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (5, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (6, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (6, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (6, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (6, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (6, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (7, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (7, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (7, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (7, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (7, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (8, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (8, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (8, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (8, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (8, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (9, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (9, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (9, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (9, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (9, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (10, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (10, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (10, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (10, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (10, @row5_start, @row5_end, @row5_justification);

-- Sector Assessment Score Rows
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (11, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (11, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (11, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (11, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (11, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (12, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (12, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (12, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (12, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (12, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (13, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (13, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (13, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (13, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (13, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (14, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (14, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (14, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (14, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (14, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (15, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (15, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (15, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (15, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (15, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (16, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (16, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (16, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (16, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (16, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (17, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (17, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (17, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (17, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (17, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (18, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (18, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (18, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (18, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (18, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (19, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (19, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (19, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (19, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (19, @row5_start, @row5_end, @row5_justification);

INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (20, @row1_start, @row1_end, @row1_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (20, @row2_start, @row2_end, @row2_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (20, @row3_start, @row3_end, @row3_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (20, @row4_start, @row4_end, @row4_justification);
INSERT INTO `assessment_score_row` (question_assessment_id, start, end, justification) VALUES (20, @row5_start, @row5_end, @row5_justification);