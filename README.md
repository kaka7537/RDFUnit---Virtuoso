RDFUnit - Virtuoso : Delete triples which were occuring errors 
==========

**Homepage**: Not served yet <br/>
**Documentation**: Not served yet  <br/>
**Mailing list**: kaka7537@kaist.ac.kr  <br/>

### Basic usage

The simplest setting is as follows:

```console
$ mvn -Dmaven.test.skip=true install
$ bin/rdfunit -d <ANY-COMMAND>
```

What RDFUnit will do is:

1. Get statistics about all properties & classes in the dataset
1. Get the namespaces out of them and try to dereference all that exist in [LOV](http://lov.okfn.org)
1. Run our [Test Generators](https://github.com/AKSW/RDFUnit/wiki/Patterns-Generators) on the schemas and generate RDFUnit Test cases
1. Run the RDFUnit test cases on the dataset
1. You get a results report in html (by default) but you can request it in [RDF](http://rdfunit.aksw.org/ns/core#) or even multiple serializations with e.g.  `-o html,turtle,jsonld`
  * The results are by default aggregated with counts, you can request different levels of result details using `-r {status|aggregated|shacl|shacllite|rlog|extended}`. See [here](https://github.com/AKSW/RDFUnit/wiki/Results) for more details.

You can also run:
```console
$ bin/rdfunit -d <dataset-uri> -s <schema1,schema2,schema3,...>
```

Where you define your own schemas and we pick up from step 3. You can also use prefixes directly (e.g. `-s foaf,skos`) we can get everything that is defined in [LOV](http://lov.okfn.org).
