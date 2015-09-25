package de.wdilab.coma.gui.extensions.semantica;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Evmorfia
 * Date: 29/6/2014
 * Time: 6:24 μμ
 * To change this template use File | Settings | File Templates.
 */
public class SemanticWorkspace {

    private String name;

    public SemanticWorkspace(String name){
        this.name=name;
    }




//    public String getOntologyPrev(){
//
//        String url="http://semanticas.testbed.fi-ware.eu:8080/semantic-workspaces-service/rest/workspaces/"+name+"/ontology/list";;
//
//        try {
//
//            HttpClient client = new DefaultHttpClient();
//
//            HttpGet httpget = new HttpGet(url);
//            HttpResponse response = client.execute(httpget);
//            System.out.println(response.getStatusLine().getStatusCode());
//            if (response.getStatusLine().getStatusCode()==200)  {
//                String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
//                System.out.println("yeeeeah"+responseString);
//                responseString.replace("<?xml version='1.0' encoding='UTF-8' ?>","");
//                DOMParser parser = new DOMParser();
//
//                try {
//                    parser.parse(new InputSource(new java.io.StringReader(responseString)));
//                    org.w3c.dom.Document doc = parser.getDocument();
//                    System.out.println(doc.getFirstChild());
//
//                    NodeList nodeList = doc.getElementsByTagName("name");
//                    for(int i=0; i<nodeList.getLength(); i++){
//                        Node childNode = nodeList.item(i);
//                        System.out.println(childNode.getTextContent());
//                    }
//                    String message = doc.getDocumentElement().getTextContent();
//                    System.out.println(message);
//                } catch (SAXException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            else
//                System.out.println(response);
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//
//        return "";
//    }

    public String[] getOntologyList(){

        String url="http://semanticas.testbed.fi-ware.eu:8080/semantic-workspaces-service/rest/workspaces/"+name+"/ontology/list";;


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
            ArrayList<String>  ontologies = new ArrayList<String>();
            for(int i=0; i<nodeList.getLength(); i++){
                Node childNode = nodeList.item(i);
                ontologies.add(childNode.getTextContent());
//                System.out.println(childNode.getTextContent());
            }
//            for(String s:ontologies)
//                System.out.println("ontology: "+s);
            String[] ontologyArray;
            if (ontologies.size()>0){
                ontologyArray = new String[ontologies.size()];
                ontologyArray = ontologies.toArray(ontologyArray);
            }
            else{
                ontologyArray = new String[1];
                ontologyArray[0]="The workspace does not contain any ontologies.";
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
        emptyArray[0]="Cannot find any ontologies. The workspace may not exist.";
        return emptyArray;
    }

    public boolean loadOntology(String ontologyName){

//        System.out.println("loaded: !!!!!!!!!!!!");
        String[] ontoInfo = SemanticRepoUtilities.decodeOntoNameVersion(ontologyName);
        boolean loaded = false;
        String url = "http://semanticas.testbed.fi-ware.eu:8080/semantic-workspaces-service/rest/workspaces/"+name+"/ontology/"+ontoInfo[0];
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);

        try {
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("version", ontoInfo[1]));

            request.setEntity(new UrlEncodedFormEntity(postParameters));
            HttpResponse response = client.execute(request);
            if((response.getStatusLine().getStatusCode())==200) {
                loaded=true;
//                System.out.println("loaded: "+name+"   "+ontologyName);
            }
            else  {
//                System.out.println(response.getEntity().getContent());
//                System.out.println("NOT loaded: "+name+"   "+ontologyName);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return loaded;
    }


    public boolean createContext(String context){
        boolean loaded=false;
        String url = "http://semanticas.testbed.fi-ware.eu:8080/semantic-workspaces-service/rest/workspaces/"+name+"/context/context";
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("rdf", context));
        try {

            request.setEntity(new UrlEncodedFormEntity(postParameters));
            HttpResponse response = client.execute(request);
            if((response.getStatusLine().getStatusCode())==200) {
                loaded=true;
//                System.out.println("loaded");
//                System.out.println(EntityUtils.toString(response.getEntity()));
            }
            else  {
//                System.out.println(EntityUtils.toString(response.getEntity()));
//                System.out.println("NOT loaded");

            }

        } catch (IOException e) {
        }
        return loaded;
        }



    public boolean loadContext(String context){

        boolean loaded=false;
        String url = "http://semanticas.testbed.fi-ware.eu:8080/semantic-workspaces-service/rest/workspaces/"+name+"/context/context";
        HttpClient client = new DefaultHttpClient();
        HttpPut request = new HttpPut(url);
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("rdf", context));
        try {

            request.setEntity(new UrlEncodedFormEntity(postParameters));
            HttpResponse response = client.execute(request);
            if((response.getStatusLine().getStatusCode())==200) {
                loaded=true;
//                System.out.println("loaded");
            }
            else  {
//                System.out.println(EntityUtils.toString(response.getEntity()));
//                System.out.println("NOT loaded");

            }

        } catch (IOException e) {
        }
        return loaded;

    }


    public String queryWorkspace(String query){

        String url="http://semanticas.testbed.fi-ware.eu:8080/semantic-workspaces-service/rest/workspaces/"+name+"/sparql";

        try {


            HttpClient client = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(url);
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("query", query));

            httppost.setEntity(new UrlEncodedFormEntity(postParameters));
            HttpResponse response = client.execute(httppost);
//            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode()==200) {
                String result = EntityUtils.toString(response.getEntity());
                return result;
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return "SeMa has faced an unexpected error while executing query.";
    }
}
