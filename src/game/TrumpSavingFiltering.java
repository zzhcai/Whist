package game;

import java.util.List;
import java.util.stream.Collectors;

import ch.aplu.jcardgame.Card;

/**
 * NPC will try to save the card in the trump suit.
 * To do so, attempt to leave the cards in the lead suit.
 * If the NPC does not have the cards in the lead suit, then leave the cards in the trump suit.
 * If the NPC does not have cards in the lead suit nor the trump suit, or the NPC takes the lead,
 * it will select all cards in the hand (i.e., no filtering).
 */
public class TrumpSavingFiltering implements IFiltering
{
	@Override
	public List<Card> filter(List<Card> hand, Suit trump, Suit lead) {
		if (lead == null) return hand;
		List<Card> inLead = hand.stream().filter(card -> card.getSuit().equals(lead))
				.collect(Collectors.toList());
		if (!inLead.isEmpty()) return inLead;
		List<Card> inTrump = hand.stream().filter(card -> card.getSuit().equals(trump))
				.collect(Collectors.toList());
		if (!inTrump.isEmpty()) return inTrump;
		return hand;
	}
}
