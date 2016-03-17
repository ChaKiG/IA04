package Binaire;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;



public class AgentArbre  extends Agent{
	private static final long serialVersionUID = 1L;
	private AID fils1;
	private AID fils2;
	protected int nodeValue;

	protected void setup() {
		System.out.println(getLocalName() + "--> installed");
		Object[] args = getArguments();
        nodeValue = (int) args[0];
        addBehaviour(new Listen());
	}
	
	protected class Listen extends Behaviour {
		private static final long serialVersionUID = 11L;
		
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			ObjectMapper m = new ObjectMapper();
			if (message != null) {
				try {
					OperationValue mes = m.readValue(message.getContent(), OperationValue.class);
					String s = mes.getComment();
					if (s.equals("show")) {
						addBehaviour(new Show(message.getConversationId(), message.getSender()));
					} else if (s.equals("insert") || s.equals("check")) {
						addBehaviour(new Value(mes.getValue(), s, message.getConversationId(), message.getSender()));
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());;
				}
			}
		}
		public boolean done() {
			return false;
		}
	}
	
	protected class Value extends Behaviour {
		private static final long serialVersionUID = 11L;
		private String UniqueID;
		private AID reply;
		private int value;
		private String operation;
		private int state;
		
		public Value(int value, String operation, String UniqueID, AID reply) {
			this.UniqueID = UniqueID;
			this.reply = reply;
			this.value = value;
			this.operation = operation;
		}
		
		public void action() {
			switch (state) {
			case 0:
				if (nodeValue == value) {
					if (operation.equals("check")) {
						/* envoyer true au pere */
					} else if (operation.equals("insert")) {
						/* envoyer false au pere */
					}
				} else {
					if (fils1 != null && value < nodeValue) {
						/* envoi demande */
						state = 1;
					} else if (fils2 != null && value > nodeValue) {
						/* envoi demande */
						state = 1;
					} else {
						if (operation.equals("insert")) {
							if (value > nodeValue) {
								/* creer fils droit (2) */
							} else {
								/* creer fils gauche (1) */								
							}
							/* envoyer true au pere */
						} else {
							/* envoyer false au pere */
						}
					}
				}
				break;
			case 1:
				/* recevoir réponse des enfants pour renoyer la reponse */
				state = 2;
				break;
			}
		}
		public boolean done() {
			return (state == 2);
		}
	}
	
	protected class Show extends Behaviour {
		private static final long serialVersionUID = 13L;
		private String UniqueID;
		private AID reply;
		
		public Show(String UniqueID, AID reply) {
			this.UniqueID = UniqueID;
			this.reply = reply;
		}
		
		public void action() {
		}

		public boolean done() {
			return false;
		}
	}
}
