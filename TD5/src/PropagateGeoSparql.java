import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
 
/*
 * Lister pays et capitale
SELECT *
WHERE { 
  ?country a lgdo:Country ; 
     lgdo:capital_city ?city ; 
     lgdo:wikipedia ?name . 
} 
*/


public class PropagateGeoSparql extends Agent {
	private static final long serialVersionUID = 1L;
	
	protected void setup() {
		System.out.println(getLocalName() + "--> installed");
		addBehaviour(new Listen());
	}

	protected class Listen extends Behaviour {
		private static final long serialVersionUID = 1L;
		
		private Listen() {}
		
		public void action() {
			ACLMessage message = null;
			try {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				message = receive(mt);
				if (message != null) {
					System.out.println("Propagate received message, formating and sending to GeodataAgent");
					String request = message.getContent();
					message = new ACLMessage(ACLMessage.REQUEST);
					message.addReceiver(new AID("GeodataAgent", AID.ISLOCALNAME));
					SparqlRequest r = new SparqlRequest(request);
					message.setContent(new ObjectMapper().writeValueAsString(r));

					String id = UUID.randomUUID().toString();
					message.setConversationId(id);
					this.getAgent().addBehaviour(new Response(id));
					send(message);
				}
					
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		public boolean done() {
			return false;
		}
			
	}
	
	protected class Response extends Behaviour {
		private static final long serialVersionUID = 1L;
		private int state;
		private String UniqueID;
		
		Response(String UniqueID) {
			this.state = 0;
			this.UniqueID = UniqueID;
		}

		public void action() {
			ACLMessage message = null;
			try {
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId(UniqueID));
				message = receive(mt);
				if (message != null) {
					String response = message.getContent();
					System.out.println("PropagateGeoSparql received response : ");
					System.out.println(response);
					state = 1;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public boolean done() {
			return state == 1;
		}
	}
	
}
