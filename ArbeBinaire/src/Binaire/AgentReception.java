package Binaire;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;



public class AgentReception  extends Agent{
	private static final long serialVersionUID = 1L;

	protected void setup() {
		System.out.println(getLocalName() + "--> installed");
		addBehaviour(new Listen());
	}
	
	protected class Listen extends Behaviour {
		private static final long serialVersionUID = 11L;
		private AID racine = null;
		
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			if (message != null) {
				String mContent = message.getContent();
				
				if (racine != null) {
					if (mContent.endsWith("?")) {
						if (mContent.length() == 1) {
							addBehaviour(new Show(racine));
						} else {
							int val = Integer.parseInt(mContent.substring(0, mContent.length()-1));
							addBehaviour(new CheckValue(val, racine));
						}
					} else {
						int val = Integer.parseInt(mContent);
						System.out.println("Valeur demandée : " + val);
						addBehaviour(new AddValue(val, racine));
					}
				} else {
					try {
						// L'agent Reception créé l'agent Arbre
						int val = Integer.parseInt(mContent);
						Object[] args = {val};
						this.getAgent().getContainerController().createNewAgent("racine(" + String.valueOf(val) + ")", "Binaire.AgentArbre",  args);
						this.getAgent().getContainerController().getAgent("racine(" + String.valueOf(val) + ")").start();
						racine = new AID("racine(" + String.valueOf(val) + ")", AID.ISLOCALNAME); // Pour avoir comme attr racine, qui correspond Ã  la racine de l'arbre
						
						System.out.println("Valeur ajoutée pour la racine : " + (int) args[0]);
					} catch (Exception e) {
						System.out.println("erreur : " + e.getMessage());
					}
				}
			}
		}
		public boolean done() {
			return false;
		}
	}
	
	protected class AddValue extends Behaviour {
		private static final long serialVersionUID = 11L;
		private String uniqueID;
		private AID racine;
		private int val;
		private int state;
		
		public AddValue(int val, AID racine) {
			this.uniqueID = UUID.randomUUID().toString();
			this.racine = racine;
			this.val = val;
			this.state = 0;
		}
		
		public void action() {
			ACLMessage message = null;
			ObjectMapper mapper = null;
			switch (this.state) {
			case 0: // Demande
				try {
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId(uniqueID);
					OperationResult r = new OperationResult(val, "insert");
	
					mapper = new ObjectMapper();
					message.setContent(mapper.writeValueAsString(r));
					message.addReceiver(racine);
					send(message);
					state = 1;
				} catch (Exception e) {
					System.out.println("erreur :" + e.getMessage());
				}
				break;
				case 1: // Réponse
					MessageTemplate mt = MessageTemplate.and(
							MessageTemplate.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.MatchConversationId(uniqueID));
					message = receive(mt);
					if (message != null) {
						System.out.println("Réception a reçu la réponse : " + message.getContent());
						state = 2;
					}
				break;
			}
		}
		public boolean done() { // Terminé
			return (state == 2);
		}
	}
	
	protected class CheckValue extends Behaviour {
		private static final long serialVersionUID = 12L;
		private String uniqueID;
		private AID racine;
		private int val;
		private int state;
		
		public CheckValue(int val, AID racine) {
			this.uniqueID = UUID.randomUUID().toString();
			this.racine = racine;
			this.val = val;
			this.state = 0;
		}
		
		public void action() {
			ACLMessage message = null;
			ObjectMapper mapper = null;
			switch (this.state) {
			case 0:	// Demande
				try {
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId(uniqueID);
					OperationResult r = new OperationResult(val, "check");
	
					mapper = new ObjectMapper();
					message.setContent(mapper.writeValueAsString(r));
					message.addReceiver(racine);
					send(message);
					state = 1;
				} catch (Exception e) {
					System.out.println("erreur :" + e.getMessage());
				}
				break;
			case 1:	// Réponse
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId(uniqueID));
				message = receive(mt);
				if (message != null) {
					System.out.println("réponse add/check :" + message.getContent());
					state = 2;
				}
				break;
			}
		}
		public boolean done() { // Terminé
			return (state == 2);
		}
	}
	
	protected class Show extends Behaviour {
		private static final long serialVersionUID = 13L;
		private String uniqueID;
		private AID racine;
		private int state;
		
		public Show(AID racine) {
			this.uniqueID = UUID.randomUUID().toString();
			this.racine = racine;
			this.state = 0;
		}
		
		public void action() {
			ACLMessage message = null;
			ObjectMapper mapper = null;
			switch (this.state) {
			case 0:	// Demande
				try {
					System.out.print("show");
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId(uniqueID);
					OperationResult r = new OperationResult(0, "show");				
					mapper = new ObjectMapper();
					message.setContent(mapper.writeValueAsString(r));
					message.addReceiver(racine);
					send(message);
					state = 1;
				} catch (JsonProcessingException e) {
					System.out.println("Problèmes JSON" + e.getMessage());
				}
				break;
			case 1:	// Réponse
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId(uniqueID));
				message = receive(mt);
				if (message != null) {
					System.out.println("show response : " + message.getContent());
					state = 2;
				}
				break;
			}
		}

		public boolean done() {	// Terminé
			return (state == 2);
		}
	}
}