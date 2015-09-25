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

import junit.framework.TestCase;

/**
 * @author Sabine Massmann
 */
public class TRepository extends TestCase {
	
	static public void setDatabaseProperties(){
		System.setProperty("comaUrl", "jdbc:mysql://localhost/coma-project?autoReconnect=true");
		System.setProperty("comaUser", "");
		System.setProperty("comaPwd", "");
	}
	
	public static void testRepository(){
		setDatabaseProperties();
		
		Repository rep = new Repository();
		rep.dropRepositorySchema();		
		rep.createRepositorySchema();		
		rep.createRepositorySchema();
		rep.dropRepositorySchema();		
		rep.closeDatabaseConnection();
		
	}

}
