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

package de.wdilab.coma.matching;

import junit.framework.TestCase;

/**
 * Test if resolution id and toString/stringToResolution leads to same id
 * 
 * @author Sabine Massmann
 *
 */
public class TResolution extends TestCase {

	
	public void testResolutionStrings() {
		for (int i = 0; i < Resolution.RES1.length; i++) {
			int id = Resolution.RES1[i];
			String resolution = Resolution.resolutionToString(id);
			int id2= Resolution.stringToResolution(resolution);
			assertEquals(id, id2);
		}
		for (int i = 0; i < Resolution.RES2.length; i++) {
			int id = Resolution.RES2[i];
			String resolution = Resolution.resolutionToString(id);
			int id2= Resolution.stringToResolution(resolution);
			assertEquals(id, id2);
		}
		for (int i = 0; i < Resolution.RES3.length; i++) {
			int id = Resolution.RES3[i];
			String resolution = Resolution.resolutionToString(id);
			int id2= Resolution.stringToResolution(resolution);
			assertEquals(id, id2);
		}
	}
	
	
	// TODO test correct resolutions outcome

}
