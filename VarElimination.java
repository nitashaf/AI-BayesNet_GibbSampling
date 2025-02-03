import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

public class VarElimination {
	BayesNet bayesNet;
	Graph graph;
	Map<String, Factor> lfactors;
	Map<List<String>,Factor> sFactors;
	
	
	public VarElimination(BayesNet net, Graph g) {
		this.bayesNet = net;
		this.graph = g;
		this.lfactors = new HashMap<String, Factor>();
		this.sFactors = new HashMap<List<String>,Factor>();
	}
	
	
	private void variableElimination(String variable, HashMap<String, String> evidence) {
		
		Stack <Node> vStack = this.graph.getStack();
		ArrayList<String> qeNodes = new ArrayList<String>();
		String[] parents;
		List<String> summedName;
		ArrayList<Node> nodes = new ArrayList<Node>();
		
		Node temNode;
		
		if(evidence != null) {
			instantiate(evidence);
		}
		
		//make a list of query and evidence nodes
		qeNodes.add(variable);
		if(evidence != null) {
			 for (Entry<String, String>  set :
				 evidence.entrySet()) {
				 qeNodes.add(set.getKey());
			 }
		}
		
		//getting all nodes which are not part of query or evidence
		while(! vStack.empty() ) {
			temNode = getNode(vStack, qeNodes);
			
			//node itself
			if(temNode != null) {
				
				parents = temNode.getParents();
				//if tempNode has parents
				
				if(parents != null) {
					//nodes parents
					for (String parent : parents) {

						//check if the parent is already summed 
						summedName = summedOut(parent);
						if(summedName != null) {
							//use this probability
						}else {
							
							//else check if the parent is in initial factorsList
							if(lfactors.containsKey(parent)) {
								//use this probabilities
							}else {
								//use the original probability
							}
						}
					}					
				} //end if it has parents
				
				//add node itself
				nodes.add(temNode);

			}
			
			calJointProbability(temNode);
		}

		
	}
	
	//lets assume it doesn't have parent
	private void calJointProbability(Node node) {
		
		Map<List<String>, Double> Nprobabilities = node.getProbabilities();
		Map<String, ArrayList<Node>> chidren = this.graph.getChidren();
		ArrayList<Node> child;
		Map<List<String>, Double> jointP = new HashMap<List<String>, Double>();
		String[] nodeStates = node.getdStates();
		Map<List<String>, Double> Cprobabilities;
		List<String> tempState = new ArrayList<String>();
		List<String> tempCState;
		String[] cParents;
		Map<List<String>, Double> tempProb = new HashMap<List<String>, Double>() ;
		
		HashMap<String, List<String>> CParents = new HashMap<String, List<String>>();
		HashMap<String, Map<List<String>, Double>> CselectedProbabilities = new HashMap<String, Map<List<String>, Double>>();
		ArrayList<String> children = new ArrayList<>();
		
		
		for (String state : nodeStates) {
			
			tempState.clear();
			tempState.add(state);
			Double prob = Nprobabilities.get(tempState);
			
			//if node has children
			child = chidren.get(node.getName());

			if(child != null) {
				for (Node ch : child) {
					children.add(ch.getName());
					Cprobabilities = ch.getProbabilities();
					//get the child's parents and find this node's position
					cParents = ch.getParents();
					int index = Integer.MAX_VALUE;
				    for (int i = 0; i < cParents.length; i++) {
				            if (cParents[i].equals(node.getName())) {
				                index = i; // Update index if the string is found
				                break; // Exit the loop since the string is found
				            }
				    } 
				    
					
				    if(index != Integer.MAX_VALUE) {
				    	tempProb.clear();
					//get child node probabilities related to this state				    	
						for (Entry<List<String>, Double>  set :
							Cprobabilities.entrySet()) {
							
							tempCState = set.getKey();
							 if( tempCState.get(index).contains(state) ) { //state of node
								 tempProb.put(tempCState, set.getValue());
							 }
							
						}						
				    }
					
				    //add child's parents and selected probabilities to data structure
				    CParents.put(ch.getName(), Arrays.asList(cParents));
				    CselectedProbabilities.put(ch.getName(), tempProb);
				    ArrayList<Double> results = new ArrayList<>();
				    multiplyProbability(CselectedProbabilities, results, 0, 1.0,children);	
				    
				    
			        for (double value : results) {
			            System.out.println(value);
			        }
			        
				}
				//now we have all the probabilities related to state in the children.
				//create a joint table
			}
			
		}

		
		
	}
	
	
	
