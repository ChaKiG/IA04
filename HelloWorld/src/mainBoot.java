import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;


public class mainBoot {

	public static void main(String[] args) {
		String MAIN_PROPERTIES_FILE = "main_properties";
		Runtime rt = Runtime.instance();
		Profile p = null;
		try {
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			rt.createMainContainer(p);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
