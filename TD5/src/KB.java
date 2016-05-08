import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class KB extends Agent {
	private static final long serialVersionUID = 1L;
	protected Model model;
	
	protected void setup() {
		model = ModelFactory.createDefaultModel(); 
		try {
			model.read(new FileInputStream("foaf.n3"), null, "TURTLE");
			model.read(new FileInputStream("baseConnaissances"), null, "TURTLE");
			System.out.println(getLocalName() + "--> installed");
			addBehaviour(new Request());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*public class Listen extends Behaviour {
		private static final long serialVersionUID = 10L;

		public void action() {
			ACLMessage message = null;
			message = receive();
			if (message != null) {
				String queryString = message.getContent();
				runSelectQuery(queryString);
			}
		}
		
		public boolean done() {
			return false;
		}
		*/
	
	public class Request extends Behaviour {
		private int state;
		private String uniqueID;
	
		private Request() {
			state = 0;
			uniqueID = UUID.randomUUID().toString();
		}
		
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			OperationResult mes = null;
			if (message != null) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					mes = mapper.readValue(message.getContent(), OperationResult.class);
					Query query = QueryFactory.create(mes.getRequest()); 
					QueryExecution queryExecution = QueryExecutionFactory.create(query, model); 
					ResultSet r = queryExecution.execSelect(); 
					ResultSetFormatter.out(System.out,r); 
					queryExecution.close(); 
				}
				catch (Exception e) {
				System.out.println("Mauvais message reçu : " + mes.getRequest());
				}
			}
			
		}
		public boolean done() {
			return false;
		}
		
	}
}
