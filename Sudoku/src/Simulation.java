import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Simulation extends Agent {
	private static final long serialVersionUID = 1L;
	private List<AID> subscribed;
	
	public void setup() {
		System.out.println(getLocalName() + "--> installed");
		addBehaviour(new Listen());
	}
	
	public class Listen extends Behaviour {
		private static final long serialVersionUID = 11L;
		private int state;
		
		public Listen() {
			state = 0;
		}
		
		public void action() {
			if (state == 0) {
				ACLMessage message = null;
				if (subscribed.size() < 27) {
					/*  RECOIS LES SUBSCRIBE   */
					MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
					while ((message = receive(mt)) != null) {
						System.out.println("subscribed :" + message.getSender().getName());
						subscribed.add(message.getSender());
					}
				}
				if (subscribed.size() >= 27) {
					this.getAgent().addBehaviour(new Tick(this.getAgent(), 5000));
					state = 1;
				}
			}
		}

		public boolean done() {
			return (state == 1);
		}
		
		
		
		
	}
	
	public class Tick extends TickerBehaviour {
		private static final long serialVersionUID = 12L;

		public Tick(Agent a, long period) {
			super(a, period);
		}
		
		protected void onTick() {
			/*    ENVOI LES DEMANDES DE TICKER  */
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
			if ( message != null && message.getContent().equals("Fini"))
				this.getAgent().doDelete();
			else {
				for (int i = 0 ; i < subscribed.size() ; i++) {
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setContent(String.valueOf(i));
					message.addReceiver(new AID("environnement", AID.ISLOCALNAME));
					message.addReplyTo(subscribed.get(i));
					send(message);
				}
			}			
		}
	}
}
