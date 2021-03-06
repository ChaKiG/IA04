import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class Main {
	
	public static void main(String[] args) {
		/*
		// Etape 1
		BaseConnaissances b = new BaseConnaissances();
		//Recherche d'une entit�
		b.runSelectQuery("requestPersonName");
		//Recherche des classes
		b.runSelectQuery("requestClass");
		//Recherche des propri�t�s
		b.runSelectQuery("requestProperty");
		//Recherche des propri�t�s
		b.runSelectQuery("requestDomain");		
		
		//Etape 2
		BaseExterieur.query("requestExterieurPays");
		*/
		//Etape 3
		String MAIN_PROPERTIES_FILE = "main_properties";
		String SEC_PROPERTIES_FILE = "sec_properties";
		Runtime rt = Runtime.instance();
		Profile p_main = null;
		Profile p_sec = null;
		try {
			p_main = new ProfileImpl(MAIN_PROPERTIES_FILE);
			p_sec = new ProfileImpl(SEC_PROPERTIES_FILE);
			rt.createMainContainer(p_main);
			
			AgentContainer ac = rt.createAgentContainer(p_sec);
			AgentController KB = ac.createNewAgent("KB", "KB", null);
			AgentController propagKB = ac.createNewAgent("PropagateSparql", "PropagateSparql", null);
			AgentController GA = ac.createNewAgent("GeodataAgent", "GeodataAgent", null);
			AgentController propagGA = ac.createNewAgent("PropagateGeoSparql", "PropagateGeoSparql", null);
			KB.start();
			propagKB.start();
			GA.start();
			propagGA.start();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
	
}
