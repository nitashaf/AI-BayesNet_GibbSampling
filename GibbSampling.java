import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Stack;

public class GibbSampling {
	
	BayesNet bayesNet;
	Graph graph;
	Random rand ;
	List<HashMap<String, String>> listSamples;
	HashMap<String, String> pSample;
	HashMap<String, List<String>> markovB;
	HashMap<String, Double[]> FinalProbabilities;
	
	int totalIteration = 50000;
	
	public GibbSampling(BayesNet net, Graph g) {
		this.bayesNet = net;
		this.graph = g;
		rand = new Random(); 
		listSamples = new ArrayList<HashMap<String, String>>();
		FinalProbabilities = new HashMap<String, Double[]>();
	}
	
	
	private String randomGenerator(HashMap<String, String> sampling, Node node ){
		
		//System.out.print("Inside the Random Generator");
		//node.printProbabilites();
		
		String[] parents = null;;
		if(node.getParents() != null) {
			parents = node.getParents();
		}
		
		String[] states = node.getdStates();
		Map<List<String>, Double> probabilities = node.getProbabilities();		
		Double[] p = new Double[states.length];
		String sState;
		
		List<String> pState = new ArrayList<String>();
		
		//randomly selected parent's state
		if(parents != null) {
			for(int i = 0; i < parents.length; i++) {
				//System.out.println("Parents " + parents[i] + "Selected state:"+ sampling.get(parents[i]));
				pState.add(sampling.get(parents[i]));			
			}
		}
		
		//getting the only selected parents probabilities
		for(int s= 0; s < states.length; s++ ) {			
			states[s] = states[s].trim();
			//System.out.println(states[s]);
			pState.add(states[s]);
			p[s] = probabilities.get(pState);	
			//System.out.println("Probability for s: "+p[s]);
			pState.remove(pState.size()-1);
		}
		
		//now select the random state using probabilities
		sState = selectState(states,p,rand.nextDouble());
		//System.out.println("Randomly selected State:"+ sState);
		return sState;
	}
	
    private String selectState(String[] states, Double [] probabilities, Double randomDouble) {
    	//System.out.println("Random number"+ randomDouble);
        double cumulativeProbability = 0.0;
        for (int i = 0; i < states.length; i++) {
            cumulativeProbability += probabilities[i];
            //System.out.println("Actual Probability"+ probabilities[i]);
            //System.out.println("cumulativeProbability"+ cumulativeProbability);
            if (randomDouble <= cumulativeProbability) {
                return states[i].trim();
            }
        }
        // This should not happen if probabilities are correctly defined, but handle it just in case
        return states[states.length - 1].trim();
    }
    
    public void query(String variable, HashMap<String, String> evidence) {
    	
 //   	System.out.println("Query for variable: "+variable );
    	//HashMap<String, String> temSample;
    	//int count = 0;
    	//find the number of of times we have selected the state for that node
    	//System.out.println("Size of Samples:" +listSamples.size());
    	
//    	for(int s= 0; s < listSamples.size(); s++) {
//    		temSample = listSamples.get(s);
//    		//System.out.println("Size Hashmap:" +temSample.size());
//    		if(temSample.containsKey(variable)) {
//    			if(temSample.get(variable).equals(state)) {
//    				//System.out.println("Count + the numbers");
//    				count++;
//    			}
//    		}
//    		else {
//    			System.out.println("Hashmap doesn't have the node:");
//    		}
//    	}
//    	    System.out.println("Total Count of the for "+variable+" is "+state +" is"+ count);
    	gibbSampling(evidence);
    	System.out.println("\nFinal new distribution for node "+ variable);
    	Double[] finalP = FinalProbabilities.get(variable);
    	if(finalP != null) {
    	for(int i = 0; i < finalP.length; i++) {
    		System.out.print(finalP[i]+ "  ");
    	}
    	}else {
    		Node tempNode = this.graph.getNodes().get(variable);
    		//for(int i = 0; i < tempNode.ge; i++) {
        	//	System.out.print(finalP[i]+ " ");
        }   	
    }
    
