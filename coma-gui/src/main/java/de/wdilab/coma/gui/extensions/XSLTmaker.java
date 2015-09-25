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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Evmorfia
 * Date: 7/8/2013
 * Time: 2:15 μμ
 * To change this template use File | Settings | File Templates.
 *
 *
 */
public class XSLTmaker {

    private static StringBuffer strB;
    private static StringBuffer storeBeforeAttributes;
    private static StringBuffer storeAttributes;


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

//        for(String s:map.keySet()){
//            System.out.println(s+"  :"+map.get(s)+"!");
//        }

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

//        for(String s:map.keySet()){
//            System.out.println(s+"  :"+map.get(s)+"!");
//        }

        return map;

    }

    public static String xsltMake(MatchResult result, boolean leftTOright, String outputPath, TextFrame f, Controller c) throws IOException {

        //fix bug_double_appearances
        HashSet<String> unique = new HashSet<String>();

        strB = new StringBuffer();
        storeBeforeAttributes=new StringBuffer();
        storeAttributes=new StringBuffer();

        ResultSet rs;
        HashMap<String,String> resultMap=new HashMap<String, String>();
        ArrayList<String> names=new ArrayList<String>();
        UUID uid;
        int sourceID;
        int targetID;
        String selectPath="";
        String selectNamesStatement;
        String selectSourceAttsStatement;
        String selectTargetAttsStatement;
        uid=UUID.randomUUID();

        HashMap<String,String> map;
        HashSet<String> sourceAtts=new HashSet<String>();
        HashSet<String> targetAtts=new HashSet<String>();
        HashSet<String> sourceMaxOccurs=new HashSet<String>();
        HashSet<String> targetMaxOccurs=new HashSet<String>();
        ArrayList<String> current=new ArrayList<String>();
        ArrayList<String> next=new ArrayList<String>();
        LinkedList<String> list=new LinkedList<String>();    //keep track of the tag you are currently inside

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
        selectNamesStatement="SELECT name,accession FROM object WHERE kind=5 and source_id="+targetID+" ORDER BY accession ";
//        selectSourceAttsStatement="SELECT o1.name, o2.comment from object o1,object o2 where o1.kind=5 and o" +
//                "2.kind=1 and o1.accession=o2.accession and o2.comment is not null and o1.source_" +
//                "id="+sourceID;
//        selectTargetAttsStatement="SELECT o1.name, o2.comment from object o1,object o2 where o1.kind=5 and o" +
//                "2.kind=1 and o1.accession=o2.accession and o2.comment is not null and o1.source_" +
//                "id="+targetID;

        String selectSTMT ="select object_id, comment from object where comment is not null and source_id=";
        String selectSTMT2="select name, accession from object where kind=5 and source_id=";

        //create 2 sets for source: 1 for storing the attributes and 1 for storing the elements that have maxOccurs>1
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

        //create 2 sets for source: 1 for storing the attributes and 1 for storing the elements that have maxOccurs>1
//        rs=c.getManager().getAccessor().performSelectQuery(selectSourceAttsStatement);
//        try {
//            while (rs.next()){
//                String s=rs.getString("name");
//                String com=rs.getString("comment");
//                if (com.equals("no"))
//                    sourceAtts.add(s);
//                else if (com.equals("more"))
//                    sourceMaxOccurs.add(s);
//                else
//                    System.out.println("--------------------  oooops  ------------------");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

//        for(String str:sourceAtts)
//            System.out.println("s att:  "+str);

        //create 2 sets for target: 1 for storing the attributes and 1 for storing the elements that have maxOccurs>1
//        rs=c.getManager().getAccessor().performSelectQuery(selectTargetAttsStatement);
//        try {
//            while (rs.next()){
//                String s=rs.getString("name");
//                String com=rs.getString("comment");
//                if (com.equals("no"))
//                    targetAtts.add(s);
//                else if (com.equals("more"))
//                    targetMaxOccurs.add(s);
//                else
//                    System.out.println("--------------------  oooops  ------------------");
//
//                System.out.println(s);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

//        for(String str:targetAtts)
//            System.out.println("t att:  "+str);

        //get full path to every element in the order they should appear in the xml file
        rs=c.getManager().getAccessor().performSelectQuery(selectNamesStatement);
        try {
            while (rs.next())  {
                // fix bug_double_appearances start
                String lastPartAccession=rs.getString("accession");
                lastPartAccession = lastPartAccession.substring(lastPartAccession.lastIndexOf('.')+1);
                if (unique.contains(lastPartAccession))
                    continue;
                unique.add(lastPartAccession);
//                System.out.println(lastPartAccession);
                // fix bug_double_appearances end
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

//        for (String mor:names)
//            System.out.println(mor);

        strB.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        strB.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">\n");
        strB.append("<xsl:output method=\"xml\" encoding=\"UTF-8\" indent=\"yes\"/>\n");
        strB.append("<xsl:template match=\"/\">\n");

        for (int i=0;i<names.size();i++) {
//            strB.append(names.get(i));
//            strB.append("\n");

//            outputBull.println(names.get(i)+"---!--"+storeBeforeAttributes.toString()+"--------");
//            outputBull2.println(names.get(i) + "___!____" + storeAttributes + "__________");

//            for(String str:list)
//                System.out.println("list:   "+str);

            String currentName=names.get(i);
            if(i==0){
                strB.append("<"+currentName+">\n");
                strB.append("<xsl:for-each select=\""+map.get(currentName)+"\">");
                list.add(currentName);
                list.add("xsl:for-each");
                storeBeforeAttributes.append("###");
                storeAttributes.append("###");
                continue;
            }
            if(!map.containsKey(currentName)){  //TODO: find cases where absence of corresp creates probs
//                System.out.println("not found:"+currentName);
                continue;
            }
            String nextName;
            if(i<names.size()-1) {
                int currentIndex=i+1;
                nextName=names.get(currentIndex);
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

            }
            else
                nextName="nonExistingTag";
//            System.out.println(currentName+"   "+nextName);
            String[] name1=currentName.split("\\.");
            String[] name2=nextName.split("\\.");

            if(name1.length<name2.length){
//                System.out.println("*");

                String tag=name1[name1.length-1];
                if(targetMaxOccurs.contains(currentName)){
                    storeBeforeAttributes.append("<xsl:for-each select=\""+selectPath+map.get(currentName).substring(map.get(currentName).lastIndexOf('.')+1)+"\">\n");
                    selectPath="";
                    storeBeforeAttributes.append("<"+tag+">\n");
                    list.add("xsl:for-each");
                    list.add(tag);

                }
                else{
                    storeBeforeAttributes.append("<"+tag+">\n");
                    list.add(tag);
                    String addToPath=map.get(currentName);
                    addToPath=addToPath.substring(addToPath.lastIndexOf('.')+1);
                    selectPath+=addToPath;
                    selectPath+="/";
                }
                storeBeforeAttributes.append("###");
                storeAttributes.append("###");
                if(targetAtts.contains(nextName))
                    storeBeforeAttributes.append("<xsl:value-of select=\""+selectPath+map.get(currentName).substring(map.get(currentName).lastIndexOf('.')+1)+"\"/>\n");

            }
            else if (name1.length==name2.length){
                String tag=name1[name1.length-1];
                if(targetAtts.contains(currentName)){
                    if (sourceAtts.contains(map.get(currentName))){
                        storeAttributes.append("<xsl:attribute name=\""+tag+"\">\n");
                        storeAttributes.append("<xsl:value-of select=\""+selectPath+"@"+map.get(currentName).substring(map.get(currentName).lastIndexOf('.')+1)+"\"/>\n");
                        storeAttributes.append("</xsl:attribute>\n");
                    }
                    else{
                        storeAttributes.append("<xsl:attribute name=\""+tag+"\">\n");
                        storeAttributes.append("<xsl:value-of select=\""+selectPath+map.get(currentName).substring(map.get(currentName).lastIndexOf('.')+1)+"\"/>\n");
                        storeAttributes.append("</xsl:attribute>\n");
                    }
                }
                else{
                    if (sourceAtts.contains(map.get(currentName))){
                        storeBeforeAttributes.append("<"+tag+">\n");
                        storeBeforeAttributes.append("<xsl:value-of select=\""+selectPath+"@"+map.get(currentName).substring(map.get(currentName).lastIndexOf('.')+1)+"\"/>\n");
                        storeBeforeAttributes.append("</"+tag+">\n");
                    }
                    else{
                        if(targetMaxOccurs.contains(currentName)){
                            storeBeforeAttributes.append("<xsl:for-each select=\""+selectPath+map.get(currentName).substring(map.get(currentName).lastIndexOf('.')+1)+"\">\n");
                            selectPath="";
                            storeBeforeAttributes.append("<"+tag+">\n");
                            storeBeforeAttributes.append("<xsl:value-of select=\".\"/>\n");
                            storeBeforeAttributes.append("</"+tag+">\n");
                            storeBeforeAttributes.append("</xsl:for-each>\n");
                        }
                        else{
                            storeBeforeAttributes.append("<"+tag+">\n");
                            storeBeforeAttributes.append("<xsl:value-of select=\""+selectPath+map.get(currentName).substring(map.get(currentName).lastIndexOf('.')+1)+"\"/>\n");
                            storeBeforeAttributes.append("</"+tag+">\n");
                        }

                    }
                }
//                System.out.println("***");
//                outputBull.println(names.get(i)+"<><><><>---!!--"+storeBeforeAttributes.toString()+"--------");
//                outputBull2.println(names.get(i)+"<><><><><>___!!____"+storeAttributes+"__________");

            }
            else {
//                System.out.println("********");
                String tag=name1[name1.length-1];
                if(targetAtts.contains(currentName)){
                    if (sourceAtts.contains(map.get(currentName))){
                        storeAttributes.append("<xsl:attribute name=\""+tag+"\">\n");
                        storeAttributes.append("<xsl:value-of select=\""+selectPath+"@"+map.get(currentName).substring(map.get(currentName).lastIndexOf('.')+1)+"\"/>\n");
                        storeAttributes.append("</xsl:attribute>\n");
                    }
                    else{
                        storeAttributes.append("<xsl:attribute name=\""+tag+"\">\n");
                        storeAttributes.append("<xsl:value-of select=\""+selectPath+map.get(currentName).substring(map.get(currentName).lastIndexOf('.')+1)+"\"/>\n");
                        storeAttributes.append("</xsl:attribute>\n");
                    }
                }
                else{
                    if (sourceAtts.contains(map.get(currentName))){
                        storeBeforeAttributes.append("<"+tag+">\n");
                        storeBeforeAttributes.append("<xsl:value-of select=\""+selectPath+"@"+map.get(currentName).substring(map.get(currentName).lastIndexOf('.')+1)+"\"/>\n");
                        storeBeforeAttributes.append("</"+tag+">\n");
                    }
                    else{
                        if(targetMaxOccurs.contains(currentName)){
                            storeBeforeAttributes.append("<xsl:for-each select=\""+selectPath+map.get(currentName).substring(map.get(currentName).lastIndexOf('.')+1)+"\">\n");
                            selectPath="";
                            storeBeforeAttributes.append("<"+tag+">\n");
                            storeBeforeAttributes.append("<xsl:value-of select=\".\"/>\n");
                            storeBeforeAttributes.append("</"+tag+">\n");
                            storeBeforeAttributes.append("</xsl:for-each>\n");
                        }
                        else{
                            storeBeforeAttributes.append("<"+tag+">\n");
                            storeBeforeAttributes.append("<xsl:value-of select=\""+selectPath+map.get(currentName).substring(map.get(currentName).lastIndexOf('.')+1)+"\"/>\n");
                            storeBeforeAttributes.append("</"+tag+">\n");
                        }

                    }
                }

                //begin to unpack...
//                outputBull.println(names.get(i)+"---!!--"+storeBeforeAttributes.toString()+"--------");
//                outputBull2.println(names.get(i)+"___!!____"+storeAttributes+"__________");
                String stopHere="";
//                System.out.println("1");
                int stopIndex=0;
                for(int ind1=0;ind1<name1.length;ind1++){
                    if(name1[ind1].equals(name2[ind1]))
                        continue;
                    stopHere=name1[ind1];
                    stopIndex=ind1;
                    break;
                }
//                System.out.println("2 stopHere: "+stopHere);

                // added 22/12/2013
                for(int ind1=name1.length-2;ind1>stopIndex-1;ind1--){
                    if(!selectPath.contains("/"))
                        break;
                    if(selectPath.endsWith(name1[ind1]+"/"))   {
                        selectPath=selectPath.substring(0,selectPath.length()-2);
                        selectPath=selectPath.substring(0,selectPath.lastIndexOf('/')+1);
                    }
                    else {
                        break;
                    }

                }

                if (list.size()==2){
//                    System.out.println("3");
//                    inComplexType=false;
                    int index1 = storeBeforeAttributes.lastIndexOf("###");
                    int index2 = storeAttributes.lastIndexOf("###");
                    String others = storeBeforeAttributes.substring(index1+3);

                    String attris = storeAttributes.substring(index2+3);
                    String next2 = attris+others;

                    storeBeforeAttributes=storeBeforeAttributes.delete(index1,storeBeforeAttributes.length());
                    storeBeforeAttributes.append(next2);
                    storeAttributes=storeAttributes.delete(index2, storeAttributes.length());
//                    outputBull.println("---!!!--"+storeBeforeAttributes.toString()+"--------");
//                    outputBull2.println("___!!!____"+storeAttributes+"__________");

                }
                else{
//                    System.out.println("4  "+list.size());
//                    inComplexType=true;

                    String closeTag=list.removeLast();
                    int countClosedTags=1;
                    String next2="";
//                    outputBull.println("---!!!!--"+storeBeforeAttributes.toString()+"--------");
//                    outputBull2.println("___!!!!____"+storeAttributes+"__________");
                    while (!closeTag.equals(stopHere)){

                        if (!closeTag.equals("xsl:for-each")) {
                            countClosedTags++;
                            int index1 = storeBeforeAttributes.lastIndexOf("###");
                            int index2 = storeAttributes.lastIndexOf("###");
                            String others = storeBeforeAttributes.substring(index1+3);
//                            System.out.println("heeey  "+storeAttributes.length()+"     "+index2);
                            String attris = storeAttributes.substring(index2+3);

                            next2 = attris+others;
                            next2+="</"+closeTag+">";
                            storeBeforeAttributes=storeBeforeAttributes.delete(index1,storeBeforeAttributes.length());
                            storeBeforeAttributes.append(next2);
                            storeAttributes=storeAttributes.delete(index2, storeAttributes.length());
//                            outputBull.println("---!!!!!!!!!!--"+storeBeforeAttributes.toString()+"--------");
//                            outputBull2.println("___!!!!!!!!!!!!!____"+storeAttributes+"__________");
                        }
                        else   {
                            countClosedTags--;
//                            next2+="</"+closeTag+">";
                            storeBeforeAttributes.append("</"+closeTag+">");
                        }

//                        System.out.println("stuck   "+stopHere+"   "+closeTag);
                        closeTag=list.removeLast();
//                        System.out.println("4445   "+list.size());
                    }
//                    outputBull.println("---!-!-"+storeBeforeAttributes.toString()+"--------");
//                    outputBull2.println("___!_!___"+storeAttributes+"__________");
                    int index1 = storeBeforeAttributes.lastIndexOf("###");
                    int index2 = storeAttributes.lastIndexOf("###");
                    String others = storeBeforeAttributes.substring(index1+3);
                    String attris = storeAttributes.substring(index2+3);

                    next2 = attris+others;
                    next2+="</"+closeTag+">";
                    if((list.size()!=2)&&(list.size()!=0))   {
                        String nextTag=list.removeLast();
                        if ((nextTag.equals("xsl:for-each"))){
                            next2+="</xsl:for-each>";
                            countClosedTags--;
                        }
                        else{
                            list.add(nextTag);

                        }


//                        System.out.println("4445777   "+list.size());
                    }
                    storeBeforeAttributes=storeBeforeAttributes.delete(index1,storeBeforeAttributes.length());
                    storeBeforeAttributes.append(next2);
                    storeAttributes=storeAttributes.delete(index2, storeAttributes.length());
//                    outputBull.println("!!!---!--"+storeBeforeAttributes.toString()+"--------");
//                    outputBull2.println("!!!___!____"+storeAttributes+"__________");
//                    while(countClosedTags!=0){
//                        countClosedTags--;
//                        if(!selectPath.contains("/"))
//                            break;
//                        selectPath=selectPath.substring(0,selectPath.length()-2);
//                        selectPath=selectPath.substring(0,selectPath.lastIndexOf('/')+1);
//                        System.out.println("SELECT PATH:"+selectPath+"   "+countClosedTags);
//                    }

//                    System.out.println("6");
//                    System.out.println(index1+"   "+index2+"    "+storeBeforeAttributes.length()+"   "+storeAttributes.length());


                }

            }



        }

        if(list.size()==2){

            strB.append(storeBeforeAttributes);
            strB.append("</"+list.removeLast()+">");
            strB.append("</"+list.removeLast()+">");
        }
        else if (list.size()==0){
//            storeAttributes.append(storeBeforeAttributes);
            strB.append(storeBeforeAttributes);
        }
        else
//            System.out.println("nooooooooooooooo");
        strB.append("</xsl:template>");
        strB.append("</xsl:stylesheet>");
        //delete next 3 lines
//        strB.append(storeBeforeAttributes.toString());
//        strB.append("%%%%%%%%%%%%%%%%%%%%%%\n");
//        strB.append(storeAttributes.toString());

        PrintWriter output =new PrintWriter(new FileWriter(outputPath));
        String stringB=strB.toString();
        stringB=stringB.replaceAll("#","");
        output.append(stringB);
        output.close();

//        java.awt.Desktop.getDesktop().open(new File(outputPath));
//        RemoteRepo rp = new RemoteRepo(stringB,uid);
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
