package game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.io.FileReader;
import java.util.*;

public class Whist
{
	// Constants
	/** The current game version */
	public static final String VERSION = "1.1";
	/** The name of file that specifies npc' gaming logic */
	public static final String PROP_FILENAME = "whist.properties";
	/** The file paths of trump images */
	public static final String TRUMP_IMAGES[] = {"bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif"};
	public static final Font BIG_FONT = new Font("Serif", Font.BOLD, 36);
	public static final Color FONT_COLOR = Color.WHITE;
	public static final int HAND_WIDTH = 400;
	public static final int TRICK_WIDTH = 40;
    public static final Location TRICK_LOCATION = new Location(350, 350);
    public static final Location WIN_LOCATION = new Location(350, 450);
    public static final Location HIDE_LOCATION = new Location(-500, -500);
    public static final Location TRUMP_LOCATION = new Location(50, 50);
	public static final Location[] HAND_LOCATIONS = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };
	public static final Location[] SCORE_LOCATIONS = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            new Location(650, 575)
    };

	private Random random;
	/** Whether or not there involves a real-person player */
	private boolean isInteractive;
	/** Whether or not a card's verso is facing up by default  */
	private boolean versoUp;
	/** The number of cards in a player's hand */
	private int numCards;
	/** The winning score for a game */
	private int winningScore;
	/** The number of non-person-characters */
	private int numNpc;
	/** The halting time when an npc decides to play a card */
	private int npcThinkingTime;
	/** The array that stores all npcs filtering strategies */
	private String[] npcFilteringStrategies;
	/** The array that stores all npcs selecting strategies */
	private String[] npcSelectingStrategies;
	/** Whether or not to enforce rules checking */
	private boolean enforceRule;

	/** Assign all responsibilities of screen display to this object */
	private final GraphicDisplayer displayer;
	/** The factory for players creation */
	private PlayerFactory playerFactory;
	private Player[] players;
	private GameRound round;

	public static void main(String args[]) {
		new Whist();
	}

	public Whist() {
		displayer = new GraphicDisplayer();
		setUpProperties(PROP_FILENAME);
		createPlayers(dealCards());
		// Game setup ends
		playRound();
		displayWin(round.getWinner().get());
	}

	private void setUpProperties(String PropFilename) {
		Properties whistProperties = new Properties();
		// Read properties
		FileReader inStream = null;
		try {
			inStream = new FileReader(PropFilename);
			whistProperties.load(inStream);
			if (inStream != null) inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		// Setup properties
		long seed = (long)Integer.parseInt(whistProperties.getProperty("Seed"));
		random = new Random(seed);
		isInteractive = Boolean.parseBoolean(whistProperties.getProperty("Interactive"));
		versoUp = Boolean.parseBoolean(whistProperties.getProperty("VersoUp"));
		numCards = Integer.parseInt(whistProperties.getProperty("NumCards"));
		winningScore = Integer.parseInt(whistProperties.getProperty("WinningScore"));
		numNpc = Integer.parseInt(whistProperties.getProperty("NumNpc"));
		npcThinkingTime = Integer.parseInt(whistProperties.getProperty("NpcThinkingTime"));
		npcFilteringStrategies = new String[numNpc];
		npcSelectingStrategies = new String[numNpc];
		for (int i = 0; i < numNpc; i++) {
			npcFilteringStrategies[i] = whistProperties.getProperty("Npc"+(i+1)+"Filtering");
			npcSelectingStrategies[i] = whistProperties.getProperty("Npc"+(i+1)+"Selecting");
		}
		enforceRule = Boolean.parseBoolean(whistProperties.getProperty("EnforceRule"));
	}

	private Hand[] dealCards() {
		int x, numPlayer = numNpc + (isInteractive ? 1 : 0);
		// Dealing out using seed
		Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
		Hand pack = deck.toHand(false);
		Hand[] startHands = new Hand[numPlayer];
		for (int i = 0; i < numPlayer; i++) {
			startHands[i] = new Hand(deck);
			for (int j = 0; j < numCards; j++) {
				x = random.nextInt(pack.getNumberOfCards());
				Card dealt = pack.get(x);
				dealt.removeFromHand(false);
				startHands[i].insert(dealt, false);
			}
		}
		// Sort hands
        for (int i = 0; i < numPlayer; i++) {
        	startHands[i].sort(Hand.SortType.SUITPRIORITY, true);
        	if (i == 0 && isInteractive) {
        		startHands[0].setVerso(false);
        	} else {
        		startHands[i].setVerso(versoUp);
        	}
        }
        return startHands;
	}

	private void createPlayers(Hand[] startHands) {
		int extra = isInteractive ? 1 : 0;
		players = new Player[numNpc+extra];
		playerFactory = PlayerFactory.getInstance();
		if (isInteractive) {
			players[0] = playerFactory.getRealPerson(startHands[0]);
		}
		for (int i = extra; i < numNpc+extra; i++) {
			players[i] = playerFactory.getNpc(npcFilteringStrategies[i-extra], npcSelectingStrategies[i-extra],
					startHands[i], npcThinkingTime, random);
		}
	}

	private void playRound() {
		// Play rounds until someone wins
		int lastRoundWinner = -1;
		int roundCount = 0;
		round = new GameRound(random, displayer, enforceRule, players, winningScore);
		round.selectTrump();
		while (!round.getWinner().isPresent()) {
			lastRoundWinner = round.playRound(lastRoundWinner);
			roundCount++;
			// Played all the cards without anyone winning
			if (!round.getWinner().isPresent() && roundCount % numCards == 0) {
				displayer.removeActor(round.getTrumpActor());
				round.selectTrump();
				round.redeal(dealCards());
				round.clearSubscribers();  // clean the history of trick observers
			}
		}
	}

	/** 
	 * Display a win message.
	 */
	private void displayWin(Integer winner) {
    	displayer.addActor(new Actor("sprites/gameover.gif"), Whist.WIN_LOCATION);
    	displayer.setStatusText("Game over. Winner is player: " + winner);
    	displayer.refresh();
    }
}