    private void calMarkovBlanket() {
    	
//    	System.out.print("Inside Markov Blanket Calculation");
    	markovB = new HashMap<String, List<String>>();
    	List <String> markNodes;
    	Map<String, ArrayList<Node>> lchildren = this.graph.getChidren();
    	ArrayList<Node> lchildNode;
    	String[] lnodeParents;
    	String[] lcnodeParent;
    	
		for (Entry<String, Node>  set :
	        this.graph.getNodes().entrySet()) {
			
			markNodes = new ArrayList<String>();
			
			
			//add parents
//			if(set.getValue().getParents() != null) {
//				lnodeParents = set.getValue().getParents();
//				for (String string : lnodeParents) {
//					markNodes.add(string);
//				}
//			}
			
			
			//add children
			if( lchildren.get(set.getKey()) != null ) {
				lchildNode = lchildren.get(set.getKey());
				for (Node node : lchildNode) {
					markNodes.add(node.getName());
					
					//add children's other parents
//					if(node.getParents() != null) {
//						lcnodeParent = node.getParents();
//						for (String string : lcnodeParent) {
//							if( ! string.equals(set.getKey()) ){
//								markNodes.add(string);
//							}
//						}
//					} 
				}
			}
			
			
			//Markov blanket for node is ready
			markovB.put(set.getKey(), markNodes);
			
		}
    }
    
    public void printMarkovBlanket() {
    	
    	List<String> l;
		for (Entry<String, List<String>>  set :
	        this.markovB.entrySet()) {
			
			System.out.println("\n"+set.getKey()+ ": ");
			l = set.getValue();
			for (String string : l) {
				System.out.print(string + " , ");
			}
		}
    }
    
    private void gibbSampling(HashMap<String, String> evidence) {
    	
    	
    	priorSampling(evidence);
    	calMarkovBlanket();
    	//variables other than evidence variable
    	ArrayList<String> nodes = new ArrayList<String>();
    	HashMap<String, String> localSample = (HashMap<String, String>)this.pSample.clone();
    	Map<String, Node> localNodes = this.graph.getNodes();
    	String randVar;
    	String lstate;
    	
    	
    	//System.out.println("\nSample Value: "+ localSample.get("HR"));
		for (Entry<String, String>  set :
	           this.pSample.entrySet()) {
			
			if(evidence == null || !(evidence.containsKey(set.getKey())) ){
					nodes.add(set.getKey());
			}
			
		}
		for(int i = 0; i < totalIteration; i ++) {		
		//randomly select variable other than evidence variables 
		randVar = nodes.get( rand.nextInt(0,nodes.size()) );
		
		//Generating samples on this local sample hashmap.
		//System.out.println("\nNode Selected for resample: "+ randVar);
		lstate = reSample(localSample, localNodes.get(randVar));
		//System.out.println("\nState Selected after resample: "+ lstate);
		localSample.put(randVar, lstate);
		}
		
    }
    
