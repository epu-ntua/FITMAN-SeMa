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


import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.StringWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Evmorfia
 * Date: 21/5/2014
 * Time: 8:59 πμ
 * To change this template use File | Settings | File Templates.
 */
public class Registration {
    /**
     */
    public static boolean registerS(String source, String sourceFormat, String target, String targetFormat, String descr, String doc, String name) {
        boolean registered=false;
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            String url = "http://demos.txt.it/WSMXBridge/services/WSMXEntryPointsRequest?wsdl";
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(source,sourceFormat,target,targetFormat,descr,doc,name), url);

            // Process the SOAP Response
            registered=printSOAPResponse(soapResponse);

            soapConnection.close();
            return registered;
        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request to Server");
            e.printStackTrace();
            return  registered;
        }
    }



    private static SOAPMessage createSOAPRequest(String source, String sourceFormat, String target, String targetFormat, String descr, String doc, String name) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String serverURI = "http://ws.wsmx.coin.txtgroup.com";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("ws", serverURI);


        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("registerWebService", "ws");
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("serviceName", "ws");
        soapBodyElem1.addTextNode(name);
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("description", "ws");
        soapBodyElem2.addTextNode(descr);
//        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("contributor", "ws");
//        soapBodyElem3.addTextNode("test");
        SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("title", "ws");
        soapBodyElem4.addTextNode("Transformation");
        SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("sourceDocFormat", "ws");
        soapBodyElem5.addTextNode(source);
        SOAPElement soapBodyElem6 = soapBodyElem.addChildElement("sourceDocExtension", "ws");
        soapBodyElem6.addTextNode(sourceFormat);
        SOAPElement soapBodyElem7 = soapBodyElem.addChildElement("targetDocFormat", "ws");
        soapBodyElem7.addTextNode(target);
        SOAPElement soapBodyElem8 = soapBodyElem.addChildElement("targetDocExtension", "ws");
        soapBodyElem8.addTextNode(targetFormat);
        SOAPElement soapBodyElem9 = soapBodyElem.addChildElement("targetDoc", "ws");
        soapBodyElem9.addTextNode(doc);


        soapMessage.saveChanges();

        /* Print the request message */
//        System.out.print("Request SOAP Message = ");
        soapMessage.writeTo(System.out);
//        System.out.println();

        return soapMessage;
    }

    /**
     * Method used to print the SOAP Response
     */
    private static boolean printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
//        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(new StringWriter());
        transformer.transform(sourceContent, result);
        boolean registered = result.getWriter().toString().contains("<ns:return>true</ns:return>");
//        System.out.println(registered+"   and the response is: "+result.getWriter().toString());
        return registered;
    }
}
