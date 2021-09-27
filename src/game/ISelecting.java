package game;

import java.util.List;

import ch.aplu.jcardgame.Card;

/**
 * An interface for selecting strategies.
 */
public interface ISelecting
{
	public abstract Card select(List<Card> hand, List<Card> trick, Suit trump, Suit lead, int numPlayer);
}
