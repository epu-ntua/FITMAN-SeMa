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

package de.wdilab.coma.center.reuse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.antlr.runtime.tree.Tree;

import de.wdilab.coma.center.Manager;
import de.wdilab.coma.insert.metadata.XDRParser;
import de.wdilab.coma.insert.relationships.MatchResultParser;
import de.wdilab.coma.matching.Combination;
import de.wdilab.coma.center.reuse.MappingReuse;
import de.wdilab.coma.matching.validation.TreeToWorkflow;
import de.wdilab.coma.repository.TRepository;
import de.wdilab.coma.structure.EvaluationMeasure;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import junit.framework.TestCase;

/**
 * @author Sabine Massmann
 */
public class TMappingReuse extends TestCase {

	
	// coma-project\coma-engine\resources\Sources\CSV
	String directory = (new File ("")).getAbsolutePath().replace("\\", "/") + "/resources/Sources/";
	
	String providerSrc = directory+"Po_xdr/Apertum.xdr";
	String providerTrg = directory+"Po_xdr/CIDXPOSCHEMA.xdr";
	
	Manager manager = null;
	MatchResult intended = null;
	
	
	private void reusePathLength1(Graph sourceGraph, Graph targetGraph){
		boolean exact = true;
		int topKPaths= 1 , maxPathLen= 1, combination = Combination.COM_MAX, composition = MatchResult.COMPOSITION_MAX;
		boolean usePivotSchema = false;		
		MappingReuse reuse = new MappingReuse(topKPaths, maxPathLen, exact, composition, combination, usePivotSchema);
		MatchResult result1 = reuse.executeReuse(manager, sourceGraph, targetGraph);
		System.out.println(result1.toString());
		MatchResult result2 = reuse.executeReuse(manager, targetGraph, sourceGraph);
		System.out.println(result2.toString());
		assertEquals(result1.getMatchCount(), result2.getMatchCount());
		assertEquals(result1.getSrcMatchObjectsCount(), result2.getTrgMatchObjectsCount());
		assertEquals(result1.getTrgMatchObjectsCount(), result2.getSrcMatchObjectsCount());
		if (intended!=null){
			EvaluationMeasure values1 = intended.compare(result1);
			EvaluationMeasure values2 = MatchResult.transpose(intended).compare(result2);
			assertEquals(values1, values2);
		}
	}
	
	public void testReuse(){
		TRepository.setDatabaseProperties();
		
		manager = new Manager();
		manager.getImporter().dropRepositorySchema();
		manager.getImporter().createRepositorySchema();
		
		boolean dbInsert = true;
		XDRParser xdrPar = new XDRParser(dbInsert);
		xdrPar.parseMultipleSources(directory.replace("/", "\\")+"PO_xdr");	
	

		manager.loadRepository();
		
		MatchResultParser mrParser = new MatchResultParser(manager, dbInsert);
		mrParser.loadMatchResultFile(directory+"Mappings/mappings-PO.txt");
		
		Graph sourceGraph = manager.loadGraph(manager.getAccessor().getSourceId(providerSrc));
		Graph targetGraph = manager.loadGraph(manager.getAccessor().getSourceId(providerTrg));
		
		HashSet<Integer> relIds = manager.getAccessor().getSourceRelIds(sourceGraph.getSource().getId(), targetGraph.getSource().getId());
		if (relIds!=null){
			 intended = manager.loadMatchResult(manager.getSourceRel(relIds.iterator().next()));
			 if (!intended.getSourceGraph().equals(sourceGraph)){
				 intended = MatchResult.transpose(intended);
			 }
		}
		System.out.println();
		System.out.println("*****************");
		reusePathLength1(sourceGraph, targetGraph);
		System.out.println("*****************");
		reuse_Length2Top1(sourceGraph, targetGraph);
		System.out.println("*****************");
		reuseMaxPath(sourceGraph, targetGraph);
		System.out.println("*****************");
		reuseTopK(sourceGraph, targetGraph);
		System.out.println("*****************");
		reuseExact(sourceGraph, targetGraph);
		System.out.println("*****************");
	}
	
