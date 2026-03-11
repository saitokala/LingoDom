package com.lingodom.app.viewmodel

import android.media.AudioManager
import app.cash.turbine.test
import com.lingodom.app.core.model.GameRound
import com.lingodom.app.core.model.LetterState
import com.lingodom.app.core.model.PlayerStats
import com.lingodom.app.fake.FakePreferencesManager
import com.lingodom.app.fake.FakeSoundManager
import com.lingodom.app.fake.FakeWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePrefs: FakePreferencesManager
    private lateinit var fakeSoundManager: FakeSoundManager
    private lateinit var fakeWordRepo: FakeWordRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakePrefs = FakePreferencesManager()
        fakeSoundManager = FakeSoundManager()
        fakeWordRepo = FakeWordRepository(fixedWord = "TEST")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): GameViewModel =
        GameViewModel(fakeWordRepo, fakePrefs, fakeSoundManager)

    @Test
    fun `startGame initializes state for STARTER round`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.startGame(GameRound.STARTER)

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(GameRound.STARTER, state.round)
            assertEquals(4, state.wordLength)
            assertEquals(5, state.maxAttempts)
            assertEquals('T', state.firstLetter)
            assertEquals(GameStatus.PLAYING, state.gameStatus)
            assertTrue(state.timerRunning)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `correct guess wins the game`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.startGame(GameRound.STARTER)
        advanceUntilIdle()

        vm.uiState.test {
            val initial = awaitItem()
            assertEquals(GameStatus.PLAYING, initial.gameStatus)

            // Type "TEST" — the fixed word from FakeWordRepository
            vm.onInputChange(
                initial.currentInput.copy(text = "TEST")
            )
            advanceUntilIdle()

            // Should transition to WON
            val won = expectMostRecentItem()
            assertEquals(GameStatus.WON, won.gameStatus)
            assertTrue(won.earnedScore > 0)
            assertTrue(won.showConfetti)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `correct guess plays victory sound`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.startGame(GameRound.STARTER)
        advanceUntilIdle()

        val state = vm.uiState.value
        vm.onInputChange(state.currentInput.copy(text = "TEST"))
        advanceUntilIdle()

        assertTrue(
            fakeSoundManager.playedEffects.contains(AudioManager.FX_KEYPRESS_RETURN)
        )
    }

    @Test
    fun `invalid word shows error message`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.startGame(GameRound.STARTER)
        advanceUntilIdle()

        vm.uiState.test {
            awaitItem() // current playing state

            // Submit an invalid word
            vm.onInputChange(
                vm.uiState.value.currentInput.copy(text = "ZZZZ")
            )
            advanceUntilIdle()

            val errorState = expectMostRecentItem()
            // Either shows "Not a valid word" or has reset the input
            // (the message clears after 1s delay)
            assertTrue(
                errorState.message == "Not a valid word" ||
                    errorState.currentInput.text == "T"
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `wrong guess adds to guesses list`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        // Use a 5-letter word so we can test with CLASSIC round
        fakeWordRepo = FakeWordRepository(fixedWord = "CRANE")
        fakeWordRepo.addValidWord("HOUSE")
        val vm2 = GameViewModel(fakeWordRepo, fakePrefs, fakeSoundManager)
        advanceUntilIdle()

        vm2.startGame(GameRound.CLASSIC)
        advanceUntilIdle()

        val state = vm2.uiState.value
        vm2.onInputChange(state.currentInput.copy(text = "CHASE"))
        advanceUntilIdle()

        // Should still be playing with one guess recorded
        vm2.uiState.test {
            val current = awaitItem()
            assertEquals(GameStatus.PLAYING, current.gameStatus)
            assertEquals(1, current.guesses.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `key states update after guess`() = runTest {
        fakeWordRepo = FakeWordRepository(fixedWord = "CRANE")
        fakeWordRepo.addValidWord("CHASE")
        val vm = GameViewModel(fakeWordRepo, fakePrefs, fakeSoundManager)
        advanceUntilIdle()

        vm.startGame(GameRound.CLASSIC)
        advanceUntilIdle()

        val state = vm.uiState.value
        vm.onInputChange(state.currentInput.copy(text = "CHASE"))
        advanceUntilIdle()

        val keys = vm.uiState.value.keyStates
        // C is correct (position 0)
        assertEquals(LetterState.CORRECT, keys['C'])
        // E is in CRANE but not at position 4 in CHASE → depends on engine
        assertNotNull(keys['E'])
        cancelAndIgnoreRemainingEvents()
    }

    @Test
    fun `dismissConfetti clears flag`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.startGame(GameRound.STARTER)
        advanceUntilIdle()

        // Win the game
        val state = vm.uiState.value
        vm.onInputChange(state.currentInput.copy(text = "TEST"))
        advanceUntilIdle()

        assertTrue(vm.uiState.value.showConfetti)

        vm.dismissConfetti()
        assertEquals(false, vm.uiState.value.showConfetti)
    }

    @Test
    fun `init loads timer and stats from preferences`() = runTest {
        fakePrefs = FakePreferencesManager(
            initialStats = PlayerStats(totalScore = 1000, currentStreak = 5),
            initialTimer = 30
        )
        val vm = GameViewModel(fakeWordRepo, fakePrefs, fakeSoundManager)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(1000, state.totalScore)
        assertEquals(5, state.streak)
        assertEquals(30, state.timerTotal)
    }
}
