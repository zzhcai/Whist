package game;

import java.util.ArrayList;
import java.util.List;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class Npc extends Player
{
	private final int thinkingTime;
	// Gaming strategies
	private final IFiltering filteringStrategy;
	private final ISelecting selectingStrategy;

	public Npc(int thinkingTime, IFiltering filtering, ISelecting selecting, Hand startHand) {
		super(startHand);
		this.thinkingTime = thinkingTime;
		filteringStrategy = filtering;
		selectingStrategy = selecting;
	}

	@Override
	protected Card play(Suit trump, Suit lead, Hand trick, int numPlayer) {
		List<Card> filtered = filteringStrategy.filter(getHand().getCardList(), trump, lead);
		Card selected = selectingStrategy.select(filtered, new ArrayList<>(trick.getCardList()), trump, lead, numPlayer);
		setSelected(selected);
		return selected;
	}

	protected int getThinkingTime() {
		return thinkingTime;
	}

	protected ISelecting getSelectingStrategy() {
		return selectingStrategy;
	}
}
