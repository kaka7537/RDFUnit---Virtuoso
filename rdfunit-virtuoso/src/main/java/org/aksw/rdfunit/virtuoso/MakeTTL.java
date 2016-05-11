package org.aksw.rdfunit.virtuoso;

import virtuoso.jena.driver.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class MakeTTL  {

	public static String makeTTLfile(String HOST, String USERNAME, String PASSWORD, String GRAPHIRI) throws IOException
	{
		System.out.println(GRAPHIRI + " TTL MAKING START");		
		Model OUTPUT = ModelFactory.createDefaultModel();

	        VirtGraph set = new VirtGraph(GRAPHIRI, HOST, USERNAME, PASSWORD);
	        Query sparql = QueryFactory.create("SELECT ?s ?p ?o FROM <" + GRAPHIRI + "> WHERE {?s ?p ?o}");

	        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, set);
	        ResultSet results = vqe.execSelect();
	        while(results.hasNext()) {
	                QuerySolution qs = results.next();
			RDFNode subject = qs.get("s");
			RDFNode predicate = qs.get("p");
			RDFNode object = qs.get("o");

			Property property = OUTPUT.createProperty(predicate.toString());
			String string_object = object.toString();
			if(string_object.contains("http://ko.dbpedia"))
			{
			    Resource resource_object = OUTPUT.createResource(string_object);
			    OUTPUT.createResource(subject.toString()).addProperty(property, resource_object);
			}
			else if(string_object.contains("http://dbpedia.org/ontology"))
			{
			    Resource resource_object = OUTPUT.createResource(string_object);
			    OUTPUT.createResource(subject.toString()).addProperty(property, resource_object);
			}
			else
			{
			    OUTPUT.createResource(subject.toString()).addProperty(property, string_object);
			}
	        }
		String filename = GRAPHIRI.substring(7, 17);
		filename = "ttl-resource/"+filename+".ttl";
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			OUTPUT.write(new OutputStreamWriter(fos, "UTF8"), "TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fos.close();
	        set.close();
		OUTPUT.close();

		System.out.println(GRAPHIRI + " TTL MAKING DONE");		
		return filename;
	}

}

