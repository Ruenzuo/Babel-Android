package com.ruenzuo.babel.definitions;

import com.ruenzuo.babel.models.enums.BabelDifficultyType;
import com.ruenzuo.babel.models.enums.DifficultyDialogFragmentType;

/**
 * Created by ruenzuo on 22/08/14.
 */
public interface OnDifficultySelectedListener {

    public void onDifficultySelected(BabelDifficultyType babelDifficultyType, DifficultyDialogFragmentType difficultyDialogFragmentType);

}
