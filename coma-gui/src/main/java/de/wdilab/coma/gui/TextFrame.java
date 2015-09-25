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

package de.wdilab.coma.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledEditorKit;

import de.wdilab.coma.gui.extensions.XSLTmaker;
import de.wdilab.coma.gui.extensions.XSLTmakerNoHierarchy;
import org.antlr.runtime.tree.Tree;

import de.wdilab.coma.export.relationships.MatchResultExport;
import de.wdilab.coma.export.relationships.RDFExport;
import de.wdilab.coma.gui.dlg.Dlg_WorkflowVariables;
import de.wdilab.coma.gui.dlg.editor.GrammarSyntaxDocument;
import de.wdilab.coma.matching.validation.TreeToWorkflow;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.EvaluationMeasure;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;

/**
 * TextFrame shows a given string in this frame. It is used e.g. to show the results of
 * match result comparison, list a workflow variable, show instances and the correspondences
 * of a match result.
 * 
 * @author Sabine Massmann
 */
public class TextFrame extends JFrame {
	//----------------------------------------------
	//	  STATIC FINAL
	//----------------------------------------------
	static final int MATCHRESULT = 0;
	static final int RDF = 1;
    static final int RIGHT_TO_LEFT_XSLT = 10;
    static final int LEFT_TO_RIGHT_XSLT = 20;
	static final int MATCHRESULT_INFO = 2;
	static final int SPICY = 3;
	public static final int WORKFLOW = 4;
	//	static final int SCHEMA = 2;
	//	static final int COMPARE = 3;
	//----------------------------------------------

	JEditorPane pane = null;
	DataImport importer = null;
	Dlg_WorkflowVariables dlgParent = null;
	Controller controller = null;
	
	/*
	 * Constructor of TextFrame, for showing the result of comparing two
	 * matchresults
	 */
	public TextFrame(MatchResult _intendedResult, Object[][] _allmatchresults,
			String _titel /* , int _kind */) {
		super(_titel);
		String output = GUIConstants.COMPARE_LINE2;
		for (int i = 0; i < _allmatchresults.length; i++) {
			MatchResult testResult = (MatchResult) _allmatchresults[i][0];
			if (testResult != _intendedResult) {
				 EvaluationMeasure results = _intendedResult.compare(testResult);
				output += GUIConstants.COMPARE_INT_MATCHRESULT + GUIConstants.TAB2
				//+ tabs.getSelectedTab().getName();
						+ _intendedResult.getName();
				output += GUIConstants.COMPARE_TEST_MATCHRESULT + GUIConstants.TAB2
				//os[1].toString();
						+ testResult.getName();
				output += GUIConstants.LINEBREAK;
				output += GUIConstants.COMPARE_SOURCE
						+ GUIConstants.TAB3
						+ _intendedResult.getSourceGraph().getSource()
								.getName();
				output += GUIConstants.COMPARE_TARGET
						+ GUIConstants.TAB3
						+ _intendedResult.getTargetGraph().getSource()
								.getName();
				output += GUIConstants.COMPARE_PRECISION + GUIConstants.TAB3 + results.getPrecision();
				output += GUIConstants.COMPARE_RECALL + GUIConstants.TAB3 + results.getRecall();
				output += GUIConstants.COMPARE_FMEASURES + GUIConstants.TAB2 + results.getFmeasure();
				// not used: Overall
//				output += GUIConstants.COMPARE_OVERALL + GUIConstants.TAB3 + results[3];
				output += GUIConstants.COMPARE_INT_C + GUIConstants.TAB
						+ results.getIntendedCorresp();
				output += GUIConstants.COMPARE_TEST_C + GUIConstants.TAB
						+ results.getTestCorresp();
				output += GUIConstants.COMPARE_CORRECT + GUIConstants.TAB
						+ results.getCorrectMatches();
				output += GUIConstants.COMPARE_LINE;
			}
		}
		init(output);
	}

