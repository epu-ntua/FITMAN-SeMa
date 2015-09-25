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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


/**
 * Created with IntelliJ IDEA.
 * User: Evmorfia
 * Date: 15/10/2013
 * Time: 4:53 μμ
 * To change this template use File | Settings | File Templates.
 */
public class RemoteRepo {

    private static String url = "http://83.212.114.237:8081/RA/public_process/xsltStore?";
    String charset = "UTF-8";
    private String param_uuid;
    private String param_content;
//    private String xslt_text;
//    private UUID uuid;

//    String insertStmnt = "";
//    PreparedStatement insertXslt;

//    public RemoteRepo(String s, UUID uid){
//
////        xslt_text=s.replaceAll("\"","\\\"");
////        xslt_text=xslt_text.replaceAll("\'","\\\'");
////        uuid=uid;
////        getRemoteConnection();
//    }



    public static boolean getRemoteConnection(String s, UUID uid){

        UUID uuid;
        String xslt_text;
        boolean is_stored=false;

        xslt_text=s.replaceAll("\"","\\\"");
        xslt_text=xslt_text.replaceAll("\'","\\\'");
        uuid=uid;

        try {
            File f = new File("helpingFile.csv");
            PrintWriter output =new PrintWriter(new FileWriter(f));
            output.println("uuid,data");
            xslt_text.trim();
            xslt_text=xslt_text.replace("\n", "").replace("\r", "");
            output.println(uuid+","+xslt_text);
            output.close();

            HttpClient client = new DefaultHttpClient();
            FileEntity entity = new FileEntity(f, ContentType.create("text/plain", "UTF-8"));

            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(entity);
            HttpResponse response = client.execute(httppost);
//            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode()==200)
                is_stored=true;
            f.delete();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

//        System.out.println("xslt stored   "+is_stored);

        return is_stored;


    }



}
