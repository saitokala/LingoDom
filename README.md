# 🎮 LingoDom

<p align="center">
  <img src="app/src/main/assets/app_icon.png" alt="LingoDom App Icon" width="200"/>
</p>

<p align="center">
  <strong>A fast-paced word-guessing game for Android</strong><br/>
  Inspired by the classic TV game show <em>Lingo</em>
</p>

<p align="center">
  🎯 Guess the word • ⏱️ Beat the clock • 🏆 Unlock harder rounds
</p>

---

## 📖 About the Game

**LingoDom** is a single-player word puzzle game that brings the excitement of the television game show **Lingo** to your Android device. First aired in the 1980s, Lingo has captivated audiences worldwide with its simple yet addictive word-guessing format. Players must deduce a hidden word using color-coded feedback—similar to games like Wordle, but with the added pressure of a countdown timer!

### 🎬 The Lingo Legacy

The TV show **Lingo** originated in the United States and gained international popularity with versions produced in countries including the UK, the Netherlands, France, and Australia. Contestants face off in teams, racing against the clock to guess five-letter words. Each correct guess fills in a row on a 5×5 bingo-style board, with the goal of completing a line to win.

**LingoDom** captures the core excitement of the show—**the race against time, the strategic guessing, and the satisfaction of cracking the code**—while adapting it for solo mobile play. No teams, no bingo boards, just you versus the dictionary!

---

## 📥 Download & Play

### Quick Start (Just Want to Play?)

**[📲 Download the APK](release/LingoDom.apk)** ← Click to download the latest build

1. Download `LingoDom.apk` to your Android device
2. Open the file and tap **Install**
3. You may need to enable "Install from Unknown Sources" in your device settings
4. Launch **LingoDom** and start playing!

**Requirements:** Android 8.0 (Oreo / API 26) or higher

---

## 🎯 How to Play

### The Basics

Each round, you're given a hidden word to guess:

1. **The first letter is revealed** as your starting hint
2. **Type your guess** using the on-screen keyboard (or your device keyboard)
3. **Press GO** to submit
4. **Analyze the feedback:**
   - 🟩 **Green** = Correct letter in the correct position
   - 🟨 **Yellow** = Correct letter in the wrong position  
   - ⬜ **Gray** = Letter not in the word at all

5. **Use the clues** to refine your next guess
6. **Beat the timer!** Each guess has a countdown—run out and you lose an attempt

### 🏁 Winning & Losing

- ✅ **Win** by guessing the correct word within your attempts
- ❌ **Lose** if you exhaust all attempts or let the timer expire
- 🔥 **Build streaks** for bonus points on consecutive wins
- 🎊 **Unlock new rounds** by reaching score milestones

---

## 🎮 Game Modes (Rounds)

Progress through increasingly challenging rounds:

| Round | 📏 Word Length | 🎯 Attempts | 🔓 Unlock Score |
|-------|---------------|------------|----------------|
| **🟢 Starter** | 4 letters | 5 attempts | Free (Start here!) |
| **🔵 Classic** | 5 letters | 5 attempts | 500 points |
| **🟡 Challenge** | 6 letters | 6 attempts | 2,000 points |
| **🔴 Master** | 7 letters | 6 attempts | 5,000 points |

Each round gets progressively harder with longer words. Can you conquer them all?

---

## 💯 Scoring System

### Points Per Guess

The faster you solve, the more you score:

| Guess # | Points Awarded |
|---------|----------------|
| 1st guess | 100 pts 🌟 |
| 2nd guess | 80 pts |
| 3rd guess | 60 pts |
| 4th guess | 40 pts |
| 5th guess | 20 pts |
| 6th guess | 10 pts |

### Bonus Multipliers

- **📦 Round Bonus:** +50 points × round number (harder rounds = bigger rewards)
- **🔥 Streak Bonus:** +10 points × current streak (consecutive wins)

### Example Calculation

*Win on 2nd guess in Classic Round (Round 2) with a 3-word streak:*
- Base: **80 pts** (2nd guess)
- Round bonus: **100 pts** (50 × 2)
- Streak bonus: **30 pts** (10 × 3)
- **Total: 210 points!** 🎉

---

## 🏆 Ranks & Achievements

Climb the ranks as you master the game:

| Rank | Score Required | Badge |
|------|----------------|-------|
| Novice | 0 pts | 🔤 |
| Wordsmith | 500 pts | ✏️ |
| Linguist | 2,000 pts | 📖 |
| Lexicon Master | 5,000 pts | 🏆 |
| LingoDom Champion | 10,000 pts | 👑 |

---

## ⚙️ Settings & Features

