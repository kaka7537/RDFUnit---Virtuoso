package org.aksw.rdfunit.virtuoso;

import virtuoso.jena.driver.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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

	public static String makeTTLfile(String HOST, String USERNAME, String PASSWORD, List<String> GRAPHIRI_LIST, String integratedTTL) throws IOException
	{
		System.out.println(GRAPHIRI_LIST + " TTL MAKING START");		
		Model OUTPUT = ModelFactory.createDefaultModel();
		Set<String> subject_set = new HashSet<String>();

		// ADD triples to local ttl files from l2k, c2k virtuoso
		for(String GRAPHIRI : GRAPHIRI_LIST)
		{
	            VirtGraph set = new VirtGraph(GRAPHIRI, HOST, USERNAME, PASSWORD);
	            Query sparql = QueryFactory.create("SELECT ?s ?p ?o FROM <" + GRAPHIRI + "> WHERE {?s ?p ?o}");

	            VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, set);
	            ResultSet results = vqe.execSelect();
	            while(results.hasNext()) {
	                QuerySolution qs = results.next();
			RDFNode subject = qs.get("s");
			RDFNode predicate = qs.get("p");
			RDFNode object = qs.get("o");
			
			subject_set.add(subject.toString());

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
		    vqe.close();
	            set.close();
		}

		
		// GET Type triples using binding
		String sparqlQuery_start = "SELECT ?s ?o WHERE {?s a ?o} BINDINGS ?s {";
		String sparqlQuery_end = "}";

		Iterator<String> iter = subject_set.iterator();
		while(iter.hasNext())
		{
		    String addSubject = iter.next();
		    addSubject = "(" + addSubject + ")";
		    sparqlQuery_start = sparqlQuery_start.concat(addSubject);
		}

		String sparqlQuery_complete = sparqlQuery_start.concat(sparqlQuery_end);
		
		/* BIND QUERY SAMPLE
		String sparqlQuery_complete = "select * where{?s a ?o} BINDINGS ?s {" +
						"(<http://ko.dbpedia.org/resource/'77_서울가요제>)" +
						"(<http://ko.dbpedia.org/resource/%22O%22-正.反.合.>)" +
						"(<http://ko.dbpedia.org/resource/!!!>)" +
						"(<http://ko.dbpedia.org/resource/메간_폭스>)" +
						"}";
		System.out.println(sparqlQuery_complete);
		*/
		Query query = QueryFactory.create(sparqlQuery_complete);
		VirtGraph graph = new VirtGraph(HOST, USERNAME, PASSWORD);
		VirtuosoQueryExecution qexec = VirtuosoQueryExecutionFactory.create(query, graph);
		ResultSet results = qexec.execSelect();
		Property Type_property = OUTPUT.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		while(results.hasNext())
		{
		    QuerySolution qs = results.next();
		    RDFNode Type_subject = qs.get("s");
		    RDFNode Type_object = qs.get("o");
		    Resource resource_object = OUTPUT.createResource(Type_object.toString());
		    OUTPUT.createResource(Type_subject.toString()).addProperty(Type_property, resource_object); 
		}
		qexec.close();
		graph.close();

		String filename = "ttl-resource/" + integratedTTL;
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
		OUTPUT.close();

		System.out.println(GRAPHIRI_LIST + " TTL MAKING DONE");		
		return filename;
	}

}

