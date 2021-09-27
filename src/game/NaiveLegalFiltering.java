package game;

import java.util.List;
import java.util.stream.Collectors;

import ch.aplu.jcardgame.Card;

/**
 * Leaves the cards in the lead suit and the cards in the trump suit.
 * If the NPC does not have cards in the lead suit nor in the trump suit, or the NPC takes the lead,
 * it will select all cards in the hand (i.e. no filtering).
 */
public class NaiveLegalFiltering implements IFiltering
{
	@Override
	public List<Card> filter(List<Card> hand, Suit trump, Suit lead) {
		List<Card> candidates = hand.stream()
				.filter(card -> card.getSuit().equals(lead) || card.getSuit().equals(trump))
				.collect(Collectors.toList());
		if (lead != null && !candidates.isEmpty()) return candidates;
		return hand;
	}
}
