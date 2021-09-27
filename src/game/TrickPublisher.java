package game;

import java.util.ArrayList;
import java.util.List;

import ch.aplu.jcardgame.Card;

/**
 * This class is a part of the observer pattern.
 * It publishes the information of the trick in each round, when the round is over.
 */
public class TrickPublisher
{
	private final List<ITrickSubscriber> subscribers = new ArrayList<>();

	protected void addSubscribers(List<ITrickSubscriber> newSubscribers) {
		subscribers.addAll(newSubscribers);
	}

	protected void notifySubscribers(List<Card> trick) {
		for (ITrickSubscriber s : subscribers) {
			s.update(new ArrayList<>(trick));  // Use the copy of trick
		}
	}

	protected void clearSubscribers() {
		for (ITrickSubscriber s : subscribers) {
			s.clear();
		}
	}
}
