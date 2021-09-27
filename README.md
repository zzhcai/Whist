# Summary

The Whist game play is briefly described below.

- The game starts with a hand of thirteen cards being dealt to each player. Then, a trump suit being randomly selected (displayed in the upper left of the window), and a player being randomly selected to start or lead.

- In each round, the game play proceeds as follows:
  1. The player taking the lead can play any card they wish from their hand to the centre; this card provides the basis for a trick.
  2. Play proceeds clockwise from the lead with each player playing one card in turn to follow the lead.
  3. Following players must play a card of the same suit as that lead or a card of the trump suit if they have one. If not, they may play any card they wish.
  4. Once every player has played one card to the centre, the winner of the round is the player who has played the highest card of the lead suit (i.e., the suit of the card that the first played). If there are other players who played a card in the trump suit, the winner will be the player who played the highest card in the trump suit instead.
  5. The winner receives one point for winning the trick
  6. The winner takes the lead for the next round starting a new trick. 
  7. The game play ends as soon as a player has received 24 points and that play wins the game. If the players have played all their cards without anyone winning, play continues exactly as described from 1 starting with new deal.

<br/>

An NPC will do 2 steps, i.e., cards filtering and card selecting.

- Naïve legal approach: The NPC will attempt to select the cards that consistent with the rules, i.e., the cards in the lead suit and the cards in the trump suit. If the NPC does not have cards in the lead suit nor in the trump suit, it will select all cards in the hand (i.e., no filtering). If the NPC takes the lead, then no filtering.

- Trump saving approach: This approach is similar to the naive legal approach, but the NPC will try to save the card in the trump suit. To do so, the NPC will attempt to select the cards in the lead suit. If the NPC does not have the cards in the lead suit, then the NPC will select the cards in the trump suit. Same as the naïve legal approach, if the NPC does not have cards in the lead suit nor the trump suit, it will select all cards in the hand (i.e., no filtering). If the NPC takes the lead, then no filtering.

- Random selection: The NPC will randomly select the card to play.

- Highest rank selection: The NPC will select the card with the highest rank to play. If many cards are applicable for a selection, NERD Games Inc. is open to your idea on how to select the card.

- Smart selection: The NPC will record the relevant information (e.g., collecting the cards that been already played) and makes a reasonable selection. For example, the NPC may consider the chance whether it will win or not. If not, it will violate the rules by selecting card with the lowest rank. For this approach, NERD Games Inc. is open to your ideas for the smart selection approach. However, NERD Games Inc. does not expect a sophisticated algorithm. At least, this approach should make a decision based on the collected information and it should look smarter than the combination of pre-defined approaches above.


# Design Diagrams

See [uml_class.pdf](https://github.com/zzhcai/Whist/blob/main/uml_class.pdf) and [uml_sequence.pdf](https://github.com/zzhcai/Whist/blob/main/uml_sequence.pdf) for details.
