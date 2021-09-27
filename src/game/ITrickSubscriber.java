package game;

import java.util.List;

import ch.aplu.jcardgame.Card;

/**
 * An interface for trick subscribers.
 */
public interface ITrickSubscriber
{
	public abstract void update(List<Card> trick);
	
	public abstract void clear();
}
