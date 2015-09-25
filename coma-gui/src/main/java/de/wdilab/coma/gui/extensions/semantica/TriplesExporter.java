package de.wdilab.coma.gui.extensions.semantica;


import de.wdilab.coma.gui.Controller;
import de.wdilab.coma.structure.SourceRelationship;
import org.apache.commons.lang.WordUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TriplesExporter {

    private String source_base;
    private String target_base;
    private int sourceRel_id;
    private int source_id;
    private int target_id;
    private Controller controller;

    public TriplesExporter(SourceRelationship sr, Controller c){
        source_id = sr.getSourceId();
        target_id = sr.getTargetId();
//        System.out.println(source_id+"   "+target_id);
        sourceRel_id = sr.getId();
        controller=c;
        source_base=getbase(source_id);
        if (!source_base.endsWith("/"))
            source_base=source_base+"/";
        target_base=getbase(target_id);
        if (!target_base.endsWith("/"))
            target_base=target_base+"/";
//        System.out.println(source_base+"   "+target_base);
    }

    private String getbase(int id){

        ResultSet rs;

        String selectSTMT ="select url from source where source_id=";

        rs=controller.getManager().getAccessor().performSelectQuery(selectSTMT+id);
        try {
            while (rs.next()){
                String res = rs.getString("url");
                if(res==null)
                    continue;
                String[] urls = res.split(",");
                return urls[0];
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

        }
        return "";
    }

    public String createTriplets(){

        ResultSet rs;

        String selectSTMT ="select o1.name as sourceName, o2.name as targetName from object_rel orl,object o1,object o2 where orl.object1_id=o1.object_id and orl.object2_id=o2.object_id and orl.sourcerel_id="+sourceRel_id;

        rs=controller.getManager().getAccessor().performSelectQuery(selectSTMT);
        try {
            StringBuilder strb = new StringBuilder();
            strb.append("<rdf:RDF xmlns:src=\""+source_base+"\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"  xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">");
            while (rs.next()){
                String sourceElem = rs.getString("sourceName");
                if(sourceElem.contains("."))
                    sourceElem = sourceElem.substring(sourceElem.lastIndexOf(".")+1);
                WordUtils.capitalize(sourceElem);
                sourceElem=sourceElem.replaceAll(" ","");

                String targetElem = rs.getString("targetName");
                if(targetElem.contains("."))
                    targetElem = targetElem.substring(targetElem.lastIndexOf(".")+1);
                WordUtils.capitalize(targetElem);
                targetElem = targetElem.replaceAll(" ","");
                strb.append(" <src:"+sourceElem);
                strb.append(" rdf:about=\""+source_base+sourceElem+"\"> <rdfs:seeAlso rdf:resource=\""+target_base+targetElem+"\"/>");
                strb.append(" </src:"+sourceElem+"> ");

            }
            strb.append(" </rdf:RDF> ");
            String result = strb.toString();
//            System.out.println(result);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

        }
        return "";

    }


}
