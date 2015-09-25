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

package de.wdilab.coma.matching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.wdilab.coma.matching.util.Tokenizer;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.Path;

/**
 * Resolution resolves the elements (set of nodes/paths/names, etc) that are 
 * used in the next part of the workflow, there exist 3 kinds of resolution 
 * which can be used to "navigate" through the schema/ontology and its information
 * 
 * part of the grammar
 * 
 * (in previous prototype the outcome is called constituents)
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class Resolution{
	
	// start counting 100
	public static int TYPE_RES1 =  Constants.RES_CNT + 1;
	public static int TYPE_RES2 =  Constants.RES_CNT + 2;
	public static int TYPE_RES3 =  Constants.RES_CNT + 3;
	
	// Resolution 1
	// this resolutions are directly derived from the graph
	// OR from another match result
	public static final int RES1_PATHS = Constants.RES_CNT + 101;
	public static final int RES1_INNERPATHS = Constants.RES_CNT + 102;
	public static final int RES1_LEAFPATHS = Constants.RES_CNT + 103;
	public static final int RES1_NODES = Constants.RES_CNT + 104;
	public static final int RES1_INNERNODES = Constants.RES_CNT + 105;
	public static final int RES1_LEAFNODES = Constants.RES_CNT + 106;
	public static final int RES1_ROOTS = Constants.RES_CNT + 107;
	public static final int RES1_SHARED = Constants.RES_CNT + 108;
	public static final int RES1_UPPATHS = Constants.RES_CNT + 109;
	public static final int RES1_DOWNPATHS = Constants.RES_CNT + 110;
	public static final int RES1_SHAREDPATHS = Constants.RES_CNT + 111;
	
	
	
	public static final int RES1_USER = Constants.RES_CNT + 120;
	
	public static final Integer[] RES1 = {
		RES1_PATHS, RES1_INNERPATHS, RES1_LEAFPATHS, RES1_NODES, 
		RES1_INNERNODES, RES1_LEAFNODES, RES1_ROOTS, RES1_SHARED, 
		RES1_UPPATHS, RES1_DOWNPATHS		
	};
	public static final Integer[] RES1_NODETYPE = {
		RES1_NODES, RES1_INNERNODES, RES1_LEAFNODES, RES1_ROOTS, RES1_SHARED, 
	};
	public static final Integer[] RES1_PATHTYPE = {
		RES1_PATHS, RES1_INNERPATHS, RES1_LEAFPATHS, RES1_UPPATHS, RES1_DOWNPATHS, RES1_SHAREDPATHS	
	};
	public static final Integer[] RES1_FRAGSEL = {
		RES1_USER, RES1_ROOTS, RES1_INNERPATHS, RES1_LEAFPATHS, RES1_SHAREDPATHS,
		
	};
	public static final List<Integer> RES1_LIST = Arrays.asList(RES1);
	public static final List<Integer> RES1_NODETYPE_LIST = Arrays.asList(RES1_NODETYPE);
	public static final List<Integer> RES1_PATHTYPE_LIST = Arrays.asList(RES1_PATHTYPE);
	
	// Resolution 2
	// this resolutions are derived from graph objects e.g. Node or Path
	// can be used for "browsing" 
	public static final int RES2_SELFPATH = Constants.RES_CNT + 201;
	public static final int RES2_SELFNODE = Constants.RES_CNT + 202;
	public static final int RES2_PARENTS = Constants.RES_CNT + 203;
	public static final int RES2_CHILDREN = Constants.RES_CNT + 204;
	public static final int RES2_LEAVES = Constants.RES_CNT + 205;
	public static final int RES2_SIBLINGS = Constants.RES_CNT + 206;
	public static final int RES2_ALLNODES = Constants.RES_CNT + 207;
	public static final int RES2_SUCCESSORS = Constants.RES_CNT + 208;
	
	public static final Integer[] RES2 = {
		RES2_SELFPATH, RES2_SELFNODE, RES2_PARENTS, RES2_CHILDREN,
		RES2_LEAVES, RES2_SIBLINGS, RES2_ALLNODES, RES2_SUCCESSORS
	};
	public static final List<Integer> RES2_LIST = Arrays.asList(RES2);
	
	public static final Integer[] RES2_API = {
		RES2_SELFPATH, RES2_SELFNODE, RES2_PARENTS, RES2_CHILDREN,
		RES2_LEAVES, RES2_SIBLINGS
	};
	
	// Resolution 3
	// this resolution dilivers the actual information for the matcher e.g. name or datatype
	public static final int RES3_NAME = Constants.RES_CNT + 301;
	public static final int RES3_NAMETOKEN = Constants.RES_CNT + 302;
	public static final int RES3_DATATYPE = Constants.RES_CNT + 303;
	public static final int RES3_COMMENT = Constants.RES_CNT + 304;
	public static final int RES3_COMMENTTOKEN = Constants.RES_CNT + 305;
	public static final int RES3_PATH = Constants.RES_CNT + 306;
	public static final int RES3_PATHTOKEN = Constants.RES_CNT + 307;
	public static final int RES3_SYNONYMS = Constants.RES_CNT + 308;
//	public static final int RES3_CONSTRAINTS = Manager.RES_CNT + 309; // XXX RES3_CONSTRAINTS
	public static final int RES3_STATISTICS = Constants.RES_CNT + 310;
	public static final int RES3_NAMESYN = Constants.RES_CNT + 311;
	public static final int RES3_INST_CONSTRAINTS = Constants.RES_CNT + 312;
	public static final int RES3_INST_CONTENT_DIRECT = Constants.RES_CNT + 313;
	public static final int RES3_INST_CONTENT_INDIRECT = Constants.RES_CNT + 314;
	public static final int RES3_INST_CONTENT_ALL = Constants.RES_CNT + 315;
	public static final int RES3_PATHSYN = Constants.RES_CNT + 316;
	
	public static final Integer[] RES3 = {
		RES3_NAME, RES3_NAMETOKEN, RES3_DATATYPE, RES3_COMMENT, RES3_COMMENTTOKEN, 
		RES3_PATH, RES3_PATHTOKEN, RES3_SYNONYMS, RES3_NAMESYN, RES3_PATHSYN, // RES3_CONSTRAINTS, 		
		RES3_STATISTICS, RES3_INST_CONSTRAINTS, RES3_INST_CONTENT_DIRECT, RES3_INST_CONTENT_INDIRECT, RES3_INST_CONTENT_ALL
	};
	public static final List<Integer> RES3_LIST = Arrays.asList(RES3);
	
    String name= null;
    int id = Constants.UNDEF;
        
	public Resolution(int resolution){
		id = resolution;
		this.name=resolutionToString(resolution);
	}
	
	public int getId() { return id; }
	
	public String getName() {	
		if (name==null){
			// generate Name
			name=toString();
		}
		return name;	
	}

	public int returnType(){
		return getType(id);
	}
	
	public static int getType(int resolution){
		if (RES1_LIST.contains(resolution)){
			return TYPE_RES1;
		} else if (RES2_LIST.contains(resolution)){
			return TYPE_RES2;
		}else if (RES3_LIST.contains(resolution)){
			return TYPE_RES3;
		}
		return Constants.UNDEF;
	}
	
	// TODO execuConstantssolution probably
	// --> for smaller cases use memory based matching - get information directly
	// --> for larger cases use database based matching - get position of information e.g. attribute in database incl. range
	
	public static String resolutionToString(int resolution) {
		switch (resolution) {
		case RES1_PATHS:      		return "Paths";
		case RES1_INNERPATHS:       return "InnerPaths";
		case RES1_LEAFPATHS:      	return "LeafPaths";
		case RES1_NODES:      		return "Nodes";
		case RES1_INNERNODES:       return "InnerNodes";
		case RES1_LEAFNODES:      	return "LeafNodes";
		case RES1_ROOTS:      		return "Roots";
		case RES1_SHARED:      		return "Shared";
		case RES1_UPPATHS:    		return "UpPaths";
		case RES1_DOWNPATHS:    	return "DownPaths";
		case RES1_USER:    			return "User";
		case RES1_SHAREDPATHS:      return "SharedPaths";
		
		case RES2_SELFPATH:       	return "SelfPath";
		case RES2_SELFNODE:      	return "SelfNode";
		case RES2_PARENTS:       	return "Parents";
		case RES2_CHILDREN:       	return "Children";
		case RES2_LEAVES:         	return "Leaves";
		case RES2_SIBLINGS:       	return "Siblings";
		case RES2_ALLNODES:       	return "AllNodes";
		case RES2_SUCCESSORS:      	return "Successors";
	
		    	
		case RES3_NAME:         	return "Name";
		case RES3_NAMETOKEN:     	return "Nametoken";
		case RES3_DATATYPE:      	return "Datatype";
		case RES3_COMMENT:    		return "Comment";
		case RES3_COMMENTTOKEN:  	return "Commenttoken";
		case RES3_PATH:    			return "Path";
		case RES3_PATHTOKEN:    	return "Pathtoken";
		case RES3_SYNONYMS:    		return "Synonyms";
		case RES3_PATHSYN:    		return "PathSyn";		
//		case RES3_CONSTRAINTS:		return "Constraints";
		case RES3_STATISTICS:    	return "Statistics";
		case RES3_INST_CONSTRAINTS: return "Instance_Constraints";
		case RES3_INST_CONTENT_DIRECT:     return "Instance_Content";
		case RES3_INST_CONTENT_INDIRECT:     return "Instance_Content_Indirect";
		case RES3_INST_CONTENT_ALL:     return "Instance_All";
		case RES3_NAMESYN:    		return "NameAndSynonyms";
		
		default: return "UNDEF";
		}
	}
	
    /**
     * @param resolution
     * @return the id for the given string representation of a resolution
     */
    public static int stringToResolution(String resolution) {
        if (resolution==null) return Constants.UNDEF;
        resolution = resolution.toLowerCase();
        if (resolution.equals("paths"))      return RES1_PATHS;
        else if (resolution.equals("innerpaths"))  return RES1_INNERPATHS;
        else if (resolution.equals("leafpaths")) return RES1_LEAFPATHS;
        else if (resolution.equals("nodes"))  return RES1_NODES;
        else if (resolution.equals("innernodes"))  return RES1_INNERNODES;
        else if (resolution.equals("leafnodes")) return RES1_LEAFNODES;
        else if (resolution.equals("roots")) return RES1_ROOTS;
        else if (resolution.equals("shared")) return RES1_SHARED;
        else if (resolution.equals("uppaths"))  return RES1_UPPATHS;
        else if (resolution.equals("downpaths"))  return RES1_DOWNPATHS;
        else if (resolution.equals("user"))  return RES1_USER;
        else if (resolution.equals("sharedpaths"))  return RES1_SHAREDPATHS;
        
        else if (resolution.equals("selfpath"))  return RES2_SELFPATH;
        else if (resolution.equals("selfnode")) return RES2_SELFNODE;
        else if (resolution.equals("parents"))  return RES2_PARENTS;
        else if (resolution.equals("children"))  return RES2_CHILDREN;
        else if (resolution.equals("leaves")) return RES2_LEAVES;
        else if (resolution.equals("siblings"))  return RES2_SIBLINGS;
        else if (resolution.equals("allnodes"))  return RES2_ALLNODES;
        else if (resolution.equals("successors"))  return RES2_SUCCESSORS;

        else if (resolution.equals("name"))  return RES3_NAME;
        else if (resolution.equals("nametoken")) return RES3_NAMETOKEN;
        else if (resolution.equals("datatype"))  return RES3_DATATYPE;
        else if (resolution.equals("comment"))  return RES3_COMMENT;
        else if (resolution.equals("commenttoken")) return RES3_COMMENTTOKEN;
        else if (resolution.equals("path"))  return RES3_PATH;
        else if (resolution.equals("pathtoken"))  return RES3_PATHTOKEN;
        else if (resolution.equals("synonyms"))  return RES3_SYNONYMS;
        else if (resolution.equals("pathsyn"))  return RES3_PATHSYN;        
//        else if (resolution.equals("constraints")) return RES3_CONSTRAINTS;
        else if (resolution.equals("statistics")) return RES3_STATISTICS;
        else if (resolution.equals("instance_constraints"))  return RES3_INST_CONSTRAINTS;
        else if (resolution.equals("instance_content"))  return RES3_INST_CONTENT_DIRECT;  
        else if (resolution.equals("instance_content_indirect"))  return RES3_INST_CONTENT_INDIRECT;
        else if (resolution.equals("instance_all"))  return RES3_INST_CONTENT_ALL;
        else if (resolution.equals("nameandsynonyms"))  return RES3_NAMESYN;
        
        return Constants.UNDEF;
      }
	
	// Resolution 1
	// getting from a graph a list of paths or nodes	
	public ArrayList<Object> getResolution1(Graph input){
		if (id==Constants.UNDEF){
			return null;
		}
		// TODO: what about user selected matching???
		 ArrayList<Object> constituents = null;
		switch (id) {
		case RES1_PATHS:
			constituents = new ArrayList<Object>(input.getAllPaths());
			break;
		case RES1_INNERPATHS:
			constituents = new ArrayList<Object>(input.getInnerPaths());
			break;
		case RES1_LEAFPATHS:
			constituents = new ArrayList<Object>(input.getLeafPaths());
			break;
		case RES1_SHAREDPATHS:
			ArrayList<Path> shared = input.getSharedPaths();
			if (shared!=null){
				constituents = new ArrayList<Object>(shared);
			}
			break;
		case RES1_NODES:
			constituents = new ArrayList<Object>(input.getAllNodes());
			break;
		case RES1_INNERNODES:
			if (input.getInners()!=null){
				constituents = new ArrayList<Object>(input.getInners());
			}
			break;
		case RES1_LEAFNODES:
			constituents = new ArrayList<Object>(input.getLeaves());
			break;
		case RES1_ROOTS:
			constituents = new ArrayList<Object>(input.getRoots());
			break;
		case RES1_SHARED:
			constituents = new ArrayList<Object>(input.getShared());
			break;
		default:
			System.out.println("Resolution.getResolution1 no such resolution " + id);
			break;
		}
		if (constituents==null){
			constituents = new ArrayList<Object>();
		}
		return constituents;
	}

	// Resolution 2
	// getting for a element that is identified by its path 
	// the corresponding parents, children, siblings, leaves,...
	ArrayList<Object> getResolution2(Graph graph, Path input){
		if (id==Constants.UNDEF){
			return null;
		}
		 ArrayList<Object> constituents = null;
		 Element element = input.getLastElement();
		switch (id) {
//		// use the one for element, give all parents back (not just the path one) like in COMA++ 
//		case RES2_PARENTS:
//			constituents = new ArrayList<Object>();
//			List<Edge> edges = input.getEdgeList();
//			if (edges!=null && !edges.isEmpty()){
//				Edge edge = edges.get(edges.size()-1);
//				constituents.add(edge.getSource());
//			}
//			break;
//		// TODO case RES2_SIBLINGS:
////			constituents = new ArrayList<Object>(graph.getSiblings(input));
////			break;
		case RES2_SELFNODE:
			constituents = new ArrayList<Object>();
			constituents.add(element);
			break;
		case RES2_SELFPATH:
			constituents = new ArrayList<Object>();
			constituents.add(input);
			break;
		case RES1_UPPATHS:
			// get paths for local (fragment) root
			Element localRoot = input.getFirstElement();
			if (graph.isRoot(localRoot)){
				constituents = new ArrayList<Object>();
				constituents.add(input);
			} else {			
				ArrayList<Path> paths = graph.getUpRootPaths(input.getFirstElement());
				if (paths!=null){
					constituents = new ArrayList<Object>();
					for (Path path : paths) {
						path.appendAtEnd(input);
						constituents.add(path);
					}					
				}
			}
			break;
		case RES2_PARENTS:
		case RES2_CHILDREN:
		case RES1_DOWNPATHS:
		case RES2_LEAVES:
		case RES2_SIBLINGS:
		case RES2_SUCCESSORS: 
			return getResolution2(graph, element);
		case RES2_ALLNODES:
			constituents = new ArrayList<Object>();
			constituents.addAll(input.getVertices());
			break;
		default:
			System.out.println("Resolution.getResolution2 no such resolution " + id);
			break;
		}
		if (constituents==null || constituents.isEmpty()) return null;
		return constituents;
	}
	
	// getting for a element in the graph 
	// all corresponding parents, children, siblings, leaves,...
	ArrayList<Object> getResolution2(Graph graph, Element input){
		if (id==Constants.UNDEF){
			return null;
		}
		 ArrayList<Object> constituents = null;
		switch (id) {
		case RES2_CHILDREN:
			ArrayList<Element> children = graph.getChildren(input);
			if (children!=null){
				constituents = new ArrayList<Object>(children);				
//			} else{
//				constituents = new ArrayList<Object>();
//				constituents.add(input);
			}
			break;
		case RES2_LEAVES:
			ArrayList<Element> leaves = graph.getLeaves(input);			
			if (leaves!=null){
				constituents = new ArrayList<Object>(leaves);
//				constituents.add(input);
			}
			break;
		case RES2_PARENTS:
			ArrayList<Element> parents = graph.getParents(input);			
			if (parents!=null){
				constituents = new ArrayList<Object>(parents);
//				constituents.add(input);
			}
			break;
		case RES2_SIBLINGS:
			ArrayList<Element> siblings = graph.getSiblings(input);			
			if (siblings!=null){
				constituents = new ArrayList<Object>(siblings);
//				constituents.add(input);
			}
			break;
		case RES2_SUCCESSORS:
			ArrayList<Element> successors = graph.getSubNodes(input);
			if (successors!=null){
				constituents = new ArrayList<Object>(successors);
//				constituents.add(input);
			}
			break;			
		case RES2_SELFNODE:
		case RES2_ALLNODES:
			constituents = new ArrayList<Object>();
			constituents.add(input);
			break;
		case RES2_SELFPATH:
		case RES1_UPPATHS:
			constituents = new ArrayList<Object>();
			ArrayList<Path> upRootPaths = graph.getUpRootPaths(input);
			if (upRootPaths!=null){
				constituents.addAll(upRootPaths);
			}
			break;
		case RES1_DOWNPATHS:
			constituents = new ArrayList<Object>();
			ArrayList<Path> downPaths = graph.getDownPaths(input);
			if (downPaths!=null){
				constituents.addAll(downPaths);
			}
			break;
		default:
			System.out.println("Resolution.getResolution2 no such resolution " + id);
			break;
		}
		return constituents;
	}
	
	public ArrayList<ArrayList<Object>> getResolution2(Graph graph, ArrayList<Object> res1){
		if (res1==null) return null;
		ArrayList<ArrayList<Object>> res2 = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < res1.size(); i++) {
			Object current = res1.get(i);
			ArrayList<Object> objects = null;
			if (current instanceof Path){
				objects = getResolution2(graph, (Path)current);
			} else if (current instanceof Element){
				objects = getResolution2(graph, (Element)current);
			}
//			if (objects!=null){
				res2.add(i, objects);
//			}
		}
		return res2;
	}
	
	public ArrayList<ArrayList<Object>> getResolution3(Graph graph, ArrayList<Object> res2){
		if (res2==null) return null;
		ArrayList<ArrayList<Object>> res3 = new ArrayList<ArrayList<Object>>();
		int size=0;
		for (int i = 0; i < res2.size(); i++) {
			Object current = res2.get(i);
			ArrayList<Object> objects = null;
			if (current instanceof Path){
				objects = getResolution3(graph, (Path)current);
			} else if (current instanceof Element){
				objects = getResolution3(graph, (Element)current);
			}
			if (objects!=null){
				res3.add(size, objects);
			} else {
				res3.add(size, null);
			}
			size++;
		}
		return res3;
	}
	
	static public ArrayList<Object> getSingleObjects(ArrayList<ArrayList<Object>> compositeObjects){
		if (compositeObjects==null) return null;
		ArrayList<Object> singleObjects = new ArrayList<Object>();
		for (int i = 0; i < compositeObjects.size(); i++) {
			ArrayList<Object> current = compositeObjects.get(i);
			if (current!=null){
				singleObjects.addAll(current);
			} else {
				singleObjects.add(null);
			}
		}
		return singleObjects;
	}
	
	public ArrayList<Object> getResolution2(Graph graph, Object input){
		ArrayList<Object> objects = null;
		if (input instanceof Path){
			objects = getResolution2(graph, (Path)input);
		} else if (input instanceof Element){
			objects = getResolution2(graph, (Element)input);
		}
		return objects;
	}
	
	ArrayList<Object> getResolution3(Graph graph, Object input){
		ArrayList<Object> objects = null;
		if (input instanceof Path){
			objects = getResolution3(graph, (Path)input);
		} else if (input instanceof Element){
			objects = getResolution3(graph, (Element)input);
		}
		return objects;
	}
	
	
	// Resolution 3
	// getting for a element that is identified by its path 
	// the assigned name, datatype, pathtoken, instances, ...
	ArrayList<Object> getResolution3(Graph graph, Path input){
		if (id==Constants.UNDEF){
			return null;
		}
		// TODO: what about user selected matching???
		 ArrayList<Object> constituents = null;
		 Element element = input.getLastElement();
		switch (id) {
		case RES3_NAME:
		case RES3_NAMETOKEN: 
		case RES3_COMMENT:
		case RES3_COMMENTTOKEN:	
		case RES3_DATATYPE:
		case RES3_SYNONYMS:
		case RES3_NAMESYN:
		case RES3_INST_CONTENT_DIRECT:
		case RES3_INST_CONTENT_INDIRECT:
		case RES3_INST_CONTENT_ALL:
		case RES3_INST_CONSTRAINTS:
			return getResolution3(graph, element);
		case RES3_PATH:
			constituents = new ArrayList<Object>();
			String path = input.toNameString();
			if (path==null){
				return null;
			}
			path = path.replace(".", " ");
			constituents.add(path);
			break;
		case RES3_PATHTOKEN:
			constituents = new ArrayList<Object>();
			String path2 = input.toNameString();
			if (path2==null){
				return null;
			}
			path2 = path2.replace(".", " ");
			// expanding of abbreviation moved to exec workflow as preprocessing
//	    	ArrayList<String> list3 = Tokenizer.expandTokenize(abbrevList, fullFormList, path2);
			ArrayList<String> list3 = Tokenizer.tokenize(path2);
	        if (list3!=null) constituents.addAll(list3);
			break;
		case RES3_PATHSYN:
			constituents = new ArrayList<Object>();
			String path3 = input.toNameSynString();
			if (path3==null){
				return null;
			}
			path3 = path3.replace(".", " ");
			constituents.add(path3);
			break;
//		case RES3_CONSTRAINTS:
//			constituents = new ArrayList<Object>();
////			constituents.add(input.get);
//			break;
		case RES3_STATISTICS:
			constituents = new ArrayList<Object>();
			constituents.add(element.getStatistics());
			break;
		default:
			System.out.println("Resolution.getResolution3 no such resolution " + id);
			break;
		}
		return constituents;
	}
	
	// getting for a element in the graph 
	// the assigned name, datatype, pathtoken, instances, ...
	ArrayList<Object> getResolution3(Graph graph, Element input){
		if (id==Constants.UNDEF){
			return null;
		}
		// TODO: what about user selected matching???
		 ArrayList<Object> constituents = null;
		switch (id) {
		case RES3_NAME:
			constituents = new ArrayList<Object>();
			constituents.add(input.getName());
			break;
		case RES3_NAMETOKEN:
			constituents = new ArrayList<Object>();
			// expanding of abbreviation moved to exec workflow as preprocessing
//	    	ArrayList<String> list1 = Tokenizer.expandTokenize(abbrevList, fullFormList, input.getName());
	    	ArrayList<String> list1 = Tokenizer.tokenize(input.getName());
	        if (list1!=null) constituents.addAll(list1);
//	        System.out.println(input.getName() + "\t" + constituents);
			break;
		case RES3_COMMENT:
			constituents = new ArrayList<Object>();
			constituents.add(input.getComment());
			break;
		case RES3_COMMENTTOKEN:
			constituents = new ArrayList<Object>();
			// expanding of abbreviation moved to exec workflow as preprocessing
//	    	ArrayList<String> list2 = Tokenizer.expandTokenize(abbrevList, fullFormList, input.getComment());
	    	ArrayList<String> list2 = Tokenizer.tokenize(input.getComment());
	        if (list2!=null) constituents.addAll(list2);
			break;
		case RES3_PATH:
			constituents = new ArrayList<Object>();	
			ArrayList<Path> paths = graph.getUpRootPaths(input);
			if (paths!=null){
				for (int i = 0; i < paths.size(); i++) {					
					String path = paths.get(i).toNameString();
					if (path==null){
						continue;
					}
					path = path.replace(".", " ");
					constituents.add(path);
				}
			}
			break;
		case RES3_PATHTOKEN:
			constituents = new ArrayList<Object>();
//			constituents.add(input.get);
			ArrayList<Path> paths2 = graph.getUpRootPaths(input);
			if (paths2!=null){
				for (int i = 0; i < paths2.size(); i++) {		
					String path2 = paths2.get(i).toNameString();
					if (path2==null){
						continue;
					}
					path2 = path2.replace(".", " ");			
					// expanding of abbreviation moved to exec workflow as preprocessing
//			    	ArrayList<String> list3 = Tokenizer.expandTokenize(abbrevList, fullFormList, path2);
			    	ArrayList<String> list3 = Tokenizer.tokenize(path2);
			        if (list3!=null) constituents.addAll(list3);
				}
			}

			break;
		case RES3_DATATYPE:
			constituents = new ArrayList<Object>();
			constituents.add(input.getType());
			break;
//		case RES3_CONSTRAINTS:
//			constituents = new ArrayList<Object>();
////			constituents.add(input.get);
//			break;
		case RES3_SYNONYMS:
			constituents = new ArrayList<Object>();
			String synonym = input.getSynonym();
			if (synonym!=null){
				if (synonym.contains("|")){
					// several synonyms - split them
					String[] parts = synonym.split("|");
					for (int i = 0; i < parts.length; i++) {
						String current = parts[i];
						if (!current.isEmpty()){
							constituents.add(current);
						}
					}
				} else {
					constituents.add(synonym);
				}
			}
			break;
		case RES3_NAMESYN: 
			constituents = new ArrayList<Object>();
			constituents.add(input.getName());
			String synonym2 = input.getSynonym();
			if (synonym2!=null){
				if (synonym2.contains("|")){
					// several synonyms - split them, use [|] because regular expression expected
					String[] parts = synonym2.split("[|]");
					for (int i = 0; i < parts.length; i++) {
						// trim because sometimes unnecessary space
						String current = parts[i].trim();
						// ignore if empty or already in set (because e.g. equal to name)
						if (!current.isEmpty() && !constituents.contains(current)){
							constituents.add(current);
						}
					}
				} else {
					constituents.add(synonym2);
				}
//				System.out.println(input + "\t" +  constituents);
			}
			break;
		case RES3_STATISTICS: 
			constituents = new ArrayList<Object>();
			constituents.add(input.getStatistics());
			break;
		case RES3_INST_CONSTRAINTS: // TODO RES3_INST_CONSTRAINTS for Element
			constituents = new ArrayList<Object>();
//			constituents.add(input.get);
			break;
		case RES3_INST_CONTENT_DIRECT:
	    	  if (input.hasDirectInstancesSimple()){
	    		  constituents = input.getDirectInstancesSimple();
	    	  }
	          if (constituents==null && input.hasDirectInstancesComplex()){
	  			  constituents = new ArrayList<Object>();
//	        	  constituents = ((Element)vertex.getObject()).getLimitedInstancesComplex(true, INSTANCE_MAX);
//	        	  constituents.add(input.getDirectInstancesComplex());
	  			HashMap<String, ArrayList<String>> instances = input.getDirectInstancesComplex();
//	  			//version1
//	  			for (String  key : instances.keySet()) {
//	  				// single values
//	  				constituents.addAll(instances.get(key));
//				}
	  			//version 2
	  			String text = instances.toString();
	  			text=text.replace(", ", " ").replace("=", " ").replace("[", "").replace("]", "").replace("{", "").replace("}", "");
	  			constituents.add(text);
	          }
			break;
		case RES3_INST_CONTENT_INDIRECT:
	    	  if (input.hasIndirectInstancesSimple()){
	    		  constituents = input.getIndirectInstancesSimple();
	    	  }
	          if (constituents==null && input.hasIndirectInstancesComplex()){
	  			  constituents = new ArrayList<Object>();
	        	  constituents.add(input.getIndirectInstancesComplex());
	          }
			break;
		case RES3_INST_CONTENT_ALL:
	    	  if (input.hasAllInstancesSimple()){
	    		  constituents = input.getAllInstancesSimple();
	    	  }
	          if (constituents==null && input.hasAllInstancesComplex()){
	  			  constituents = new ArrayList<Object>();
	        	  constituents.add(input.getAllInstancesComplex());
	          }
			break;
		default:
			System.out.println("Resolution.getResolution3 no such resolution " + id);
			break;
		}
		return constituents;
	}
	
	public String toString(){
		return name;
	}
	
