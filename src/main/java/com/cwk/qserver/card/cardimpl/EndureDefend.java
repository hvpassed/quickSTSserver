package com.cwk.qserver.card.cardimpl;

import com.cwk.qserver.card.DefendCard;
import com.cwk.qserver.constant.CardTargetConstant;
import com.cwk.qserver.utils.IsCard;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.card.cardimpl
 * @Author: chen wenke
 * @CreateTime: 2023-12-05 23:31
 * @Description: TODO
 * @Version: 1.0
 */
@IsCard(cardId = 4)
public class EndureDefend extends DefendCard {

    protected static int getCardIdByAnn(){
        IsCard isCard= EndureDefend.class.getAnnotation(IsCard.class);
        return isCard.cardId();
    }

    public EndureDefend(){
        super(getCardIdByAnn());
        this.block = 15;
        this.title = "忍耐";
        this.description=String.format("获得%d点护甲",this.block);
        this.cost=2;
        this.select = CardTargetConstant.SELF;
    }
}
