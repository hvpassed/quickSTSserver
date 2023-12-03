package com.cwk.qserver.card;

import com.cwk.qserver.dao.entity.Monster;
import com.cwk.qserver.utils.IsCard;
import lombok.Data;

import java.util.List;

@Data
@IsCard(cardId = -1)
public class Card {
    protected int cardid;

    protected String title;

    protected String description;

    protected int cost;

    protected int select;


    public Card(){

    }

    public Card(int cardid){
        this.cardid =cardid;
    }

    public void impact(Object obj) {
    }
    public void impactAll(List<Monster> objects) {
    }
}
