package com.cwk.qserver.card;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Data

public class CardsPile {
    public List<Card> cardList=new ArrayList<>();

    public CardsPile(List<Card> cardList){

        this.cardList.addAll(cardList);
    }
    public String serialize(){
        StringBuilder ret = new StringBuilder("[");
        int length = cardList.size();
        for(int i =0;i<length;i++){
            ret.append(cardList.get(i).getCardid());
            if(i!=length-1){
                ret.append(",");
            }else{
                ret.append("]");
            }
        }
        return ret.toString();
    }
}
