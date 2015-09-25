/*
 *  COMA 3.0 Community Edition
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package de.wdilab.coma.insert.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.Source;

/**
 * Import instances of an owl file. Assumption is that the owl file
 * used to import the metadata also contains the instance data. 
 * 
 * @author Sabine Massmann
 */
public class InstanceOWLParser_V3 {
//
////	public final static String LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
////	public final static String COMMENT = "http://www.w3.org/2000/01/rdf-schema#comment";
//	public final static String LABEL = "label";
//	public final static String COMMENT = "comment";
//	

    private final static String idSuffix = "_id";
////    public static int allCount = 0;
////    public static int allCountWithoutImported = 0;
////    public static int allVertices = 0;
////    public static int allVerticesWithoutImported = 0;
//    
    DataImport importer = null;
    
    HashMap<String, HashMap<String, ArrayList<String>>> instancesComplex = new HashMap<String, HashMap<String, ArrayList<String>>>();
    
////    public void filterInstancesAdd(boolean label){
////    	HashMap<String, Integer> list = new HashMap<String, Integer>();
////    	HashMap<String, HashSet<String>> data = null;
////    	HashSet<String> stopWords = new HashSet<String>();
////
////    	if (label)data=instancesLabel; else data = instancesComment;
////    	for (Iterator iter = data.keySet().iterator(); iter.hasNext();) {
////			String element = (String) iter.next();
////			HashSet<String> instances = data.get(element);
////			for (Iterator iterator = instances.iterator(); iterator.hasNext();) {
////				String instance = (String) iterator.next();
////				Integer i = list.get(instance);
////				if (i==null){
////					i = new Integer(0);
////				}
////				list.put(instance, new Integer(i.intValue()+1));
////			}
////		}
////    	float threshold =(float) ((float) data.size() * 0.05); // = 10%
////    	for (Iterator iter = list.keySet().iterator(); iter.hasNext();) {
////			String instance = (String) iter.next();
////			if (list.get(instance).intValue()>threshold){
////				stopWords.add(instance);
////			}
////    	}
////    	int beforeCount = 0, afterCount = 0;
////    	for (Iterator iter = data.keySet().iterator(); iter.hasNext();) {
////			String element = (String) iter.next();
////			HashSet<String> instances = data.get(element);
////			beforeCount += instances.size();
////			instances.removeAll(stopWords);
////			data.put(element, instances);
////			afterCount += instances.size();
////		}
////    	String filename = "./Sources/ontologies/filter"+ label+".txt";
////    	saveToFile(filename, null,false);	
////    	saveToFile(filename, stopWords.toString(),false);	
////    	System.out.println("Before: "  + beforeCount + "  , after: " + afterCount);	
////    }
//    
    public InstanceOWLParser_V3(DataImport _importer) {
		importer = _importer;
	}
//
////	private ArrayList<String> filterList(ArrayList<String> words){
////    	ArrayList<String> list = new ArrayList<String>();
////    	if (words==null){
////    		return list;
////    	}
////    	while (words!=null && words.size()>0){
////	    	String word= words.get(0);
////	    	int size =words.size();
////	    	while (words.contains(word)) words.remove(word);
////	    	int newSize = words.size();
////	    	int count = size-newSize;
////	    	if (count>1){ 
//////    		if (count==1){ 
//////	    		for (int i = 0; i < count; i++) {
////	    			list.add(word);
//////				}	    		
////    		}
////    	}
////
//////    	HashSet set = new HashSet(words);
//////    	list.addAll(set);
////
////    	return list;
//////    	 test 0: test with removing all stopwords
//////    	 test 1: test with removing only ypical stopwords
//////    	 test 2: test with words that appear at least twice
//////    	 test 3: test with words that appear at most once
////    }
//    
    public Graph parseInstances(Source source, Graph schemaGraph, HashMap<String, Integer> _instancesId) {
//        // Ontologie laden
    	
    	String provider = source.getProvider().replace(" ", "%20");
    	
		//Load Ontology
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	 	IRI iri = IRI.create(provider);
		 
	 	OWLOntology ontology = null;
		try {
//			ontology = manager.loadOntologyFromOntologyDocument(iri);
			ontology = manager.loadOntology(iri);
//			 OWLOntologyFormat f = 
//			 manager.getOntologyFormat(ontology);s
		} catch (Throwable e2) {
			if (!provider.startsWith("http") && !provider.startsWith("ftp") &&!provider.contains("file:") && e2.getLocalizedMessage().contains("Could not find an appropriate factory to load ontology from ontology document")){
				provider = "file:/" + provider;
				iri = IRI.create(provider);
				try {
//					ontology = manager.loadOntologyFromOntologyDocument(iri);
					ontology = manager.loadOntology(iri);
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}
			} else {
				e2.printStackTrace();	
			}
		}

        HashSet<String> stopWords = new HashSet<String>(); 
//        	importStopWords();
        HashMap<String, ArrayList<String>> extractedInstances = null;
        Set vertices = schemaGraph.getElementSet();

        HashMap<String, Element> verticesAccession = new HashMap<String, Element>();
        for (Iterator iter = vertices.iterator(); iter.hasNext();) {
            Element element = (Element) iter.next();
            verticesAccession.put(element.getAccession(), element);			
		}
        
        int sourceid = schemaGraph.getSource().getId();
        importer.prepareInstancesStatement(sourceid);
        
        try {
            extractedInstances = extractInstances(sourceid,source.getProvider(), ontology, stopWords, verticesAccession);
//            if (verbose)
//                printExtractedInstances(extractedInstances);
        } catch (OWLException e) {
            e.printStackTrace();
            return schemaGraph;
        }
        if (extractedInstances.size()==0 && instancesComplex.isEmpty()){
        	return schemaGraph;
        }
        
////        filterInstancesAdd(true);
////        filterInstancesAdd(false);
//
////        int count = 0;
////        int vertexCount = 0;
////        int countWithoutImported = 0;
////        Iterator it = vertices.iterator();
////        int instancesCount =0;
////        while (it.hasNext()) {
////            VertexImpl vertex = (VertexImpl) it.next();
////            Element element = (Element) vertex.getObject();
////            String uri = element.getNamespace();
////            if (element.getNamespace() != null
////                    && !(element.getNamespace().equals(element.getAccession())))
////                uri += "#" + element.getAccession();
////            ArrayList instances = extractedInstances.get(uri);
////            boolean hasInstances = false;
////            if (instances != null) {
////            	instancesCount+=instances.size();
////            	if (_instancesId!=null){
////	            	// ***** change the instance to an id ******
//////	            	ArrayList instancesIds = new ArrayList();
//////	            	for (Iterator iter = instances.iterator(); iter.hasNext();) {
//////						String instance = (String) iter.next();
//////						Integer id = _instancesId.get(instance);
//////						if (id==null){
//////							id = new Integer(_instancesId.size());
//////							_instancesId.put(instance, id);
//////						}
//////						instancesIds.add(id);
//////					}
//////	            	instances.clear();
//////	            	instances.addAll(instancesIds);            	
////	            	// *****  end -    *****
////            	}
////                element.setInstancesSimple(instances);                
////                hasInstances = true;
////            }
////            HashMap instComplex = instancesComplex.get(uri);
////            if (instComplex!=null){
////                hasInstances = true;
////                element.setDirectInstancesComplex(instComplex);
////            }
////            ArrayList<String> instancesId = extractedInstances.get(uri
////                    + idSuffix);
////            if (instancesId != null) {
////                element.setIdentifiers(instancesId);
////                hasInstances = true;
////            }
////            if (hasInstances)
////                count++;
////            
////            boolean isImported = ((Element) vertex.getObject()).getAccession()
////                    .startsWith("http:");
////            if (!isImported)
////                vertexCount++;
////            if (hasInstances && !isImported)
////                countWithoutImported++;
////            extractedInstances.remove(uri);
////            extractedInstances.remove(uri+idSuffix);
////        }
////        System.out.println(count + " instance sets, instances total: " + instancesCount
////        		+ " extractedInstances left: " + extractedInstances.size());
////        allCount += count;
////        allVertices += vertices.size();
////        allCountWithoutImported += countWithoutImported;
////        allVerticesWithoutImported += vertexCount;
        return schemaGraph;
    }
    
