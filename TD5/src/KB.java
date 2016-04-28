import java.io.FileInputStream;

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

public class KB extends Agent {
	private static final long serialVersionUID = 1L;
	protected Model model;
	
	protected void setup() {
		model = ModelFactory.createDefaultModel(); 
		try {
			model.read(new FileInputStream("foaf.n3"), null, "TURTLE");
			model.read(new FileInputStream("baseConnaissances"), null, "TURTLE");
			addBehaviour(new Listen());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public class Listen extends Behaviour {
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
		
		
		private void runSelectQuery(String queryString) { 
			Query query = QueryFactory.create(queryString); 
			QueryExecution queryExecution = QueryExecutionFactory.create(query, model); 
			ResultSet r = queryExecution.execSelect(); 
			ResultSetFormatter.out(System.out,r); 
			queryExecution.close(); 
		}
	}
}
