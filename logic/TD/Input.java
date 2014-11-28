package logic.TD;

public class Input extends Neuron {

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
	protected void update(double change) {
		rawvalue = rawvalue + change;
		value = rawvalue;
	}
}
