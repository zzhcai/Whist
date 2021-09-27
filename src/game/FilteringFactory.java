package game;

/**
 * A creator for filtering strategies. Applies singleton and factory patterns.
 */
public class FilteringFactory
{
	private static FilteringFactory instance = null;

	private FilteringFactory() {
	}

	protected static synchronized FilteringFactory getInstance() {
		if (instance == null) {
			instance = new FilteringFactory();
		}
		return instance;
	}

	/**
	 * @param strategy is the filtering strategy name.
	 * @param id is the number of player using this filtering.
	 */
	protected IFiltering getFiltering(String strategy) {
		try {
			return (IFiltering) Class.forName("game."+strategy).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
}
