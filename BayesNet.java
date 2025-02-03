import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BayesNet {
	
	String name;
	static ArrayList<Node> nodes;
	static BIFReader bifR;
	
	
	public BayesNet() {
		nodes = new ArrayList<>();
		bifR = new BIFReader();
	}
	
	public static ArrayList<Node> getNodes() {
		return nodes;
	}
	
	public void createBayesNet(String name) {
		bifR = new BIFReader();
		this.name = name;
		String fileName = name+".bif";
		this.nodes = bifR.readNodesofBNet(fileName);
	}
	
	public void printBayesNet() {
		System.out.println("------BayesNet----------");
		System.out.println("Name: "+ this.name);
		System.out.println("No of nodes:"+ this.nodes.size());
		
		for(int n=0; n < this.nodes.size(); n++) {
			this.nodes.get(n).printNode();
			//this.nodes.get(n).printProbabilites();
		}
	}
	
	public static void main(String[] args) {
		
		BayesNet net;
		Graph g;
		GibbSampling gS;
		List<HashMap<String, String>> listEvidence = new ArrayList<HashMap<String, String>>();
		
		String[] bayesnets = {"alarm","child","hailfinder","insurance","win95pts"};
//		//String[] bayesnets = {"alarm"};
		HashMap<String, String> evidence = new HashMap<String, String>();
//		String [] query = {"PVSAT", "CO2", "InsSclInScen", "MakeModel", "GrbldOtpt"};
//		evidence.put("HR", "HIGH");	
//		evidence.put("CATECHOL", "HIGH");
//		listEvidence.add((HashMap<String, String>)evidence.clone());
//		evidence.clear();
//		evidence.put("LungParench", "Normal");
//		listEvidence.add((HashMap<String, String>)evidence.clone());
//		evidence.clear();
//		listEvidence.add(null);
//		evidence.put("RiskAversion", "Adventurous");
//		listEvidence.add((HashMap<String, String>)evidence.clone());
//		evidence.clear();
//		evidence.put("NetPrint", "No__Local_printer_");
//		listEvidence.add((HashMap<String, String>)evidence.clone());
//		evidence.clear();
//		//creating the ordering graph
//		for(int i = 0; i < bayesnets.length; i++) {
//			
//			net = new BayesNet();
//			net.createBayesNet(bayesnets[i]);
//			//net.printBayesNet();
//			g = new Graph();
//			g.createGraph(net);
//			gS = new GibbSampling(net, g);
//			if(! (listEvidence.isEmpty())) {
//				gS.query(query[i], listEvidence.get(i));
//			}else {
//				gS.query(query[i], null);
//			}
//			//gS.printMarkovBlanket();
//			
//		}
		
		net = new BayesNet();
		net.createBayesNet(bayesnets[0]);

		g = new Graph();
		g.createGraph(net);
		VarElimination vE = new VarElimination(net,g);
		evidence.clear();
		evidence.put("HR", "HIGH");	
		evidence.put("CATECHOL", "HIGH");
		vE.query("PVSAT", evidence);

		
		
		
	}

}
