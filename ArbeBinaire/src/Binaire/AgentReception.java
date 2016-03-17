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
						addBehaviour(new AddValue(val, racine));
					}
				} else {
					try {
						Object[] args = {Integer.parseInt(mContent)};
						this.getAgent().getContainerController().createNewAgent("racine", "Binaire.AgentArbre",  args);
						racine = new AID("racine", AID.ISLOCALNAME);
						System.out.println("Valuer ajoutée : " + (int) args[0]);
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
			case 0:
				try {
					System.out.print("addvalue :" + String.valueOf(val));
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId(uniqueID);
					OperationValue r = new OperationValue(val, "insert");
	
					mapper = new ObjectMapper();
					message.setContent(mapper.writeValueAsString(r));
					message.addReceiver(racine);
					send(message);
					state = 1;
				} catch (Exception e) {
					System.out.println("erreur :" + e.getMessage());
				}
				break;
				case 1:
					MessageTemplate mt = MessageTemplate.and(
							MessageTemplate.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.MatchConversationId(uniqueID));
					message = receive(mt);
					if (message != null) {
						System.out.println(message.getContent());
						state = 2;
					}
				break;
			}
		}
		public boolean done() {
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
			case 0:
				try {
					System.out.print("addvalue :" + String.valueOf(val));
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId(uniqueID);
					OperationValue r = new OperationValue(val, "check");
	
					mapper = new ObjectMapper();
					message.setContent(mapper.writeValueAsString(r));
					message.addReceiver(racine);
					send(message);
					state = 1;
				} catch (Exception e) {
					System.out.println("erreur :" + e.getMessage());
				}
				break;
			case 1:
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId(uniqueID));
				message = receive(mt);
				if (message != null) {
					System.out.println(message.getContent());
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
			case 0:
				try {
					System.out.print("show");
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId(uniqueID);
					OperationValue r = new OperationValue(0, "show");				
					mapper = new ObjectMapper();
					message.setContent(mapper.writeValueAsString(r));
					message.addReceiver(racine);
					send(message);
					state = 1;
				} catch (JsonProcessingException e) {
					System.out.println(e.getMessage());
				}
				break;
			case 1:
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId(uniqueID));
				message = receive(mt);
				if (message != null) {
					System.out.println(message.getContent());
					state = 2;
				}
				break;
			}
		}

		public boolean done() {
			return (state == 2);
		}
	}
}