//	  // getting for a element in the graph 
//		// the assigned name, datatype, pathtoken, instances, ...
//	public String getResolution3(){
//			if (id==Constants.UNDEF){
//				return null;
//			}
//	String resolution = null;
//			switch (id) {
//			case RES3_NAME:
//			case RES3_NAMETOKEN:
//				resolution = "name";
//				break;
//			case RES3_COMMENT:
//			case RES3_COMMENTTOKEN:
//				resolution = "comment";
//				break;
//			case RES3_PATH:
//			case RES3_PATHTOKEN:
//				resolution = "name";
//				break;
//			case RES3_DATATYPE:
//				resolution = "type";
//				break;
//			case RES3_SYNONYMS:
//				resolution = "synonyms";
//				break;
//			case RES3_NAMESYN: 
//				resolution = "name, synonyms";
//				break;
////			// TODO
////			case RES3_STATISTICS: 
////			case RES3_INST_CONSTRAINTS: 
////			case RES3_INST_CONTENT_DIRECT:
////			case RES3_INST_CONTENT_INDIRECT:
////			case RES3_INST_CONTENT_ALL:
//			default:
//				System.out.println("Resolution.getResolution3 no such resolution " + id);
//				break;
//			}
//			return resolution;
//		}

	//Determine distinct atomic constituents from a list of composite objects (thus paths)
	  public static ArrayList<Object> determineAtomicConstituents(ArrayList<Object> compositeObjects) {
	    if (compositeObjects==null) return null;
	    ArrayList<Object> atomicObjects = new ArrayList<Object>();
	    int compCnt = compositeObjects.size();
	    for (int i=0; i<compCnt; i++) {
	    	Path compositeConst = (Path)compositeObjects.get(i);
	      if (compositeConst==null) continue;
	      for (Iterator<Element> iterator = compositeConst.getVertices().iterator(); iterator.hasNext();) {
			 Element element = iterator.next();
			 if (! atomicObjects.contains(element)) atomicObjects.add(element);
	      }
	    }
	    if (atomicObjects.isEmpty()) return null;
	    return atomicObjects;
	  }
	

	  public static Class getResolutionOutputType(int resolution) {
		  switch (resolution) {
		  // ArrayList of GraphPathImpl 	// ? GraphPathImpl.class;
		  case RES1_PATHS:
		  case RES1_INNERPATHS:
		  case RES1_LEAFPATHS: 
		  case RES1_UPPATHS:
		  case RES1_DOWNPATHS:
		  // ArrayList of Element		// ? Element.class;
		  case RES1_NODES:
		  case RES1_INNERNODES:
		  case RES1_LEAFNODES:
		  case RES1_ROOTS:
		  case RES1_SHARED: 
			  return ArrayList.class;
				    					
		  case RES2_SELFPATH:				    
			  return Path.class;
					
		  case RES2_SELFNODE:
		  case RES2_PARENTS:
		  case RES2_CHILDREN:
		  case RES2_LEAVES:
		  case RES2_SIBLINGS: 
			  return Element.class;
			    	
		  case RES3_NAME: 
		  case RES3_NAMETOKEN: 
		  case RES3_DATATYPE: 
		  case RES3_COMMENT: 
		  case RES3_COMMENTTOKEN: 
		  case RES3_PATH: 
		  case RES3_PATHTOKEN: 
		  case RES3_SYNONYMS:
		  case RES3_NAMESYN: 
		  case RES3_INST_CONSTRAINTS: 
		  case RES3_INST_CONTENT_DIRECT:
		  case RES3_INST_CONTENT_INDIRECT:
		  case RES3_INST_CONTENT_ALL:
			  return String.class;
						
//		  case RES3_CONSTRAINTS: 
		  case RES3_STATISTICS: 
			  return float[].class;
		  }
		  return null;
	  }
	  
	  public static boolean checkResolutionSimMeasureCompatibility(int resolution, int simMeasure) {
		  Class resOutType = getResolutionOutputType(resolution);
		  Class<Element> constInputType = SimilarityMeasure.getMeasureInputType(simMeasure);
		    if (resOutType==null || constInputType==null) return false;
		    else if (resOutType.equals(constInputType)) return true;
		    else if (resOutType.equals(ArrayList.class)) return true;
		    return false;
	  }
	  
//	  public static boolean checkResolutionCompatibility(int resolution, int id) {
//		  Class resOutType = getResolutionOutputType(resolution);
//		  Class<?> classId =  Manager.getClass(id);
//		  
//			// Find constructor
//			Constructor matcherConstructor = null;
//			Class[] argClasses = new Class[] {};
//			try {
//				matcherConstructor = classId.getConstructor(int.class);
//			} catch (NoSuchMethodException e) {
//				System.out.println(	": Invalid constructor ");
//			}
//			Object instance = matcherConstructor.newInstance(id);
//			Resolution r = null;
//			if (instance instanceof Matcher){
//				r = ((Matcher)instance).getResolution();
//			}
//			
//		  
//		  Class<Element> constInputType = SimilarityMeasure.getMeasureInputType(simMeasure);
//		    if (resOutType==null || constInputType==null) return false;
//		    else if (resOutType.equals(constInputType)) return true;
//		    else if (resOutType.equals(ArrayList.class)) return true;
//		    return false;
//	  }

	
}