    private String replace(String string){
    	string = string.replaceAll("ä", "ae");
    	string = string.replaceAll("ö", "oe");
    	string = string.replaceAll("ü", "ue");
    	string = string.replaceAll("ß", "sz");
    	string = string.replaceAll("Ä", "Ae");
    	string = string.replaceAll("Ö", "Oe");
    	string = string.replaceAll("Ü", "Ue");
    	while (string.contains("  ")){
    		string = string.replace("  ", " ");
    	}
    	if (string.startsWith("\"") && string.endsWith("\"")){
    		string = string.substring(1,string.length()-1);
    	}
    	return string;
    }
//    
//    
////    private String addInstancesLabel(OWLOntology ontology, OWLIndividual individual, String uri, HashSet<String> stopWords, boolean label) throws OWLException{
////	    	String instanceOrg = getLabel(individual, ontology, label);
////	        if (instanceOrg != null){
////	        	instanceOrg = replace(instanceOrg);
////	        	String instance = instanceOrg;
////	//        	 HashSet<String> tmpInstancesAdd = null;
////	        	 ArrayList<String> tmpInstancesAdd = null;
////	        	 if (label){
////	        		 tmpInstancesAdd = instancesLabel.get(uri);
////	        	 } else {
////	        		 tmpInstancesAdd = instancesComment.get(uri);
////	        	 }
////	        	 
////	             if (tmpInstancesAdd == null)
////	//            	 tmpInstancesAdd = new HashSet<String>();
////	            	 tmpInstancesAdd = new ArrayList<String>();
////	             
////	//            tmpInstances.add(instance);
////	//             processLabelAndComment(stopWords, tmpInstancesAdd, instance);
////	             tmpInstancesAdd.add(instance);
////	            if (tmpInstancesAdd.size() > 0){
////	            	if (label){
////	            		instancesLabel.put(uri, tmpInstancesAdd);
////	            	} else {
////	            		instancesComment.put(uri, tmpInstancesAdd);
////	            	}
////	            }
////	        }
////	        return instanceOrg;
////	    }
//
//
//
    
