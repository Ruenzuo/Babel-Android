package com.ruenzuo.babel.models.enums;

/**
 * Created by ruenzuo on 26/08/14.
 */
public enum BabelAchievementType {

    BABEL_ACHIEVEMENT_TYPE_EASY_5 ("CgkI2ev7m90IEAIQBg"), BABEL_ACHIEVEMENT_TYPE_NORMAL_10 ("CgkI2ev7m90IEAIQBw"), BABEL_ACHIEVEMENT_TYPE_HARD_15 ("CgkI2ev7m90IEAIQCA"), BABEL_ACHIEVEMENT_TYPE_NORMAL_30 ("CgkI2ev7m90IEAIQCQ"), BABEL_ACHIEVEMENT_TYPE_HARD_45 ("CgkI2ev7m90IEAIQCg");

    private final String achievementIdentifier;

    BabelAchievementType( String achievementId) {
        achievementIdentifier = achievementId;
    }

    public String toAchievementIdentifier() {
        return achievementIdentifier;
    }

}
