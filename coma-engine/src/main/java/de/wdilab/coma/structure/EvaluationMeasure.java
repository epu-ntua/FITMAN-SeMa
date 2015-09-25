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

//import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * EvaluationMeasure contains the result values of an comparison from an
 * intended result and a test result from an algorithm
 *
 * @author Sabine Massmann
 */
public class EvaluationMeasure {

    private int UNDEF_VALUE = -1;
    /**
     * # intended correspondences, I
     */
    int intendedCorresp = UNDEF_VALUE;
    /**
     * # test correspondences, T
     */
    int testCorresp = UNDEF_VALUE;
    /**
     * # true positives (correct matches in test), M
     */
    int correctMatches = UNDEF_VALUE;
    /**
     * # false positives (false matches in test), P
     */
    int falseMatches = UNDEF_VALUE;
    /**
     * # false negatives (false non-matches in test), N
     */
    int falseNonmatches = UNDEF_VALUE;
    /**
     * # true negatives (correct non-matches in test)
     */
    int correctNonmatches = UNDEF_VALUE;
    float precision = UNDEF_VALUE;
    float recall = UNDEF_VALUE;
    float fmeasure = UNDEF_VALUE;
    float overall = UNDEF_VALUE;

    /**
	 * @param intendedC
	 * @param testC
	 * @param correctM
	 * Constructor that uses the three input parameters to calculate all the other
	 * value like precision and recall
	 */
    public EvaluationMeasure(int intendedC, int testC, int correctM) {
        this.intendedCorresp = intendedC;
        this.testCorresp = testC;
        this.correctMatches = correctM;

        // false positives, predicted in test
        falseMatches = testC - correctM;
        // false negative, not found in test
        falseNonmatches = intendedCorresp - correctMatches;

        // calculate evaluation measure from given numbers
        precision = (float) correctMatches / testCorresp;
        recall = (float) correctMatches / intendedCorresp;
        fmeasure = (float) 2 * correctMatches / (intendedCorresp + testCorresp);
        overall = (float) (correctMatches - falseMatches) / intendedCorresp;

        // set to default 0 if result was not a number (e.g. result of divided by 0)
        if (Float.isNaN(precision)) {
            precision = 0;
        }
        if (Float.isNaN(recall)) {
            recall = 0;
        }
        if (Float.isNaN(fmeasure)) {
            fmeasure = 0;
        }
        if (Float.isNaN(overall)) {
            overall = 0;
        }
    }

    // simple getter	
    public int getIntendedCorresp() {
        return intendedCorresp;
    }

    public int getTestCorresp() {
        return testCorresp;
    }

    public int getCorrectMatches() {
        return correctMatches;
    }

    public int getFalseMatches() {
        return falseMatches;
    }

    public int getFalseNonmatches() {
        return falseNonmatches;
    }

    public int getCorrectNonmatches() {
        return correctNonmatches;
    }

    public float getPrecision() {
        return precision;
    }

    public float getRecall() {
        return recall;
    }

    public float getFmeasure() {
        return fmeasure;
    }

    public float getOverall() {
        return overall;
    }

    // function	
    public void print() {
        System.out.println(" intendedCorresp " + intendedCorresp + ", testCorresp " + testCorresp
                + ", correctMatches " + correctMatches + ", falseMatches " + falseMatches);
        System.out.println("-> precision " + precision + ", recall " + recall + ", fmeasure " + fmeasure + ", overall " + overall);

    }

    public String toReadableStringShort() {
        return ("-> precision " + precision + ", recall " + recall + ", fmeasure " + fmeasure + ", overall " + overall);
    }

    public String toReadableStringLong() {
        String re = ("intendedCorresp: " + intendedCorresp + ", testCorresp: " + testCorresp
                + ", correctMatches: " + correctMatches + ", falseMatches: " + falseMatches);
        re += (", precision: " + precision + ", recall: " + recall + ", fmeasure: " + fmeasure + ", overall: " + overall);
        return re;
    }
    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("intendedCorresp", intendedCorresp);
            jo.put("testCorresp", testCorresp);
            jo.put("correctMatches", correctMatches);
            jo.put("falseMatches", falseMatches);
            jo.put("precision", precision);
            jo.put("recall", recall);
            jo.put("fmeasure", fmeasure);
            jo.put("overall", overall);
        } catch (JSONException ex) {
            Logger.getLogger(EvaluationMeasure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jo;
    }
    
//    public String toJson() {
//        Gson gson = new Gson();
//        return gson.toJson(this);
//    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof EvaluationMeasure)) {
            return false;
        }
        EvaluationMeasure measure = (EvaluationMeasure) object;
        if (getIntendedCorresp() != measure.getIntendedCorresp()) {
            return false;
        }
        if (getTestCorresp() != measure.getTestCorresp()) {
            return false;
        }
        if (getCorrectMatches() != measure.getCorrectMatches()) {
            return false;
        }
        if (getFalseMatches() != measure.getFalseMatches()) {
            return false;
        }
        return true;
    }
}
