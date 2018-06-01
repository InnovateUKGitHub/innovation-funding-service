package org.innovateuk.ifs.form.resource;

/**
 * This enum marks questions as a given type.
 */
public enum QuestionType {
	COST,
	GENERAL,
	// TODO: name might change.
	// This is the new type for application team and research category questions
	// on the application menu, which behave differently to normal questions.
	// Hopefully the Application details question will become part of this category too, because it is also
	// 'lead only' and doesn't use form inputs like a normal question either.
	LEAD_ONLY
}
