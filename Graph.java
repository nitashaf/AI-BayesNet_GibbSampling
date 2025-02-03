import java.util.*;
import java.util.Map.Entry;

public class Graph {
	 String name;
	 //<node name, node, children nodes>
	 private Map<String, Node> nodes;
	 private Map<String, ArrayList<Node>> chidren;
	 private Stack<Node> stack;

	 Graph() {
	    nodes = new HashMap<>();
	    chidren = new HashMap<>();
	 }
	 
	 public Map<String, Node> getNodes() {
		return this.nodes;
	}
	public Map<String, ArrayList<Node>> getChidren() {
		return chidren;
	}

	 private void addVertex(Node node) {
		 //add node to nodes map
		 nodes.put(node.getName(),node);
	    //create the node, without children
		 chidren.put(node.getName(),null);
	 }
	 
	 private void addEdge(Node node) {
		    
		String[] parents;
		ArrayList<Node> childList;
		
	    //if node has parents in it, we need to make this node the 
	    //child of all those nodes
	    if(node.getParents() != null) {
	    	//System.out.println("This node has parents");
	    	parents = node.getParents();
	    	for (String st : parents) {	    		
	    		//System.out.println(st);
	    		//add this node in children;
	    		//fetch the existing children of this parent
	    		if(chidren.get(st) != null) {
	    			childList = chidren.get(st);
	    			childList.add(node);
	    		}else {
	    			childList = new ArrayList<Node>();
	    			childList.add(node);
	    		}
	    		chidren.put(st, childList);
			}	
	    } 
	 }
	    

	 private void topologicalSortUtil(Node node, Set<Node> visited, Stack<Node> stack) {
		 //System.out.println("Node name: "+ node.getName());
	        visited.add(node);
	        //if vertex has children
	        if(this.chidren.get(node.getName()) != null) {
	        	//System.out.println(node.getName()+" has child");
	        for (Node child : this.chidren.get(node.getName())){
	            if (!visited.contains(child)) {
	                topologicalSortUtil(child, visited, stack);
	            }
	        }
	        }
	        if(!stack.contains(node)) {
	        stack.push(node);
	        }
	    }

	 	private void topologicalSort() {
	        stack = new Stack<>();
	        Set<Node> visited = new HashSet<>();

			for (Entry<String, Node>  set :
	            this.nodes.entrySet()) {
				
	            if (!visited.contains(set.getValue())) {
	                topologicalSortUtil(set.getValue(), visited, stack);
	            }
			}

//	        //Print nodes in topological order
//			System.out.println("\nStack\n");
//	        while (!stack.isEmpty()) {
//	        	
//	            System.out.print(stack.pop().getName() + " ");
//	        }
//	        System.out.println("\n");
	    }
	 	
	 	//returns a copy of the stack
	 	public Stack<Node> getStack() {
	 		return (Stack<Node>)this.stack.clone();
		}

	 
	 public void createGraph(BayesNet net) {
		 this.name = net.name;
		 
		 for(Node eachnode: net.getNodes()) {
			 addVertex(eachnode);
		 }
		 for(Node eachnode: net.getNodes()) {
			 addEdge(eachnode);
		 }
		 
		 topologicalSort();
	 }
	    
	 public static void main(String[] args) {
		
		 
	 }
	 
}