    private void printStatisitics( OWLOntology ontology){
		Set individuals = ontology.getIndividualsInSignature();
		System.out.println("****\tindividuals\t"+individuals.size());
		for (Object object : individuals) {
			System.out.println(object.toString());
		}
//		System.out.println("****");
//		Set individualAxioms = ontology.getIndividualAxioms();
//		System.out.println("****\tindividualAxioms\t"+individualAxioms.size());
//		for (Object object : individualAxioms) {
//			System.out.println(object.toString());
//		}
		Set<OWLAxiom> axioms = ontology.getAxioms();
		System.out.println("****");
		System.out.println("****\taxioms\t"+axioms.size());
		for (OWLAxiom owlAxiom : axioms) {
			if (owlAxiom instanceof OWLSubClassOfAxiom // 115
				||	owlAxiom instanceof OWLSubObjectPropertyOfAxiom // 7
				||	owlAxiom instanceof OWLSubDataPropertyOfAxiom // 3
				|| owlAxiom instanceof OWLObjectPropertyRangeAxiom // 21
				|| owlAxiom instanceof OWLObjectPropertyDomainAxiom // 24
				|| owlAxiom instanceof OWLTransitiveObjectPropertyAxiom // 1
				|| owlAxiom instanceof OWLDeclarationAxiom // 108
				|| owlAxiom instanceof OWLDataPropertyRangeAxiom // 38
				|| owlAxiom instanceof OWLDataPropertyDomainAxiom // 37
					){ 
				continue;
			}
			
			if (owlAxiom instanceof OWLAnnotationAssertionAxiom // 241
					|| owlAxiom instanceof OWLClassAssertionAxiom // 111
					|| owlAxiom instanceof OWLDataPropertyAssertionAxiom // 161
					|| owlAxiom instanceof OWLObjectPropertyAssertionAxiom // 44
					){
//				System.out.println(owlAxiom.toString());
			}
		}
		System.out.println("****");
}


