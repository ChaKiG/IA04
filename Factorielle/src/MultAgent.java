import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class MultAgent  extends Agent{
	private static final long serialVersionUID = 2L;

	protected void setup() {
		System.out.println(getLocalName() + "--> installed");
		addBehaviour(new mult());
		
		DFAgentDescription df = new DFAgentDescription();
		df.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Operation");
		sd.setName("Multiplication");;
		df.addServices(sd);
		try {
			DFService.register(this,  df);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	protected class mult extends Behaviour {
		private static final long serialVersionUID = 21L;
		
		public void action() {
			ObjectMapper mapper = null;
			ACLMessage message = receive();
			if (message != null) {
				String mContent= message.getContent();
				System.out.println(this.getAgent().getAID().getLocalName() + "   a reçu: " + mContent);
				
				try {
					mapper = new ObjectMapper();
					OperationMessage mes = mapper.readValue(message.getContent(), OperationMessage.class);
					OperationResultMessage r = new OperationResultMessage(mes.getV1() * mes.getV2(), "mult ok");
					
					ACLMessage resp = message.createReply();
					resp.setPerformative(ACLMessage.INFORM);
					resp.setContent(mapper.writeValueAsString(r));
					
					long timeout = (long) ((0.2 + Math.random()*0.8)*1000);
					addBehaviour(new sendResp(this.getAgent(), timeout, resp));
					
				} catch (Exception e) {
					System.out.println(e.getMessage());
					ACLMessage resp = message.createReply();
					resp.setPerformative(ACLMessage.FAILURE);
					try {
						resp.setContent(mapper.writeValueAsString(new OperationResultMessage(0L, "ERROR")));
					} catch (Exception eb) {
						resp.setContent("ERROR");
					}
					send(resp);
				}				
			}
		}
		public boolean done() {
			return false;
		}
	}
	
	protected class sendResp extends WakerBehaviour {
		private static final long serialVersionUID = 22L;
		private ACLMessage message;

		public sendResp(Agent a, long timeout, ACLMessage message) {
			super(a, timeout);
			this.message = message;
		}

		protected void onWake() {
			send(message);
		}
	}
}
