/*
 * This file is part of the TimeFinder project.
 *  Visit http://www.timefinder.de for more information.
 *  Copyright (c) 2009 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*
 * HungarianAlgorithm.java
 *
 * Created on 10. December 2007, 20:34
 * 
 * This code stands under the GPLv3! 
 * Use KuhnMunkresAlgorithm for an Apache2 licensed alternative.
 *
 * History:
 * The Hungarian Method based on ideas of König and Egervary, and
 *                      was published from Kuhn.
 * Later on Munkres showed that this algorithm is strongly polynomial O(n^4)
 * => Hungarian Algorithm (== Kuhn-Munkres algorithm)
 * Edmonds showed then that the time complexity of the algorithm is O(n^3)
 */
package de.wdilab.ml.impl.matcher.simple.strongweaksim;

import javolution.util.FastSet;

import java.util.Arrays;
import java.util.Set;

/**
 * This is the basic assignment algorithm known as Hungarian Method.
 * Where a matching between the sets S and T is represented as a
 * m x n matrix, with |S|=m and |T|=n.
 *
 * @author gary baker, http://www.thegarybakery.com
 * @author Peter Karich, peat_hal 'at' users 'dot' sourceforge 'dot' net
 */
public class HungarianAlgorithm  {

    /**
     * We need these arrays to ignore the invalid (all entries are MAX_VALUE)
     * rows or columns.
     */
    private boolean[] initialCovRows;
    private boolean[] initialCovCols;
    private boolean[] coveredRows;
    private boolean[] coveredCols;
    private int[] starsByRow;
    private int[] starsByCol;

    public int[][] computeAssignments(float[][] matrix) {
        return originalComputeAssignments(matrix);
    }

    public int[][] originalComputeAssignments(float[][] matrix) {
        //assert matrix[0].length <= matrix.length : "Do not process matrices where cols > rows!";

        initialCovRows = new boolean[matrix.length];
        initialCovCols = new boolean[matrix[0].length];

        // subtract minumum value from rows and columns to create lots of zeroes
        // reduceMatrix(matrix);
        reduceMatrixAndInitialize(matrix);

        // non negative values are the index of the starred or primed zero in the row or column
        starsByRow = new int[matrix.length];
        Arrays.fill(starsByRow, -1);
        starsByCol = new int[matrix[0].length];
        Arrays.fill(starsByCol, -1);
        int[] primesByRow = new int[matrix.length];
        Arrays.fill(primesByRow, -1);

        // star any zero that has no other starred zero in the same row or column
        initStars(matrix);

        coveredRows = initCoveredRows();
        coveredCols = initCoveredCols();
        coverColumnsOfStarredZeroes();

        while (!allColsAreCovered()) {

            int[] primedZero = primeSomeUncoveredZero(matrix, primesByRow);
            boolean onlyInvalidEntries = false;

            while (primedZero == null) {
                // keep making more zeroes until we find something that we can
                // prime (i.e. a zero that is uncovered)                
                if (!makeMoreZeroes(matrix)) {
                    onlyInvalidEntries = true;
                    break;
                }
                primedZero = primeSomeUncoveredZero(matrix, primesByRow);
                onlyInvalidEntries = false;
            }
            if (onlyInvalidEntries) {
                break;
            }
            // check if there is a starred zero in the primed zero's row
            int columnIndex = starsByRow[primedZero[0]];
            if (-1 == columnIndex) {
                // if not, then we need to increment the zeroes and start over
                incrementSetOfStarredZeroes(primedZero, primesByRow);
                Arrays.fill(primesByRow, -1);

                coveredCols = initCoveredCols();
                coveredRows = initCoveredRows();
                coverColumnsOfStarredZeroes();
            } else {

                // cover the row of the primed zero and uncover the column of
                // the starred zero in the same row
                coverRow(primedZero[0], true);
                coverColumn(columnIndex, false);
            }
        }

        // now we should have assigned everything
        // take the starred zeroes in each column as the correct assignments

        int[][] retval = new int[matrix[0].length][];

        for (int i = 0; i < matrix[0].length; i++) {
            if (starsByCol[i] != -1) {
                retval[i] = new int[]{starsByCol[i], i};
            }
            //else could happen if we covered an invalid col, because no valid entries
            //-> no assignment possible
        }
        return retval;
    }

    /**
     * @param b true if you want to cover the specified row; false to uncover
     */
    private void coverRow(int row, boolean b) {
        //TODO change initial array
        //TODO change maxPossibleRows
        if (b) {
            coveredRows[row] = true;
        } else {
            assert false : "uncover of rows isn't used. Row:" + row;
        }
    }

    /**
     * @param b true if you want to cover the specified columns; false to uncover
     */
    private void coverColumn(int column, boolean b) {
        //TODO change initial array
        //TODO change maxPossibleCols
        if (b) {
            coveredCols[column] = true;
        } else {
            assert !initialCovCols[column] :
                    "We should uncover an invalid col:" + column;
            coveredCols[column] = false;
        }
    }

    private boolean allColsAreCovered() {
        for (boolean covered : coveredCols) {
            if (!covered) {
                return false;
            }
        }
        return true;
    }

    private boolean[] initCoveredRows() {
        //Arrays.fill(coveredRows, false);
        boolean[] covRows = new boolean[initialCovRows.length];
        System.arraycopy(initialCovRows, 0, covRows, 0, initialCovRows.length);
        return covRows;
    }

    private boolean[] initCoveredCols() {
        //Arrays.fill(coveredCols, false);
        boolean[] covCols = new boolean[initialCovCols.length];
        System.arraycopy(initialCovCols, 0, covCols, 0, initialCovCols.length);
        return covCols;
    }

