package game;

import ch.aplu.jcardgame.CardGame;

/**
 * This class has the responsibility of screen display.
 */
@SuppressWarnings("serial")
public class GraphicDisplayer extends CardGame
{
	public GraphicDisplayer() {
		super(700, 700, 30);  // Width, height, status height
		setTitle("Whist (V" + Whist.VERSION + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
		setStatusText("Initializing...");
	}
}