    private String reSample(HashMap<String, String> lSample, Node node) {
    	
    	//calculate conditional probability
		Map<String, Node> nodes = this.graph.getNodes();
		Node tempNode;
    	List<String> children = this.markovB.get(node.getName());
    	String[] cparents; 
    	String[] parents = node.getParents();
    	List<String> pState = new ArrayList<String>();
    	List<String> cpState = new ArrayList<String>();
		Map<List<String>, Double> probabilities = node.getProbabilities();
		Map<List<String>, Double> ChildProbabilities;
		List<Double> Np = new ArrayList<Double>();
		Double[] finalP = new Double[node.getnStates()];
		int state_Index = 0;
		Double sumConst = 0.0;
    	
    	
    	//take parents state from initialized sample
		if(parents != null) {
			for(int i = 0; i < parents.length; i++) {
				//System.out.println("Parents " + parents[i] + "Selected state:"+ sampling.get(parents[i]));
				pState.add(lSample.get(parents[i]));			
			}
			
		}
				
    	
    	//for each state of the node, adding probability given its parent
    	for (String state : node.getdStates()) {
//    		System.out.println("\nState of the node is :"+ state);
    		state = state.trim();    		
    		//System.out.println(state);
    		pState.add(state);
    		
//			System.out.println("Final State List to get the Probability conditioned on Parents");
//			for (String stringS : pState) {
//				System.out.print(stringS + " ");
//			}
//    		System.out.println("\nNode's Probability Conditioned on Parent is : "+ probabilities.get(pState));
    		Np.add(probabilities.get(pState));	
    		
    	    
    	        	    
    		//add probability of children given this state
    		if(! children.isEmpty()) {
    			for (String child : children) {
    				tempNode = nodes.get(child);
    				
    				if(tempNode.getParents() != null) {
    					cparents = tempNode.getParents();
    					for(int i = 0; i < cparents.length; i++) {
    						//System.out.println("Parents " + parents[i] + "Selected state:"+ sampling.get(parents[i]));
    						if(cparents[i].equals(node.getName())) {
//    							System.out.println("\nAdd this node state as parent's state to child node:" + tempNode.getName());
    							cpState.add(state);   							
    						}else {
    							cpState.add(lSample.get(cparents[i]));
    						}
    					}					
    				}
    				   
    				//when child's node all parents are set, then set the selected state of that child node
    				cpState.add(lSample.get(tempNode.getName()));
//    				System.out.println("\nThis child's "+ tempNode.getName() + " prior state is: "+ lSample.get(tempNode.getName()));

    				//just checking what state we have in the end
//    				System.out.println("Final State List to get the Probability of child");
//    				for (String stringS : cpState) {
//						System.out.print(stringS + " ");
//					}
    				
    				
    				//get the probability from the child's node probability.
    				ChildProbabilities = tempNode.getProbabilities();
    				//add it into the probabilities
    				Np.add(ChildProbabilities.get(cpState));
    				cpState.clear();
    			}
    		}
    		
    	    
    	    //multiplying all probabilities of in the numerator list 
    		Double temp = 1.0;
 
    	    for (Double nP : Np) {
//    	    	System.out.println("probabilities to be multiplied : "+ nP);
				temp *= nP;
			}
    	   // System.out.println("\nFinal Probability for: "+ node.getName() + " State: "+ state +" = "+ temp);
    	    finalP[state_Index] = temp; 
    	    sumConst += temp;
    	    
    		pState.remove(pState.size()-1);
    		
    		state_Index++;
    		Np.clear();
		}
    	
    	//final Probabilities
    	//System.out.println("\nFinal new distribution for node "+ node.getName());
    	for(int i = 0; i < finalP.length; i++) {
    		
 //   		System.out.println("Calculation: " + finalP[i] + " /const " + sumConst);
    		finalP[i] = (Double)(finalP[i])/(sumConst);
    		FinalProbabilities.put(node.getName(), finalP);
 //   		System.out.print(finalP[i]+ " ");
    	}
    	
		return selectState(node.getdStates(), finalP, rand.nextDouble());    	
    }
   
	
    
    //initialize the initial sample for all variables
	private void priorSampling(HashMap<String, String> evidence) {
		
		//System.out.println("In the Prior Sampling Method");        
		Node tempNode;
		Stack <Node> gStack;
		HashMap<String, String> sample;
        
			
			gStack = this.graph.getStack();
			//System.out.println(this.graph.getStack().size());
			sample = new HashMap<String, String>();		
		
			//if we are given evidence 
			if(evidence != null) {
				
				for (Entry<String, String>  set :
		           evidence.entrySet()) {
					System.out.println("\nEvidence: "+ set.getKey() + " state: "+ set.getValue());
				  sample.put(set.getKey(), set.getValue());			
				}
			}
			
			
			while (! gStack.isEmpty()) {       	
				tempNode = gStack.pop();
				//System.out.println("\n"+tempNode.getName());
				if(! sample.containsKey(tempNode.getName()) ) {
					
					//System.out.println("\n"+tempNode.getName());
					sample.put(tempNode.getName(),randomGenerator(sample, tempNode));   
				}
			}
		this.pSample = sample;
		//}
	}
	
	
	public static void main(String[] args) {
		
	}

}
