package com.ruenzuo.babel.models.enums;

import java.io.Serializable;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public enum BabelDifficultyType implements Serializable {

    DIFFICULTY_TYPE_EASY ("Easy", "CgkI2ev7m90IEAIQAA", 3), DIFFICULTY_TYPE_NORMAL ("Normal", "CgkI2ev7m90IEAIQAQ", 5), DIFFICULTY_TYPE_HARD ("Hard", "CgkI2ev7m90IEAIQAg", 7);

    private final String print;
    private final String leaderboardIdentifier;
    private final int maxHints;

    BabelDifficultyType(String prnt, String leaderboardId, int mxHnts) {
        print = prnt;
        leaderboardIdentifier = leaderboardId;
        maxHints = mxHnts;
    }

    public int toMaxHints() {
        return maxHints;
    }

    public String toPrint() {
        return print;
    }

    public String toLeaderboardIdentifier() {
        return leaderboardIdentifier;
    }

    public static CharSequence[] difficultyTypes() {
        int length = BabelDifficultyType.values().length;
        CharSequence[] typesPrint = new CharSequence[length];
        for (int i = 0; i < length; i++) {
            typesPrint[i] = BabelDifficultyType.values()[i].toPrint();
        }
        return typesPrint;
    }

}
