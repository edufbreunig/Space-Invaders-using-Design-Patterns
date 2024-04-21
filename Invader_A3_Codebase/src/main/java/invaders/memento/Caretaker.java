package invaders.memento;

/**
 * Represents the caretaker in the memento design pattern.
 * The caretaker is responsible for keeping the memento (saved state) and
 * can provide access to the last saved state when needed.
 * <p>
 * The caretaker can store only one memento at a time. Once retrieved, the memento is
 * nullified in the caretaker ensuring its one-time use.
 *
 *
 *
 */


public class Caretaker {
    private Memento savedState;

    public void saveState(Memento memento) {
        savedState = memento;
    }

    /**
     * Retrieves the last saved state (memento) and makes it null  in the caretaker.
     * Ensures that each memento can be retrieved only once.
     *
     * @return The last saved memento or null if no state has been saved.
     */
    public Memento getLastSavedState() {
        if (savedState == null) {
            return null;
        }

        Memento lastState = savedState;
        savedState = null;
        return lastState;
    }
}
