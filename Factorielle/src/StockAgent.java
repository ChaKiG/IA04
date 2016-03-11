import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class StockAgent extends Agent{
	private static final long serialVersionUID = 3L;
	
	protected void setup() {
		System.out.println(getLocalName() + "--> installed");
		addBehaviour(new Stock());
		
		DFAgentDescription df = new DFAgentDescription();
		df.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Info");
		sd.setName("CachedValue");;
		df.addServices(sd);
		try {
			DFService.register(this,  df);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	protected class Stock extends Behaviour {
		private static final long serialVersionUID = 31L;
		private HashMap<Long, Long> stockedValues;

		public Stock() {
			stockedValues = new HashMap<Long, Long>();
			stockedValues.put(1L, 1L);
			stockedValues.put(0L,  0L);
		}
		
		public void addValue(Long fact, Long result) {
			stockedValues.put(fact, result);
		}
		public boolean checkValue(Long fact) {
			return (stockedValues.containsKey(fact));
		}
		
		public void action(){
			ACLMessage message = null;
			MessageTemplate mt = null;
			
			mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			message = receive(mt);
			if (message != null) {
				String mContent= message.getContent();
				System.out.println("Stock a reçu : " + mContent);				
				try {
					ObjectMapper mapper = new ObjectMapper();
					OperationMessage mess = mapper.readValue(message.getContent(), OperationMessage.class);							
					if (mess.getOperator().equals("!")) {
						addValue(mess.getV1(), mess.getV2());
					}
				} catch (Exception e) {
					System.out.print("erreur : " + e.getMessage());
				}
			}
			mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			message = receive(mt);
			if (message != null) {
				String mContent= message.getContent();
				System.out.println("Stock a reçu: " + mContent);
				int i = Integer.parseInt(mContent);
				ACLMessage resp = message.createReply();
				resp.setPerformative(ACLMessage.INFORM);
				if (checkValue((long)i)) {
					resp.setContent(String.valueOf(stockedValues.get((long)i)));
				} else {
					resp.setContent("0");
				}
				send(resp);
			}
		}
		public boolean done() {
			return false;
		}

	}
}
