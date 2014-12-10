PTTT
====
Anders Lundgren, Christina Dunning

Polar Tic-Tac-Toe is a two-player extensive form game. Players ttake turns making a move on the board with their mark: an X or an O.
This game is much like regular tic-tac-toe except that the board is circular, consisting of 4 rings and 6 axis. 
Moves are allowed anywhere a ring and axis intersect, except for the center.
After the first move, all moves must be placed adjacent to another.
A win must have 4 consecutive marks from a single player, either adjacent along an axis, adjacent along a ring, or diagonally adjacent.

Where to find the core AI implementations:

Minimax/Alpha-Beta:     Both algorithms are in logic/Search.minimax()
Reinforcement Learning: The logic/TD package encapsulates the neural net and associated functions.
Classification:         Decision tree is built in logic/DecisionTree with some feature extraction in logic/Classifier.
Heuristic:              Scoring a game state is done in the two functions logic/Heuristic.evaluate() and logic/Heuristic.evaluateMinMax().
Resolution:             Methods for win-checking via resolution and unification are in logic/Status
Player Behaviors:       Each class in polar/game/styles defines a specific player behavior by implementing the getMove() method.

How to run:
Run polar/gui/start.main to launch the primary interface and play games.
Run polar/test/TestRuns.main to play predefined test games and see resolution operations.
Run logic/DecisionTree.main to test the classifier on new game states
