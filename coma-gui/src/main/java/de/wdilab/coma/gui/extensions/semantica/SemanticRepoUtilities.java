package de.wdilab.coma.gui.extensions.semantica;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Evmorfia
 * Date: 30/6/2014
 * Time: 12:48 μμ
 * To change this template use File | Settings | File Templates.
 */
public class SemanticRepoUtilities {

    public static String[] getWorkspaceList(){

        String url="http://semanticas.testbed.fi-ware.eu:8080/semantic-workspaces-service/rest/workspaces/mgm/list";;


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            org.w3c.dom.Document doc = db.parse(new URL(url).openStream());
            NodeList nodeList = doc.getElementsByTagName("name");
            ArrayList<String> workspaces = new ArrayList<String>();
            for(int i=0; i<nodeList.getLength(); i++){
                Node childNode = nodeList.item(i);
                workspaces.add(childNode.getTextContent());
//                System.out.println(childNode.getTextContent());
            }

            String[] workspaceArray;
            if (workspaces.size()>0){
                workspaceArray = new String[workspaces.size()];
                workspaceArray = workspaces.toArray(workspaceArray);
            }
            else{
                workspaceArray = new String[1];
                workspaceArray[0]="No workspaces found";
            }

            return  workspaceArray;

        } catch (SAXException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //SAS returned with error
        //Response is not valid xml, maybe check for the presence of word "error" in response, for now handle all errors the same
        String[] emptyArray = new String[1];
        emptyArray[0]="No workspaces found";
        return emptyArray;
    }

    public static SASresponse createWorkspace(String name, String desciption){

        boolean created=false;
        SASresponse sasResponse;

        String url="http://semanticas.testbed.fi-ware.eu:8080/semantic-workspaces-service/rest/workspaces/"+name;

        try {


            HttpClient client = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(url);
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("description", desciption));

            httppost.setEntity(new UrlEncodedFormEntity(postParameters));
            HttpResponse response = client.execute(httppost);
//            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode()==200) {
                String result = EntityUtils.toString(response.getEntity());
                if (result.contains("<created>true</created>"))
                    sasResponse=new SASresponse(true);
                else if (result.contains("<error>")){
                    String message = result.substring(result.indexOf("<error>"));
                    message = message.substring(0,message.indexOf("</error>"));
                    sasResponse=new SASresponse(message,false);
                }
                else{
                    sasResponse = new SASresponse("Encountered unexpected error while creating workspace.",false);
                }

            }
            else{
                sasResponse = new SASresponse("Encountered unexpected error while creating workspace.",false);
            }

        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            sasResponse = new SASresponse("Encountered unexpected error while creating workspace. Check your internet connection",false);

        }

        return sasResponse;
    }

    public static String[] getOntologyList(){

        String url=SemanticaPaths.ONTOLOGY_LIST;


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            org.w3c.dom.Document doc = db.parse(new URL(url).openStream());
            NodeList nodeList = doc.getElementsByTagName("ontology");
            ArrayList<String> ontologies = new ArrayList<String>();
            for(int i=0; i<nodeList.getLength(); i++){
                Node childNode = nodeList.item(i);
                NamedNodeMap nnm = childNode.getAttributes();
                for (int j=0;j<nnm.getLength();j++){
                    Node k = nnm.getNamedItem("name");
                    ontologies.add(k.getTextContent());

//                    System.out.println(i+"   "+j+":  "+k.getTextContent());
                }
            }

            String[] ontologyArray;
            if (ontologies.size()>0){
                ontologyArray = new String[ontologies.size()];
                ontologyArray = ontologies.toArray(ontologyArray);
            }
            else{
                ontologyArray = new String[1];
                ontologyArray[0]="No ontologies found";
            }

            return  ontologyArray;

        } catch (SAXException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //SAS returned with error
        //Response is not valid xml, maybe check for the presence of word "error" in response, for now handle all errors the same
        String[] emptyArray = new String[1];
        emptyArray[0]="No ontologies found";
        return emptyArray;
    }

    public static String[] getOntologyListWithVersions(){

        String url=SemanticaPaths.ONTOLOGY_LIST;


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            org.w3c.dom.Document doc = db.parse(new URL(url).openStream());
            NodeList nodeList = doc.getElementsByTagName("ontology");
            ArrayList<String> ontologies = new ArrayList<String>();
            for(int i=0; i<nodeList.getLength(); i++){
                Node childNode = nodeList.item(i);
                NamedNodeMap nnm = childNode.getAttributes();
                Node k = nnm.getNamedItem("name");

//                    System.out.println(i+"   "+j+":  "+k.getTextContent());
                NodeList versions = childNode.getChildNodes();
                String maxVersion = versions.item(0).getAttributes().getNamedItem("name").getTextContent();
                ontologies.add(encodeOntoNameAndVersion(k.getTextContent(),maxVersion));

            }

            String[] ontologyArray;
            if (ontologies.size()>0){
                ontologyArray = new String[ontologies.size()];
                ontologyArray = ontologies.toArray(ontologyArray);
            }
            else{
                ontologyArray = new String[1];
                ontologyArray[0]="No ontologies found";
            }

            return  ontologyArray;

        } catch (SAXException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //SAS returned with error
        //Response is not valid xml, maybe check for the presence of word "error" in response, for now handle all errors the same
        String[] emptyArray = new String[1];
        emptyArray[0]="No ontologies found";
        return emptyArray;
    }

    private static String getOntology(String name){

        String url=SemanticaPaths.ONTOLOGY+name;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);

            return content;
        } catch (IOException e) {
//            e.printStackTrace();
            return "Not loaded.";
        }

    }

    public static boolean downloadOntology(String ontologyName, String fileName){

        String ontology = getOntology(ontologyName);
        boolean stored = false;
        try {
            PrintWriter output =new PrintWriter(new FileWriter(fileName));
            output.print(ontology);
            output.close();
            stored = true;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return stored;

    }

    public static boolean uploadOntology(String ontologyName, String fileName){

        boolean stored = false;
        String url = SemanticaPaths.ONTOLOGY+ontologyName;
        File f = new File(fileName);
        HttpClient client = new DefaultHttpClient();
        FileEntity entity = new FileEntity(f, ContentType.create("application/xml", "UTF-8"));

        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(entity);
        HttpResponse response = null;
        try {
            response = client.execute(httppost);
//            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode()==200)
                stored=true;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return stored;

    }

    private static String encodeOntoNameAndVersion(String name, String version){

        return name+"[version:"+version+"]";

    }

    public static String[] decodeOntoNameVersion(String combined){
        String[] result = new String[2];
        String s = combined.replace("]","");
        result[0] = s.substring(0,s.indexOf("[version:"));
        result[1] = s.substring(s.indexOf("[version:")+9);
        return result;
    }

}
