import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;

public class Node {
	
	private String name;
    private String[] parents;
    private int nStates;
    private String[] dStates;
    //Added afterwards 
    private boolean rootNode;
    private Map<List<String>, Double> probabilities;
    private Map<List<String>, Double> Factors; 
    
    Node(){
    	this.probabilities = new HashMap<List<String>, Double> ();
    	this.Factors = new HashMap<List<String>, Double>();
    }
    
    Node(String nName){
    	this.setName(nName);
    	this.probabilities = new HashMap<List<String>, Double>();
    	this.Factors = new HashMap<List<String>, Double>();
    }
    
    public void setFactors(Map<List<String>, Double> factors) {
		Factors = factors;
	}
    
    public void setRootNode(boolean rootNode) {
		this.rootNode = rootNode;
	}
    
    public boolean getRootNode() {
    	return this.rootNode;
    }
    
    public int getnStates() {
		return nStates;
	}
    
    public void setnStates(int nStates) {
		this.nStates = nStates;
	}

	public String[] getdStates() {
		return dStates;
	}

	public void setdStates(String[] dStates) {
		this.dStates = dStates;
	}
	
	public String[] getParents() {
		return parents;
	}
	
	public void setParents(String[] parents) {
		this.parents = parents;
	}
	
	public Map<List<String>, Double>  getProbabilities() {
		return probabilities;
	}
	
	public void printNodeStates() {
		System.out.println("States: ");
		for(int k = 0; k < this.dStates.length; k++) {
			System.out.print(this.dStates[k] + " ");
		}
		System.out.print("\n");
	}
	
	
	public void printNode() {
		System.out.println("\n-----Node: "+this.getName() + "----");
		System.out.println("States: ");
		for(int k = 0; k < this.dStates.length; k++) {
			System.out.print(this.dStates[k] + " ");
		}
		System.out.print("\n");
		System.out.println("Parents: ");
		if(this.parents != null) {
		for(int p = 0; p < this.parents.length; p++) {
			System.out.print(this.parents[p] + " ");
		}
		}else {
			System.out.print("No Parent ");
		}
		
	}
	
	public void printNodeParents() {
		System.out.print("\n");
		System.out.println("Parents: ");
		if(this.parents != null) {
		for(int p = 0; p < this.parents.length; p++) {
			System.out.print(this.parents[p] + " ");
		}
		}else {
			System.out.print("No Parent ");
		}
	}
	
	public void printProbabilites() {
		System.out.print("\n");
		System.out.println("Probabilities: ");
		for (Entry<List<String>, Double>  set :
            this.probabilities.entrySet()) {
			
			for(int i = 0; i < set.getKey().size(); i++) {
				System.out.print(set.getKey().get(i));
			}
			System.out.print(" " +set.getValue());
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProbabilities(HashMap<List<String>, Double> probability) {

		this.probabilities = probability;
		
	}
    

}
