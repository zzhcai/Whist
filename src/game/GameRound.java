package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jcardgame.RowLayout;
import ch.aplu.jcardgame.TargetArea;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.TextActor;

/**
 * This class applies the principle of pure fabrication and indirection.
 * Used to simulate all game rounds.
 */
public class GameRound
{
	private final Random random;
	private final GraphicDisplayer displayer;
	private final boolean enforceRule;
	private final Player[] players;
	private final int winningScore;

	/** Publish the trick for each turn */
	private final TrickPublisher publisher = new TrickPublisher();
	private Suit trump = null, lead = null;
	private Hand trick = null;

	// Graphics
	private Actor[] scoreActors;
	private Actor trumpActor;

	public GameRound(Random random, GraphicDisplayer displayer, boolean enforceRule, Player[] players,
			int winningScore) {
		this.random = random;
		this.displayer = displayer;
		this.enforceRule = enforceRule;
		this.players = players;
		this.winningScore = winningScore;
		publisher.addSubscribers(smart(players));
		initScore();
		displayHands();
	}

	/**
	 * Return all smart selecting strategies of players, as subscribers.
	 */
	private List<ITrickSubscriber> smart(Player[] players) {
		List<ITrickSubscriber> smart = new ArrayList<>();
		ISelecting s;
		for (Player p : players) {
			// An npc that plays the smart selecting strategy
			if (p instanceof Npc &&
					(s = ((Npc) p).getSelectingStrategy()) instanceof SmartSelecting) {
				smart.add((ITrickSubscriber)s);
			}
		}
		return smart;
	}

	/**
	 * Initiate the graphics of scores.
	 */
	private void initScore() {
		scoreActors = new Actor[players.length];
		for (int i = 0; i < players.length; i++) {
			scoreActors[i] = new TextActor("0", Whist.FONT_COLOR, displayer.bgColor, Whist.BIG_FONT);
    	    displayer.addActor(scoreActors[i], Whist.SCORE_LOCATIONS[i]);
		}
	}

	/**
	 * Draw the graphics of players' hands.
	 */
	private void displayHands() {
		RowLayout[] layouts = new RowLayout[players.length];
        for (int i = 0; i < players.length; i++) {
            layouts[i] = new RowLayout(Whist.HAND_LOCATIONS[i], Whist.HAND_WIDTH);
            layouts[i].setRotationAngle(90 * i);
            // layouts[i].setStepDelay(10);
            players[i].getHand().setView(displayer, layouts[i]);
            players[i].getHand().setTargetArea(new TargetArea(Whist.TRICK_LOCATION));
            players[i].getHand().draw();
	    }
	}

	/**
	 * Add 1 point for a player who wins a round.
	 */
    private void updateScore(int id) {
    	players[id].addScore(1);
        displayer.removeActor(scoreActors[id]);
        scoreActors[id] = new TextActor(String.valueOf(players[id].getScore()), Whist.FONT_COLOR,
        		displayer.bgColor, Whist.BIG_FONT);
        displayer.addActor(scoreActors[id], Whist.SCORE_LOCATIONS[id]);
    }

    /**
     * This method plays one round of game.
     * Takes an integer that shows which player to start from.
     * If the number is -1, randomly select someone to lead the round.
     * Returns the number of the player who will lead the next game.
     */
    protected int playRound(int startFrom) {
    	lead = null;
    	trick = new Hand(new Deck(Suit.values(), Rank.values(), "cover"));
        int nextPlayer;
        if (startFrom == -1) {  // Randomly select a player to lead for this round
            nextPlayer = random.nextInt(players.length);
        } else {
        	nextPlayer = startFrom;
        }
        int winner = nextPlayer;
        Card selected, winningCard = selectLead(nextPlayer);
        // Other players play
        for (int i = 1; i < players.length; i++) {
        	if (++nextPlayer >= players.length) nextPlayer = 0;  // From last back to first
        	selected = selectCard(nextPlayer, enforceRule);
        	System.out.println("Winning card: "+winningCard.toString());
			System.out.println("Player "+nextPlayer+" play: "+selected.toString()+" from ["
					+printHand(players[nextPlayer].getHand().getCardList())+"]");
			// Update the winning card
			if (winCard(selected, winningCard, trump)) {
				winner = nextPlayer;
				winningCard = selected;
			}
        }
        // Publish
     	publisher.notifySubscribers(trick.getCardList());
        // Results
		GraphicDisplayer.delay(600);
		trick.setView(displayer, new RowLayout(Whist.HIDE_LOCATION, 0));
		trick.draw();
		System.out.println("Winner: "+winner);
		displayer.setStatusText("Player " + nextPlayer + " wins trick.");
		updateScore(winner);
		return winner;
    }

