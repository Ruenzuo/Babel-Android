package com.ruenzuo.babel.helpers;

import com.ruenzuo.babel.models.enums.BabelAchievementType;
import com.ruenzuo.babel.models.enums.BabelDifficultyType;

/**
 * Created by ruenzuo on 26/08/14.
 */
public class GooglePlayGameServicesHelper {

    public BabelAchievementType achievementUnlocked(int points, BabelDifficultyType babelDifficultyType) {
        switch (babelDifficultyType) {
            case DIFFICULTY_TYPE_EASY: {
                if (points >= 5) {
                    return BabelAchievementType.BABEL_ACHIEVEMENT_TYPE_EASY_5;
                }
                break;
            }
            case DIFFICULTY_TYPE_NORMAL: {
                if (points >= 30) {
                    return BabelAchievementType.BABEL_ACHIEVEMENT_TYPE_NORMAL_30;
                } else if (points >= 10) {
                    return BabelAchievementType.BABEL_ACHIEVEMENT_TYPE_NORMAL_10;
                }
                break;
            }
            case DIFFICULTY_TYPE_HARD: {
                if (points >= 45) {
                    return BabelAchievementType.BABEL_ACHIEVEMENT_TYPE_HARD_45;
                } else if (points >= 15) {
                    return BabelAchievementType.BABEL_ACHIEVEMENT_TYPE_HARD_15;
                }
                break;
            }
        }
        return null;
    }

}
