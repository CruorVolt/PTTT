package logic.TD;
import java.util.ArrayList;
import java.util.HashMap;

import polar.game.Move;
import logic.TimeSlice;

public class NeuralNet {
	HashMap<Move, TimeSlice> slices;
	ArrayList<Output> outputs;
	ArrayList<Input> inputs;
	ArrayList<ArrayList<Neuron>> hiddenlayers;
	double learnRate;
	double decay;
	
	enum playState {
		win, lose, draw, active;
	}
	
	public NeuralNet() {
		outputs = new ArrayList<Output>();
		inputs = new ArrayList<Input>();
		hiddenlayers = new ArrayList<ArrayList<Neuron>>();
		slices = new HashMap<Move, TimeSlice>();
	}
}
