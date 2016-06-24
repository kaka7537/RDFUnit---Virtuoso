package org.aksw.rdfunit.virtuoso;

import virtuoso.jena.driver.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class WriteDateToVirtuoso  {

    public static void WriteDate(String HOST, String USERNAME, String PASSWORD, String GRAPHIRI, String date)
    {
        VirtGraph set = new VirtGraph(GRAPHIRI, HOST, USERNAME, PASSWORD);
        System.out.println("Virtuoso date writing START!");
	Node node_s = NodeFactory.createURI("KBoxOperationDate");
	Node node_p = NodeFactory.createURI("Date");
	Node node_o = NodeFactory.createLiteral(date); 
        set.add(new Triple(node_s, node_p, node_o));
	
	set.close();

    }
}
