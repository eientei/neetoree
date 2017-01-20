package org.eientei.discord;

/**
 * Created by Alexander <iamtakingiteasy> Tumin on 2017-01-15.
 */
public class VoteMute {
    private final int min;
    private int current = 0;

    public VoteMute(int min) {
        this.min = min;
    }

    public boolean add() {
        return ++current > min;
    }

    public int getCurrent() {
        return current;
    }
}
