package de.wdilab.coma.gui.extensions.semantica;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Evmorfia
 * Date: 11/7/2014
 * Time: 12:02 μμ
 * To change this template use File | Settings | File Templates.
 */
public class SASOntology {

    public static SASresponse loadToRegistry(String filePath, String name){

        String url = SemanticaPaths.ONTOLOGY +name;
        File f = new File(filePath);

        HttpClient client = new DefaultHttpClient();
        FileEntity entity = new FileEntity(f, ContentType.create("application/xml", "UTF-8"));

        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(entity);
        HttpResponse response = null;
        try {
            response = client.execute(httppost);

//            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode()==200)
                return new SASresponse(true);

            return new SASresponse(EntityUtils.toString(response.getEntity()),false);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return new SASresponse("There was an unexpected error",false);
        }
    }

    public static void main(String[] args){
        loadToRegistry("C:\\Users\\Evmorfia\\Documents\\WORK\\ontology allignment test\\ontoSynonyms.rdf","ontoSynonyms.rdf");
    }


    public static void loadFromRegistry(String name){

        String url = SemanticaPaths.ONTOLOGY_REGISTRY+SemanticaPaths.ONTOLOGY +name;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new URL(url).openStream());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
}
