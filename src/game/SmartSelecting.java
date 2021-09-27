package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;

/**
 * Make a reasonable selection based on the tracked (trick) information.
 */
public class SmartSelecting implements ITrickSubscriber, ISelecting
{
	private final Random random;
	/** Card history */
	private final List<Card> playedCards = new ArrayList<>();

	public SmartSelecting(Random random) {
		this.random = random;
	}

	@Override
	public void update(List<Card> trick) {
		playedCards.addAll(trick);
	}

	@Override
	public void clear() {
		playedCards.clear();
	}

	@Override
	public Card select(List<Card> hand, List<Card> trick, Suit trump, Suit lead, int numPlayer) {
		int x = random.nextInt(hand.size());
		if (lead == null) return hand.get(x);  // Leads the turn
		List<Card> candidates = filterBeatPriors(hand, trick, trump, lead);
		boolean isLast = numPlayer == trick.size() + 1;  // Is the last player of this turn
		if (candidates.isEmpty()) {  // No way to win
			candidates = hand;
		} else {
			candidates = isLast ? candidates : getBestCards(hand, candidates, trick, trump);
		}
		return getCardLowestLoss(candidates, trump);
	}

	/**
	 * Return all cards in hand that can beat all prior cards in a trick.
	 */
	private List<Card> filterBeatPriors(List<Card> hand, List<Card> trick, Suit trump, Suit lead) {
		// Find the prior winning card in the current trick
		Card winningCard;
		List<Card> inTrump = trick.stream().filter(card -> card.getSuit().equals(trump))
				.collect(Collectors.toList());
		List<Card> inLead = trick.stream().filter(card -> card.getSuit().equals(lead))
				.collect(Collectors.toList());
		if (!inTrump.isEmpty()) {
			winningCard = highest(inTrump).get(0);
		} else {
			winningCard = highest(inLead).get(0);
		}
		// Find all cards that can beat the winning card
		return hand.stream().filter(card -> GameRound.winCard(card, winningCard, trump))
				.collect(Collectors.toList());
	}

	/**
	 * Play a card in the lowest possible rank to reduce loss.
	 * Prefer to save cards in the trump suit.
	 */
	private Card getCardLowestLoss(List<Card> candidates, Suit trump) {
		List<Card> nonTrump = candidates.stream().filter(card -> !card.getSuit().equals(trump))
				.collect(Collectors.toList());
		if (!nonTrump.isEmpty()) return lowest(nonTrump).get(0);
		return lowest(candidates).get(0);
	}

	/**
	 * Find the best cards based on how many unplayed cards it can beat by playing it.
	 */
	private List<Card> getBestCards(List<Card> hand, List<Card> candidates, List<Card> trick, Suit trump) {
		// Generate all cards
		Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
		List<Card> allCards = deck.toHand(false).getCardList();
		// End generate
		int numBeat, bestNumBeat = -1;
		List<Card> bestCards = new ArrayList<>();
		for (Card c : candidates) {
			numBeat = getNumBeat(c, hand, trick, allCards, trump);
			if (numBeat == bestNumBeat) {
				bestCards.add(c);
			} else if (numBeat > bestNumBeat) {
				bestCards.clear();
				bestNumBeat = numBeat;
				bestCards.add(c);
			}
		}
		return bestCards;
	}

	/**
	 * Return the number of other players' cards that the card c can win.
	 */
	private int getNumBeat(Card c, List<Card> hand, List<Card> trick, List<Card> allCards, Suit trump) {
		int numBeat = 0;
		List<Card> newPlayedCards = new ArrayList<>(playedCards);
		newPlayedCards.addAll(trick);
		List<Card> leftCards = new ArrayList<>(allCards);
		leftCards.removeAll(newPlayedCards);
		leftCards.removeAll(hand);
		for (Card c2 : leftCards) {
			if (GameRound.winCard(c, c2, trump)) numBeat++;
		}
		return numBeat;
	}

	/**
	 * Find a bunch of cards with the highest rank.
	 */
	private List<Card> highest(List<Card> candidates) {
		List<Card> cardList = new ArrayList<>();
		int greatestRank = candidates.get(0).getRankId();
		for (Card c : candidates) {
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

	/**
	 * Find a bunch of cards with the lowest rank.
	 */
	private List<Card> lowest(List<Card> candidates) {
		List<Card> cardList = new ArrayList<>();
		int greatestRank = candidates.get(0).getRankId();
		for (Card c : candidates) {
			if (c.getRankId() == greatestRank) {
				cardList.add(c);
			} else if (c.getRankId() > greatestRank) {  // Warning: Reverse rank order of cards (see comment on enum)
				cardList.clear();
				greatestRank = c.getRankId();
				cardList.add(c);
			}
		}
		return cardList;
	}
}
