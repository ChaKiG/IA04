import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Analyse extends Agent {
	private static final long serialVersionUID = 2L;

	public void setup() {
		System.out.println(getLocalName() + "--> installed");
		ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
		message.addReceiver(new AID("simulation", AID.ISLOCALNAME));
		send(message);
		addBehaviour(new SearchValue());
	}
	
	//recoit 9 cases et renvoit les nouvelles valeurs possibles pour ces cases
	
	
	public class SearchValue extends Behaviour{
		private static final long serialVersionUID = 21L;
		public void action() {
			try {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage message = receive(mt);
				if (message != null) {
					ObjectMapper mapper = new ObjectMapper();
					List<Cellule> cellules = mapper.readValue(message.getContent(), mapper.getTypeFactory().constructCollectionType(List.class, Cellule.class));
					for (Cellule c : cellules) {
						for (Cellule c2 : cellules) {
							c.removePossible(c2.getValue());
						}
					}
					for (int i=0; i<9; i++) {
						Cellule cell = null;
						int nb = 0;
						for (Cellule c : cellules) {
							if (c.getPossible().contains(i)) {
								if (nb == 0)
									cell = c;
								nb++;
							}
						}
						if (nb == 1 && cell != null) {
							cell.setValue(i);
							cell.setPossible(new ArrayList<Integer>());
						}
					}
	/*
					for (Cellule c : cellules) {
						List<Integer> possibles = c.getPossible();
						int i1 = 0;
						int i2 = 0;
						if (possibles.size() == 2) {
							if (i1 == 0 && i2 == 0) {
								i1 = possibles.get(0);
								i2 = possibles.get(1);
							} else {
	
							}
						}
					}
	*/				
					message = message.createReply();
					message.setPerformative(ACLMessage.INFORM);
					message.setContent(new ObjectMapper().writeValueAsString(cellules));
					send(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		public boolean done() {
			return false;
		}
		
	}
}
