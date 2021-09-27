package game;

import java.util.ArrayList;
import java.util.List;

import ch.aplu.jcardgame.Card;

/**
 * Select the highest ranked card from hand.
 * If there are more than one options, select the card in lead suit.
 * If there is no card in the lead suit, select the card in the trump suit.
 * If neither of these exist, then select the first card.
 */
public class HighestRankSelecting implements ISelecting
{
	@Override
	public Card select(List<Card> hand, List<Card> trick, Suit trump, Suit lead, int numPlayer) {
		List<Card> candidates = highest(hand);
		for (Card c : candidates) {
			if (c.getSuit().equals(lead)) return c;
		}
		for (Card c : candidates) {
			if (c.getSuit().equals(trump)) return c;
		}
		return candidates.get(0);
	}

	/**
	 * Find a bunch of cards with the highest rank.
	 */
	private List<Card> highest(List<Card> hand) {
		List<Card> cardList = new ArrayList<>();
		int greatestRank = hand.get(0).getRankId();
		for (Card c : hand) {
			if (c.getRankId() == greatestRank) {
				cardList.add(c);
			} else if (c.getRankId() < greatestRank) {  // Warning: Reverse rank order of cards (see comment on enum)
				cardList.clear();
				greatestRank = c.getRankId();
				cardList.add(c);
			}
		}
		return cardList;
	}
}
