/*
 *  FITMAN SeMa
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
package de.wdilab.coma.gui.view;

/**
 * Created with IntelliJ IDEA.
 * User: Evmorfia
 * Date: 20/5/2014
 * Time: 3:04 μμ
 * To change this template use File | Settings | File Templates.
 */

import de.wdilab.coma.gui.extensions.Registration;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TreeTXT extends JFrame
{
    public TreeTXT()
    {

        final String s1 =  "Source format:";
        final int s1_length = s1.length();
        final String s2=   "Target format:";
        final int s2_length = s2.length();
        final String s3 = "Source extension:";
        final int s3_length = s3.length();
        final String s4 = "Target extension:";
        final int s4_length = s4.length();
        final String s5 = "Document type:";
        final int s5_length = s5.length();
        final JLabel selected1 = new JLabel(s1,JLabel.LEFT)  ;
        final JLabel selected2 = new JLabel(s2)  ;
        final JLabel selected3 = new JLabel(s3)  ;
        final JLabel selected4 = new JLabel(s4)  ;
        final JLabel selected5 = new JLabel(s5)  ;
        final JTree sourceFormatTree = createFormatTree();
//        JPanel pan1 = new JPanel();
//        pan1.add(new JScrollPane(sourceFormatTree));
        sourceFormatTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) sourceFormatTree.getLastSelectedPathComponent();
                selected1.setText(s1+selectedNode.getUserObject().toString());
            }
        });

        final JTree targetFormatTree = createFormatTree();
//        JPanel pan2 = new JPanel();
//        pan2.add(new JScrollPane(targetFormatTree));
        targetFormatTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) targetFormatTree.getLastSelectedPathComponent();
                selected2.setText(s2+selectedNode.getUserObject().toString());
            }
        });

        final JTree sourceExtTree = createExtensionTree();
//        JPanel pan3 = new JPanel();
//        pan3.add(new JScrollPane(sourceExtTree));
        sourceExtTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) sourceExtTree.getLastSelectedPathComponent();
                selected3.setText(s3+selectedNode.getUserObject().toString());
            }
        });

        final JTree targetExtTree = createExtensionTree();
//        JPanel pan4 = new JPanel();
//        pan4.add(new JScrollPane(targetExtTree));
        targetExtTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) targetExtTree.getLastSelectedPathComponent();
                selected4.setText(s4+selectedNode.getUserObject().toString());
            }
        });


//        JPanel pan5 = new JPanel();
//        pan5.add(new JScrollPane(docTree));

        /////

        JPanel source = new JPanel(new BorderLayout());
        JLabel labelSource = new JLabel("Select source format and extension");
        source.add(labelSource,BorderLayout.BEFORE_FIRST_LINE);
        JSplitPane splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JScrollPane(sourceFormatTree),new JScrollPane(sourceExtTree));
        splitPane.setDividerLocation(150);
        source.add(splitPane,BorderLayout.CENTER);
//        source.add(pan1,BorderLayout.EAST);
//        source.add(pan3,BorderLayout.WEST);

        JPanel target = new JPanel(new BorderLayout());
        JLabel labelTarget = new JLabel("Select target format and extension");

        target.add(labelTarget, BorderLayout.BEFORE_FIRST_LINE);
        JSplitPane splitPane2=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JScrollPane(targetFormatTree),new JScrollPane(targetExtTree));
        splitPane2.setDividerLocation(150);
        target.add(splitPane2, BorderLayout.CENTER);

        JSplitPane splitPane3=new JSplitPane(JSplitPane.VERTICAL_SPLIT,source,target);
         splitPane3.setDividerLocation(200);

