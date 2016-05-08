import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/*
Requêtes SPARQL :  
1. demander les personnes connues d'une personne particulière de la base :

SELECT ?x WHERE { ex:Jean foaf:knows ?x }

2. rechercher les personnes intéressées par le même pays qu'une personne particulière. 

SELECT ?x
WHERE {
	ex:Jean foaf:topic_interest ?pays
	?x foaf:topic_interest ?pays
}

*/


public class PropagateSparql extends Agent {
	private static final long serialVersionUID = 1L;
	
	protected void setup() {
		System.out.println(getLocalName() + "--> installed");
		addBehaviour(new Listen());
	}

	protected class Listen extends Behaviour {
		private int state;
		private String uniqueID;
	
		private Listen() {
			state = 0;
			uniqueID = UUID.randomUUID().toString();
		}
		
		public void action() {
			ACLMessage message = null;
			ObjectMapper mapper = null;
			
			try {
				
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST); // On envoie à l'aide de la console Jade
				message = receive(mt);
		
				if (message != null) {
					String request = message.getContent(); // On envoie la requête par la console
					
					// On envoie la requête à KB
					mapper = new ObjectMapper();
					message = null;
					message = new ACLMessage(ACLMessage.REQUEST);
					message.addReceiver(new AID("KB", AID.ISLOCALNAME));
					message.setConversationId(uniqueID);
					message.setContent(mapper.writeValueAsString(new OperationResult(request)));
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
}
