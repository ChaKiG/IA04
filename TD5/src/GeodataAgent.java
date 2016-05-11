import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GeodataAgent extends Agent {
	private static final long serialVersionUID = 1L;
	
	protected void setup() {
		try {
			//System.setProperty("http.proxyHost","proxyweb.utc.fr"); 
			//System.setProperty("http.proxyPort","3128");
			System.out.println(getLocalName() + "--> installed");
			addBehaviour(new Request());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public class Request extends Behaviour {
		private static final long serialVersionUID = 1L;
				
		private Request() {}
		
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			SparqlRequest mes = null;
			if (message != null) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					mes = mapper.readValue(message.getContent(), SparqlRequest.class);
					Query query = QueryFactory.create(mes.transformRequest()); 
					QueryExecution qexec = QueryExecutionFactory.sparqlService("http://linkedgeodata.org/sparql",query); 
					ResultSet r = qexec.execSelect();
					System.out.println("KB received request, executing...");
					
					ACLMessage response = message.createReply();
					response.setPerformative(ACLMessage.INFORM);
					response.setContent(ResultSetFormatter.asText(r));
					send(response); 
					qexec.close();
				}
				catch (Exception e) {
					if (mes != null)
						System.out.println("Mauvais message reçu : " + mes.transformRequest());
					else
						System.out.println("Erreur : " + message.getContent());
					e.printStackTrace();
				}
			}
			
		}
		public boolean done() {
			return false;
		}
		
	}
}
