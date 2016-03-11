import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class HelloWorld  extends Agent{
	private static final long serialVersionUID = 1L;

	protected void setup() {
		System.out.println(getLocalName() + "--> installed");
		System.out.println("Hello World");
		addBehaviour(new test());
	}
	
	protected class test extends Behaviour {
		private static final long serialVersionUID = 11L;
		
		public void action() {
			ACLMessage message = receive();
			if (message != null) {
				System.out.println("Contact: " + message.getContent());
			}
			else
				block();
			done();
		}
		public boolean done() {
			return false;
		}
	}
}
