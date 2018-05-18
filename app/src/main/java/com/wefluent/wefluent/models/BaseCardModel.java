package com.wefluent.wefluent.models;

import com.wefluent.wefluent.enums.CardType;

import java.io.Serializable;

public class BaseCardModel implements Serializable {

    private CardType cardType;

    private Object cardValue;

    public BaseCardModel setCardType(CardType cardType) {

        this.cardType = cardType;

        return this;

    }

    public BaseCardModel setCardValue(Object cardValue) {

        this.cardValue = cardValue;

        return this;

    }

    public CardType getCardType() {

        return cardType;

    }

    public <T> T getCardValue() {

        return (T) cardValue;

    }

}