    private HashMap<String, ArrayList<String>> extractInstances(  int sourceid, String provider, 
            OWLOntology ontology, HashSet<String> stopWords,  HashMap<String, Element> verticesAccession) throws OWLException {
        HashMap<String, ArrayList<String>> extractedInstances = new HashMap<String, ArrayList<String>>();
        System.out.println("individuals: " + ontology.getIndividualsInSignature().size());
        OWLOntologyID ontoID = ontology.getOntologyID();
        System.out.println();
        String namespace= ontoID.getOntologyIRI().toString();
//        if (namespace==null){
//        	namespace=OWLParser.getNamespace(provider);
//        }
        printStatisitics(ontology);

        
        HashSet<String> uris = new HashSet<String>();
        HashSet<OWLIndividual> uriNull = new HashSet<OWLIndividual>();
        // alle Individuals (Instanzen von Klassen) der Ontology durchgehen
//        for (OWLIndividual individual : ontology.getIndividualsInSignature()) {
//        	dealWithIndividual(ontology, individual, uris, uriNull,namespace,verticesAccession,extractedInstances);
//        }
        Set<OWLClassAssertionAxiom> caa = ontology.getAxioms(AxiomType.CLASS_ASSERTION);
        for (OWLClassAssertionAxiom owlClassAssertionAxiom : caa) {
        	OWLIndividual individual = owlClassAssertionAxiom.getIndividual();
        	dealWithIndividual(ontology, individual, uris, uriNull,namespace,verticesAccession,extractedInstances);
		}
        System.out.println("uris: "+uris.size());
        System.out.println("uriNull: "+uriNull.size());
////        System.out.println(uris.toString().replace(", ", "\n"));
        return extractedInstances;
    }
    
