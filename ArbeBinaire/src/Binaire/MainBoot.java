package Binaire;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


public class MainBoot {

	public static void main(String[] args) {
		String MAIN_PROPERTIES_FILE = "Binaire/main_properties";
		String SEC_PROPERTIES_FILE = "Binaire/sec_properties";
		Runtime rt = Runtime.instance();
		Profile p_main = null;
		Profile p_sec = null;
		try {
			p_main = new ProfileImpl(MAIN_PROPERTIES_FILE);
			p_sec = new ProfileImpl(SEC_PROPERTIES_FILE);
			rt.createMainContainer(p_main);
			
			AgentContainer ac = rt.createAgentContainer(p_sec);
			AgentController recep = ac.createNewAgent("recep",  "Binaire.AgentReception",  null);
			recep.start();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
