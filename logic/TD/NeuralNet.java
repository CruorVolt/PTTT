package logic.TD;
import java.util.ArrayList;
import java.util.HashMap;

import polar.game.Move;
import logic.state.TimeSlice;

public class NeuralNet {
	HashMap<Move, TimeSlice> slices;
	Neuron[] outputs;
	Input[] inputs;
	Neuron[] frontHiddenLayer;
	Neuron[] backHiddenLayer;
	double learnRate;
	double decay;
	// gradient descent used for learning rate
	double gradient;											
	int numFeatures;
	boolean maxplayer;
	
	// create new neural net with given # of features
	public NeuralNet(int numFeatures, boolean maxplayer) {
		gradient = -.1;
		outputs = new Neuron[numFeatures];
		inputs = new Input[numFeatures];
		frontHiddenLayer = new Neuron[numFeatures];
		backHiddenLayer = new Neuron[numFeatures];
		slices = new HashMap<Move, TimeSlice>();
		this.numFeatures = numFeatures;
		this.maxplayer = maxplayer;
		
		for(int i=0;i<numFeatures;i++) {
			inputs[i] = new Input();
			outputs[i] = new Neuron();
			frontHiddenLayer[i] = new Neuron();
			backHiddenLayer[i] = new Neuron();
		}
		// link all neurons to neurons in the previous layer
		for(int i=0;i<numFeatures;i++) {
			for(int j=0;j<numFeatures;j++) {
				outputs[i].addLink(backHiddenLayer[j]);
				backHiddenLayer[i].addLink(frontHiddenLayer[j]);
				frontHiddenLayer[i].addLink(inputs[j]);
			}
		}
	}
	private void backPropagation(TimeSlice slice) {
		// target features to train
		Double[] features = slice.evalFeatures(maxplayer);
		Double[] outputDeltas = new Double[features.length];
		Double[] backDeltas = new Double[features.length];
		Double[] frontDeltas = new Double[features.length];
		
		// track our deltas used to recalculate weights and biases
		outputDeltas = calcOuterDeltas(features);
		backDeltas = calcInnerDeltas(backHiddenLayer, outputDeltas);
		frontDeltas = calcInnerDeltas(frontHiddenLayer, backDeltas);
		
		//			outputs.get(i).updateBias(delta[i]*gradient);
		
	}
	private Double[] calcOuterDeltas(Double[] features) {
		Double[] delta = new Double[features.length];
		for(int i = 0;i<features.length;i++) {
			delta[i] = outputDelta(outputs[i].getValue(), features[i]);
		}
		return delta;
	}
	// given prior deltas, evaluate all hidden nueron deltas
	private Double[] calcInnerDeltas(Neuron[] layer, Double[] priors) {
		Double[] deltas = new Double[priors.length];
		for(int i = 0;i<priors.length;i++) {
			Neuron n = layer[i];
			deltas[i] = innerDelta(n.getValue(), priors, n.getWeights());
		}
		return deltas;
	}
	private Double outputDelta(Double output, Double target) {
		// derived delta for output nodes of sigmoidal NN
		return output*(1-output)*(output-target);
	}
	private Double innerDelta(Double output, Double[] priors, Double[] weights) {
		double delta = 0.;
		// calculate sum of weighted delta on all forward links
		for(int i = 0;i<priors.length;i++) {
			delta += priors[i]*weights[i];
		}
		delta = delta*(1-delta);
		return delta;
	}
	public void train() {
		
	}
}
