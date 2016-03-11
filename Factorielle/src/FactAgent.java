import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;



public class FactAgent  extends Agent{
	private static final long serialVersionUID = 1L;
	protected int valeurActuelle;
	protected int valeurSouhaitee;

	protected void setup() {
		System.out.println(getLocalName() + "--> installed");
		addBehaviour(new fact());
	}
	
	protected class fact extends Behaviour {
		private static final long serialVersionUID = 11L;
		
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			if (message != null) {
				String mContent= message.getContent();
				System.out.println("Fact a reçu: " + mContent);
				if (mContent.endsWith("!") && mContent.length() >= 2) {
					String r = mContent.substring(0, mContent.length()-1);
					Long rVal = Long.parseLong(r);
					addBehaviour(new checkValue(rVal));
				}
			}
		}
		public boolean done() {
			return false;
		}
	}
	
	
	protected class checkValue extends Behaviour {
		private static final long serialVersionUID = 12L;
		long val;
		String uniqueID;
		private int state;
		
		public checkValue(long val) {
			this.val = val;
			state = 0;
			uniqueID = UUID.randomUUID().toString();
		}
		
		public void action() {
			ACLMessage message = null;
			if (state == 0) {
				message = new ACLMessage(ACLMessage.REQUEST);
				message.setConversationId(uniqueID);
				message.setContent(String.valueOf(val));
				message.addReceiver(getReceiver("info"));
				send(message);
				state = 1;
			} else if (state == 1) {
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId(uniqueID));
				message = receive(mt);
				if (message != null) {
					String s = message.getContent();
					long i = Long.parseLong(s);
					if (i == 0L) {
						System.out.println("Pas en cache, debut du calcul pour " + i +"!"); 
						addBehaviour(new increment(val));
					} else {
						System.out.println("Valeur en cache, resulatat : " + i); 
					}
					state = 2;
				}
			}
		}
		public boolean done() {
			return (state == 2);
		}
	}
	
	
	
	
	
	
	protected class increment extends Behaviour {
		private static final long serialVersionUID = 13L;
		protected long curr;
		protected long max;
		long beginMS;
		String uniqueID;
		
		private increment(long val) {
			curr = 1L;
			max = val;
			beginMS = System.currentTimeMillis();
			uniqueID = UUID.randomUUID().toString();
		}
		
		public void action() {
			ACLMessage message = null;
			ObjectMapper mapper = null;
			try {
				mapper = new ObjectMapper();
				
				if (curr == 1L) {
					message = new ACLMessage(ACLMessage.REQUEST);
					message.setConversationId(uniqueID);
					message.setContent(mapper.writeValueAsString(new OperationMessage((long)curr, (long)curr+1, "*")));
					message.addReceiver(getReceiver("mult"));
					send(message);
					curr++;
				} else {
					MessageTemplate mt = MessageTemplate.and(
							MessageTemplate.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.MatchConversationId(uniqueID));
					message = receive(mt);
					if (message != null) {
						try {
							OperationResultMessage r = mapper.readValue(message.getContent(), OperationResultMessage.class);							
							if (r.getComment().equals("mult ok")) {
								if (curr == max) {
									long endMS   = System.currentTimeMillis();
									long totalMS = endMS - beginMS;
									message = new ACLMessage(ACLMessage.INFORM);
									message.setContent(mapper.writeValueAsString(new OperationMessage(max, r.getResult(), "!")));
									message.addReceiver(getReceiver("info"));
									send(message);
									System.out.println("final response : " + String.valueOf(r.getResult()));
									System.out.println("temps : " + totalMS/1000 + "s");
									curr++;
								} else {
									curr++;
									message = new ACLMessage(ACLMessage.REQUEST);
									message.setConversationId(uniqueID);
									message.setContent(mapper.writeValueAsString(new OperationMessage(r.getResult(), (long)curr, "*")));
									message.addReceiver(getReceiver("mult"));
									send(message);
								}
							} else {
								System.out.println("Mauvais message reçu : " + r.getComment());
							}
						} catch (Exception e) {
							System.out.println("Mauvais message reçu : " + message.getContent());
						}
					}
				}				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		public boolean done() {
			return (curr == max+1);
		}
	}
	
	private AID getReceiver(String info) {
		AID rec = null;
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		if (info.equals("mult")) {
			sd.setType("Operation");
			sd.setName("Multiplication");
		} else {
			sd.setType("Info");
			sd.setName("CachedValue");
		}
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this,  template);
			if (result.length > 0) {
				rec = result[(int) Math.floor(Math.random()*result.length)].getName();
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return rec;
	}

}
