package game;

import java.util.Random;

import ch.aplu.jcardgame.Hand;

/**
 * A creator for players. Applies singleton and factory patterns.
 */
public class PlayerFactory
{
	private static PlayerFactory instance = null;
	private final FilteringFactory filteringFactory;
	private final SelectingFactory selectingFactory;

	private PlayerFactory() {
		filteringFactory = FilteringFactory.getInstance();
		selectingFactory = SelectingFactory.getInstance();
	}

	protected static synchronized PlayerFactory getInstance() {
		if (instance == null) {
			instance = new PlayerFactory();
		}
		return instance;
	}

	/**
	 * @param startHand is the card dealt at the start of the game.
	 */
	protected Player getRealPerson(Hand startHand) {
		return new RealPerson(startHand);
	}

	/**
	 * @param id is the player id.
	 * @param filteringStrategy is the filtering strategy name.
	 * @param selectingStrategy is the selecting strategy name.
	 * @param thinkingTime is npc's halting time before playing a card.
	 */
	protected Player getNpc(String filteringStrategy, String selectingStrategy,
			Hand startHand, int thinkingTime, Random random) {
		IFiltering filtering = filteringFactory.getFiltering(filteringStrategy);
		ISelecting selecting = selectingFactory.getSelecting(selectingStrategy, random);
		return new Npc(thinkingTime, filtering, selecting, startHand);
	}
}
