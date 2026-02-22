package com.lingodom.app.core.model

/**
 * Represents the evaluation state of a single letter in a guess.
 */
enum class LetterState {
    CORRECT,        // Green — right letter, right position
    WRONG_POSITION, // Yellow — right letter, wrong position
    ABSENT,         // Gray — letter not in the word
    EMPTY           // Not yet evaluated
}

/**
 * A single letter and its evaluated state after a guess.
 */
data class LetterResult(
    val letter: Char,
    val state: LetterState
)