    private void dealWithIndividual(OWLOntology ontology, OWLIndividual individual,HashSet<String> uris, HashSet<OWLIndividual> uriNull,
    		String namespace,  HashMap<String, Element> verticesAccession,HashMap<String, ArrayList<String>> extractedInstances){
    	 // URI der Klasse bestimmen
        String uri=null;
		try {
			uri = getEntityFromIndividual(individual, ontology);
		} catch (OWLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (uri==null){
        	uriNull.add(individual);
        	return;
        }
        if (namespace!=null && uri.indexOf(namespace)<0){
//        	System.out.println();
        }
        String connect = individual.toStringID();
        if (connect.indexOf('#')>0){
        	connect = connect.substring(connect.indexOf('#')+1);
        }
//        	//workaround for webdirectories
//        	if (connect.indexOf("_http")>0){
//        		connect = connect.substring(connect.indexOf("_http")+1);
//        	}
        String accession=uri;
        boolean accessionKey = false;
        
        if (accession.indexOf('#')>0){
//        		accession = accession.substring(accession.indexOf('#')+1);
        	uris.add(accession);
        }
        accessionKey = verticesAccession.containsKey(accession);
        	
        if (!accessionKey){
        	return;
        }
        int elementId = verticesAccession.get(accession).getId();

		// schon vorhandene Instanzen (Wert aus rdfs:label) der Klasse
		ArrayList<String> tmpInstances = extractedInstances.get(uri);
		// schon vorhandene Ids (rdf:about oder rdf:ID) der Klasse
		ArrayList<String> tmpInstancesId = extractedInstances.get(uri
				+ idSuffix);

		// ArrayLists anlegen, wenn noch nicht vorhanden
		if (tmpInstances == null)
			tmpInstances = new ArrayList<String>();
		if (tmpInstancesId == null)
			tmpInstancesId = new ArrayList<String>();
		// // Instanzwert einer Klasse sollte m�glichst das Label sein
		// String instance = getLabel(individual, ontology,true);
		String instance = null;
		// if (instance != null){
		// tmpInstances.add(instance);
		// if (accessionKey){
		// int id = importer.insertInstance(connect, elementId);
		// importer.insertInstanceValue(sourceid, id, null, instance);
		// }
		// }
		int id = -1;
		// ID, falls kein Label vorhanden
		if (instance == null) {
			// instance = getInstanceValueFromURI(individual.getURI());
			instance = getInstanceValueFromURI(individual.toStringID());
			if (instance == null) {
				// System.out.println();
			} else {
				// // //workaround for webdirectories
				// if (instance.indexOf("_http")>0){
				// instance =
				// instance.substring(instance.indexOf("_http")+1);
				// }
				// tmpInstancesId.add(instance);
				// tmpInstances.add(instance); // Nur beim Benchmark, sonst
				// in Id-ArrayList
				HashMap<String, ArrayList<String>> instances = instancesComplex
						.get(uri);
				if (instances == null) {
					instances = new HashMap<String, ArrayList<String>>();
				}
				ArrayList<String> values = instances.get("id");
				if (values == null) {
					values = new ArrayList<String>();
				}
				instance = replace(instance);
				values.add(instance);
				instances.put("id", values);
				// if (accessionKey){
				id = importer.insertInstance(connect, elementId, id, "id",
						instance);
				importer.updateInstance(id, id);
				// }
			}
		}
		try {
			getInstanceComplex(individual, ontology, elementId, uri, connect,
					instancesComplex);
		} catch (OWLException e) {
			e.printStackTrace();
		}
		// if (accessionKey){
		// instance = addInstancesLabel(ontology, individual, uri,stopWords,
		// true);
		// if (instance!=null){
		// if (id==-1){
		// id = importer.insertInstance(connect, elementId, id, "label",
		// instance);
		// importer.updateInstance(id, id);
		// } else {
		// importer.insertInstance(connect, elementId, id, "label",
		// instance);
		// }
		// }
		// instance = addInstancesLabel(ontology, individual, uri,stopWords,
		// false);
		// if (instance!=null){
		// if (id==-1){
		// id = importer.insertInstance(connect, elementId, id, "comment",
		// instance);
		// importer.updateInstance(id, id);
		// } else {
		// importer.insertInstance(connect, elementId, id, "comment",
		// instance);
		// }
		// }
		// }

		// ArrayLists (mit evtl. je einem Element mehr) wieder in die
		// extractedInstances legen
		if (tmpInstances.size() > 0)
			extractedInstances.put(uri, tmpInstances);
		if (tmpInstancesId.size() > 0)
			extractedInstances.put(uri + idSuffix, tmpInstancesId);
      

		
        // Instanzen der DataProperties des Individuals (leer, falls nicht vorhanden)
        HashMap<String, ArrayList<String>> dataProperties=null;
		try {
			dataProperties = extractDataPropertyValues(
			        individual, ontology);
		} catch (OWLException e) {
			e.printStackTrace();
		}
        // TODO: save instances with the same key -> belonging to the same real world object
        if (dataProperties!=null){
        	for (String key : dataProperties.keySet()) {
	            ArrayList<String> tmpDataPropertyInstances = extractedInstances
	                    .get(key);
	            if (tmpDataPropertyInstances == null) {
	                tmpDataPropertyInstances = new ArrayList<String>();
	            }
	            tmpDataPropertyInstances.addAll(dataProperties.get(key));
	            extractedInstances.put(key, tmpDataPropertyInstances);
	            	// import into database
	           	ArrayList instances = dataProperties.get(key);
	           	if (key.indexOf('#')>0){
	//           		key = key.substring(key.indexOf('#')+1);
	           	}
	           	if (verticesAccession.containsKey(key)){
	               	for (int i = 0; i < instances.size() && i<Repository.INSTANCES_MAX_PER_ELEMENT; i++) {					
	   					String current = (String) instances.get(i);
	   					int keyId = verticesAccession.get(key).getId();
	   					importer.insertInstance(connect, keyId, -1, null, current);
	               	}
	           	}
	        }
        }
        // Instanzen der ObjectProperties des Individuals (leer, falls nicht vorhanden)
        HashMap<String, ArrayList<String>> objectProperties=null;
		try {
			objectProperties = extractObjectPropertyValues(
			        individual, ontology);
		} catch (OWLException e) {
			e.printStackTrace();
		}
        // TODO: save instances with the same key -> belonging to the same real world object
       if (objectProperties!=null){ 
	        for (String key : objectProperties.keySet()) {
	            ArrayList<String> tmpObjectPropertyInstances = extractedInstances
	                    .get(key);
	            if (tmpObjectPropertyInstances == null) {
	                tmpObjectPropertyInstances = new ArrayList<String>();
	            }
	            tmpObjectPropertyInstances.addAll(objectProperties.get(key));
	            extractedInstances.put(key, tmpObjectPropertyInstances);
	            ArrayList instances = objectProperties.get(key);
	            if (instances!=null){    
	            	if (key.indexOf('#')>0){
	//            		key = key.substring(key.indexOf('#')+1);
	            	}
	               	if (verticesAccession.containsKey(key)){
	                	for (int i = 0; i < instances.size() && i<Repository.INSTANCES_MAX_PER_ELEMENT; i++) {					
	    					String current = (String) instances.get(i);
	    					int keyId = verticesAccession.get(key).getId();
		   					importer.insertInstance(connect, keyId, -1, null, current);
	                	}
	               	}
	            }
	        }
       }
    }

	    private String getLabel(OWLIndividual individual, OWLOntology ontology, boolean label)
	            throws OWLException {
	    	Set<OWLAnnotation> annotations = null;
	    	if (individual instanceof OWLNamedIndividualImpl){
	        		annotations = ((OWLNamedIndividualImpl)individual).getAnnotations(ontology);
	        	} else {
//	        		OWLDataFactory datafactory = ((OWLAnonymousIndividualImpl)individual).getOWLDataFactory();
//	        		OWLAnnotationProperty ap = datafactory.getOWLAnnotationProperty(IRI.create(((OWLAnonymousIndividualImpl)individual).toStringID()));
//	        		Set<OWLAnnotation> ann = ((OWLAnnotationPropertyImpl)ap).getAnnotations(ontology);
//	        		System.out.println("getLabel() unexpected individual " + individual);
	        		return null;
	        	}
	    	for (OWLAnnotation annotationInstance : annotations) {				
	            OWLAnnotationProperty annotationProperty = annotationInstance
	                    .getProperty();
	            if ( (label && "http://www.w3.org/2000/01/rdf-schema#label".equals(annotationProperty.toStringID()))
	            || (!label && "http://www.w3.org/2000/01/rdf-schema#comment".equals(annotationProperty.toStringID())))
	                try {
	                	OWLAnnotationValue concreteDataImpl = annotationInstance.getValue();
	                	if (concreteDataImpl instanceof OWLLiteralImpl){
	                		return ((OWLLiteralImpl)concreteDataImpl).getLiteral();
//	                	} else{
//	                		System.out.println("getLabel() unexpected concreteDataImpl " + concreteDataImpl);
	                	}
	                    // TODO: Mehrsprachigkeit!!!
//	                    concreteDataImpl.getLang();
	                } catch (ClassCastException e) {
	                    continue;
	                }
	        }
	        return null;
	    }

    private void getInstanceComplex(OWLIndividual individual, OWLOntology ontology, int elementId, String uri,
    		String connect, HashMap<String, HashMap<String, ArrayList<String>>> instancesComplex)
            throws OWLException {
    	if (uri==null){
    		return;
    	}
    	OWLNamedIndividualImpl ind = null;
    	if (individual instanceof OWLNamedIndividualImpl){
    		ind = (OWLNamedIndividualImpl) individual;
    	} else {
    		System.out.println("getInstanceComplex(): Error , OWLNamedIndividualImpl expected" );
    	}
    	HashMap<String, ArrayList<String>> instances = instancesComplex.get(uri);
    	if (instances==null){
    		instances  = new HashMap<String, ArrayList<String>>();
    	}
    	if (ind!=null){
	    	Set<OWLAnnotation> annotations = ind.getAnnotations(ontology); 
	
	        int id = -1;
	        for (OWLAnnotation annotationInstance : annotations) {
	        	OWLAnnotationProperty annotationProperty = annotationInstance.getProperty();
	
	            // "http://www.w3.org/2000/01/rdf-schema#label"
	            // "http://www.w3.org/2000/01/rdf-schema#comment"
	            String annotationLabel = annotationProperty.toStringID();
	//
	            if (annotationLabel.indexOf("#")>-1){
	            	annotationLabel = annotationLabel.substring(annotationLabel.indexOf("#")+1);
	            }
	             ArrayList<String> values = instances.get(annotationLabel);
	            if (values==null){
	            	values = new  ArrayList<String>();
	            }
	//          //   TODO: Mehrsprachigkeit
	//            concreteDataImpl.getLang();
	            String value = annotationInstance.getValue().toString();
	            value = replace(value);
	            values.add(value);
	            instances.put(annotationLabel, values);
	            
	   		 if (id==-1){
				 id = importer.insertInstance(connect, elementId, id, annotationLabel, value);
				 importer.updateInstance(id, id);
			 	} else {
				 importer.insertInstance(connect, elementId, id, annotationLabel, value);
			 	}
	        }
	        instancesComplex.put(uri, instances);
    	}
    }

    private String getInstanceValueFromURI(String uri) {
        if (uri == null)
            return null;
        int index = uri.lastIndexOf("#");
        if (index < 0)
            return null;
        String instanceValue = uri.substring(index+1);
        return instanceValue;
    }
 
//    
    private String getEntityFromIndividual(OWLIndividual individual,
            OWLOntology ontology) throws OWLException {
        Set types = individual.getTypes(ontology);
        // Klassen
        if (types != null && types.size() > 0) {
            OWLClass owlClass = (OWLClass) types.iterator().next();
            return owlClass.getIRI().toString();
        }
        return null;
    }

    private HashMap<String, ArrayList<String>> extractDataPropertyValues(
            OWLIndividual individual, OWLOntology ontology) throws OWLException {
        HashMap<String, ArrayList<String>> extractedInstances = new HashMap<String, ArrayList<String>>();

        // aus jedem Individual die dataProperties holen
        Map<OWLDataPropertyExpression, Set<OWLLiteral>> dataPropertyValues = individual.getDataPropertyValues(ontology);
        if (dataPropertyValues == null || dataPropertyValues.isEmpty()) {
            return extractedInstances;
        }

        Set<OWLDataPropertyExpression> dataPropertyValuesKeys = dataPropertyValues.keySet();
        for (OWLDataPropertyExpression dataProperty : dataPropertyValuesKeys) {
            // zu jeder DataProperty die Werte holen
            Set<OWLLiteral> dataPropertiesValues = dataPropertyValues.get(dataProperty);

            if (dataPropertiesValues == null || dataPropertiesValues.isEmpty()){
                continue;
            }
//            Iterator valuesIt = dataPropertiesValues.iterator();
            ArrayList<String> tmpInstances = new ArrayList<String>();

            for (OWLLiteral concreteDataImpl : dataPropertiesValues) {
                // zu jedem Wert die URI in extractedInstances speichern
            	String text = concreteDataImpl.getLiteral();
//            	OWLDatatype datatype = concreteDataImpl.getDatatype();
//            	  if (datatype!=null && datatype.toString().equals("xsd:string")){
                    tmpInstances.add(text);
//            	  } else {
//            		  System.out.println("extractDataPropertyValues() unexpected datatype " + datatype);
//            	  }
            }
            if (dataProperty instanceof OWLDataProperty){            	
            	extractedInstances.put(((OWLDataProperty)dataProperty).toStringID(),
                    tmpInstances);
            } else {
            	  System.out.println("extractDataPropertyValues() unexpected dataProperty " + dataProperty.getClass());
            }
        }

        return extractedInstances;
    }
    

    private HashMap<String, ArrayList<String>> extractObjectPropertyValues(
            OWLIndividual individual, OWLOntology ontology) throws OWLException {
        HashMap<String, ArrayList<String>> extractedInstances = new HashMap<String, ArrayList<String>>();

        Map<OWLObjectPropertyExpression, Set<OWLIndividual>> objectPropertyValues = individual.getObjectPropertyValues(ontology);
        if (objectPropertyValues == null || objectPropertyValues.isEmpty()) {
            return extractedInstances;
        }
//        // TODO: when is this needed???????????????????
        Set<OWLObjectPropertyExpression> objectPropertyValuesKeys = objectPropertyValues.keySet();
        for (OWLObjectPropertyExpression objectProperty : objectPropertyValuesKeys) {
        	Set<OWLIndividual> objectPropertiesValues = objectPropertyValues
                    .get(objectProperty);

            if (objectPropertiesValues == null
                    || objectPropertiesValues.isEmpty()){
                continue;
            }

            ArrayList<String> tmpInstances = new ArrayList<String>();
//            ArrayList<String> tmpInstancesId = new ArrayList<String>();
           for (OWLIndividual individualImpl : objectPropertiesValues) {

//           	Set<OWLClassAssertionAxiom> caa = ontology.getClassAssertionAxioms(individualImpl);
//           	Set<OWLDataPropertyAssertionAxiom> dpaa = ontology.getDataPropertyAssertionAxioms(individualImpl);
           	
                // zu jedem Wert die URI in extractedInstances speichern
                String first = getLabel(individual, ontology, true);
                String firstId = null;
                if (individual instanceof OWLNamedIndividual){
                	firstId = ((OWLNamedIndividual)individual).toStringID();
                	if (firstId.contains("#")){
                		firstId = firstId.substring(firstId.indexOf("#")+1);
                	}
                } else {
                	System.out.println();
                }
//                if (firstId == null)
//                    firstId = getInstanceValueFromURI(individual.getAnonId());
                if (first == null && firstId == null)
                    continue;
//
                String second = getLabel(individualImpl, ontology, true);
                String secondId = null;
                if (individualImpl instanceof OWLAnonymousIndividual){
                	secondId = ((OWLAnonymousIndividual)individualImpl).toStringID();
                	if (secondId.contains("#")){
                		secondId = secondId.substring(secondId.indexOf("#")+1);
                	}
                } else {
                	System.out.println();
                }
//                if (secondId == null)
//                    secondId = getInstanceValueFromURI(individualImpl
//                            .getAnonId());
                if (second == null && secondId == null)
                    continue;
//
                if (first != null && second != null) {
                    String value = first + " " + second;
                    tmpInstances.add(value);
                }
                if (firstId != null && secondId != null) {
                    String value = firstId + " " + secondId;
//                    tmpInstancesId.add(value);
                    tmpInstances.add(value);
                }
            }
            extractedInstances.put(((OWLObjectPropertyImpl)objectProperty).getIRI().toString(),
                    tmpInstances);
////            extractedInstances.put(objectProperty.getURI().toString()
////                    + idSuffix, tmpInstancesId);
        }
        return extractedInstances;
    }
//
//    private void printExtractedInstances(
//            HashMap<String, ArrayList<String>> extractedInstances) {
//
//        for (String key : extractedInstances.keySet()) {
//            System.out.println(key + ": " + extractedInstances.get(key));
//            System.out.println("-----");
//        }
//        System.out.println("Gesamt: " + extractedInstances.keySet().size());
//    }
//    
//	
//	public static void saveToFile(String _fileName, String _text, boolean _append){
//	    if (_fileName==null) return;
//	    PrintStream out=null;
//	    try { out = new PrintStream(new FileOutputStream(_fileName, _append), true); }
//	    catch(FileNotFoundException e) {
//	      System.out.println("Error opening output file :" + e.getMessage());
//	    }
//	    if (_text==null) return;
//	    out.println(_text);
//	    out.close();
//	    out=null;
//	}
//	
////
////    private HashSet<String> importStopWords(){
////    	String fileName = "./Sources/ontologies/stopwoerter.txt";
////	    HashSet<String> stopWords = new HashSet<String>();
////		try {
////		BufferedReader in = new BufferedReader(new FileReader(fileName));
////	    String line;
//////	    String newFileName = _fileName.replaceFirst(".txt", "_Real.txt");
//////	    GoogleSites.saveToFile(newFileName, null,false);	
////		while ((line = in.readLine()) != null) {
////	    	if (line.length()<1 || line.startsWith("#")){
////	    		continue;
////	    	}
////	    	String[] current = line.split(" ");
////	    	for (int i = 0; i < current.length; i++) {
////	    		stopWords.add( current[i]);
////			}	    	
////	    }
////		} catch (FileNotFoundException e) {
////	    	e.printStackTrace();
////	    } catch (IOException e) {
////	    	e.printStackTrace();
////		}	
////	    return stopWords;
////    }
}
