package game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

/**
 * An abstract superclass for RealPerson and Npc.
 */
public abstract class Player
{
	/** the player's score */
	private int score;
	/** The player's cards */
	private Hand hand;
	/** The player is selecting this card now */
	private Card selected;

	public Player(Hand startHand) {
		score = 0;
		hand = startHand;
		selected = null;
	}

	protected int getScore() {
		return score;
	}
	
	protected void addScore(int inc) {
		score += inc;
	}
	
	protected Hand getHand() {
		return hand;
	}
	
	protected void setHand(Hand hand) {
		this.hand = hand;
	}
	
	protected Card getSelected() {
		return selected;
	}
	
	protected void setSelected(Card selected) {
		this.selected = selected;
	}
	
	protected void removeSelected() {
		selected = null;
	}
	
	/**
	 * Return a selected card to play in a game round.
	 */
	protected abstract Card play(Suit trump, Suit lead, Hand trick, int numPlayer);
}
