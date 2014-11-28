package logic.TD;

import java.util.HashMap;

public class Neuron {
	protected HashMap<Neuron, Double> links;
	protected double value;
	protected double lastvalue;
	protected double rawlastvalue;
	protected double rawvalue;
	private double bias;
	
	// outputs a real number equal to a weighted linear sum of inputs feeding into it, followed by a nonlinear sigmoidal "squashing" 
	public Neuron() {
		rawvalue = 1.0;
		rawlastvalue = 1.0;
		value = sigmoid(rawvalue);
		lastvalue = value;
		links = new HashMap<Neuron, Double>();
	}
	// update raw sum of links with amount of change in a link.
	protected void update(double change) {
		rawvalue = rawvalue + change;
		value = sigmoid(rawvalue);
	}
	// run updates on the next layer
	protected void feedForward() {
		double valueChange = value - lastvalue;
		for(Neuron neuron : links.keySet()) {
			// update the next layer with the weighted value change of this neuron
			neuron.update(valueChange*links.get(neuron));					
		}
		rawlastvalue = rawvalue;
		lastvalue = value;
	}
	// create a new outgoing link with a default weight
	protected void addLink(Neuron neuron) {
		// generate non-zero random weight
		Double weight = 0.;
		while(weight==0.) 
			weight = Math.random();
		links.put(neuron, weight);
	}
	// used during back-propagation
	protected void updateLink(Neuron neuron, Double change) {
		double weight = links.get(neuron) + change;
		links.put(neuron, weight);
	}
	protected Double getWeight(Neuron n) {
		return links.get(n);
	}
	// used during back-propagation
	protected void updateBias(double change) {
		this.bias += change;
	}
	protected double sigmoid(double x) {
		x += bias;
		double e = Math.E;
		double result = 1/(1+Math.exp(-x));
		return result;
	}
	protected double getValue() {
		return value;
	}
	protected Double[] getWeights() {
		return links.values().toArray(new Double[links.size()]);
	}
	// the derivative of the sigmoid
	private double prime(double x) {
		return sigmoid(x)*(1-sigmoid(x));
	}
}