    /**
     * Select and display trump suit.
     */
    protected void selectTrump() {
    	int x = random.nextInt(Suit.class.getEnumConstants().length);
        trump = Suit.class.getEnumConstants()[x];
        trumpActor = new Actor("sprites/"+Whist.TRUMP_IMAGES[trump.ordinal()]);
        displayer.addActor(trumpActor, Whist.TRUMP_LOCATION);
    }

    /**
     * Select a lead card depending on the type of the player.
     * @param id is the player's number
     */
    private Card selectLead(int id) {
        Player p = players[id];
        Card selected = selectCard(id, false);  // not enforce rule
        lead = (Suit) selected.getSuit();
        System.out.println("New trick: Lead Player = "+id+", Lead suit = "+selected.getSuit()+
        		", Trump suit = "+trump);
        System.out.println("Player "+id+" play: "+selected.toString()+" from ["+
        		printHand(p.getHand().getCardList())+"]");
        return selected;
    }

    /**
     * Select a card depending on the type of the player.
     */
    private Card selectCard(int id, boolean enforceRule) {
    	Player p = players[id];
    	p.removeSelected();  // remove the player's previous selection
        if (p instanceof Npc) {
        	displayer.setStatusText("Player "+id+" thinking...");
        	GraphicDisplayer.delay(((Npc) p).getThinkingTime());
            p.play(trump, lead, trick, players.length);
        } else {
        	displayer.setStatusText("Player 0 double-click on card to lead.");
            while (p.play(trump, lead, trick, players.length) == null) {
            	GraphicDisplayer.delay(100);
            };
        }
        Card selected = players[id].getSelected();
        // transfer to trick (includes graphic effect)
        trick.setView(displayer, new RowLayout(Whist.TRICK_LOCATION,(trick.getNumberOfCards()+2)*Whist.TRICK_WIDTH));
        trick.draw();
        selected.setVerso(false);
        checkViolation(id, enforceRule);
        selected.transfer(trick, true);
        return selected;
    }

    /**
     * Raise an error when a player had a card in lead suit or trump suit but did not play it.
     * @param id is the player's number
     */
    private void checkViolation(int id, boolean enforceRule) {
    	Player p = players[id];
    	Card selected = players[id].getSelected();
		if (selected.getSuit() != lead && p.getHand().getNumberOfCardsWithSuit(lead) > 0) {
			String violation = "Follow rule broken by player " + id + " attempting to play " + selected;
			//System.out.println(violation);
			if (enforceRule) {
				try {
					throw (new BrokeRuleException(violation));
				} catch (BrokeRuleException e) {
					e.printStackTrace();
					System.out.println("A cheating player spoiled the game!");
					System.exit(0);
				}
			} 
		}
    }

    private String printHand(List<Card> cards) {
        String out = "";
        for (int i = 0; i < cards.size(); i++) {
            out += cards.get(i).toString();
            if (i < cards.size() - 1) {
            	out += ",";
            }
        }
        return out;
    }

    /**
     * Return whether or not c1 beats c2.
     */
    protected static boolean winCard(Card c1, Card c2, Suit trump) {
    	boolean sameSuitGreaterRank = c1.getSuit() == c2.getSuit() &&
    			c1.getRankId() < c2.getRankId();  // Warning: Reverse rank order of cards (see comment on enum)
		boolean trumpSuit = c1.getSuit() == trump && c2.getSuit() != trump;
		return sameSuitGreaterRank || trumpSuit;
    }

    /**
     * Return the winner of the game if any.
     */
    protected Optional<Integer> getWinner() {
    	for (int i = 0; i < players.length; i++) {
    		if (winningScore == players[i].getScore()) {
    			return Optional.of(i);
    		}
    	}
    	return Optional.empty();
    }

	/**
	 * Re-deal hands, display graphics.
	 */
	protected void redeal(Hand[] newCards) {
		Player p;
		for (int i = 0; i < players.length; i++) {
			p = players[i];
			p.setHand(newCards[i]);
			if (p instanceof RealPerson) ((RealPerson) p).addListener();
		}
		displayHands();
	}

	protected void clearSubscribers() {
		publisher.clearSubscribers();
	}

	protected Actor getTrumpActor() {
		return trumpActor;
	}
}
