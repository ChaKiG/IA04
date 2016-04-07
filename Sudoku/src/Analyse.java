
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
		//System.out.println(getLocalName() + "--> installed");
		ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
		message.addReceiver(new AID("simulation", AID.ISLOCALNAME));
		send(message);
		addBehaviour(new SearchValue());
	}
	
	
	public class SearchValue extends Behaviour{
		private static final long serialVersionUID = 21L;
		public void action() {
			try {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage message = receive(mt);
				if (message != null) {
					ObjectMapper mapper = new ObjectMapper();
					List<Cellule> cellules = mapper.readValue(message.getContent(), mapper.getTypeFactory().constructCollectionType(List.class, Cellule.class));
					
					
					//   Retrait des valeurs déjà trouvées
					for (Cellule c : cellules) {
						int val = c.getValue();
						if (val > 0 ) {
							for (Cellule c2 : cellules) {
								if (c2.getPossible().contains(val)) {
									c2.removePossible(val);
								}
							}							
						}
					}
					
					// recherche et definition des cellules n'ayant qu'une valeur possible
					for (Cellule c : cellules) {
						int val = c.getValue();
						List<Integer> possible = c.getPossible();
						if (val == 0 && possible.size() == 1) {
							c.defineValue(possible.get(0));
						}
					}
					
					
					// Reherche des valeurs non encore définies
					/*List<Integer> notDefined = new ArrayList<Integer>();
					for (Cellule c : cellules) {
						if (c.getValue() > 0)
							notDefined.add(c.getValue());
					}
					
					//  Recherche des valeurs non trouvées n'aparaissant que dans UNE cellule					
					for (int i : notDefined) {
						int cell = 0;
						int nb = 0;
						for (int index=0; index<9; index++) {
							if (cellules.get(index).getPossible().contains(i)) {
								cell = index;
								nb++;
							}
						}
						// Si elle n'apparait qu'une fois alors on la défini,
						// pas besoin de la retirer des autres possibles 
						// puisqu'elle n'apparait pas ailleurs
						if (nb == 1) {
							cellules.get(cell).defineValue(i);
						}
					}*/
					
					//  Recherche d'un doublon de valeurs n'aparaissant que dans DEUX cellules 
					/*  NON ENCORE FONCTIONNEL 
					for (int i=1; i<6; i++) {
						for (int j=6; j<10; j++) {
							for (int k=0; k<9; k++) {
								List<Integer> l = cellules.get(k).getPossible();
								if (l.contains(i) && l.contains(j)) {
									cells.add(k);
								}
							}
							//   Retrait des valeurs trouvées en doublon   
							if (cells.size() == 2) {
								int cell1 = cells.get(0);
								int cell2 = cells.get(1);
								List<Integer> values = new ArrayList<Integer>();
								values.add(i);
								values.add(j);
								cellules.get(cell1).setPossible(values);
								cellules.get(cell2).setPossible(values);

								for (int k=0; k<9; k++) {
									if (k != cell1 && k != cell2) {
										cellules.get(k).removePossible(i);
										cellules.get(k).removePossible(j);
									}
								}
							}
						}
					}*/
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