//        getContentPane().add(splitPane3, BorderLayout.CENTER);
//        JSplitPane splp = getDescrAndDocType();
        JPanel eleos = new JPanel(new BorderLayout());
        final JTextArea serviceDescriptionArea = new JTextArea();
        JLabel descriptionLabel = new JLabel("Provide service description:");
        JLabel transformationTypeLabel = new JLabel("Select transformation type:");
        final JTree docTree = createDocumentTypeTree();
        docTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) docTree.getLastSelectedPathComponent();
                selected5.setText(s5+selectedNode.getUserObject().toString());
            }
        });
        eleos.add(descriptionLabel,BorderLayout.BEFORE_FIRST_LINE);
        eleos.add(serviceDescriptionArea);

        JSplitPane docTreePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,transformationTypeLabel,new JScrollPane(docTree));

        //let user choose name - unique name not required (?)
        JPanel nameServicePanel = new JPanel(new BorderLayout());
        JLabel nameServiceLabel = new JLabel("Provide service name");
        final JTextField nameServiceField = new JTextField();
        nameServicePanel.add(nameServiceLabel,BorderLayout.BEFORE_FIRST_LINE);
        nameServicePanel.add(nameServiceField,BorderLayout.CENTER);



        JPanel results = new JPanel();
        results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
        final JButton butt = new JButton("Register");
        final JButton buttCancel = new JButton("Cancel");
        buttCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        results.add(selected1);
        results.add(selected3);
        results.add(selected2);
        results.add(selected4);
        results.add(selected5);
        final JLabel errorLabel = new JLabel("    ");
        butt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameServiceField.getText().trim().equals("")) {
                     errorLabel.setText("Service name cannot be null.");
                    errorLabel.setForeground(Color.red);
                }
                else if (serviceDescriptionArea.getText().trim().equals("")){
                    errorLabel.setText("Service description cannot be null.");
                    errorLabel.setForeground(Color.red);
                }
                else if (selected1.getText().equals(s1)){
                    errorLabel.setText("Source format cannot be null.");
                    errorLabel.setForeground(Color.red);
                }
                else if (selected2.getText().equals(s2)){
                    errorLabel.setText("Target format cannot be null.");
                    errorLabel.setForeground(Color.red);
                }
                else if (selected3.getText().equals(s3)){
                    errorLabel.setText("Source extension cannot be null.");
                    errorLabel.setForeground(Color.red);
                }
                else if (selected4.getText().equals(s4)){
                    errorLabel.setText("Target extension cannot be null.");
                    errorLabel.setForeground(Color.red);
                }
                else if (selected5.getText().equals(s5)){
                    errorLabel.setText("Document type cannot be null.");
                    errorLabel.setForeground(Color.red);
                }
                else{
                    errorLabel.setText("");
                    boolean registered= Registration.registerS(selected1.getText().substring(s1_length), selected3.getText().substring(s3_length), selected2.getText().substring(s2_length), selected4.getText().substring(s4_length), serviceDescriptionArea.getText(), selected5.getText().substring(s5_length), nameServiceField.getText());
                    if (registered){
                        JTextArea text = new JTextArea("Service "+nameServiceField.getText()+" was registered successfully.");
                        text.setOpaque(false);
                        text.setEditable(false);
                        text.setFont(text.getFont().deriveFont(12f));
                        JOptionPane.showMessageDialog(butt.getParent(),text,"Service registered",JOptionPane.DEFAULT_OPTION);
                    }
                    else{
                        JTextArea text = new JTextArea("There has been an unexpected error. Service "+nameServiceField.getText()+" could not be registered.");
                        text.setOpaque(false);
                        text.setEditable(false);
                        text.setFont(text.getFont().deriveFont(12f));
                        JOptionPane.showMessageDialog(butt.getParent(),text,"Service registration failed",JOptionPane.ERROR_MESSAGE);
                    }
                        dispose();
                }
            }
        });
        JPanel buttons = new JPanel(new BorderLayout());

        results.add(butt);

//        results.add(buttCancel);

        buttons.add(buttCancel,BorderLayout.EAST);
        results.add(errorLabel);
//        results.add(buttons);



        JSplitPane nameServicePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,eleos,nameServicePanel);
        nameServicePane.setDividerLocation(160);
        JSplitPane descrPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,nameServicePane,results);

        descrPane.setDividerLocation(200);
        JSplitPane splitPaneDown=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,docTreePane,descrPane);
        splitPaneDown.setDividerLocation(170);
        JSplitPane finalSP = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,splitPane3,splitPaneDown);
        finalSP.setDividerLocation(300);
        getContentPane().add(finalSP);

        ////////////
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle("Register Transformation Service");
        setSize(700,400);
