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
package de.wdilab.coma.gui.extensions;

import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.gui.TextFrame;
import de.wdilab.coma.gui.view.TreeTXT;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Path;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;
import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: Evmorfia
 * Date: 7/8/2013
 * Time: 2:15 μμ
 * To change this template use File | Settings | File Templates.
 *
 *
 */
public class XSLTmakerNoHierarchy {

    private static StringBuffer strB;
    private static StringBuffer storeBeforeAttributes;
    private static StringBuffer storeAttributes;
    private static int sourceID;
    private static int targetID;
    private static HashSet<String> sourceAtts;
    private static HashSet<String> targetAtts;
    private static HashSet<String> sourceMaxOccurs;
    private static HashSet<String> targetMaxOccurs;
    private static ArrayList<String> names;
    private static ArrayList<String> sourceNamesComplex;


    private static HashMap<String,String> transformMap(HashMap<String,String> prevMap, Controller c){

        String selectSTMT="select k.name from object k, object s where k.source_id=s.source_id and s.accession=k.accession and k.kind=5 and \n" +
                "s.object_id=";
        ResultSet rs;
        HashMap<String,String> resultMap = new HashMap<String, String>();
        String key;
        String value;


        for(String s:prevMap.keySet()){
            String newKey="";
            String newValue="";
            key = s.substring(0,s.indexOf('['));
            value = prevMap.get(s).substring(0,s.indexOf('['));
            int ind1 = Integer.parseInt(key);
            int ind2 = Integer.parseInt(value);
            rs=c.getManager().getAccessor().performSelectQuery(selectSTMT+ind1);
            try {
                while (rs.next()){
                    newKey = rs.getString("name");
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            rs=c.getManager().getAccessor().performSelectQuery(selectSTMT+ind2);
            try {
                while (rs.next()){
                    newValue = rs.getString("name");
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            resultMap.put(newKey,newValue);
        }

        return resultMap;
    }


    private static HashMap<String,String> returnMapLeftToRight(MatchResult result, Controller c){

        HashMap<String,String> map=new HashMap<String, String>();
        ArrayList<Object> srcObjects = result.getSrcObjects();
        ArrayList<Object> trgObjects = result.getTrgObjects();

        for (int i=0; i<srcObjects.size(); i++) {
            Object srcObject = srcObjects.get(i);
            for (int j=0; j<trgObjects.size(); j++) {
                Object trgObject = trgObjects.get(j);
                float sim = result.getSimilarity(srcObject, trgObject);
                if (sim>0) {
                    if (srcObject instanceof Path && trgObject instanceof Path)
                        map.put((((Path) trgObject).toNameString()), (((Path) srcObject).toNameString()));
                    else if (srcObject instanceof Element && trgObject instanceof Element)
                        map.put((trgObject.toString()),(srcObject.toString()));
                    else
                        map.put((trgObject.toString()), (srcObject.toString()));
                }
            }
        }
        boolean isNameStrategy = false;
        for (String s:map.keySet()){
            if(s.contains("[")){
                isNameStrategy=true;
                break;
            }
            break;
        }

        if (isNameStrategy)
            map=transformMap(map,c);



        return map;

    }

    private static HashMap<String,String> returnMapRightToLeft(MatchResult result, Controller c){

        HashMap<String,String> map=new HashMap<String, String>();
        ArrayList<Object> srcObjects = result.getSrcObjects();
        ArrayList<Object> trgObjects = result.getTrgObjects();

        for (int i=0; i<srcObjects.size(); i++) {
            Object srcObject = srcObjects.get(i);
            for (int j=0; j<trgObjects.size(); j++) {
                Object trgObject = trgObjects.get(j);
                float sim = result.getSimilarity(srcObject, trgObject);
                if (sim>0) {
                    if (srcObject instanceof Path && trgObject instanceof Path)
                        map.put((((Path) srcObject).toNameString()), (((Path) trgObject).toNameString()));
                    else if (trgObject instanceof Element && srcObject instanceof Element)
                        map.put((srcObject.toString()),(trgObject.toString()));
                    else
                        map.put((srcObject.toString()), (trgObject.toString()));
                }
            }
        }

        boolean isNameStrategy = false;
        for (String s:map.keySet()){
            if(s.contains("[")){
                isNameStrategy=true;
                break;
            }
            break;
        }

        if (isNameStrategy)
            map=transformMap(map,c);


        return map;

    }

    //create 2 sets for source: 1 for storing the attributes and 1 for storing the elements that have maxOccurs>1
    //do the same for target
    private static void createMaps(Controller c){

        ResultSet rs;
        HashMap<String,String> resultMap = new HashMap<String, String>();
        sourceAtts=new HashSet<String>();
        targetAtts=new HashSet<String>();
        sourceMaxOccurs=new HashSet<String>();
        targetMaxOccurs=new HashSet<String>();
        String selectSTMT ="select object_id, comment from object where comment is not null and source_id=";
        String selectSTMT2="select name, accession from object where kind=5 and source_id=";
        //for source

        rs=c.getManager().getAccessor().performSelectQuery(selectSTMT+sourceID);
        try {
            while (rs.next()){
                String s=rs.getString("object_id");
                String com=rs.getString("comment");
                resultMap.put(s,com);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        rs=c.getManager().getAccessor().performSelectQuery(selectSTMT2+sourceID);
        try {
            while (rs.next()){
                String name=rs.getString("name");
                String acc=rs.getString("accession");
                if(!acc.contains("."))
                    continue;
                String lastID=acc.substring(acc.lastIndexOf('.')+1);
                if (resultMap.containsKey(lastID)) {
                    if(resultMap.get(lastID).equals("no"))
                        sourceAtts.add(name);
                    else if(resultMap.get(lastID).equals("more"))
                        sourceMaxOccurs.add(name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //same for target

        resultMap=new HashMap<String, String>();
        rs=c.getManager().getAccessor().performSelectQuery(selectSTMT+targetID);
        try {
            while (rs.next()){
                String s=rs.getString("object_id");
                String com=rs.getString("comment");
                resultMap.put(s,com);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        rs=c.getManager().getAccessor().performSelectQuery(selectSTMT2+targetID);
        try {
            while (rs.next()){
                String name=rs.getString("name");
                String acc=rs.getString("accession");
                if(!acc.contains("."))
                    continue;
                String lastID=acc.substring(acc.lastIndexOf('.')+1);
                if (resultMap.containsKey(lastID)) {
                    if(resultMap.get(lastID).equals("no"))
                        targetAtts.add(name);
                    else if(resultMap.get(lastID).equals("more"))
                        targetMaxOccurs.add(name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    private static HashMap<String,String> defineSourceAndTargetIDs(MatchResult result,boolean leftTOright,Controller c){

        HashMap<String,String> map;
        if (leftTOright){           //source to target transformation
            sourceID=result.getSourceGraph().getSource().getId();
            targetID=result.getTargetGraph().getSource().getId();
            map=returnMapLeftToRight(result,c);
        }
        else{
            sourceID=result.getTargetGraph().getSource().getId();
            targetID=result.getSourceGraph().getSource().getId();
            map=returnMapRightToLeft(result,c);
        }
        return map;

    }

    private static void createComplexElementsSet(Controller c){

        ResultSet rs;
        HashSet<String> unique = new HashSet<String>();
        String selectNamesStatement="SELECT name,accession FROM object WHERE kind=5 and source_id="+sourceID+" ORDER BY accession ";
        rs=c.getManager().getAccessor().performSelectQuery(selectNamesStatement);
        ArrayList<String> sourceNames=new ArrayList<String>();
        try {
            while (rs.next())  {
                // fix bug_double_appearances start
                String lastPartAccession=rs.getString("accession");
                lastPartAccession = lastPartAccession.substring(lastPartAccession.lastIndexOf('.')+1);
                if (unique.contains(lastPartAccession))
                    continue;
                unique.add(lastPartAccession);
                // fix bug_double_appearances end
                sourceNames.add(rs.getString("name"));
            }
            sourceNamesComplex = new ArrayList<String>();
            for (int i=0;i<sourceNames.size();i++){
                String current = sourceNames.get(i);
                if (i<(sourceNames.size()-1)){
                    String next = sourceNames.get(i+1);
                    String[] cur = current.split("\\.");
                    String[] nex = next.split("\\.");
                    if (cur.length<nex.length){
                        sourceNamesComplex.add(current);
                    }
//                    System.out.println(current+" "+next+"  "+cur.length+"  "+nex.length);
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private static String findNextExistingName(HashMap<String,String> map,int currentIndex){


        String nextName=names.get(currentIndex);
        while(!map.containsKey(nextName)) {
            if(currentIndex==(names.size()-1))  {
                nextName="nonExistingTag";
                break;
            }
            else{
                currentIndex++;
                nextName= names.get(currentIndex);
            }

        }
        return nextName;
    }

    private static void createNamesOrder(Controller c){

        ResultSet rs;
        HashSet<String> unique = new HashSet<String>();
        String selectNamesStatement="SELECT name,accession FROM object WHERE kind=5 and source_id="+targetID+" ORDER BY accession ";
        rs=c.getManager().getAccessor().performSelectQuery(selectNamesStatement);
        names=new ArrayList<String>();
        try {
            while (rs.next())  {
                // fix bug_double_appearances start
                String lastPartAccession=rs.getString("accession");
                lastPartAccession = lastPartAccession.substring(lastPartAccession.lastIndexOf('.')+1);
                if (unique.contains(lastPartAccession))
                    continue;
                unique.add(lastPartAccession);
                // fix bug_double_appearances end
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
//        for (String name:names)
//            System.out.println(name);

    }
    public static String xsltMake(MatchResult result, boolean leftTOright, String outputPath, TextFrame f, Controller c) throws IOException {

        strB = new StringBuffer();
        storeBeforeAttributes=new StringBuffer();
        storeAttributes=new StringBuffer();
        Boolean complexSourceElement;

        HashMap<String,String> map = defineSourceAndTargetIDs(result,leftTOright,c);
        LinkedList<String> list=new LinkedList<String>();    //keep track of the tag you are currently inside
        createMaps(c);

        //get full path to every element in the order they should appear in the xml file
        createNamesOrder(c);

        strB.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        strB.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">\n");
        strB.append("<xsl:output method=\"xml\" encoding=\"UTF-8\" indent=\"yes\"/>\n");
        strB.append("<xsl:template match=\"/\">\n");

        for (int i=0;i<names.size();i++) {

            String currentName=names.get(i);
            if(i==0){
                strB.append("<"+currentName+">\n");
                list.add(currentName);
                storeBeforeAttributes.append("###");
                storeAttributes.append("###");
                continue;
            }
            if(!map.containsKey(currentName)){  //TODO: find cases where absence of corresp creates probs
                continue;
            }
            createComplexElementsSet(c);
            if(sourceNamesComplex.contains(map.get(currentName)))
                complexSourceElement=true;
            else
                complexSourceElement=false;
//            System.out.println(map.get(currentName)+"    "+complexSourceElement);

            String currentNameCorresp = map.get(currentName);
            String nextNameCorresp="";
            String nextName;
            if(i<names.size()-1) {
                int currentIndex=i+1;
                nextName=findNextExistingName(map,currentIndex);

            }
            else
                nextName="nonExistingTag";
            if(!nextName.equals("nonExistingTag"))
                nextNameCorresp=map.get(nextName);
//            System.out.println(currentName+"   "+nextName);
//            System.out.println(currentNameCorresp+"   "+nextNameCorresp);
            String[] name1=currentName.split("\\.");
            String[] name2=nextName.split("\\.");

            if(name1.length<name2.length){
//                System.out.println("*");

                String tag=name1[name1.length-1];


                storeBeforeAttributes.append("<"+tag+">\n");
                list.add(tag);

                storeBeforeAttributes.append("###");
                storeAttributes.append("###");
                if(targetAtts.contains(nextName))
                    if(!complexSourceElement)
                        storeBeforeAttributes.append("<xsl:value-of select=\""+map.get(currentName).replace('.','/')+"\"/>\n");

            }
            else if (name1.length==name2.length){
                String tag=name1[name1.length-1];
                if(targetAtts.contains(currentName)){
                    if (sourceAtts.contains(map.get(currentName))){
                        storeAttributes.append("<xsl:attribute name=\""+tag+"\">\n");
                        String attributePath = map.get(currentName).replace(".", "/");
                        String attrName = "@"+attributePath.substring(attributePath.lastIndexOf('/')+1);
                        attributePath = attributePath.substring(0, attributePath.lastIndexOf('/') + 1);
                        attributePath = attributePath+attrName;
                        storeAttributes.append("<xsl:value-of select=\""+attributePath+"\"/>\n");
                        storeAttributes.append("</xsl:attribute>\n");
                    }
                    else{
                        storeAttributes.append("<xsl:attribute name=\""+tag+"\">\n");
                        storeAttributes.append("<xsl:value-of select=\""+map.get(currentName).replace('.','/')+"\"/>\n");
                        storeAttributes.append("</xsl:attribute>\n");
                    }
                }
                else{
                    if (sourceAtts.contains(map.get(currentName))){
                        storeBeforeAttributes.append("<"+tag+">\n");
                        String attributePath = map.get(currentName).replace(".", "/");
                        String attrName = "@"+attributePath.substring(attributePath.lastIndexOf('/')+1);
                        attributePath = attributePath.substring(0, attributePath.lastIndexOf('/') + 1);
                        attributePath = attributePath+attrName;
                        storeAttributes.append("<xsl:value-of select=\""+attributePath+"\"/>\n");
                        storeBeforeAttributes.append("</"+tag+">\n");
                    }
                    else{

                        storeBeforeAttributes.append("<"+tag+">\n");
                        storeBeforeAttributes.append("<xsl:value-of select=\""+map.get(currentName).replace('.','/')+"\"/>\n");
                        storeBeforeAttributes.append("</"+tag+">\n");


                    }
                }
//                System.out.println("***");

            }
            else {
//                System.out.println("********");
                String tag=name1[name1.length-1];
                if(targetAtts.contains(currentName)){
                    if (sourceAtts.contains(map.get(currentName))){
                        storeAttributes.append("<xsl:attribute name=\""+tag+"\">\n");
                        String attributePath = map.get(currentName).replace(".", "/");
                        String attrName = "@"+attributePath.substring(attributePath.lastIndexOf('/')+1);
                        attributePath = attributePath.substring(0, attributePath.lastIndexOf('/') + 1);
                        attributePath = attributePath+attrName;
                        storeAttributes.append("<xsl:value-of select=\""+attributePath+"\"/>\n");
                        storeAttributes.append("</xsl:attribute>\n");
                    }
                    else{
                        storeAttributes.append("<xsl:attribute name=\""+tag+"\">\n");
                        storeAttributes.append("<xsl:value-of select=\""+map.get(currentName).replace('.','/')+"\"/>\n");
                        storeAttributes.append("</xsl:attribute>\n");
                    }
                }
                else{
                    if (sourceAtts.contains(map.get(currentName))){
                        storeBeforeAttributes.append("<"+tag+">\n");
                        String attributePath = map.get(currentName).replace(".", "/");
                        String attrName = "@"+attributePath.substring(attributePath.lastIndexOf('/')+1);
                        attributePath = attributePath.substring(0, attributePath.lastIndexOf('/') + 1);
                        attributePath = attributePath+attrName;
                        storeAttributes.append("<xsl:value-of select=\""+attributePath+"\"/>\n");
                        storeBeforeAttributes.append("</"+tag+">\n");
                    }
                    else{

                        storeBeforeAttributes.append("<"+tag+">\n");
                        storeBeforeAttributes.append("<xsl:value-of select=\""+map.get(currentName).replace('.', '/')+"\"/>\n");
                        storeBeforeAttributes.append("</"+tag+">\n");


                    }
                }

                String stopHere="";
                for(int ind1=0;ind1<name1.length;ind1++){
                    if(name1[ind1].equals(name2[ind1]))
                        continue;
                    stopHere=name1[ind1];
                    break;
                }

                // added 22/12/2013


                if (list.size()==1){
                    int index1 = storeBeforeAttributes.lastIndexOf("###");
                    int index2 = storeAttributes.lastIndexOf("###");
                    String others = storeBeforeAttributes.substring(index1+3);

                    String attris = storeAttributes.substring(index2+3);
                    String next2 = attris+others;

                    storeBeforeAttributes=storeBeforeAttributes.delete(index1,storeBeforeAttributes.length());
                    storeBeforeAttributes.append(next2);
                    storeAttributes=storeAttributes.delete(index2, storeAttributes.length());

                }
                else{

                    String closeTag=list.removeLast();
                    String next2="";
                    while (!closeTag.equals(stopHere)){


                        int index1 = storeBeforeAttributes.lastIndexOf("###");
                        int index2 = storeAttributes.lastIndexOf("###");
                        String others = storeBeforeAttributes.substring(index1+3);
                        String attris = storeAttributes.substring(index2+3);

                        next2 = attris+others;
                        next2+="</"+closeTag+">";
                        storeBeforeAttributes=storeBeforeAttributes.delete(index1,storeBeforeAttributes.length());
                        storeBeforeAttributes.append(next2);
                        storeAttributes=storeAttributes.delete(index2, storeAttributes.length());


                        closeTag=list.removeLast();
                    }
                    int index1 = storeBeforeAttributes.lastIndexOf("###");
                    int index2 = storeAttributes.lastIndexOf("###");
                    String others = storeBeforeAttributes.substring(index1+3);
//                    System.out.println("store attributes:"+storeAttributes);

                    String attris = storeAttributes.substring(index2+3);
//                    System.out.println("store attributes:"+storeAttributes);

                    next2 = attris+others;
                    next2+="</"+closeTag+">";
                    if((list.size()!=1)&&(list.size()!=0))   {
                        String nextTag=list.removeLast();

                        list.add(nextTag);




                    }
                    storeBeforeAttributes=storeBeforeAttributes.delete(index1,storeBeforeAttributes.length());
                    storeBeforeAttributes.append(next2);
                    storeAttributes=storeAttributes.delete(index2, storeAttributes.length());


                }

            }



        }

        if(list.size()==1){

            strB.append(storeBeforeAttributes);
//            strB.append("</"+list.removeLast()+">");
            strB.append("</"+list.removeLast()+">");
        }
        else if (list.size()==0){
            strB.append(storeBeforeAttributes);
        }
        else
//            System.out.println("nooooooooooooooo");
        strB.append("</xsl:template>");
        strB.append("</xsl:stylesheet>");


        PrintWriter output =new PrintWriter(new FileWriter(outputPath));
        String stringB=strB.toString();
        stringB=stringB.replaceAll("#","");
        output.append(stringB);
        output.close();

        ////////////////
        ///////////////
        //////////////
        /////////////
        //services handling till end
        UUID uid=UUID.randomUUID();
        boolean storeToDb = RemoteRepo.getRemoteConnection(stringB,uid);
//        boolean storeToDb=true;
        String output_msg="";
        String output_title="";
        if (storeToDb){
            output_msg = "You can retrieve the xslt file from : \nhttp://83.212.114.237:8081/RA/public_process/xslt?xslt_uid="+uid+
                    "\n\nYou can create a transformation service that will use the created xslt file to transform xml documents. \nThe service will be registered and invoked through DIPS platform.";
            output_title = "File Successfully Stored";
            JTextArea ar=new JTextArea(output_msg);
            JCheckBox box = new JCheckBox("I want to register the service.");
            Object[] params = {ar,box};
            ar.setOpaque(false);
            ar.setEditable(false);
            ar.setFont(ar.getFont().deriveFont(12f));
            int answer = JOptionPane.showConfirmDialog(f, params, output_title, JOptionPane.DEFAULT_OPTION);
            boolean register = box.isSelected();
            if (register){

                final TreeTXT t = new TreeTXT();
                t.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {

                    }
                });
            }

        }
        else  {
            output_msg = "File could not be saved to remote repository. Please check your internet connection.";
            output_title = "A problem occured.";
            JTextArea ar=new JTextArea(output_msg);
            ar.setOpaque(false);
            ar.setEditable(false);
            ar.setFont(ar.getFont().deriveFont(12f));
            JOptionPane.showMessageDialog(f, ar, output_title, JOptionPane.ERROR_MESSAGE);
        }
        return stringB;

    }
}
