package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.CardListener;
import ch.aplu.jcardgame.Hand;

public class RealPerson extends Player
{
	public RealPerson(Hand startHand) {
		super(startHand);  // Real person's id is 0
		addListener();
	}

	@Override
	protected Card play(Suit trump, Suit lead, Hand trick, int numPlayer) {
		getHand().setTouchEnabled(true);
		return getSelected();
	}
	
	/**
	 * Set up human player for interaction.
	 */
	protected void addListener() {
		CardListener cardListener = new CardAdapter() {
        	public void leftDoubleClicked(Card card) {
				setSelected(card);
				getHand().setTouchEnabled(false);
			}};
        getHand().addCardListener(cardListener);
	}
}
