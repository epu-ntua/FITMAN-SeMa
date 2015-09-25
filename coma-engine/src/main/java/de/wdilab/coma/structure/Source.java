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

package de.wdilab.coma.structure;

/**
 * Source contains the metadata about a schema/ontology source
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class Source{

	  public static final int UNDEF = -1;    

	  //Source type, arbitrary classification
	  public static final int TYPE_XDR         = 1;       //XDR schemas
	  public static final int TYPE_XSD         = 2;       //XSD schemas
	  public static final int TYPE_ODBC        = 3;      //Relational schemas
	  public static final int TYPE_ONTOLOGY    = 4;  //XML Namespaces
	  public static final int TYPE_INTERN      = 5;    //Graph representation, for schema operations 
	  public static final int TYPE_WEBSITE       = 7;     //Undefined type
	  public static final int TYPE_CSV         = 8;       //Comma separated values
	  public static final int TYPE_SQL         = 9;       //Comma separated values
	  public static final int[] ALL_TYPE = { TYPE_ONTOLOGY, TYPE_XDR, TYPE_XSD, TYPE_ODBC, 
		  TYPE_INTERN, UNDEF, TYPE_WEBSITE, TYPE_CSV, TYPE_SQL};

	  
	String name = null;
	int type = -1;
	int id = -1;
	String url = null;
	String provider = null;
	String date = null;
	String author = null;
	String version = null;
	String domain = null;
	String comment = null;
	
	int objCount = -1;
	
	public Source(int id, String name, String type, String url, String provider, 
			String date, String author, String domain, String version, String comment){
		this.id=id;
		this.name=name;
		this.type=stringToType(type);
		this.url=url;
		this.provider=provider;
		this.date=date;
		this.author=author;
		this.domain=domain;
		this.version=version;
		this.comment=comment;
	}
	
	public Source(int id, String name, String type, String url, String provider, 
			String date){
		this.id=id;
		this.name=name;
		this.type=stringToType(type);
		this.url=url;
		this.provider=provider;
		this.date=date;
	}

	public boolean equals(Object object){
		if (object==null) return false;
		if (!(object instanceof Source)) return false;
		return ((Source)object).getId()== id;
	}
	
    public Source copy() {
        //id is to be set to a new value after copy ? --> comment from last version
        int copId = id;  
        
        String copName = (name!=null?new String(name):null);
        String copType = typeToString(type);
        String copUrl = (url!=null?new String(url):null);
        String copProvider = (provider!=null?new String(provider):null);
        String copDate = (date!=null?new String(date):null);
        String copAuthor= (author!=null?new String(author):null);
        String copDomain = (domain!=null?new String(domain):null);
        String copVersion = (version!=null?new String(version):null);
        String copComment = (comment!=null?new String(comment):null);
        
        
        Source copSource = new Source(copId, copName, copType, copUrl,
        		copProvider, copDate, copAuthor, copDomain, copVersion, copComment);
        return copSource;      
    }
	
    // simple getter
	public String getName() { return name; }	
	public int getType() { return type; }
	public int getId() { return id; }
	public String getUrl() { return url; }	
	public String getProvider() { return provider; }
	public String getAuthor() { return author; }
	public String getVersion() { return version; }
	public String getDomain() { return domain; }
	public String getComment() { return comment; }
	public String getDate() { return date; }	
	public int getObjectCount() { return objCount; }
	
	
	// simple setter
	public void setName(String name) { this.name = name; }
	public void setType(int type) { this.type = type; }
	public void setId(int id) { this.id = id; }
	public void setProvider(String provider) { this.provider = provider; }

	public void setAuthor(String author) { this.author = author; }
	public void setVersion(String version) { this.version = version; }
	public void setDomain(String domain) { this.domain = domain; }
	public void setComment(String comment) { this.comment = comment; }
	public void setUrl(String url) { this.url = url; }
	public void setObjectCount(int objCount) { this.objCount = objCount; }
	
	
    // functions
	
	public String toString() {
		return id + ": " + name + "("+provider+")";
	}
	
	
	public boolean hasName() {
		if (name!=null){
			return true;
		}
		return false;
	}
	
    public static String typeToString(int type) {
    	switch (type) {
    		case TYPE_XDR:   return "XDR";
    		case TYPE_XSD:        return "XSD";
    		case TYPE_ODBC:        return "ODBC";
    		case TYPE_ONTOLOGY:    return "ONTOLOGY";
    		case TYPE_INTERN:    return "INTERN";
    		case TYPE_WEBSITE:        return "WEBSITE";
    		case TYPE_CSV:        return "CSV";
    		case TYPE_SQL:    return "SQL";
    		default: return "UNDEF";  //Undefined type
    	}
    }
    
    public static int stringToType(String type) {
        if (type==null) return UNDEF;
        else if (type.equals("XDR")) return TYPE_XDR;
        else if (type.equals("XSD"))    return TYPE_XSD;
        else if (type.equals("ODBC"))    return TYPE_ODBC;
        else if (type.equals("ONTOLOGY"))    return TYPE_ONTOLOGY;
        else if (type.equals("INTERN")) return TYPE_INTERN;
        else if (type.equals("WEBSITE"))    return TYPE_WEBSITE;
        else if (type.equals("CSV"))    return TYPE_CSV;
        else if (type.equals("SQL")) return TYPE_SQL;
        return UNDEF; //Undefined type
      }
    

	
}
