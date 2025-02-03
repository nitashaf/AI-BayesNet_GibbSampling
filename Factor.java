import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Factor {
	private String name;
	private String[] joinNames;
	private Map<List<String>, Double> probabilities;
	
	
	Factor(String name, Map<List<String>, Double> probabilities) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.probabilities = probabilities;
	}
	
	Factor() {
		this.probabilities = new HashMap<List<String>, Double>();
	}
	
	Factor(String name){
		this.name = name;
		this.probabilities = new HashMap<List<String>, Double>();
	}
	public void setProbabilities(Map<List<String>, Double> probabilities) {
		this.probabilities = probabilities;
	}
	
	public Map<List<String>, Double> getProbabilities() {
		return probabilities;
	}

}

