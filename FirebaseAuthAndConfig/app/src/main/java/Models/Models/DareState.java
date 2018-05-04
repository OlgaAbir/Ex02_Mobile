package Models;

/**
 * Created by user on 28-Apr-18.
 */

public enum DareState {
    Available(0),
    InProgress(1),
    Succeeded(2),
    Failed(3);

    private final int mValue;
    private DareState(int value) {
        this.mValue = value;
    }

    public int getValue() {
        return this.mValue;
    }
}
