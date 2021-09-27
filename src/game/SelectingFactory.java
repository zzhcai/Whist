package game;

import java.util.Random;

/**
 * A creator for selecting strategies. Applies singleton and factory patterns.
 */
public class SelectingFactory
{
	private static SelectingFactory instance = null;

	private SelectingFactory() {
	}

	protected static synchronized SelectingFactory getInstance() {
		if (instance == null) {
			instance = new SelectingFactory();
		}
		return instance;
	}

	/**
	 * @param strategy is the selecting strategy name.
	 */
	protected ISelecting getSelecting(String strategy, Random random) {
		if (strategy.equals("RandomSelecting")) {
			return new RandomSelecting(random);
		} else if (strategy.equals("HighestRankSelecting")) {
			return new HighestRankSelecting();
		} else if (strategy.equals("SmartSelecting")) {
			return new SmartSelecting(random);
		}
		return null;
	}
}
