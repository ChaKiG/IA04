package Binaire;
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
    	fils1 = null;
    	fils2 = null;
        addBehaviour(new Listen());
	}
	
	
	protected class Listen extends Behaviour {
		private static final long serialVersionUID = 11L;
		
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			ObjectMapper m = new ObjectMapper();
			if (message != null) {	// On a reçu une demande de l'agent Réception ou de père de ce noeud
				try {
					OperationResult mes = m.readValue(message.getContent(), OperationResult.class);
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
		private AID reply;	// L'agent qui nous a envoyé un message
		private int value;
		private String operation;
		private int state;
		
		public Value(int value, String operation, String UniqueID, AID reply) {
			this.UniqueID = UniqueID;
			this.reply = reply;
			this.value = value;
			this.operation = operation;
			this.state = 0;
		}
		
		public void action() {
			ACLMessage message = null;
			ObjectMapper mapper = null;
			switch (this.state) {
			case 0:
				if (nodeValue == value) {
					message = new ACLMessage(ACLMessage.INFORM);
					message.setConversationId(UniqueID);
					message.addReceiver(reply);
					if (operation.equals("check")) {  // Envoie true au père 
						message.setContent("true");	
					} else if (operation.equals("insert")) { // Envoie false au père 
						message.setContent("false");
					}
					send(message);
					state = 2;
				} else if ((fils1 != null && value < nodeValue) || (fils2 != null && value > nodeValue)) { 
					try {
						message = new ACLMessage(ACLMessage.REQUEST);
						message.setConversationId(UniqueID);
						mapper = new ObjectMapper();
						message.setContent(mapper.writeValueAsString(new OperationResult(value, operation)));
						if (value < nodeValue) {
							message.addReceiver(fils1);
							System.out.println("On envoie la demande au fils gauche :" + String.valueOf(value) + " < " + String.valueOf(nodeValue));
						} else {
							message.addReceiver(fils2);								
							System.out.println("On envoie la demande au fils droit :" + String.valueOf(value) + " < " + String.valueOf(nodeValue));
						}
						send(message);
						state = 1;
					} catch (Exception e) {
						System.out.println("Mauvais message reçu : " + message.getContent());
					}
				} else {	// Le fils n'est pas créé
					if (operation.equals("insert")) {
						Object[] args = {value};
						String agentName = String.valueOf(value); // On doit leur valeur comme nom aux agents
						try {
							this.getAgent().getContainerController().createNewAgent(agentName, "Binaire.AgentArbre",  args);
							this.getAgent().getContainerController().getAgent(agentName).start();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (value < nodeValue) {   // On créé le fils gauche
							fils1 = new AID(agentName, AID.ISLOCALNAME);
						} else {  // On créé le fils droit
							fils2 = new AID(agentName, AID.ISLOCALNAME);
							System.out.println("Valeur ajoutée dans le fils droit : " + (int) args[0]);								
						}
						// Envoie de true au père (on a bien inséré)
						message = new ACLMessage(ACLMessage.INFORM);
						message.setConversationId(UniqueID);
						message.setContent("true");
						message.addReceiver(reply);
						send(message);
						state = 1;
					} else {
						message = new ACLMessage(ACLMessage.INFORM);
						message.setConversationId(UniqueID);
						message.setContent("false");
						message.addReceiver(reply);
						send(message); 
						state = 1;
					}
				}
				break;
			case 1:
				// Réception de la réponse des enfants pour renvoyer la réponse
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId(UniqueID));
				message = receive(mt);
				if (message != null) {
					String reponse = message.getContent();
					message = new ACLMessage(ACLMessage.INFORM);
					message.setConversationId(UniqueID);
					message.setContent(reponse);
					message.addReceiver(reply);
					send(message);
					state = 2;
				}
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
		private int state;
		private String respTextGauche;
		private String respTextDroit;
		
		public Show(String UniqueID, AID reply) {
			this.UniqueID = UniqueID;
			this.reply = reply;
			respTextGauche = "";
			respTextDroit = "";
			state = 0;
		}
		
		public void action() {
			ACLMessage message = null;
			System.out.println(state);
			if (state == 0) {
				if (fils1 != null || fils2 != null) {
					try {
						System.out.println("AID : " + reply.getName() + ",  ID : " +UniqueID);
						message = new ACLMessage(ACLMessage.REQUEST);
						message.setConversationId(UniqueID);
						ObjectMapper mapper = new ObjectMapper();
						message.setContent(mapper.writeValueAsString(new OperationResult(0, "show")));
						if (fils1 != null)
							message.addReceiver(fils1);
						else 
							state++;
						if (fils2 != null) 
							message.addReceiver(fils2);
						else 
							state++;
						send(message);
						state++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else
					state = 3;
			}
			if ( state > 0 && state < 3) {
				System.out.println(state);
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId(UniqueID));
				message = receive(mt);
				if (message != null) {
					if (message.getSender().equals(fils1)) {
						respTextGauche = message.getContent();
					} else {
						respTextDroit = message.getContent();
					}
					state++;
				}
			}
			if (state == 3) {
				System.out.println(state);
				message = new ACLMessage(ACLMessage.INFORM);
				message.setContent("(" + String.valueOf(nodeValue) + "(" + respTextGauche + ")(" + respTextDroit + "))");
				message.addReceiver(reply);
				message.setConversationId(UniqueID);
				send(message);
				state = 4;
			}
		}

		public boolean done() {
			return (state == 4);
		}
	}
}