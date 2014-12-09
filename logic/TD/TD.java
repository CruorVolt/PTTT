package logic.TD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import logic.state.GameState;
import logic.state.TimeSlice;

public class TD {
	private double weights[][][];
	private double outputweights[];
	private double[][] layers;
	private double estimate;
	private double decay;
	private double gradient;
	private boolean maxplayer;
	private final int numFeatures = 9;
	// load stored weights
	public TD(boolean maxplayer, String filename) {
		decay = 1./9.;
		this.maxplayer = maxplayer;
		this.weights = loadWeights(filename);
		outputweights = new double[numFeatures];
		layers = new double[3][9];
		gradient = -.2;
		estimate = 0;
		// initialize all weights and bias inputs at random, between 0 and 1.
		for(int l=0;l<2;l++)
			for(int j=0;j<numFeatures;j++) {
				outputweights[j] = 0.;
				while(outputweights[j]==0.) 
					outputweights[j] = Math.random();
				layers[l][j] = 0.;
				layers[l+1][j] = 0.;
			}
	}
	private double[][][] loadWeights(String filename) {
		double[][][] weights = new double[3][numFeatures][numFeatures];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			for(int l=0;l<2;l++)
				for(int j=0;j<numFeatures;j++) {
					String line = reader.readLine();
					String[] vals = line.split(",");
					for(int i=0;i<numFeatures;i++) {
						double eval = Double.parseDouble(vals[i]);
						weights[l][j][i] = eval;
					}
				}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return weights;
	}
	protected TD(boolean maxplayer) {
		decay = 1./9.;
		this.maxplayer = maxplayer;
		weights = new double[2][numFeatures][numFeatures];	
		outputweights = new double[numFeatures];
		layers = new double[3][9];
		gradient = -.2;
		estimate = 0;
		// initialize all weights and bias inputs at random, between 0 and 1.
		for(int l=0;l<2;l++)
			for(int j=0;j<numFeatures;j++) {
				for(int i=0;i<numFeatures;i++) {
					weights[l][j][i] = 0.;
					while(weights[l][j][i]==0.) 
						weights[l][j][i] = Math.random();
				}
				outputweights[j] = 0.;
				while(outputweights[j]==0.) 
					outputweights[j] = Math.random();
				layers[l][j] = 0.;
				layers[l+1][j] = 0.;
			}
	}
	// returns the result of the sigmoid function on x
	protected double sigmoid(double x) {
		double result = 1/(1+Math.exp(x*-1));
		return result;
	}
	// feed state values into network and derive resulting estimate
	protected double feedForward(double[] state) {
		//double bias = 100;
		// layers of the neural net, starting with the input layer: state
		layers[0] = state;
		//layers[0][8] += bias;		// add bias to end states.
		estimate = 0.;
		// iterate through weights and update layers
		for(int l=1;l<3;l++) {
			for(int j=0;j<numFeatures;j++) {
				// initialize value of the node before summing.
				layers[l][j] = 0.;
				for(int i=0;i<numFeatures;i++) {
					// each node of the next layer is the sum of sigmoid(node)*weight for each node/weight in the previous layer 
					layers[l][j] += sigmoid(layers[l][i])*weights[l-1][i][j];
				}
			}
		}
		for(int i=0;i<numFeatures;i++)
		estimate += sigmoid(layers[2][i])*outputweights[i];
		estimate = estimate/numFeatures;
		return estimate;
	}
	// extract state values and feed them into network to be used by DifferencePlayerStyle
	public double valueOf(GameState state) {
		TimeSlice slice = new TimeSlice(state);
		double[] features = slice.baseFeatures(maxplayer);
		return feedForward(features);
	}
	// extract state values and provide feedback for training
	public void feedback(GameState state, double reward) {
		TimeSlice slice = new TimeSlice(state);
		feedback(slice, reward);
	}
	// extract state values and provide feedback for training
	public void feedback(TimeSlice slice, double reward) {
		double[] features = slice.baseFeatures(maxplayer);
		backprop(features, reward);
	}
	//performs backpropagation to update weights in the network
	protected void backprop(double[] features, double target) {
		double expected = feedForward(features);
		double[] backDelta = new double[numFeatures];
		double[] frontDelta = new double[numFeatures];
		
		// get deltas from last layer to first layer
		double outDelta = outDelta(expected, target);
		backDelta = calcInnerDeltas(1, outDelta);
		frontDelta = calcInnerDeltas(0,backDelta);
		double outputChange = gradient*outDelta;
		double[][] layerChange = new double[numFeatures][numFeatures];
		
		// update final weights first
		for(int i=0;i<numFeatures;i++) {
			outputweights[i] += outputChange;
		}
		// update weights between hidden layers and input layer
		for(int l=1;l>=0;l--)
			for(int j=0;j<numFeatures;j++) {
				for(int i=0;i<numFeatures;i++) {
					if(l==1) {
						layerChange[j][i] = gradient*backDelta[i]+decay*outputChange;
						weights[l][j][i] += layerChange[j][i];
					}
					else if(l==0) {
						weights[l][j][i] += gradient*frontDelta[i]+decay*layerChange[j][i];
					}
				}
			}
	}
	private double[] calcInnerDeltas(int layer, double[] priors) {
		double[] deltas = new double[priors.length];
		for(int i = 0;i<priors.length;i++) {
			deltas[i] = innerDelta(layers[layer][i], priors, weights[layer][i]);
		}
		return deltas;
	}
	private double[] calcInnerDeltas(int layer, double prior) {
		double[] deltas = new double[numFeatures];
		for(int i = 0;i<numFeatures;i++) {
			deltas[i] = innerDelta(layers[layer][i], prior, outputweights[i]);
		}
		return deltas;
	}
	private double innerDelta(double output, double[] priors, double[] weights) {
		double delta = 0.;
		// calculate sum of weighted delta on all forward links
		for(int i = 0;i<priors.length;i++) {
			delta += priors[i]*weights[i];
		}
		delta = delta*output*(1-output);
		return delta;
	}
	private double innerDelta(double output,double prior, double weight) {
		// calculate weighted delta
		double delta = prior*weight;
		delta = delta*output*(1-output);
		return delta;
	}
	private double outDelta(double output, double target) {
		// derived delta for output nodes of sigmoidal NN
		return output*(1-output)*(output-target);
	}
	// get weights so that the network learning can be preserved
	public double[][][] getWeights() {
		return weights;
	}
}
