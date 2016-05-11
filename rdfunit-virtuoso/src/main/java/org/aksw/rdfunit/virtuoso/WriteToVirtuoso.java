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

public class WriteToVirtuoso  {

    public static void Write(String HOST, String USERNAME, String PASSWORD, String GRAPHIRI, String filename)
    {
	VirtGraph set = new VirtGraph(GRAPHIRI, HOST, USERNAME, PASSWORD);
	Model testModel = ModelFactory.createDefaultModel();
	testModel.read(filename);
	System.out.println(filename + " Virtuoso writing START!");
	StmtIterator iterator = testModel.listStatements();
        while(iterator.hasNext())
        {
            Statement stm = iterator.next();
            //System.out.println(stm.getSubject() + " " + stm.getPredicate() + " " + stm.getObject());
	    Node subject = NodeFactory.createURI(stm.getSubject().toString());
	    Node predicate = NodeFactory.createURI(stm.getPredicate().toString());
	    Node object = NodeFactory.createURI(stm.getObject().toString());

	    set.add(new Triple(subject, predicate, object));
        }

	testModel.close();
	set.close();
	System.out.println(filename + " Virtuoso writing DONE!");
    }
} 