    private static void multiplyProbability(HashMap<String, Map<List<String>, Double>> probabilities,
    		List<Double> resultList, int currentIndex, double currentProduct,ArrayList<String> children ) {
        // Base case: if currentIndex is equal to the number of arrays, add the product to the result list
        if (currentIndex == children.size()) {
            resultList.add(currentProduct);
            return;
        }

        // Multiply current array's elements with the current product
        String name = children.get(currentIndex);
        Map<List<String>, Double> map = probabilities.get(name);
        
		for (Entry<List<String>, Double>  set :
			map.entrySet()) {
        	multiplyProbability(probabilities, resultList, currentIndex + 1, currentProduct * set.getValue(), children);
        }
    }
	
	
	
	
	
	private List<String> summedOut(String node){
		
		List<String> tempsum;
		for (Entry<List<String>, Factor>  set :
			sFactors.entrySet()) {
			tempsum = set.getKey();
			if(tempsum.contains(node)) {
				 return tempsum;
			}
		}
		return null;
	}
	
	private Node getNode(Stack<Node> vStack, ArrayList<String> qeNodes) {
		Node temNode;
		//get all variable from stack, 
		if(vStack.empty()) {
			return null;
		}else {
			temNode = vStack.pop();
			//if temp Node is not in Query and evidence list
			for (String string : qeNodes) {
				if(string.equals(temNode.getName())) {
					getNode(vStack, qeNodes);	
				}
			}
			return temNode;
		}		
	}
	
	
	//instantiate with given evidence the factors having this evidence
	private void instantiate(HashMap<String, String> evidence) {
		
		 Map<String, Node> nodes = this.graph.getNodes();
		 Map<String, ArrayList<Node>> chidren = this.graph.getChidren();
		 ArrayList<Node> childNodes; 
		 Node tempNode;
		 Map<List<String>, Double> nodeProb;
		 String[] tempParents;
		 List<String> probValues;
		 Map<List<String>, Double> nodeFact = new HashMap<List<String>, Double>();
		 Factor f;
		 
		 //for all evidence nodes
		 for (Entry<String, String>  set :
			 evidence.entrySet()) {
			 	
			 	//node of the evidence
			 	//get probabilities and filter only the probabilities with evidence
				nodeFact.clear();				
			 	tempNode = nodes.get(set.getKey());
			 	nodeProb =  tempNode.getProbabilities();
				 for (Entry<List<String>, Double>  prob :
					 nodeProb.entrySet()) {
					 
					 probValues = prob.getKey();
					 if( probValues.get(probValues.size()-1).contains(set.getValue()) ) {
						 nodeFact.put(probValues, prob.getValue());
					 }
				 }
			 	f = new Factor(set.getKey());
			 	f.setProbabilities(nodeFact);
			 	lfactors.put(set.getKey(),f);
			 	
			 	//create factor for its children
			 	
				childNodes = chidren.get(set.getKey());
				//this evidence node has children
				if(childNodes != null) {
					for (Node childnode : childNodes) {
						nodeFact.clear();
						//get the index of the evidence as parent for this child
						tempParents = childnode.getParents();
						int index = Integer.MAX_VALUE;
					    for (int i = 0; i < tempParents.length; i++) {
					            if (tempParents[i].equals(set.getKey())) {
					                index = i; // Update index if the string is found
					                break; // Exit the loop since the string is found
					            }
					    }   
						
					    if(index != Integer.MAX_VALUE) {
						//get child node probabilities
					 	nodeProb =  childnode.getProbabilities();
						 for (Entry<List<String>, Double>  prob :
							 nodeProb.entrySet()) {
							 
							 probValues = prob.getKey(); //list of states
							 if( probValues.get(index).contains(set.getValue()) ) { //state of evidence
								 nodeFact.put(probValues, prob.getValue());
							 }
						 }
						 
						 f = new Factor(childnode.getName());
						 f.setProbabilities(nodeFact);
						 lfactors.put(set.getKey(),f);
						 
						 
					    }else {
					    	System.out.println("This child doesn't have the evidence as its parent");
					    }
					    
					}
				}
				
		 }		 

		 
	} 
	
	
    public void query(String variable, HashMap<String, String> evidence) {
    	
    	variableElimination(variable, evidence);
    	//System.out.println("\nFinal new distribution for node "+ variable); 	
    }
	
	
}
