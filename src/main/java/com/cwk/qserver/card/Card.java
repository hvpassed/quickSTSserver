package com.cwk.qserver.card;

import com.cwk.qserver.utils.IsCard;
import lombok.Data;

@Data
@IsCard(cardId = -1)
public class Card {
    protected int cardid;

    protected String title;

    protected String description;

    protected int cost;




    public Card(){

    }

    public Card(int cardid){
        this.cardid =cardid;
    }
}
