package Binaire;

/*
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
*/
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;



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
			if (message != null) {	// On a reçu une demande de l'agent Réception ou de père de ce noeud
				try {
					OperationResult mes = m.readValue(message.getContent(), OperationResult.class);
					String s = mes.getComment();
					if (s.equals("show")) {
						System.out.println("Show the tree");
						addBehaviour(new Show(message.getConversationId(), message.getSender()));
					} else if (s.equals("insert") || s.equals("check")) {
						System.out.println("Insert or check the tree");
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
					if (operation.equals("check")) {
						// Envoie true au père 
						message = new ACLMessage(ACLMessage.INFORM);
						message.setConversationId(UniqueID);
						message.setContent("true");
						message.addReceiver(reply);
						send(message);
						
					} else if (operation.equals("insert")) {
						// Envoie false au père 
						message = new ACLMessage(ACLMessage.INFORM);
						message.setConversationId(UniqueID);
						message.setContent("false");
						message.addReceiver(reply);
						send(message);
						
					}
				} else {
					if (fils1 != null && value < nodeValue) {
						// Envoi d'une demande au fils Gauche 
						System.out.println("On envoie la demande au fils gauche :" + String.valueOf(value) + " < " + String.valueOf(nodeValue));
						try {
							message = new ACLMessage(ACLMessage.REQUEST);
							message.setConversationId(UniqueID);

							OperationResult r = new OperationResult(value, operation);
							mapper = new ObjectMapper();
							message.setContent(mapper.writeValueAsString(r));
							message.addReceiver(fils1);
							send(message);
							state = 1;
						} catch (Exception e) {
							System.out.println("Mauvais message reÃ§u : " + message.getContent());
						}
					} else if (fils2 != null && value > nodeValue) {
						// Envoi d'une demande au fils Droit 
						System.out.println("On envoie la demande au fils droit :" + String.valueOf(value) + " > " + String.valueOf(nodeValue));
						try {
							message = new ACLMessage(ACLMessage.REQUEST);
							message.setConversationId(UniqueID);
	
							OperationResult r = new OperationResult(value, operation);
							mapper = new ObjectMapper();

							message.setContent(mapper.writeValueAsString(r));
							message.addReceiver(fils2);
							send(message);
							state = 1;
						} catch (Exception e) {
							System.out.println("Mauvais message reÃ§u : " + message.getContent());
						}
					} else {	// Le fils n'est pas créé
						if (operation.equals("insert")) {
							if (value < nodeValue) {
								// On crée le fils gauche
								Object[] args = {value};
								String agentName = String.valueOf(value); // On doit leur valeur comme nom aux agents
								try {
									this.getAgent().getContainerController().createNewAgent(agentName, "AgentArbre",  args);
									this.getAgent().getContainerController().getAgent(agentName).start();
									fils1 = new AID(agentName, AID.ISLOCALNAME);
								} catch (StaleProxyException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ControllerException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								System.out.println("Valeur ajoutée dans le fils gauche : " + (int) args[0]);
								
							} else {
								// On créé le fils droit
								Object[] args = {value};
								String agentName = String.valueOf(value);
								try {
									this.getAgent().getContainerController().createNewAgent(agentName, "AgentArbre",  args);
									this.getAgent().getContainerController().getAgent(agentName).start();
									fils2 = new AID(agentName, AID.ISLOCALNAME);
								} catch (StaleProxyException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ControllerException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
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
		
		public Show(String UniqueID, AID reply) {
			this.UniqueID = UniqueID;
			this.reply = reply;
		}
		
		public void action() {
			System.out.println("AID : " + reply.getName() + ",  ID : " +UniqueID);
		}

		public boolean done() {
			return false;
		}
	}
}