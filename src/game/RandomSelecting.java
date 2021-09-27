package game;

import java.util.List;
import java.util.Random;

import ch.aplu.jcardgame.Card;

/**
 * Return a random card from hand.
 */
public class RandomSelecting implements ISelecting
{
	private final Random random;

	public RandomSelecting(Random random) {
		this.random = random;
	}

	@Override
	public Card select(List<Card> hand, List<Card> trick, Suit trump, Suit lead, int numPlayer) {
		int x = random.nextInt(hand.size());
        return hand.get(x);
	}
}
