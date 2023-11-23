package com.cwk.qserver;

import com.cwk.qserver.card.Card;
import com.cwk.qserver.card.CardsPile;
import com.cwk.qserver.card.CardsPileFactory;
import com.cwk.qserver.dao.IService.impl.UserServiceimpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

@SpringBootTest
@Service
class QServerApplicationTests {
    @Autowired
    private UserServiceimpl userServiceimpl;
    @Autowired
    private CardsPileFactory cardsPileFactory;

    @Test

    public void test(){
        try {
            Card card = new Card();
            card.setTitle("123");
            card.setCardid(2);
            CardsPile cardsPile = cardsPileFactory.getObject();
            CardsPile cardsPile1 =cardsPileFactory.getObject();
            System.out.println(cardsPile.serialize());
            cardsPile.cardList.add(card);
            System.out.println(cardsPile.serialize());
            System.out.println(cardsPile1.serialize());

        }catch (Exception err){
            System.out.println(err.toString());
        }
    }
}
