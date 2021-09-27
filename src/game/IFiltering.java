package game;

import java.util.List;

import ch.aplu.jcardgame.Card;

/**
 * An interface for filtering strategies.
 */
public interface IFiltering
{
	public abstract List<Card> filter(List<Card> hand, Suit trump, Suit lead);
}
