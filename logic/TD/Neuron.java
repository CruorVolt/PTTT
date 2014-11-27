package logic.TD;

import java.util.HashMap;

public class Neuron {
	HashMap<Neuron, Double> links;
	double value;
	double lastvalue;

	// outputs a real number equal to a weighted linear sum of inputs feeding into it, followed by a nonlinear sigmoidal "squashing" 
	public Neuron() {
		value = 0.0;
		lastvalue = 0.0;
		links = new HashMap<Neuron, Double>();
	}
	// update sum of links with amount of change in a link
	private void update(double change) {
		value = value + change;
	}
	// run updates on the next layer
	void feedForward() {
		double valueChange = value - lastvalue;
		for(Neuron neuron : links.keySet()) {
			// update the next layer with the weighted value change of this neuron
			neuron.update(valueChange*links.get(neuron));					
		}
		lastvalue = value;
	}
	// create a new outgoing link
	void addLink(Neuron neuron, Double weight) {
		links.put(neuron, weight);
	}
	// create a new outgoing link with a default weight
	void addLink(Neuron neuron) {
		Double weight = .5;
		links.put(neuron, weight);
	}
}