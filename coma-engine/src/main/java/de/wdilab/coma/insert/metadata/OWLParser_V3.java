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

package de.wdilab.coma.insert.metadata;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.vocab.Namespaces;

import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationAssertionAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLClassAssertionImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyAssertionAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;
import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Source;

/**
 * This class extracts metadata from an owl file as a model.
 * 
 * @author Hong Hai Do, Sabine Massmann, Viet Hung Do
 */
public class OWLParser_V3 extends InsertParser {
	String IGNORE_W3ORG = "http://www.w3.org/";
	String INGORE_ERROR = "http://org.semanticweb.owlapi/error";
	String IGNORE_PURL = "http://purl.org/";
	
	// load only classes (true) or also properties (false)
	boolean conceptHiearchyOnly = false;
	// whether labels if any should be used as element.name  instead of rdf:IDs
	// TODO try to recognize which is better (anatomy/webdirectory true, benchmark?)
	boolean labelsPreferred = true;

	// load only classes and properties from a certain iri
	String iriFilter = null;
	// whether element names should contain <url.org> to distinguish external URIs.	
	boolean stressExternalIRIs = false; 
	
	Set<IRI> allIRIs;
	List<String> shortNames;
	Map<String, String> known;
	String[] names = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
			"l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
			"y", "z", };
	
	public OWLParser_V3(boolean dbInsert){
		super(dbInsert, Source.TYPE_ONTOLOGY);
	}
	
	public OWLParser_V3(boolean dbInsert, boolean conceptHiearchyOnly, boolean labelsPreferred, String iriFilter){
		super(dbInsert, Source.TYPE_ONTOLOGY);
		this.conceptHiearchyOnly=conceptHiearchyOnly;
		this.labelsPreferred=labelsPreferred;
		this.iriFilter=iriFilter;
	}
	
	public boolean isLabelsPreferred() {
		return labelsPreferred;
	}

	public void setLabelsPreferred(boolean labelsPreferred) {
		this.labelsPreferred = labelsPreferred;
	}

	@Override
	public int parseSingleSource(String filename, String schemaName,
			String author, String domain, String version, String comment) {
		beforeParse(schemaName, filename);
	    // parse information into temp tables (parse_<tablename>)
		parse();
		// additional: author, domain, version
		setInformation(author, domain, version, comment);
		// copy information into real tables (<tablename>), delete temp tables, close statements
		afterParse();
	    return source_id;
	}
	
	public int parseSingleSource(String filename, String schemaName, OWLOntology ontology) {
		beforeParse(schemaName, filename);
	    // parse information into temp graph
		loadOntology(ontology);
		// copy information into real tables (<tablename>), delete temp tables, close statements
		afterParse();
	    return source_id;
	}
	
	  void parse() {
			//Load Ontology
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		 	IRI iri = IRI.create(provider);
		 	OWLOntology ontology = null;
			try {
				ontology = manager.loadOntology(iri);
//				 OWLOntologyFormat f = 
//				 manager.getOntologyFormat(ontology);s
			} catch (Throwable e2) {
				if (!provider.startsWith("http") && !provider.startsWith("ftp") &&!provider.contains("file:") && e2.getLocalizedMessage().contains("Could not find an appropriate factory to load ontology from ontology document")){
					provider = "file:///" + provider;
					iri = IRI.create(provider);
					try {
						ontology = manager.loadOntology(iri);
					} catch (OWLOntologyCreationException e) {
						e.printStackTrace();
					}
				} else {
					e2.printStackTrace();	
				}
			}
			loadOntology(ontology);
	  }
	  

		/**
		 * load all iris from ontology into variable allIRIs
		 * @param ontology
		 */
		private void setAllIRIs( OWLOntology ontology ){
			allIRIs = new HashSet<IRI>();
			Set<OWLEntity> enties = ontology.getSignature();
		
			for(Iterator eit = enties.iterator(); eit.hasNext();)
			{
				OWLEntity ent = (OWLEntity) eit.next();
				
		        String uri = ent.getIRI().toURI().toString();
		        if(!uri.startsWith("http://org.semanticweb.owlapi/error")){
		        	allIRIs.add(ent.getIRI());		
		        }
			}	
		}
		
		/**
		 * generates a list of namespaces 
		 */
		private void generateShortNames() {
			/* Generates a list of namespaces. */
			shortNames = new ArrayList<String>();
			known = new HashMap<String, String>();
			known.put(Namespaces.OWL.toString(), "owl");
			known.put(Namespaces.RDF.toString(), "rdf");
			known.put(Namespaces.RDFS.toString(), "rdfs");
			known.put(Namespaces.XSD.toString(), "xsd");
			
			
			for (Iterator it = allIRIs.iterator(); it.hasNext();) {
				try {
					IRI iri = (IRI) it.next();
					if (iri.toURI().getFragment() != null) {
						String ssp = new URI(iri.toURI().getScheme(), iri.toURI()
								.getSchemeSpecificPart(), null).toString();
						/* Trim off the fragment bit if necessary */
						if (!ssp.endsWith("#")) {
							ssp = ssp + "#";
						}
						if (!known.keySet().contains(ssp)
								&& !shortNames.contains(ssp)) {
							shortNames.add(ssp);
						}
					}
				} catch (URISyntaxException ex) {}
			}
		}
	  
	    /**
	     * check whether the namespace is known 
	     */
		public boolean checkKnownNamespace(String ssp) {
			if (known == null || ssp == null)
				return false;
			if (!ssp.endsWith("#")) {
				ssp = ssp + "#";
			}
			if (known.containsKey(ssp))
				return true;
			return false;
		}
		
		/**
		 * get namespace of a iri
		 * @param iri
		 * @param file
		 * @param namespace
		 * @return
		 */
		public static String getNamespace(IRI iri, String file, String namespace) {
			if (iri == null)
				return null;
			URI uri = iri.toURI();
			try {
				String aProp = "<owl:AnnotationProperty";
				if (namespace!=null && namespace.indexOf(aProp)>-1){
					namespace =namespace.substring(0,namespace.indexOf(aProp));
				}
				if (uri.getSchemeSpecificPart().equals(file)){
					uri = new URI(uri.getScheme(), namespace, uri.getFragment());
				}
				if (uri.getFragment() == null || uri.getFragment().equals("")) {
					// It's not of the form http://xyz/path#frag
					return uri.toString();
				}
				// It's of the form http://xyz/path#frag
				String ssp = new URI(uri.getScheme(), uri.getSchemeSpecificPart(),
						null).toString();

				// Fix problems with other namespaces
				if (ssp.startsWith("http://www.w3.org/TR/xmlschema-2"))
					ssp = "http://www.w3.org/2001/XMLSchema";
				
				return ssp;
			} catch (URISyntaxException ex) {}
			String uriString = uri.toString();
			if(uriString.contains("#")){
				uriString = uriString.substring(0, uriString.indexOf("#"));
			}
			return uriString;
		}
		
	    private void loadOntology(OWLOntology ontology) {
	    	
	    	// TODO check correctness and usefulness of the following two functions
			setAllIRIs(ontology);
			generateShortNames();
	    	
	    	Set<OWLClass> classes = ontology.getClassesInSignature();
	    	Set<OWLObjectProperty> objectProps = ontology.getObjectPropertiesInSignature();
	    	Set<OWLDataProperty> dataProps = ontology.getDataPropertiesInSignature();
	    	Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature();
			
			// AnnotationAssertion SubClassOf Declaration
			Set<OWLAxiom> axioms = ontology.getAxioms();
			HashMap<String, String> annotation = new HashMap<String, String>();
			if (axioms!=null && !axioms.isEmpty()){
				for (OWLAxiom axiom : axioms) {
					if (axiom instanceof OWLAnnotationAssertionAxiomImpl){
						OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiomImpl)axiom).getSubject();
						OWLAnnotationValue value = ((OWLAnnotationAssertionAxiomImpl)axiom).getValue();
						if (value instanceof OWLLiteralImpl){
							String literal =((OWLLiteralImpl)value).getLiteral();
							annotation.put(subject.toString(), literal);
						}
//		        	 } else { 
//		        		 System.out.println("OWLParserNew.loadOntology not yet supported ");
					}
				}
			}
	    	System.out.println("classes\t" + classes.size());
	    	System.out.println("objectProps\t" + objectProps.size());
	    	System.out.println("dataProps\t" + dataProps.size());
	    	System.out.println("individuals\t" + individuals.size());
			System.out.println("annotation\t"+annotation.size());
			System.out.println();
			
			
			OWLOntologyID ontologyID = ontology.getOntologyID();
			if (ontologyID.getOntologyIRI()!=null){
			String url = ontologyID.getOntologyIRI().toString();
				if (url.equals(provider)){
					HashSet<String> possible = new HashSet<String>();
					for (OWLClass clazz : classes) {
						String iri = clazz.getIRI().toString();
						if (iri.contains("#") && !iri.contains("w3.org")){
							iri = iri.substring(0, iri.indexOf("#"));
							possible.add(iri);
						}
					}
					if (!possible.isEmpty()){
						url = possible.toString().replace("[", "").replace("]", "");
					}
				}
				updateSourceURL(source_id, url);
			} else {
				updateSourceURL(source_id, ontologyID.toString());
			}

			
	    	HashMap<String, Integer> classIDs = new HashMap<String, Integer>();
			for (Iterator it = orderedEntities(ontology, classes).iterator(); it.hasNext();) {
				OWLClass next =  (OWLClass) it.next() ;
				int id = loadClass(ontology, next, annotation);
				if (id!=Element.UNDEF){
					classIDs.put(next.getIRI().toString(), id);
				}
			}
			for (OWLClass next : classes) {
				loadHierarchy(ontology, next, classIDs);
			}
			
			HashMap<String, Integer> propertyIDs = new HashMap<String, Integer>();
			if (!conceptHiearchyOnly) {
			
				for (Iterator<OWLObjectProperty> it = orderedEntities(ontology, objectProps)
						.iterator(); it.hasNext();) {
					OWLObjectProperty next = it.next() ;
					int id = loadObjectProperty(ontology, next, annotation, classIDs);
					if (id!=Element.UNDEF){
						propertyIDs.put(next.getIRI().toString(), id);
					}
				}
	    	
				for (Iterator<OWLDataProperty> it = orderedEntities(ontology, dataProps)
						.iterator(); it.hasNext();) {
					OWLDataProperty next = it.next() ;
					loadDataProperty(ontology, next, annotation, classIDs);
				}
						    	
				for (Iterator<OWLAxiom> it = orderedEntities(ontology, axioms)
						.iterator(); it.hasNext();) {
					OWLAxiom axiom = it.next() ;
					if (axiom instanceof OWLAnnotationAssertionAxiomImpl || axiom instanceof OWLClassAssertionImpl || axiom instanceof OWLDataPropertyAssertionAxiomImpl){
						continue;
					}
					if (axiom instanceof OWLObjectPropertyDomainAxiom &&  ((OWLObjectPropertyDomainAxiom)axiom).getDomain().toString().equals("owl:Thing")){
		        		 continue;
		        	 }
		        	 if (axiom instanceof OWLObjectPropertyRangeAxiom){
		        		 OWLObjectPropertyExpression property = ((OWLObjectPropertyRangeAxiom)axiom).getProperty();
		        		 String propertyIri = ((OWLNamedObject) property).getIRI().toString();
		        		 OWLClassExpression range = ((OWLObjectPropertyRangeAxiom)axiom).getRange();
		        		 if (range instanceof OWLNamedObject){
			        		 String rangeIri = ((OWLNamedObject) range).getIRI().toString();
			        		 if (!rangeIri.contains(IGNORE_W3ORG)){
			        			 int propertyId = propertyIDs.get(propertyIri);
			        			 int rangeId = classIDs.get(rangeIri.toString());
			        			 insertLink(sourcerel_id, propertyId, rangeId);
			        		 }
		        		 } else{
		        			 System.out.println("OWLObjectPropertyRangeAxiom: range not yet supported - " + range.getClass());
		        		 }		        			 
		        		 continue;
		        	 }
		        	 if (axiom instanceof OWLObjectPropertyDomainAxiom){
		        		 OWLObjectPropertyExpression property = ((OWLObjectPropertyDomainAxiom)axiom).getProperty();
		        		 String propertyIri = ((OWLNamedObject) property).getIRI().toString();
		        		 OWLClassExpression domain = ((OWLObjectPropertyDomainAxiom)axiom).getDomain();
		        		 if (domain instanceof OWLNamedObject){
			        		 String domainIri = ((OWLNamedObject) domain).getIRI().toString();
			        		 if (!domainIri.contains(IGNORE_W3ORG)){
			        			 int propertyId = propertyIDs.get(propertyIri);
			        			 int domainId = classIDs.get(domainIri.toString());
			        			 insertLink(sourcerel_id, domainId, propertyId);
			        		 }
		        		 } else {
		        			 System.out.println("OWLObjectPropertyDomainAxiom: range not yet supported - " + domain.getClass());
		        		 }
		        		 continue;
		        	 }
		        	 if (axiom instanceof OWLSubObjectPropertyOfAxiom){
		        		 OWLObjectPropertyExpression subProperty = ((OWLSubObjectPropertyOfAxiom)axiom).getSubProperty();
		        		 String subPropertyIri = ((OWLObjectPropertyImpl) subProperty).getIRI().toString();
		        		 OWLObjectPropertyExpression superProperty = ((OWLSubObjectPropertyOfAxiom)axiom).getSuperProperty();
		        		 String superPropertyIri = ((OWLNamedObject) superProperty).getIRI().toString();
		        		 if (!superPropertyIri.contains(IGNORE_W3ORG)){
		        			 int subPropertyId = propertyIDs.get(subPropertyIri.toString());
		        			 int superPropertyId = propertyIDs.get(superPropertyIri.toString());
		        			 insertLink(sourcerel_id, superPropertyId, subPropertyId);
		        		 }
		        		 continue;
		        	 }
//		        	 System.out.println(axiom);
				}
								
			}
	    }
	    
	    
		/**
		 * filter iri
		 * @param iri
		 * @return
		 */
		private boolean filtered(String iri) {
			if (iriFilter == null) {
				return false; // nothing gets filtered (which should be the case
				// when uriFilter==null.
			}
			// if to  be included return false = not filtered.
			boolean re = (!iri.toString().startsWith(iriFilter.toString()));
			if (re) {
				System.out.println("Filtered (skipping) " + iri);
			}
			return re;
		}
	    
		/**
		 * data property is treated		
		 * @param ontology
		 * @param prop
		 */
	    private void loadDataProperty(OWLOntology ontology, OWLDataProperty prop, HashMap<String, String> annotation, HashMap<String, Integer> classIDs) {
	         IRI iri = prop.getIRI();
	         String acc = iri.toString();
	         if (acc.contains(IGNORE_PURL)){
	        	 return;
	         }
	         if (filtered(acc) ) {
	        	 return;
	         }
//	         // TODO check if needed/useful
//	         String namespace = getNamespace(iri, shortForms.getFile(), shortForms.getNamespace());
//	         String key = shortForms.shortForm(iri);
//	         if (!(checkKnownNamespace(namespace)) // also for range/domain iri
	         
	         
	         String name = getFullName(iri, ontology, prop);
	         String description = getComment(ontology, prop, annotation);
	         String synonyms = getSynoynms(ontology, prop, annotation);
	         // e.g. Reference (domains) contains an abstract (name) from type string (ranges)
	         String type = null;
	         String typespace = null;
	         Set<OWLDataRange> ranges = prop.getRanges(ontology);
	         for (OWLDataRange range : ranges) {
	        	 if (range instanceof OWLDatatype) {
		        	 type =  range.toString();
		        	 IRI ranIri = ((OWLDatatype) range).getIRI();
		        	 typespace = ranIri.toString();
		        	 System.out.print("");
//	        	 } else if (range instanceof OWLNamedObject) {
//		        	 // TODO check if possible
//	        		 // range is most likely OWL-Class
//	        		 IRI rangeIri = ((OWLNamedObject) range).getIRI();
//	        		 int rangeId = classIDs.get(rangeIri.toString());
//	        		 insertLink(sourcerel_id, id, rangeId);
//	        	 } else if (range instanceof OWLNaryBooleanClassExpression) {
//		        	 // TODO check if possible
//	     			for (Iterator subIt = ((OWLNaryBooleanClassExpression) ran)
//	    					.getOperands().iterator(); subIt.hasNext();) {
//	    				OWLClassExpression subRan = (OWLClassExpression) subIt.next();
//	    				if (subRan instanceof OWLNamedObject) {
//	    					IRI subIri = ((OWLNamedObject) subRan).getIRI();
//	   	        		 int rangeId = classIDs.get(subIri.toString());
//	    				}
//	     			}
	        	 } else {
	        		 System.out.println("OWLParserNew.loadDataProperty not yet supported " + range.toString());
	        	 }
	         }
	         
	         int id = insertObject( source_id, acc, name, type, typespace, Element.KIND_ELEMENT, description, synonyms);
	         Set<OWLClassExpression> domains = prop.getDomains(ontology);
	      // Linking domain to property
	         for (OWLClassExpression domain : domains) {
		     		if (domain instanceof OWLNamedObject) {
		     			// Domain is most likely OWL-Class
		    			IRI domainIri = ((OWLNamedObject) domain).getIRI();

                         int domainId;
                         try {
		    			 domainId = classIDs.get(domainIri.toString());
                         }
                         catch( Exception e) {
                             return;
                         }

		  				insertLink(sourcerel_id, domainId, id);
		    		} else if (domain instanceof OWLNaryBooleanClassExpression) {
		    			 // TODO check if possible
		    			for (Iterator subIt = ((OWLNaryBooleanClassExpression) domain)
		    					.getOperands().iterator(); subIt.hasNext();) {
		    				OWLClassExpression subDom = (OWLClassExpression) subIt.next();
		    				if (subDom instanceof OWLNamedObject) {
		    					IRI subIri = ((OWLNamedObject) subDom).getIRI();

                                int domainId;
                                 try {
				    			    domainId = classIDs.get(subIri.toString());
                                 }
                                 catch( Exception e) {
                                      return;
                                 }
				  				insertLink(sourcerel_id, domainId, id);
		    				}
		    			}
		        	 } else {
		        		 System.out.println("OWLParserNew.loadDataProperty not yet supported "  + domain.toString());
		    		}
			}
	    }
	    
	    
		/**
		 * load ObjectProperties into database 		
		 * @param ontology
		 * @param prop
		 * @throws OWLException
		 * @throws  
		 */
	    private int loadObjectProperty(OWLOntology ontology, OWLObjectProperty prop, HashMap<String, String> annotation, HashMap<String, Integer> classIDs){
		         IRI iri = prop.getIRI();
		         String acc = iri.toString();
		         
		     	if (filtered(acc)) {
		     		return Element.UNDEF;
		    	}
				if (acc.startsWith(INGORE_ERROR)
						|| acc.contains(IGNORE_W3ORG)) {
					return Element.UNDEF;
				}
//		     	// TODO check if needed/useful		     	
//		     	String namespace = getNamespace(iri, shortForms.getFile(), shortForms.getNamespace());
//		    	if (!known.containsKey(namespace+"#"))
		     	
		     	//characteristic of property
		     	String type = null;
	        	 if (prop.isSymmetric(ontology)){
	        		 type = "symmetric";
	        	 } else if (prop.isAsymmetric(ontology)){
	        		 type = "asymmetric";
	        	 } else if (prop.isReflexive(ontology)){
	        		 type = "reflexiv";
	        	 } else if (prop.isReflexive(ontology)){
	        		 type = "irreflexiv";
	        	 } else	if(prop.isFunctional(ontology)){ 
	        		 type = "funtional";
	        	 } else if (prop.isInverseFunctional(ontology)){
	        		 type = "inverse functional";
	        	 } else if (prop.isTransitive(ontology)){
	        		 type = "transitive";
	        	 }
		     	
		         String name = getFullName(iri, ontology, prop);
		         String description = getComment(ontology, prop, annotation);
		         String synonyms = getSynoynms(ontology, prop, annotation);
		         
//		         // TODO check if needed/useful	
//		         // equivalent properties are treated
//		         if(prop.getEquivalentProperties(ontology) != null){
//		        	 synonyms = "";
//		        	 for(OWLObjectPropertyExpression p: prop.getEquivalentProperties(ontology)){
//		        		 synonyms += getFullName(iri , ontology, (OWLNamedObject) p);
//		        	 }
//		         }
		         		         
		         Set<OWLClassExpression> domains = prop.getDomains(ontology);
		         Set<OWLClassExpression> ranges = prop.getRanges(ontology);

		        
		        int id = insertObject( source_id, acc, name, type, null, Element.KIND_ELEMENT, description, synonyms);
			       
		         if (domains.isEmpty() && ranges.isEmpty()){
		        	 return id;
		         }
		        
		     	for (OWLClassExpression domain : domains) {
		     		if (domain instanceof OWLNamedObject) {
		     			// Domain is most likely OWL-Class
		    			String domainIri = ((OWLNamedObject) domain).getIRI().toString();
		    			if (!domainIri.contains(IGNORE_W3ORG)){
			    			int domainId = classIDs.get(domainIri.toString());
			  				insertLink(sourcerel_id, domainId, id);
		    			}
		    		} else if (domain instanceof OWLNaryBooleanClassExpression) {
		    			// TODO check if correct & needed, maybe add type		    			
		    			for (Iterator subIt = ((OWLNaryBooleanClassExpression) domain)
		    					.getOperands().iterator(); subIt.hasNext();) {		    				
		    				OWLClassExpression subDom = (OWLClassExpression) subIt.next();
		    				if (subDom instanceof OWLNamedObject) {
		    					String subIri = ((OWLNamedObject) subDom).getIRI().toString();
				    			if (!subIri.contains(IGNORE_W3ORG)){
					    			int domainId = classIDs.get(subIri);
					  				insertLink(sourcerel_id, domainId, id);
				    			}
		    				}
		    			}
		        	 } else {
		        		 System.out.println("OWLParserNew.loadObjectProperty not yet supported " + domain.toString());
		    		}
				}
		     	for (OWLClassExpression range : ranges) {
		     		if (range instanceof OWLNamedObject) {
		     			// Range is most likely OWL-Class
		    			String rangeIri = ((OWLNamedObject) range).getIRI().toString();
		    			if (!rangeIri.contains(IGNORE_W3ORG)){
			    			int rangeId = classIDs.get(rangeIri.toString());
			  				insertLink(sourcerel_id, id, rangeId);
		    			}
		    		} else if (range instanceof OWLNaryBooleanClassExpression) {
		    			// TODO check if correct & needed, maybe add type
		    			for (Iterator subIt = ((OWLNaryBooleanClassExpression) range)
		    					.getOperands().iterator(); subIt.hasNext();) {		    				
		    				OWLClassExpression subRan = (OWLClassExpression) subIt.next();
		    				if (subRan instanceof OWLNamedObject) {
		    					String subIri = ((OWLNamedObject) subRan).getIRI().toString();
				    			if (!subIri.contains(IGNORE_W3ORG)){
					    			int rangeId = classIDs.get(subIri);
					    			insertLink(sourcerel_id, id, rangeId);
				    			}
		    				}
		    			}
		        	 } else {
		        		 System.out.println("OWLParserNew.loadObjectProperty not yet supported "  + range.toString());
		    		}
				}
		     	return id;
	    }
	
		/**
		 * Return a collection, ordered by the URIs.
		 * @param ontology
		 * @param entities
		 * @return
		 */
		private static SortedSet orderedEntities(final OWLOntology ontology, Set entities) {
		SortedSet ss = new TreeSet(new Comparator() {
			public int compare(Object o1, Object o2) {
				try {
					if (o1 instanceof OWLEntity && o2 instanceof OWLEntity){
						String uri1 = ((OWLEntity) o1).getIRI().toURI().toString();
						String uri2 = ((OWLEntity) o2).getIRI().toURI().toString();
						return (uri1.compareTo(uri2));
					}
					return o1.toString().compareTo(o2.toString());
				} catch (Exception ex) {
					return o1.toString().compareTo(o2.toString());
				}
			}
		});
		ss.addAll(entities);
		return ss;
	}
	    
		
		/**
		 * load OWL-Classes with their information into graph/repository  		
		 * @param ontology
		 * @param clazz
		 * @param annotation
		 * @return class id
		 */
		private int loadClass(OWLOntology ontology, OWLClass clazz,
				HashMap<String, String> annotation) {
			IRI iri = clazz.getIRI();
			String acc = iri.toString();
			// TODO check namespace as alternative
//			String key = shortForms.shortForm(iri);
//			String namespace = getNamespace(iri, shortForms.getFile(), shortForms.getNamespace());
						
			if (acc.startsWith(INGORE_ERROR)
					|| acc.contains(IGNORE_W3ORG)) {
				return Element.UNDEF;
			}
	
			if (filtered(acc) ) {
				return Element.UNDEF;
			}
			if (!acc.contains("/") && !acc.contains("\\") && shortNames.size()==1){
				// not a valid accession
				return Element.UNDEF;
			}
			String name = getFullName(iri, ontology, clazz);
			String description = getComment(ontology, clazz, annotation);
			String synonyms = getSynoynms(ontology, clazz, annotation);
			int id = insertObject(source_id, acc, name, null, null,
					Element.KIND_ELEMTYPE, description, synonyms);
	
			Set<OWLClassExpression> eqClasses = clazz
					.getEquivalentClasses(ontology);
			if (eqClasses != null && eqClasses.size() > 0) {
				// TODO check!!!
				System.out.print("\teqClasses\t" + eqClasses.size());
//		    	  for (Iterator it = clazz.getEquivalentClasses(ontology).iterator(); it.hasNext();) 
//		    	  {
//		    		
//		    	   OWLClassExpression c = (OWLClassExpression) it.next();
//		    	   //c is a class
//		    	   if(c instanceof OWLClass )
//		    	   {
//		    	   String synCandidat = getFullName( ((OWLNamedObject) c).getIRI(), ontology, (OWLNamedObject) c);
//		    	   synonyms += synCandidat;
//		    	   }
//		    	   //c is a logical class construction
//		    	   else
//		    		   if(c instanceof OWLNaryBooleanClassExpression )
//		    		   {
//		    			   Object[] classes =  c.getClassesInSignature().toArray();
//		    			   for(int i = 0; i < classes.length; i++)
//		    			   {
//		    				   OWLClass subC = (OWLClass) classes[i];
//		    				   String synCandidat = getFullName(((OWLNamedObject) subC).getIRI(),  ontology, subC);
//		    		    	   synonyms += synCandidat; 
//		    		    	   if(i < classes.length - 1)
//		    		    		   synonyms += ", ";
//		    		    		   
//		    			   }
//		    		   }
//		    	  
//		    	  }
			}
	
			System.out.print("");
			return id;
		}
	    
	    /**
	     * load super class relationships into graph/repository
	     * @param ontology
	     * @param clazz
	     * @param classIDs
	     */
	    private void loadHierarchy(OWLOntology ontology, OWLClass clazz, HashMap<String, Integer> classIDs){
	    	String iri = clazz.getIRI().toString();
		      Set<OWLClassExpression> superC = clazz.getSuperClasses(ontology);
		  	if (!superC.isEmpty()) {
		  		// TODO maybe include checkKnownNamespace
//		  		if (!(checkKnownNamespace(namespace) || checkKnownNamespace(supNamespace))) {

              int id;

               try {
		    	 id = classIDs.get(iri);
               } catch( Exception e) {
                   return;
               }

		  		for (OWLClassExpression expression : superC) {
		  			if (expression instanceof OWLClass) {
		  				// Superclass is a class
		  				String parentIri = ((OWLClass) expression).getIRI().toString();
		  				if (!parentIri.contains(IGNORE_W3ORG)){
		  					int parentId = classIDs.get(parentIri);
			  				insertLink(sourcerel_id, parentId, id);
		  				}
		  			} else if (expression instanceof OWLQuantifiedObjectRestriction) {
		  				// DataProperty Restriction (Values: allvalues, somevalues)
		  				OWLClassExpression range = ((OWLQuantifiedObjectRestriction) expression).getFiller();
		  				if (range instanceof OWLClass){
		  					String parentIri = ((OWLClass) range).getIRI().toString();
		  					if (classIDs.containsKey(parentIri)){
				  				int parentId = classIDs.get(parentIri);
				  				String type = null;
								 if(expression instanceof OWLObjectSomeValuesFrom )	{	 
									 type = "someValuesFrom";
								 } else  if(expression instanceof OWLObjectAllValuesFrom ) {
									 type = "allValuesFrom"; 
								 }
								 OWLObjectPropertyExpression property = ((OWLQuantifiedObjectRestriction) expression).getProperty();
								 if (property!=null && property.toString().contains("part_of")){
									 type = "part_of";  
								 }
				  				insertLink(sourcerel_id, parentId, id, type);
		  					}
		  				} else {
		  					// e.g. ObjectOneOf
			        		 System.out.println("OWLParserNew.loadHierarchy not yet supported "  + range.toString());
		  				}
		  			} else if (expression instanceof OWLNaryBooleanClassExpression) {
		  				 //superclass is a logical class constructor (UnionOf und IntersectionOf)
		  				// TODO check if correct, maybe also/instead in range of OWLQuantifiedObjectRestriction 
//						Set<OWLClass> classes = ((OWLNaryBooleanClassExpression) expression).getClassesInSignature();						
//						if(classes != null){
//							for(OWLClass cl: classes){
//								int parentId = classIDs.get( cl.getIRI().toString());
//								String type = null;
//								if(expression instanceof OWLObjectUnionOf )	{	
//									 type = "unionOf";
//								} else if(expression instanceof OWLObjectIntersectionOf )	{	
//									 type = "intersectionOf";
//								}							
//								insertLink(sourcerel_id, parentId, id, type);
//							}
//						}
		  			} else if (expression instanceof OWLCardinalityRestriction) {
//		  				// DataProperty and ObjectProperty Restriction (Cardinality: min, max, cardinality)
//		  				// TODO check if correct and needed
//		  				String type = null;
//						 if(expression instanceof OWLObjectExactCardinality || expression instanceof OWLDataExactCardinality){		 
//							 type = "exactCardinality";
//						 }	 else if(expression instanceof OWLObjectMaxCardinality || expression instanceof OWLDataMaxCardinality){
//							 type = "maxCardinality"; 
//						 }	 else if(expression instanceof OWLObjectMinCardinality || expression instanceof OWLDataMinCardinality){
//							 type = "minCardinality"; 
//						 }
////			  			OWLObjectExactCardinalityImpl  OWLObjectMaxCardinalityImpl  OWLObjectMinCardinalityImpl
////						OWLDataExactCardinalityImpl OWLDataMaxCardinalityImpl OWLDataMinCardinalityImpl
//
//		  				 OWLPropertyExpression property = ((OWLCardinalityRestriction) expression).getProperty();
		  			} else {
		  				System.out.println("OWLParserNew.loadHierarchy not supported " + expression.getClass());
		  			}
				}
		  	}
		  	System.out.print("");
	    }
	    
		/**
		 * get synonyms of a class/property
		 * @param ontology
		 * @param obj
		 * @return synonym string (single synonyms separated by |)
		 */
		private String getSynoynms(OWLOntology ontology, OWLNamedObject obj, HashMap<String, String> annotation) {
			if (obj == null || ontology == null || annotation.isEmpty())
			return null;
			String description = null;
			
			Set annotations = ((OWLEntity)obj).getAnnotations(ontology);
			if (!annotations.isEmpty()) {
				for (Iterator it = annotations.iterator(); it .hasNext();) {
					OWLAnnotationImpl oai = (OWLAnnotationImpl) it.next();
					OWLAnnotationProperty prop = oai.getProperty();
					if(prop.getIRI().toString().contains("hasRelatedSynonym")){
					   Object o = oai.getValue();
						String v = o.toString();
						if (description == null)
							description = annotation.get(v);
						else
							description += "|" + annotation.get(v);
					}else if( // e.g. label, comment -> treated separately
							prop.getIRI().toString().contains("label")
							|| prop.getIRI().toString().contains("comment")
							|| prop.getIRI().toString().contains("hasDefinition")
							){
					}else if( // ignored currently 
							prop.getIRI().toString().contains("hasAlternativeId")
							|| prop.getIRI().toString().contains("hasDbXref")){
		        	 } else { 
		        		 System.out.println("OWLParserNew.getSynonyms not yet supported " + prop.toString());
					}
				}
			}
			return description;
		}
	    
		/**
		 * get name of owl-element using his label or iri
		 * @param iri
		 * @param ontology
		 * @param prop
		 * @return
		 */
		public String getFullName(IRI iri, OWLOntology ontology, OWLNamedObject prop){
			
			String name = null;
			if (labelsPreferred) {
				name = getLabel(ontology, prop);
			}
			if (name == null || name.equals("")) {
				name = getName(iri);
			}
			if (stressExternalIRIs) {
				// TODO test!
				if (iri.toURI().getAuthority() != null) {
					name += " <" + iri.toURI().getAuthority() + ">";
				}
			}
			return name;
		} 
		
		
		/**
		 * get description of a class/property
		 * @param ontology
		 * @param obj
		 * @return
		 */
		private String getComment(OWLOntology ontology, OWLNamedObject obj, HashMap<String, String> annotation) {
			if (obj == null || ontology == null){
				return null;
			}
			String description = null;
			
			Set<OWLAnnotation> annotations = ((OWLEntity)obj).getAnnotations(ontology);
			if (!annotations.isEmpty()) {
				for (OWLAnnotation annoation : annotations) {
					if (!(annoation instanceof OWLAnnotationImpl)) continue;
					OWLAnnotationImpl oai = (OWLAnnotationImpl) annoation;
					if(oai.isComment()){
						OWLAnnotationValue dv = oai.getValue();
						String v = dv.toString();
						v = v.substring(1, v.lastIndexOf("\""));						
						if (description == null){
							description = v;
						} else{
							description += " " + v;		
						}
					}
				}
				if (description==null){
					// e.g. Anatomy NCI file contains in addition to synonyms definitions
					for (Iterator it = annotations.iterator(); it .hasNext();) {
						OWLAnnotationImpl oai = (OWLAnnotationImpl) it.next();
						OWLAnnotationProperty prop = oai.getProperty();
						if(prop.getIRI().toString().contains("hasDefinition")){
						   Object o = oai.getValue();
							String v = o.toString();
							if (description == null)
								description = annotation.get(v);
							else
								description += "|" + annotation.get(v);
						}
					}
				}
			}
			return description;
		}

		/**
		 * get Label of a class
		 * @param ontology
		 * @param obj
		 * @return label
		 */
	    private String getLabel(OWLOntology ontology, OWLNamedObject obj){
		if (obj == null || ontology == null)
			return null;
		String description = null;

		Set<OWLAnnotation> annotations = ((OWLEntity)obj).getAnnotations(ontology);
		if (!annotations.isEmpty()) {
			for (OWLAnnotation annoation : annotations) {
				if (!(annoation instanceof OWLAnnotationImpl)) continue;
				OWLAnnotationImpl oai = (OWLAnnotationImpl) annoation;
				if(oai.isLabel())
				{
					OWLAnnotationValue dv =  oai.getValue();
					String v = dv.toString();					
					v = v.substring(1, v.lastIndexOf("\""));
					if (description == null){
						description = v;
					} else { 
						description += " " + v;
					}
				}
			}
		} 
		return description;
	}
	    
	    /**
	     * get name of a owl-element using his iri
	     * @param iri
	     * @return
	     */
	 	public String getName(IRI iri) {
	 		if (iri == null)
	 			return null;
	 		URI uri = iri.toURI();
	 		String fragment = uri.getFragment();
	 		if (fragment == null || fragment.equals("")) {
	 			// It's not of the form http://xyz/path#frag
	 			return uri.toString();
	 		}
	 		// It's of the form http://xyz/path#frag
	 		return fragment;
	 	}
	 	
	 	/**
	 	 * get namespace using filename
	 	 * @param file
	 	 * @return
	 	 */
	 		static String getNamespace(String file){
	 		String namespace=null;
	 		if (file!=null){
	 			file=file.replace("file:", "");
	 			try {
	 			BufferedReader in = null;
				if (file.startsWith("http")){
					URL url = new URL(file);
			        URLConnection connection = url.openConnection();
			        in= new BufferedReader(
			        		new InputStreamReader(connection.getInputStream()));
				} else {
					in = new BufferedReader(new FileReader(file));
				}
	 			
	 		    String line;
	 		    String xmlns= "xmlns=";
	 			while ((line = in.readLine()) != null) {
	 		    	if (line.length()<1){
	 		    		continue;
	 		    	}
	 		    	int i = line.indexOf(xmlns);
	 		    	if (i>-1){
	 		    		namespace = line.substring(i+xmlns.length()).replace("file:", "");
	 		    		namespace= namespace.replace("\"", "");
	 		    		namespace= namespace.replaceAll("#>", "");
	 		    		break;
	 		    	}
	 			}
	 			} catch (FileNotFoundException e) {
	 				System.out.println("FileNotFoundException: "+file);
	 			} catch (IOException e) {
	 				System.out.println("IOException: "+file);
	 			}	
	 		}
	 		return namespace;
	 	}

	 	/**
	 	 * to make a short form of a iri  
	 	 */	
	 		protected class MyShortForm implements ShortFormProvider {
	 			String namespace=null;
	 			String file=null;
	 			
	 			public MyShortForm(String file){
	 				super();
	 				if (file!=null){
	 					this.file=file.replace("file:", "");
	 					namespace=OWLParser_V3.getNamespace(file);
	 				}
	 			}
	 				
	 			public String getNamespace(){ return namespace;	}
	 				
	 			public String getFile(){ return file; }
	 			
	 			public String shortForm(IRI iri) {
	 				if (iri == null) {
	 					return "_";
	 				}
	 				URI uri = iri.toURI();
	 				try {
	 					if (namespace!=null && uri.toString().indexOf(file)>-1){
	 						uri = new URI(uri.getScheme(), namespace, uri.getFragment());
	 					}
	 					if (uri.getFragment() == null || uri.getFragment().equals("")) {
	 						// It's not of the form http://xyz/path#frag
	 						return uri.toString();
	 					}
	 					// It's of the form http://xyz/path#frag
	 					String ssp = new URI(uri.getScheme(), uri
	 							.getSchemeSpecificPart(), null).toString();
	 					if (!ssp.endsWith("#")) {
	 						ssp = ssp + "#";
	 					}
	 					if (known.keySet().contains(ssp)) {
	 						return known.get(ssp) + ":" + uri.getFragment();
	 					}
	 					if (shortNames.contains(ssp)) {
	 						// Check whether the fragment is ok, e.g. just contains letters.
	 						String frag = uri.getFragment();
	 						boolean fragOk = true;
	 						// This is actually quite severe -- URIs allow other
	 						// stuff in here, but for our concrete syntax we
	 						// don't, only letters, numbers and _ */
	 						for (int i = 0; i < frag.length(); i++) {
	 							fragOk = fragOk
	 							&& (Character.isLetter(frag.charAt(i))
	 									|| Character.isDigit(frag.charAt(i)) || frag
	 									.charAt(i) == '_');
	 						}
	 						if (fragOk && (shortNames.indexOf(ssp)) < names.length) {
	 							return (names[shortNames.indexOf(ssp)]) + ":" + frag;
	 						}
	 						// We can't shorten it -- there are too many...
	 						return "<" + uri.toString() + ">";
	 					}
	 				} catch (URISyntaxException ex) {}
	 				return uri.toString();
	 			}

	 			public void dispose() {
	 			}

	 			public String getShortForm(OWLEntity arg0) {
	 				return null;
	 			}
	 		}
	 	
}
