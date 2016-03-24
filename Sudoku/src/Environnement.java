import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Environnement extends Agent {
	private static final long serialVersionUID = 3L;
	private Cellule[][] sudoku;
	
	public void setup() {
		sudoku = new Cellule[9][9];
		this.addBehaviour(new Listen());
		for (int i=0; i<27; i++) {
			try {
				this.getContainerController().createNewAgent("analyste"+String.valueOf(i), "Analyse", null);
				this.getContainerController().getAgent("analyste"+String.valueOf(i)).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected List<Cellule> getCellules(int group) {
		List<Cellule> l = new ArrayList<Cellule>();
		if (group < 9) {
			for (int i=0; i<9; i++)
				l.add(sudoku[group][i]);
		} else if (group < 18) {
			for (int i=0; i<9; i++)
				l.add(sudoku[i][group]);
		} else if (group < 27) {
			int line = (group - 18)/3;
			int col = (group - 18)%3;
			for(int i=0; i<3; i++)
				for(int j=0; j<3; j++) {
					l.add(sudoku[line+i][col+j]);
				}
		}
		return l;
	}
	
	public class Listen extends Behaviour {
		private static final long serialVersionUID = 31L;

		public void action() {
			ACLMessage message = null;
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			while ( (message = receive(mt)) != null) {
				int group = Integer.parseInt(message.getContent());
				AID agent = (AID) message.getAllReplyTo().next();
				String id = UUID.randomUUID().toString();
				
				this.getAgent().addBehaviour(new getAnalyse(group, agent, id));
			}
		}

		public boolean done() {
			return false;
		}
		
	}
	
	public class getAnalyse extends Behaviour {
		private static final long serialVersionUID = 32L;
		private int group;
		private AID analyste;
		private String UniqueID;
		private int state;
		
		getAnalyse(int group, AID analyste, String UniqueID) {
			this.group = group;
			this.analyste = analyste;
			this.UniqueID = UniqueID;
			state = 0;
		}
		
		public void action() {
			ACLMessage message = null;
			if (state == 0) {
				try {
					message = new ACLMessage(ACLMessage.REQUEST);
					message.addReceiver(analyste);
					message.setContent(new ObjectMapper().writeValueAsString(getCellules(group)));
					send(message);
					state++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (state == 1) {
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId(UniqueID));
				if ((message = receive(mt)) != null) {
					try {
						ObjectMapper mapper = new ObjectMapper();
						List<Cellule> cellules = mapper.readValue(message.getContent(), mapper.getTypeFactory().constructCollectionType(List.class, Cellule.class));
						for (int i=0; i<9; i++) {
							   getCellules(group).get(i).reviewPossible(cellules.get(i).getPossible());
						}
						state++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		public boolean done() {
			return (state == 2);
		}
	}
}