	private void reuse_Length2Top1(Graph sourceGraph, Graph targetGraph){
		boolean exact = true;
		int topKPaths= 1 , maxPathLen= 2, combination = Combination.COM_MAX;	
		MatchResult min=null, max=null, avg=null;
		boolean usePivotSchema = false;	
		for (int i = 0; i < MatchResult.COMPOSE_IDS.length; i++) {
			int composition = MatchResult.COMPOSE_IDS[i];
			MappingReuse reuse = new MappingReuse(topKPaths, maxPathLen, exact, composition, combination, usePivotSchema);
			MatchResult result1 = reuse.executeReuse(manager, sourceGraph, targetGraph);
			switch (composition) {
			case MatchResult.COMPOSITION_MIN:
				min = result1;
				break;
			case MatchResult.COMPOSITION_MAX:
				max = result1;
				break;
			case MatchResult.COMPOSITION_AVERAGE:
				avg = result1;
				break;
			}
		}
		int minCnt = min.getMatchCount();
		int maxCnt = max.getMatchCount();
		int avgCnt = avg.getMatchCount();
		
		assertEquals(minCnt, maxCnt);
		assertEquals(minCnt, avgCnt);
		
		assertEquals(min.getSrcMatchObjectsCount(), max.getSrcMatchObjectsCount());
		assertEquals(min.getSrcMatchObjectsCount(), avg.getSrcMatchObjectsCount());
		
		assertEquals(min.getTrgMatchObjectsCount(), max.getTrgMatchObjectsCount());
		assertEquals(min.getTrgMatchObjectsCount(), avg.getTrgMatchObjectsCount());
		
		ArrayList<Object> srcObjects = min.getSrcMatchObjects();
		ArrayList<Object> trgObjects = min.getTrgMatchObjects();
		for (Object srcObject : srcObjects) {
			for (Object trgObject : trgObjects) {
				float minSim = min.getSimilarity(srcObject, trgObject);
				float maxSim = max.getSimilarity(srcObject, trgObject);
				float avgSim = avg.getSimilarity(srcObject, trgObject);
				assertTrue(minSim==avgSim);
				assertTrue(avgSim==maxSim);
			}
		}
	}

	
	private void reuseMaxPath(Graph sourceGraph, Graph targetGraph){
		boolean exact = true, usePivotSchema = false;	
		int topKPaths= 1 , combination = Combination.COM_MAX, composition = MatchResult.COMPOSITION_AVERAGE;	
		
		float[][] expected={
				{1f, 1f, 1f},
				{0.98f, 0.93f, 0.95f},
				{0.98f, 0.93f, 0.95f},
				{0.98f, 0.87f, 0.92f},
		};
		
		for (int maxPathLen= 1; maxPathLen <= 4; maxPathLen++) {
			MappingReuse reuse = new MappingReuse(topKPaths, maxPathLen, exact, composition, combination, usePivotSchema);
			MatchResult result1 = reuse.executeReuse(manager, sourceGraph, targetGraph);
			EvaluationMeasure r = intended.compare(result1);
			assertEquals(expected[maxPathLen-1][0], r.getPrecision(), 0.01f);
			assertEquals(expected[maxPathLen-1][1], r.getRecall(), 0.01f);
			assertEquals(expected[maxPathLen-1][2], r.getFmeasure(), 0.01f);
//			System.out.println(r.getPrecision() + "\t" + r.getRecall() + "\t" + r.getFmeasure());
		};
	}
	
	private void reuseExact(Graph sourceGraph, Graph targetGraph){
		boolean exact = true, usePivotSchema = false;	
		int topKPaths= 4 , maxPathLen= 3, combination = Combination.COM_MAX, composition = MatchResult.COMPOSITION_AVERAGE;	
		
		float[][] expected={
				{0.96f, 0.98f, 0.97f},
				{0.98f, 1f, 0.99f},
		};
		
		MappingReuse reuse = new MappingReuse(topKPaths, maxPathLen, exact, composition, combination, usePivotSchema);
		MatchResult result = reuse.executeReuse(manager, sourceGraph, targetGraph);
		EvaluationMeasure r = intended.compare(result);
		assertEquals(expected[0][0], r.getPrecision(), 0.01f);
		assertEquals(expected[0][1], r.getRecall(), 0.01f);
		assertEquals(expected[0][2], r.getFmeasure(), 0.01f);
		
//		System.out.println(r.getPrecision() + "\t" + r.getRecall() + "\t" + r.getFmeasure());
		
		exact= false;
		
		reuse = new MappingReuse(topKPaths, maxPathLen, exact, composition, combination, usePivotSchema);
		result = reuse.executeReuse(manager, sourceGraph, targetGraph);
		r = intended.compare(result);
		assertEquals(expected[1][0], r.getPrecision(), 0.01f);
		assertEquals(expected[1][1], r.getRecall(), 0.01f);
		assertEquals(expected[1][2], r.getFmeasure(), 0.01f);
		
//		System.out.println(r.getPrecision() + "\t" + r.getRecall() + "\t" + r.getFmeasure());
	}
	
	private void reuseTopK(Graph sourceGraph, Graph targetGraph){
		boolean exact = true, usePivotSchema = false;	
		int maxPathLen=2, combination = Combination.COM_MAX, composition = MatchResult.COMPOSITION_AVERAGE;	
		
		float[][] expected={
				{0.98f, 0.93f, 0.95f},
				{0.98f, 0.94f, 0.96f},
				{0.96f, 0.98f, 0.97f},
		};
		
		for (int topKPaths= 1; topKPaths < 4; topKPaths++) {
			MappingReuse reuse = new MappingReuse(topKPaths, maxPathLen, exact, composition, combination, usePivotSchema);
			MatchResult result1 = reuse.executeReuse(manager, sourceGraph, targetGraph);
			EvaluationMeasure r = intended.compare(result1);
			assertEquals(expected[topKPaths-1][0], r.getPrecision(), 0.01f);
			assertEquals(expected[topKPaths-1][1], r.getRecall(), 0.01f);
			assertEquals(expected[topKPaths-1][2], r.getFmeasure(), 0.01f);
//			System.out.println(r.getPrecision() + "\t" + r.getRecall() + "\t" + r.getFmeasure());
		};
	}
	
	
	
	public void testValidate(){
		for (int i = 0; i < MappingReuse.REUSE.length; i++) {
			MappingReuse r = new MappingReuse(MappingReuse.REUSE[i]);
			String name = r.getName();
			String value = r.toString();
			System.out.println(name +" -> " +  value);
			Tree tree = TreeToWorkflow.getTree(value);
			System.out.println(tree.toString());
			System.out.println(tree.toStringTree());
			MappingReuse reuse = MappingReuse.buildWorkflow(tree);	
			System.out.println(reuse.toString());
		}
	}

}