- **⏱️ Adjustable Timer:** Set from 5 to 60 seconds per guess (default: 10s)
- **🔊 Sound Effects:** Toggle audio feedback on/off
- **📊 Detailed Statistics:** Track wins, losses, streaks, and guess distribution
- **🎨 Themes:** Clean, modern Material Design 3 interface
- **💾 Offline Play:** All data stored locally—no account or internet required
- **🪶 Lightweight:** No ads, no tracking, no bloat—just pure word-guessing fun!

---

## 🛠️ For Developers: Experiment with the Code

Want to modify the game, add features, or learn Android development? Here's how to get started:

### Prerequisites

- **Android Studio** (latest stable version recommended)
- **JDK 17** or higher
- An Android device or emulator running **API 26+** (Android 8.0+)

### Setup Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/[YOUR-GITHUB-USERNAME]/LingoDom.git
   cd LingoDom
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select **File → Open**
   - Navigate to the cloned `LingoDom` directory
   - Click **OK** and wait for Gradle sync

3. **Connect a device or start an emulator:**
   - **Physical Device:** Enable USB debugging in Developer Options
   - **Emulator:** Create an AVD via Device Manager (Pixel 8 or similar recommended)

4. **Run the app:**
   - Select the `app` configuration in the toolbar
   - Click the **Run** button (▶️) or press `Shift + F10`
   - The app will install and launch on your device/emulator

### Project Structure

```
LingoDom/
├── app/src/main/java/com/lingodom/app/
│   ├── core/              # Game logic & engine
│   │   ├── engine/        # Word validation, scoring
│   │   └── model/         # Data models (GameRound, PlayerStats, etc.)
│   ├── data/              # Data layer (preferences, word repository, sound)
│   ├── ui/                # Jetpack Compose UI components
│   │   ├── screens/       # Main screens (Game, Home, Stats, Settings)
│   │   ├── components/    # Reusable UI elements
│   │   ├── navigation/    # Navigation graph
│   │   └── theme/         # Material 3 theming
│   └── viewmodel/         # ViewModels for each screen
├── app/src/main/res/
│   └── raw/               # Word dictionaries (4-7 letter words)
└── build.gradle.kts       # Build configuration
```

### Customization Ideas

- **Add new word lengths:** Extend the dictionary files in `res/raw/`
- **Create new themes:** Modify `ui/theme/Color.kt` and `Theme.kt`
- **Adjust difficulty:** Change timer duration, max attempts in `GameRound.kt`
- **Add power-ups:** Implement hints, letter reveals, or time extensions
- **Leaderboards:** Integrate Firebase or Room database for persistent high scores
- **Multiplayer:** Add head-to-head mode using WebSockets or Bluetooth

### Building the APK

To generate a distributable APK:

```bash
# Windows
gradlew.bat assembleDebug

# macOS/Linux
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

Or via Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**

---

## 🧪 Testing the APK (Non-Developers)

If you just want to test the game without setting up Android Studio:

1. **Download** the [APK file](app/build/outputs/apk/debug/app-debug.apk) directly
2. **Transfer** to your Android device (via USB, email, cloud storage, etc.)
3. **Open** the file using a file manager app
4. **Allow installation** from unknown sources when prompted:
   - Settings → Security → Install unknown apps → [Your file manager] → Allow
5. **Tap Install** and wait for completion
6. **Launch** LingoDom from your app drawer

**Note:** This is a debug build for testing. For production, a signed release APK would be generated.

---

## 🎨 Changing the App Icon

Want to customize the launcher icon?

1. **Save your icon image** to your computer (PNG format recommended)
2. In Android Studio, right-click the `app` directory in the Project explorer
3. Select **New → Image Asset**
4. Under **Foreground Layer**, select `Image` as the **Asset Type**
5. For **Path**, select your image file
6. Adjust the **Resize** slider to fit within safe zone guides
7. Click **Next**, then **Finish**

Android Studio will automatically generate icons for all screen densities.

---

## 📱 Technical Details

- **Language:** Kotlin 100%
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Architecture:** MVVM with StateFlow
- **Minimum SDK:** API 26 (Android 8.0 Oreo)
- **Target SDK:** API 34 (Android 14)
- **Build System:** Gradle 9.2.1 with Kotlin DSL
- **Storage:** DataStore (Preferences)
- **Dependencies:** 
  - AndroidX Core, Lifecycle, Compose
  - Navigation Compose
  - DataStore Preferences
  - No third-party analytics or ads

---

## 📄 License

This project is open source and available for educational and personal use. Feel free to fork, modify, and experiment!

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to:

- Report bugs
- Suggest new features
- Submit pull requests
- Improve documentation

---

## 📧 Contact

Questions or feedback? Open an issue or reach out via GitHub.

---

**Enjoy the game and happy word-guessing!** 🎯✨
