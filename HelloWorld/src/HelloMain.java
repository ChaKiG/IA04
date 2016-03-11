import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


public class HelloMain {

	public static void main(String[] args) {
		String SEC_PROPERTIES_FILE = "sec_properties";
		Runtime rt = Runtime.instance();
		Profile p_sec = null;
		try {
			p_sec = new ProfileImpl(SEC_PROPERTIES_FILE);
			AgentContainer sc = rt.createAgentContainer(p_sec);
			AgentController ac_sec_1 = sc.createNewAgent("Hello_sec_1","HelloWorld",null);
			ac_sec_1.start();
			AgentController ac_sec_2 = sc.createNewAgent("Hello_sec_2","HelloWorld",null);
			ac_sec_2.start();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
