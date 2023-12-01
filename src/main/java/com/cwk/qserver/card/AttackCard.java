package com.cwk.qserver.card;

import com.cwk.qserver.card.Card;
import com.cwk.qserver.utils.IsCard;


public class AttackCard extends Card {

    protected int damage=0;
    protected int attackCount=1;


    public AttackCard(int cardIdByAnn) {
        super(cardIdByAnn);
    }
}
