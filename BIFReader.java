import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BIFReader {
	
	static Map<String, Node> nodes;
	static String folder = "C:\\Users\\nitas\\Downloads\\JavaBayes-0.347\\";
	static String filename;
	
	
	public BIFReader() {
		nodes = new HashMap<String, Node>();
	}
	
	//Since BIF format is same for all network files, i.e node names and states are in the beginning
	//and then comes the probabilities and the relations 
	private static void readnodes() {
	       String fileName = folder + filename;
	       
	        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
	            String line;
	            String nodeName = null;
	            Node node;
	            
	            //read the lines of the BIF File
	            while ((line = br.readLine()) != null) {
	                line = line.trim();
	            //if line has label "Variable", it means it 
	                if (line.startsWith("variable")) {
	                	
	                    String[] parts = line.split("\\s+");
	                    nodeName = parts[1];
	                    node = new Node(nodeName);
	                    //System.out.println(nodeName);
	                    //next line right after variable is of type.
	                    line = br.readLine();
	                    
	                    if (line.contains("type")) {
	                    	//System.out.println(line);
	                    	String[] states = line.split("\\s+\\[|\\]")[1].split("\\s+\\]|\\]");
	                    	states[0] = states[0].trim();
	                    	int noS = Integer.valueOf(states[0]);
	                    	//System.out.println(noS);
	                    	String tempS = line.split("\\{")[1].trim();
	                    	tempS = tempS.replace("};", "");	                    	
	                    	tempS = tempS.trim();
	                    	//System.out.println(tempS);
	                    	String[] stateNames = tempS.split(",");
	                    	node.setdStates(stateNames);
	                    	node.setnStates(noS);
	                    }
	                    nodes.put(nodeName, node);
	                }
	                
	            }
	            
	            // Reset the stream to the beginning. because in the previous loop, we have read
	            // whole file 
	            br.close();
	        
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	            
	            
	private static void readRrobabilities() {         
	       String fileName = folder + filename;
	       
	        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
	        	String line;
	            String name;
	            String nodeName = null;
	            String[] allStates = null;
            	HashMap<List<String>, Double> tempMap = new HashMap<List<String>, Double>();
	            
	            //read the lines of the BIF File
	            //now read the file again from beginning to load probabilities
	            while ((line = br.readLine()) != null) {
	                line = line.trim();
	            //if line has label "Variable", it means it 
	                if (line.startsWith("probability")) {
	                    
	                	String[] parts = line.split("\\(");
	                    nodeName = parts[1];
	                    parts = nodeName.split("\\)");
	                    nodeName = parts[0];
	                    //System.out.println(nodeName);
	                    //now we are left with (part inside)
	                    //first check if condition is available
	                    if(nodeName.contains("|")){
	                    	name = nodeName.split("\\|")[0].trim();
	                    	//System.out.println(name);
	                    	String parents = nodeName.split("\\|")[1].trim();
	                    	//parents = parents.trim();
	                    	//
	                    	//System.out.println(parents);
	                    	
	                    	String[] parent = parents.split(",");
	                    	
	                    	for(int l =0; l < parent.length; l++) {
	                    		parent[l] = parent[l].trim();
	                    	}
	                    	
	                    	//name of the node, fetch that node from the map
	                    	//store it in temp Node
	                    	Node tempNode = nodes.get(name);
	                    	tempNode.setParents(parent);
	                    	//tempNode.printNodeParents();
	                    	//now read and store the probabilities
                    		line = br.readLine();
	                    	while(! line.contains("}")) {
	                    		
	                    		//System.out.println(line); 	
								
	                    		if(line.contains("}")) {
	                    			break;
	                    		}else if (line.contains("(")) {
	                    			
	                                // Parse probabilities states for the current node	                    		
	                                String[] temp = line.split("\\)");
	                                temp[0] = temp[0].replace("(", "");
	                                
	                               // System.out.println(temp[0].trim());
	                                String[] states = temp[0].trim().split(",");
	                                allStates = new String[states.length + 1];
	                                
	                                for(int k = 0;k < states.length; k++) {
	                                	allStates[k] = states[k].trim();
	                                	//System.out.print(allStates[k] + " ");
	                                }
	                                //System.out.println("\n");
	                                //tempNode.printNodeStates();
	                                // Extract probability values
	                                temp[1] = temp[1].replace(";", "");
	                                //System.out.println(temp[1]);
	                                String[] probabilities = temp[1].trim().split(",");
	                                
	                                for (int i = 0; i < tempNode.getdStates().length; i++) {
	                                	allStates[allStates.length-1] = tempNode.getdStates()[i].trim();
	                                	
//		                                for(int p = 0;p < allStates.length; p++) {
//		                                	System.out.print(allStates[p] + " ");
//		                                }
	                                	//System.out.print(" "+probabilities[i]);
	                                	List<String> key = List.of(allStates);
	                                	tempMap.put(key, Double.parseDouble(probabilities[i]));
	                                    
	                                }
	                                //System.out.print("\n ");
	                    		}
	                    		
	                    		line = br.readLine();
	                    	}
//	                    	System.out.print("} Line contains");
//	                    	System.out.println("\n Printing Hashmap " );
//	                		for (Entry<List<String>, Double> set :
//	                            tempMap.entrySet()) {
//	                			
//	                			for(int i = 0; i < set.getKey().size(); i++) {
//	                				System.out.print(set.getKey().get(i));
//	                			}
//	                			System.out.print(" " +set.getValue());
//	                		}
//	                		
//	                    	System.out.print("\n");
                            tempNode.setProbabilities((HashMap<List<String>,Double>)tempMap.clone());
		                    nodes.put(name, tempNode);
                            tempMap.clear();
	                    }

	                    //if node name doesn't contain parents condition
	                    else {
	                    	//System.out.println("it doesn't contain |");
	                    	name = nodeName.trim();
	                    	//System.out.println(name);
	                    	Node tempNode = nodes.get(name);
	                    	//Added afterwards
	                    	tempNode.setRootNode(true);
	                    	//tempNode.printNode();
	                    	String [] tempName = new String[1];
	                    	String [] parentName = null;
	                    	
                    		line = br.readLine();
                    		if (line.contains("table")) {
                    			
                    			String temp = line.replace("table", "");
                    			temp = temp.replace(";", "");                    			
                    			//System.out.println(temp);
                    			allStates = temp.trim().split(",");
                    			                    		
                    			
                            for (int i = 0; i < tempNode.getdStates().length; i++) {
                            	tempName[0] =  tempNode.getdStates()[i].trim();
                            	
                            	List<String> key = List.of(tempName);
                            	allStates[i] = allStates[i].trim();
                                tempMap.put(key,Double.parseDouble(allStates[i]));
                            }

                    		}
                            tempNode.setProbabilities((HashMap<List<String>,Double>)tempMap.clone());
                            nodes.put(name, tempNode);
                            tempMap.clear();
	                    }
	                }
	            }
	        }catch (Exception e) {
	        	e.printStackTrace();
			}
	}
	            
	public ArrayList<Node> readNodesofBNet(String name){
		
		this.filename = name;
		readnodes();
		readRrobabilities();
		ArrayList<Node> bNodes = new ArrayList<Node>();
		
		for (Entry<String, Node>  set :
            this.nodes.entrySet()) {			
			bNodes.add(set.getValue());
		}
		
		return bNodes;
	}
	

    public static void main(String[] args) {
    	
    	readnodes();
    	readRrobabilities();
    	//System.out.println(" in main fucntion, read the file");
    	
    	for (Map.Entry<String, Node> entry : nodes.entrySet()) {    	    
    	    entry.getValue().printNode();
    	    entry.getValue().printProbabilites();
    	}
    	
    }
}
