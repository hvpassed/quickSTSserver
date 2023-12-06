package com.cwk.qserver.card.cardimpl;

import com.cwk.qserver.card.AttackCard;
import com.cwk.qserver.constant.CardTargetConstant;
import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.utils.IsCard;

/**
 * @BelongsProject: QServer
 * @BelongsPackage: com.cwk.qserver.card.cardimpl
 * @Author: chen wenke
 * @CreateTime: 2023-12-05 23:23
 * @Description: TODO
 * @Version: 1.0
 */
@IsCard(cardId = 2)
public class DoubleAttack extends AttackCard {


    protected static int getCardIdByAnn(){
        IsCard isCard= DoubleAttack.class.getAnnotation(IsCard.class);
        return isCard.cardId();
    }
    public DoubleAttack(){
        super(getCardIdByAnn());
        this.attackCount=2;
        this.damage = 10;
        this.title="双重攻击";
        this.description=String.format("造成%d次%d点伤害",this.attackCount,this.damage);
        this.cost=2;
        this.select= CardTargetConstant.SINGLE_MONSTER;
    }


    @Override
    public void impact(Object obj) {
        Monster monster = (Monster) obj;
        for (int i = 0; i < this.attackCount; i++) {
            int nowBlock = monster.getBlock();
            int nowHp = monster.getNowhp();
            int attackdamge = this.damage-monster.getDamageReduction();
            if (nowBlock > 0) {
                int damage = attackdamge - nowBlock;
                nowBlock = (Math.max(nowBlock - attackdamge, 0));
                monster.setBlock(nowBlock);
                if (damage > 0) {
                    monster.setNowhp(nowHp - damage);
                }
            } else {
                monster.setNowhp(nowHp - attackdamge);
            }
            if(monster.getNowhp()<0){
                monster.setNowhp(0);
            }
        }


    }
}