    /**
     * The first step of the hungarian algorithm is to find the smallest element
     * in each row and subtract it's values from all elements in that row.
     * <p/>
     * Initializes initialCovRows, initialCovCols and maxPossibleAssignment.
     */
    private void reduceMatrixAndInitialize(float[][] matrix) {

        // find the min value in each row
        float minValInRow;
        for (int row = 0; row < matrix.length; row++) {
            minValInRow = Float.MAX_VALUE;
            for (int col = 0; col < matrix[row].length; col++) {
                if (minValInRow > matrix[row][col]) {
                    minValInRow = matrix[row][col];
                }
            }

            // subtract it from all values in the row            
            if (minValInRow < Float.MAX_VALUE) {
                for (int col = 0; col < matrix[row].length; col++) {
                    if (matrix[row][col] < Float.MAX_VALUE) {
                        matrix[row][col] -= minValInRow;
                    }
                }
            } else {
                initialCovRows[row] = true;
            }
        }

        //do the same for the columns
        float minValInCol = Float.MAX_VALUE;
        for (int col = 0; col < matrix[0].length; col++) {
            minValInCol = Float.MAX_VALUE;
            for (int row = 0; row < matrix.length; row++) {
                if (minValInCol > matrix[row][col]) {
                    minValInCol = matrix[row][col];
                }
            }

            if (minValInCol < Float.MAX_VALUE) {
                for (int row = 0; row < matrix.length; row++) {
                    if (matrix[row][col] < Float.MAX_VALUE) {
                        matrix[row][col] -= minValInCol;
                    }
                }
            } else {
                initialCovCols[col] = true;
            }
        }
    }

    /**
     * Init starred zeroes. For each column find the first zero if there is no
     * other starred zero in that row then star the zero, cover the column
     * and row and go onto the next column
     */
    //create an initial matching
    private void initStars(float costMatrix[][]) {

        boolean[] rowHasStarredZero = new boolean[costMatrix.length];
        boolean[] colHasStarredZero = new boolean[costMatrix[0].length];

        for (int row = 0; row < costMatrix.length; row++) {
            for (int col = 0; col < costMatrix[row].length; col++) {
                if (0 == costMatrix[row][col] &&
                        !rowHasStarredZero[row] &&
                        !colHasStarredZero[col]) {
                    starsByRow[row] = col;
                    starsByCol[col] = row;
                    rowHasStarredZero[row] = true;
                    colHasStarredZero[col] = true;
                    break; // move onto the next row
                }
            }
        }
    }

    /**
     * Just marke the columns covered for any column containing a starred zero
     */
    private void coverColumnsOfStarredZeroes() {
        for (int col = 0; col < starsByCol.length; col++) {

            assert !(coveredCols[col] && starsByCol[col] != -1) :
                    "We shouldn't have covered a starred column; col:" + col;

            if (starsByCol[col] != -1) {
                coverColumn(col, true);
            }
        }
    }

    /**
     * Finds some uncovered zero and primes it.
     */
    private int[] primeSomeUncoveredZero(float matrix[][], int[] primesByRow) {

        // find an uncovered zero and prime it
        for (int i = 0; i < matrix.length; i++) {
            if (coveredRows[i]) {
                continue;
            }

            for (int j = 0; j < matrix[i].length; j++) {
                // if it's a zero and the column is not covered
                if (0 == matrix[i][j] && !coveredCols[j]) {
                    // ok this is an unstarred zero
                    // prime it
                    primesByRow[i] = j;
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private void incrementSetOfStarredZeroes(
            int[] unpairedZeroPrime,
            int[] primesByRow) {

        // build the alternating zero sequence (prime, star, prime, star, etc)
        int i, j = unpairedZeroPrime[1];

        Set<int[]> zeroSequence = new FastSet<int[]>();
        zeroSequence.add(unpairedZeroPrime);
        boolean paired = false;
        do {
            i = starsByCol[j];
            paired = -1 != i && zeroSequence.add(new int[]{i, j});
            if (!paired) {
                break;
            }

            j = primesByRow[i];
            paired = -1 != j && zeroSequence.add(new int[]{i, j});

        } while (paired);


        // unstar each starred zero of the sequence
        // and star each primed zero of the sequence
        for (int[] zero : zeroSequence) {
            if (starsByCol[zero[1]] == zero[0]) {
                starsByCol[zero[1]] = -1;
                starsByRow[zero[0]] = -1;
            }

            if (primesByRow[zero[0]] == zero[1]) {
                starsByRow[zero[0]] = zero[1];
                starsByCol[zero[1]] = zero[0];
            }
        }
    }

    /**
     * @return true if making more zeroes was possible.
     */
    private boolean makeMoreZeroes(float[][] matrix) {

        // find the minimum uncovered value
        float minUncoveredValue = Float.MAX_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            if (!coveredRows[i]) {
                for (int j = 0; j < matrix[i].length; j++) {
                    if (!coveredCols[j] && matrix[i][j] < minUncoveredValue) {
                        minUncoveredValue = matrix[i][j];
                    }
                }
            }
        }

        if (minUncoveredValue >= Float.MAX_VALUE) {
            return false;
        }

        // add the min value to all covered rows
        for (int row = 0; row < coveredRows.length; row++) {
            if (coveredRows[row]) {
                for (int col = 0; col < matrix[row].length; col++) {
                    if (matrix[row][col] < Float.MAX_VALUE) {
                        matrix[row][col] += minUncoveredValue;
                    }
                }
            }
        }

        // subtract the min value from all uncovered columns
        for (int col = 0; col < coveredCols.length; col++) {
            if (!coveredCols[col]) {
                for (int row = 0; row < matrix.length; row++) {
                    if (matrix[row][col] < Float.MAX_VALUE) {
                        matrix[row][col] -= minUncoveredValue;
                    }
                }
            }
        }

        return true;
    }
}
