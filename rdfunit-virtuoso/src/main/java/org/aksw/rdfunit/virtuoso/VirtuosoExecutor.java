package org.aksw.rdfunit.virtuoso;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

//import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/*import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
*/
import virtuoso.jena.driver.*;

public class VirtuosoExecutor  {

	private static final String HOST = "jdbc:virtuoso://dmserver5.kaist.ac.kr:4004/charset=UTF-8/log_enable=2";
	private static final String USERNAME = "dba";
    	private static final String PASSWORD = "dba";
    	private static final String GRAPHIRI = "http://knuremovetest.kaist.ac.kr";

    	public void test()
    	{
	
	        VirtGraph set = new VirtGraph(GRAPHIRI, HOST, USERNAME, PASSWORD);
//		set.close();

	        Query sparql = QueryFactory.create("SELECT (count(DISTINCT ?s) AS ?total) FROM <" + GRAPHIRI + "> WHERE { ?s ?p ?o }");
	        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, set);
	        ResultSet results = vqe.execSelect();
	        int result = 0;
	        if(results != null && results.hasNext()) {
	                QuerySolution qs = results.next();
	                result = qs.get("total").asLiteral().getInt();
	        }
	        System.out.println("TEST VIRTUOSO = " + result);
	        set.close();

		System.out.println("VIRTUOSO TEST");
    	}

    	public void getVirtuosoAddress(String from, String to)
    	{

    	}
}
