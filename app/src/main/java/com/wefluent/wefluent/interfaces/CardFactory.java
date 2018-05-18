package com.wefluent.wefluent.interfaces;

import com.wefluent.wefluent.models.BaseCardModel;

import java.util.List;

/**
 * Created by khalid on 4/23/18.
 */

public interface CardFactory {

    List<BaseCardModel> getCardModels();
}
