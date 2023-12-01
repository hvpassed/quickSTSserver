package com.cwk.qserver.card.cardimpl;

import com.cwk.qserver.card.DefendCard;
import com.cwk.qserver.utils.IsCard;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.card.cardimpl
 * @Author: chen wenke
 * @CreateTime: 2023-11-30 19:15
 * @Description: TODO
 * @Version: 1.0
 */
@IsCard(cardId = 1)
public class NormalDefend extends DefendCard {
    protected static int getCardIdByAnn(){
        IsCard isCard= NormalDefend.class.getAnnotation(IsCard.class);
        return isCard.cardId();
    }

    public NormalDefend(){
        super(getCardIdByAnn());
        this.block = 5;
        this.title = "防御";
        this.description="获取5点护甲";
        this.cost=1;
    }
}