	/*
	 * Constructor of TextFrame , for showing either information of a
	 * matchresult or the RDF-Alignment
	 */
	public TextFrame(MatchResult _result, String _titel, int _kind, Dimension dim) {
		super(_titel);
		String output = null;
		if (_kind == MATCHRESULT) {
//			output = _result.toString();
			output = MatchResultExport.resultToString(_result);
		} else 	if (_kind == RDF) {
			output = RDFExport.toRDFAlignment(_result);
//		} else 	if (_kind == SPICY) {
//			output = Spicy.toSpicy(_result,false,null);

        } else {
			output = _result.getStatInfo();
		}
		init(output);
		setSize(dim);
	}


    //extension for xslt
    public TextFrame(MatchResult _result, String _titel, int _kind, Dimension dim, String path, Controller c, boolean strict) {
        super(_titel);
        String output = null;
        if (_kind== RIGHT_TO_LEFT_XSLT){

            try {
                if (strict)
                    output= XSLTmaker.xsltMake(_result, false, path, this, c);
                else
                    output= XSLTmakerNoHierarchy.xsltMake(_result, false, path, this, c);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else if (_kind== LEFT_TO_RIGHT_XSLT){

            try {
                if (strict)
                    output= XSLTmaker.xsltMake(_result, true, path, this, c);
                else
                    output= XSLTmakerNoHierarchy.xsltMake(_result, true, path, this, c);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        } else {
            output = _result.getStatInfo();
        }
        init(output);
        setSize(dim);

    }

	/*
		 * Constructor of TextFrame, for showing the result of comparing two
		 * matchresults
		 */
		public TextFrame(MatchResult _intendedResult, ArrayList _allmatchresults,
				String _titel, Dimension dim /* , int _kind */) {
			super(_titel);
			String output ="";
				
			output += GUIConstants.COMPARE_INT_MATCHRESULT + GUIConstants.TAB
				//+ tabs.getSelectedTab().getName();
						+ _intendedResult.getName();
			output += GUIConstants.COMPARE_SOURCE + GUIConstants.TAB3
				+ _intendedResult.getSourceGraph().getSource()
					.getName();
			output += GUIConstants.COMPARE_TARGET + GUIConstants.TAB3
			+ _intendedResult.getTargetGraph().getSource()
					.getName();			
			output += GUIConstants.LINEBREAK + GUIConstants.COMPARE_LINE3;
//			output += GUIConstants.LINEBREAK;
			
			for (int i = 0; i < _allmatchresults.size(); i++) {
				MatchResult testResult = (MatchResult) _allmatchresults.get(i);
	//			if (testResult != _intendedResult) {
					output += GUIConstants.COMPARE_TEST_MATCHRESULT + GUIConstants.TAB2
					//os[1].toString();
							+ testResult.getName();
					if (_intendedResult.getSourceGraph().equals(testResult.getTargetGraph()) 
							&& _intendedResult.getTargetGraph().equals(testResult.getSourceGraph())){
						testResult = MatchResult.transpose(testResult);
					}
					EvaluationMeasure results = _intendedResult.compare(testResult);
					output += GUIConstants.COMPARE_PRECISION + GUIConstants.TAB2 + results.getPrecision();
					output += GUIConstants.COMPARE_RECALL + GUIConstants.TAB3 + results.getRecall();
					output += GUIConstants.COMPARE_FMEASURES + GUIConstants.TAB2 + results.getFmeasure();
					// not used: Overall
//					output += GUIConstants.COMPARE_OVERALL + GUIConstants.TAB3 + results[3];
					output += GUIConstants.COMPARE_INT_C + GUIConstants.TAB
							+ results.getIntendedCorresp();
					output += GUIConstants.COMPARE_TEST_C + GUIConstants.TAB
							+ results.getTestCorresp();
					output += GUIConstants.COMPARE_CORRECT + GUIConstants.TAB
							+ results.getCorrectMatches();
					output += GUIConstants.COMPARE_LINE3;
	//			}
			}
			init(output);
			setSize(dim);
		}

	/*
	 * Constructor of TextFrame , for showing information about the loaded
	 * schemas
	 */
	public TextFrame(Controller _controller, String _titel, Dimension dim) {
		super(_titel);
		init(getSchemaInformation(_controller));
		setSize(dim);
	}
	
	
	/*
	 * Constructor of TextFrame for editing workflow variable
	 */
	public TextFrame(Controller controller, String name, String value, String _titel, int _kind, Dimension dim) {
		super(_titel);
		this.controller = controller;
		String output = null;
		if (_kind == WORKFLOW) {
			output = name + "->" + value;
		}
		 importer = new DataImport();
		
		pane = new JEditorPane();
	    pane.setDoubleBuffered(true);
	    pane.setSelectionColor(Color.blue);
	    pane.setSelectedTextColor(Color.white);
	    
	    EditorKit editorKit = new StyledEditorKit()
	    {
	      public Document createDefaultDocument()
	      {
	        return new GrammarSyntaxDocument();
	      }
	    };

	    pane.setEditorKitForContentType("text", editorKit);
	    pane.setContentType("text");
	    pane.setText(output);
	    
	    JScrollPane jScrollPane1 = new JScrollPane();
	    jScrollPane1.getViewport().add(pane, null);
	    
	    if (name.toLowerCase().contains("reuse")){
	    	getContentPane().add(getInformation(name, value), BorderLayout.NORTH);
	    }
		getContentPane().add(jScrollPane1, BorderLayout.CENTER);
		
		
		JPanel button = getButton();
		getContentPane().add(button, BorderLayout.SOUTH);
		pack();		
		setSize(dim);
	}
	
	public TextFrame(Source _source, Element _element, String _titel, Dimension dim) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(_source.getName() + " : " + _element.getName() + "\n\n");
//		if (_element.hasAllInstancesSimple()){
		if (_element.hasDirectInstancesSimple()){
			buffer.append("Simple Instances (each Instance contains one value): \n");
//			ArrayList instances = _element.getAllInstancesSimple();
			ArrayList instances = _element.getDirectInstancesSimple();
			for (int i = 0; i < instances.size(); i++) {
				String instance = (String) instances.get(i);
				buffer.append("\t" + instance + "\n");
			}
			 buffer.append("\n");
		}
//		if (_element.hasAllInstancesComplex()){
		if (_element.hasDirectInstancesComplex()){
			buffer.append("Complex Instances (each Instance can contain several attributes with values): \n");
//			HashMap<String, ArrayList<String>> instances = _element.getAllInstancesComplex();
			HashMap<String, ArrayList<String>> instances = _element.getDirectInstancesComplex();
			for (Iterator iterator = instances.keySet().iterator(); iterator.hasNext();) {
				String attribute = (String) iterator.next();
				 ArrayList<String> current = instances.get(attribute);
				 buffer.append("attribute: " + attribute + "\n");
				 for (int i = 0; i < current.size(); i++) {
						String instance =  current.get(i);
						buffer.append("\t" + instance + "\n");
				 }
			}			
		}
		init(buffer.toString());		
		setSize(dim);
	}
	
	JTextArea getInformation(String name, String variable){
		String text = "Values: (int topKPaths, int maxPathLen, boolean exact, "+
			"int composition, int combination, boolean usePivot)";
		JTextArea info = new JTextArea(text);
		
		
		
		info.setEditable(false);
		return info;
	}
	
	public Dlg_WorkflowVariables getDlgParent(){
		return dlgParent;
	}
	
	public void setDlgParent(Dlg_WorkflowVariables dlgParent){
		this.dlgParent= dlgParent;
	}
	
	JPanel getButton() {
		// create an Save Button and add listener
		JButton validateBtn = new JButton(GUIConstants.BUTTON_VALIDATE);
		validateBtn.addActionListener(new ActionListener() {
			/*
			 * if the ok button was pressed close dialog
			 */
			public void actionPerformed(ActionEvent _event) {
				String text = null;
				if (pane!=null){
					text = pane.getText();
				}
				validate(text, true);
			}
		});
		// create an Save Button and add listener
		JButton saveBtn = new JButton(GUIConstants.BUTTON_SAVE);
		saveBtn.addActionListener(new ActionListener() {
			/*
			 * if the ok button was pressed close dialog
			 */
			public void actionPerformed(ActionEvent _event) {
				String text = null;
				if (pane!=null){
					text = pane.getText();
				}
				boolean success = validate(text, false);
				if (text!=null && success){
					String[] parts = text.split("->");
					importer.updateWorkflowVariable(parts[0], parts[1]);
					setVisible(false);
//					if (dlgParent!=null){
//						// TODO refresh
//						dlgParent.setVisible(true);
//					}
					controller.showWorkflowVariables();
				} else {
					validate(text, true);
				}
			
				
			}
		});
		// create an Cancel Button and add listener
		JButton cancelBtn = new JButton(GUIConstants.BUTTON_CANCEL);
		cancelBtn.addActionListener(new ActionListener() {
			/*
			 * if the ok button was pressed close dialog
			 */
			public void actionPerformed(ActionEvent _event) {
				setVisible(false);
				if (dlgParent!=null){
					dlgParent.setVisible(true);
				}
			}
		});
		// create a panel
		JPanel button = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		// add both buttons to the panel
		button.add(validateBtn);
		button.add(saveBtn);
		button.add(cancelBtn);
		return button;
	}
	
	/**
	 * @param text input variable to be validated
	 * @param showMessage show error/success message
	 * @return true if valid, otherwise false
	 */
	boolean validate(String text, boolean showMessage){
			if (text==null || !text.contains("->")){
				if (showMessage) JOptionPane.showMessageDialog(TextFrame.this,
					    "Must contain \"->\"",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);

				return false;
			}
			text = text.replace("\n", "").replace("\t", "");
			String[] parts = text.split("->");
			if (parts.length!=2){
				if (showMessage) JOptionPane.showMessageDialog(TextFrame.this,
					    "Not valid",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);

				return false;
			}
			// TODO replace $ in parts[1] with values or maybe with "Matcher"
			String value = parts[1];
			if (value.contains("$")){
				DataAccess accessor = new DataAccess();
				value = accessor.replaceVariableNames(value);
			}
			// change value to lower case because grammar uses only small letters
//			ANTLRStringStream c = new ANTLRStringStream(value.toLowerCase());
//			ComaWorkFlowLexer l = new ComaWorkFlowLexer(c);
//			BufferedTokenStream t = new BufferedTokenStream(l);
//			ComaWorkFlowParser parser = new ComaWorkFlowParser(t);
			String treeString = null;
//			try {
//				coma_return s = parser.coma();
//				CommonTree tree = (CommonTree) s.getTree();
				Tree tree = TreeToWorkflow.getTree(value);
				if (tree!=null && tree.getChildCount()>0){
					// remove first level "<grammar ComaWorkFlow>" 
					tree = tree.getChild(0);
					if (tree!=null && tree.getChildCount()>0)
					// remove second level "coma"
					treeString = tree.getChild(0).toStringTree();
				}
//				parser.workflow();
//				parser.strategy();
//				parser.complexMatcher();
//				parser.matcher();
//				System.out.println();
//			} catch (RecognitionException e) {
//				if (showMessage) JOptionPane.showMessageDialog(TextFrame.this,
//						e.getCause(), "Error",					    
//					    JOptionPane.ERROR_MESSAGE);
//				return false;
//			}			
			if (treeString==null){
				String message = "Error not specified";
				if (showMessage) JOptionPane.showMessageDialog(TextFrame.this, message,
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			} else if (treeString.contains("<") && treeString.contains(">")){
				// e.g. missing or mismatched
				String message = treeString.substring(treeString.indexOf("<")+1, treeString.lastIndexOf(">"));
				if (showMessage) JOptionPane.showMessageDialog(TextFrame.this,message,
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			} else if (treeString.contains("Exception")){
				// e.g. missing or mismatched
				int index = treeString.indexOf("Exception");
				String message = treeString.substring(0, index);
				message = message.substring(message.lastIndexOf(" "));
				if (showMessage) JOptionPane.showMessageDialog(TextFrame.this,message,
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			} else {
				if (showMessage) JOptionPane.showMessageDialog(TextFrame.this,
					"Validation was successful", "Validation",						   
				    JOptionPane.INFORMATION_MESSAGE);
				return true;
			}		
	}

	
//	public TextFrame(Source _source, Element _element, String _titel, Dimension dim) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append(_source.getLabel() + " : " + _element.getLabel() + "\n\n");
////		if (_element.hasAllInstancesSimple()){
//		if (_element.hasDirectInstancesSimple()){
//			buffer.append("Simple Instances (each Instance contains one value): \n");
////			ArrayList instances = _element.getAllInstancesSimple();
//			ArrayList instances = _element.getDirectInstancesSimple();
//			for (int i = 0; i < instances.size(); i++) {
//				String instance = (String) instances.get(i);
//				buffer.append("\t" + instance + "\n");
//			}
//			 buffer.append("\n");
//		}
////		if (_element.hasAllInstancesComplex()){
//		if (_element.hasDirectInstancesComplex()){
//			buffer.append("Complex Instances (each Instance can contain several attributes with values): \n");
////			HashMap<String, ArrayList<String>> instances = _element.getAllInstancesComplex();
//			HashMap<String, ArrayList<String>> instances = _element.getDirectInstancesComplex();
//			for (Iterator iterator = instances.keySet().iterator(); iterator.hasNext();) {
//				String attribute = (String) iterator.next();
//				 ArrayList<String> current = instances.get(attribute);
//				 buffer.append("attribute: " + attribute + "\n");
//				 for (int i = 0; i < current.size(); i++) {
//						String instance = (String) current.get(i);
//						buffer.append("\t" + instance + "\n");
//				 }
//			}			
//		}
//		init(buffer.toString());		
//		setSize(dim);
//	}
//	
	public TextFrame(String _text, String _titel, Dimension dim) {
		init(_text);		
		setSize(dim);
	}
	
	
//	/*
//	 * Constructor of TextFrame , for showing information about the loaded
//	 * schemas
//	 */
//	public TextFrame(Controller _controller, String _titel) {
//		super(_titel);
//		init(getSchemaInformation(_controller));
//	}
	
	/*
	 * initialize this frame with the given text and dimension
	 */
	void init(String _output) {
		TextArea text = new TextArea(_output);
		text.setBackground(MainWindow.GLOBAL_BACKGROUND);
        // morfoula
        text.setEditable(false);
		getContentPane().add(text);

		//Window-Listener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent _event) {
				setVisible(false);
				dispose();
			}
		});
		pack();
	}

	/*
	 * get the information of the loaded schemas as a string
	 */
	String getSchemaInformation(Controller _controller) {
		String sourceInfos = null;
		String targetInfos = null;
		Graph source = _controller.getGUIMatchresult().getSourceGraph();
		Graph target = _controller.getGUIMatchresult().getTargetGraph();
		if (source != null) {
//			sourceInfos = getGraphInfo(source.getGraph(_controller.getPreprocessing()));
			sourceInfos = source.getGraph(_controller.getPreprocessing()).getGraphInfo();
		} else {
			sourceInfos = GUIConstants.NO_SCHEMA;
		}
		if (target != null) {
			//				targetInfos = getLoadedSchemaInfo(target
			//targetInfos = getGraphInfo(target
			//		.getGraph(Graph.GRAPH_STATE_SIMPLIFIED));
//			targetInfos = getGraphInfo(target.getGraph(_controller.getPreprocessing()));
			targetInfos = target.getGraph(_controller.getPreprocessing()).getGraphInfo();
		} else{
			targetInfos = GUIConstants.NO_SCHEMA;
		}
		String output = GUIConstants.SRC_SCHEMA + GUIConstants.STAR_LINE + sourceInfos
				+ GUIConstants.LINEBREAK2 + GUIConstants.TRG_SCHEMA + GUIConstants.STAR_LINE
				+ targetInfos;
		return output;
	}

}