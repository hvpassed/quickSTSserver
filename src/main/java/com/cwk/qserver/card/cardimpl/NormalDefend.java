package com.cwk.qserver.card.cardimpl;

import com.cwk.qserver.card.DefendCard;
import com.cwk.qserver.constant.CardTargetConstant;
import com.cwk.qserver.target.BattlePlayer;
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
        this.description=String.format("获得%d点护甲",this.block);
        this.cost=1;
        this.select = CardTargetConstant.SELF;
    }

    @Override
    public void impact(Object obj) {
        BattlePlayer battlePlayer = (BattlePlayer) obj;
        battlePlayer.setBlock(battlePlayer.getBlock()+this.block);
    }
}