//        this.pack();
        this.setVisible(true);
    }

    private JTree createFormatTree(){

        JTree treeFormat;

        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Format");
        //create the child nodes
        DefaultMutableTreeNode staNode = new DefaultMutableTreeNode("STANDARD");
        DefaultMutableTreeNode nonstaNode = new DefaultMutableTreeNode("NON_STANDARD");

        DefaultMutableTreeNode n1 = new DefaultMutableTreeNode("COMplus");
        DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("FMN");
        DefaultMutableTreeNode n3 = new DefaultMutableTreeNode("TXT");
        DefaultMutableTreeNode n4 = new DefaultMutableTreeNode("Microsoft");

        DefaultMutableTreeNode nn41 = new DefaultMutableTreeNode("Outlook");
        DefaultMutableTreeNode nnn411 = new DefaultMutableTreeNode("_2007");
        DefaultMutableTreeNode nnn412 = new DefaultMutableTreeNode("_2010");
        nn41.add(nnn411);
        nn41.add(nnn412);

        DefaultMutableTreeNode nn42 = new DefaultMutableTreeNode("Word");
        DefaultMutableTreeNode nn43 = new DefaultMutableTreeNode("Excel");
        n4.add(nn41);
        n4.add(nn42);
        n4.add(nn43);

        DefaultMutableTreeNode n5 = new DefaultMutableTreeNode("Mozilla");
        DefaultMutableTreeNode nn51 = new DefaultMutableTreeNode("Thunderbird");
        n5.add(nn51);

        nonstaNode.add(n1);
        nonstaNode.add(n2);
        nonstaNode.add(n3);
        nonstaNode.add(n4);
        nonstaNode.add(n5);


        //add the child nodes to the root node
        root.add(staNode);
        root.add(nonstaNode);

        //create the tree by passing in the root node
        treeFormat = new JTree(root);
        return treeFormat;
    }

    private JTree createDocumentTypeTree(){

        JTree treeFormat;

        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("BusinessDocument");
        //create the child nodes
        DefaultMutableTreeNode staNode2 = new DefaultMutableTreeNode("Order");

        DefaultMutableTreeNode staNode = new DefaultMutableTreeNode("Invoice");
        DefaultMutableTreeNode nonstaNode = new DefaultMutableTreeNode("PurchaseOrder");
        DefaultMutableTreeNode sNode = new DefaultMutableTreeNode("RequestForQuotation");
        DefaultMutableTreeNode tNode = new DefaultMutableTreeNode("ExceptionCriteria");
        DefaultMutableTreeNode n1 = new DefaultMutableTreeNode("UBL21EC");
        DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("GS1EC");
        tNode.add(n1);
        tNode.add(n2);
        DefaultMutableTreeNode uNode = new DefaultMutableTreeNode("Forecast");

        DefaultMutableTreeNode vNode = new DefaultMutableTreeNode("PurchaseConditions");


        DefaultMutableTreeNode n3 = new DefaultMutableTreeNode("UBL2");
        DefaultMutableTreeNode n4 = new DefaultMutableTreeNode("GS1");
        vNode.add(n3);
        vNode.add(n4);

        //add the child nodes to the root node
        root.add(staNode2);
        root.add(staNode);
        root.add(nonstaNode);
        root.add(sNode);
        root.add(tNode);
        root.add(uNode);
        root.add(vNode);

        //create the tree by passing in the root node
        treeFormat = new JTree(root);
        return treeFormat;
    }

    private JTree createExtensionTree(){

        JTree treeFormat;

        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Extension");
        //create the child nodes
        DefaultMutableTreeNode staNode = new DefaultMutableTreeNode("XML");
        DefaultMutableTreeNode nonstaNode = new DefaultMutableTreeNode("CSV");
        DefaultMutableTreeNode sNode = new DefaultMutableTreeNode("PDF");

        root.add(staNode);
        root.add(nonstaNode);
        root.add(sNode);

        //create the tree by passing in the root node
        treeFormat = new JTree(root);
        return treeFormat;
    }


}