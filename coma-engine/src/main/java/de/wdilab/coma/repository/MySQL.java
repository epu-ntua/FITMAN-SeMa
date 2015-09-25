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

package de.wdilab.coma.repository;

/**
 * This class contains the create, drop and insert mysql statements
 * for the mainly used table.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class MySQL {
	
	static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
	static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
	static final String INSERT_INTO = "INSERT INTO ";
	static final String SELECT = "SELECT ";
	static final String UPDATE = "UPDATE ";
	static final String INFO_TABLE = "INFORMATION_SCHEMA.TABLES";

	static final String CREATE_SOURCE = CREATE_TABLE + Repository.TABLE_SOURCE + " (" +	 
		"source_id  INTEGER       AUTO_INCREMENT, " +  // generated
		"name       VARCHAR(100)  BINARY NOT NULL, " +
		"type       VARCHAR(50)  NOT NULL, " +
		"url        LONG VARCHAR  NULL, " +
		"provider   VARCHAR(250)  NULL, " +
		"date       VARCHAR(50)   NULL, " +
		// additional information
		"author     VARCHAR(50)   NULL, " +
		"domain     VARCHAR(50)   NULL, " +
		"version    VARCHAR(50)   NULL, " +
		"comment    LONG VARCHAR  NULL, " +		
		"status     VARCHAR(50)  NULL, " + // import status
		"PRIMARY KEY(source_id))  ENGINE = MYISAM";
	static final String DROP_SOURCE = DROP_TABLE + Repository.TABLE_SOURCE;
	static final String INSERT_SOURCE = INSERT_INTO + Repository.TABLE_SOURCE +
		"(name, type, url, provider, date, author, domain, version, comment)" +
		" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	static final String CREATE_SOURCE_REL = CREATE_TABLE + Repository.TABLE_SOURCE_REL + " (" +
		"sourcerel_id   INTEGER      AUTO_INCREMENT, " +
		"source1_id     INTEGER      NOT NULL, " +
		"source2_id     INTEGER      NOT NULL, " +
		"type           INTEGER 	 NOT NULL, " +
		"name           VARCHAR(100) NULL, " +
		"comment        LONG VARCHAR NULL, " +
		"provider       VARCHAR(250) NULL, " +
		"preprocessing  INTEGER NULL, " +
		"date           VARCHAR(50)  NULL, " +
		"status  		VARCHAR(50) NULL, " + // import status
		"PRIMARY KEY(sourcerel_id)," +
		"UNIQUE(source1_id, source2_id, type, name))  ENGINE = MYISAM";
	static final String DROP_SOURCE_REL = DROP_TABLE + Repository.TABLE_SOURCE_REL;
	static final String INSERT_SOURCE_REL = INSERT_INTO + Repository.TABLE_SOURCE_REL + 
		"(source1_id, source2_id, type, name, comment, provider, preprocessing, date)" +
		" VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	
	static final String CREATE_OBJECT = CREATE_TABLE + Repository.TABLE_OBJECT + " (" +
		"object_id   INTEGER      AUTO_INCREMENT, " +
		"source_id   INTEGER      NOT NULL, " +
		"accession   VARCHAR(250) BINARY NOT NULL, " +
		"name		 LONG VARCHAR NULL, " +
		"type        VARCHAR(100) NULL, " +
		"typespace   VARCHAR(100) NULL, " +
		"kind        INTEGER NULL, " +
		"comment     LONG VARCHAR NULL, " +
		"synonyms    VARCHAR(500) NULL, " +
		"PRIMARY KEY(object_id), " + // thus also unique
		"UNIQUE(source_id, accession, kind), " +
		"INDEX(source_id), INDEX(accession))  ENGINE = MYISAM";
	static final String DROP_OBJECT = DROP_TABLE + Repository.TABLE_OBJECT ;
	static final String INSERT_OBJECT = INSERT_INTO + Repository.TABLE_OBJECT + 
		"(source_id, accession, name, type, typespace, kind, comment, synonyms) " +
		"VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

	static final String CREATE_OBJECT_REL = CREATE_TABLE + Repository.TABLE_OBJECT_REL + " (" +
		"sourcerel_id INTEGER      NOT NULL, " +
		"object1_id   INTEGER      NOT NULL, " +
		"object2_id   INTEGER      NOT NULL, " +
		"similarity   FLOAT		   NULL, " +
		"type         VARCHAR(50)  NULL, " +
		"UNIQUE(sourcerel_id, object1_id, object2_id), " +
		"INDEX(sourcerel_id), INDEX(object1_id), INDEX(object2_id))  ENGINE = MYISAM";
	static final String DROP_OBJECT_REL = DROP_TABLE + Repository.TABLE_OBJECT_REL;
	static final String INSERT_OBJECT_REL = INSERT_INTO + Repository.TABLE_OBJECT_REL +
		"(sourcerel_id, object1_id, object2_id, similarity, type)" +
		" VALUES(?, ?, ?, ?, ?)";
  

	static final String CREATE_WORKFLOW = CREATE_TABLE + Repository.TABLE_WORKFLOW + " (" +	 
		"name       VARCHAR(100)  BINARY NOT NULL, " +
		"value      LONG VARCHAR  NULL, " +
		"PRIMARY KEY(name))  ENGINE = MYISAM";
	static final String DROP_WORKFLOW = DROP_TABLE + Repository.TABLE_WORKFLOW;
	static final String INSERT_WORKFLOW = INSERT_INTO + Repository.TABLE_WORKFLOW +
		"(name, value)" +
		" VALUES (?, ?)";

	
	  static final String[] CREATE_QUERIES = {
		  CREATE_SOURCE, CREATE_SOURCE_REL, CREATE_OBJECT,CREATE_OBJECT_REL, 
		  CREATE_WORKFLOW
	  };
	
	  static final String[] DROP_QUERIES = {
		  DROP_SOURCE, DROP_SOURCE_REL, DROP_OBJECT, DROP_OBJECT_REL,
		  DROP_WORKFLOW
	  };
	  
	  static final String createInstancesQuery1 = CREATE_TABLE + Repository.TABLE_INSTANCES;
	  static final String createInstancesQuery2 =" (" +
	  "id          INTEGER AUTO_INCREMENT, " +  // will be used to reference the instance values
	  "connect  	 LONG VARCHAR NULL, " +
	  "elementid   INTEGER NOT NULL, " +
	  "instance_id INTEGER NOT NULL, " +
	  "attribute   VARCHAR(100) NULL, " +
	  "value       LONG VARCHAR NOT NULL, " +
	  "PRIMARY KEY(id)," +
	  "INDEX(elementid))  ENGINE = MYISAM"; //, " +
	  //"UNIQUE (name))";
	  static final String insertInstancesQuery1 = INSERT_INTO + Repository.TABLE_INSTANCES ;
	  static final String insertInstancesQuery2 = " (connect, elementid, instance_id, attribute, value) VALUES (?, ?, ?, ?, ?)";
	  static final String updateInstancesQuery1 = UPDATE + Repository.TABLE_INSTANCES ;
	  static final String updateInstancesQuery2 = " SET instance_id=? WHERE id=?";

	
}
