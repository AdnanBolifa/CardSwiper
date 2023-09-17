package com.thef0x.cardswiper;

public class Card
{
    public String effectRight;
    public String effectLeft;
    public String text;
    // Add more fields as needed

    public Card(String effectRight, String effectLeft, String text)
    {
        this.text = text;
        this.effectRight = effectRight;
        this.effectLeft = effectLeft;
    }
}
