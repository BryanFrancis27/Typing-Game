# TypingGame Fixes - TODO List

## Issues to Fix
- [ ] No proper start button for free practice and timed modes
- [ ] Player results not displayed real-time on results dashboard
- [ ] Recent games not logged in history of sessions

## Implementation Steps
- [ ] Modify SceneManager to support passing data during navigation
- [ ] Update MenuController to pass GameMode when navigating to GAME_SCENE
- [ ] Update Main.java scene listener to retrieve passed mode and call startGame with it
- [ ] Add start button to GameController UI - disable input initially, enable after clicking start
- [ ] Verify ResultsController updateResults() is called after session save
- [ ] Test navigation with data passing
- [ ] Test both game modes start correctly
- [ ] Test results display real-time
- [ ] Test history shows correct game modes

## Files to Edit
- [ ] SceneManager.java
- [ ] MenuController.java
- [ ] Main.java
- [ ] GameController.java
- [ ] ResultsController.java (verification)
