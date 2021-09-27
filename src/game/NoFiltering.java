package game;

import java.util.List;

import ch.aplu.jcardgame.Card;

/**
 * Select all cards in the hand.
 */
public class NoFiltering implements IFiltering
{
	@Override
	public List<Card> filter(List<Card> hand, Suit trump, Suit lead) {
		return hand;
	}
}
