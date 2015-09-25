package de.wdilab.coma.matching.validation;

// $ANTLR 3.3 Nov 30, 2010 12:45:30 C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g 2012-06-29 09:44:49

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ComaWorkFlowLexer extends Lexer {
    public static final int EOF=-1;
    public static final int CHAR_BRACE_LEFT=4;
    public static final int CHAR_BRACE_RIGHT=5;
    public static final int CHAR_DOT=6;
    public static final int CHAR_COMMA=7;
    public static final int CHAR_SEMICOLON=8;
    public static final int RESULT_COMBINATION=9;
    public static final int RESOLUTION_1=10;
    public static final int RESOLUTION_2=11;
    public static final int SETCOMBINATION=12;
    public static final int RESOLUTION_3=13;
    public static final int SIMMEASURE=14;
    public static final int DIGIT=15;
    public static final int V2=16;
    public static final int BOOLEAN=17;
    public static final int COMPOSITION=18;
    public static final int SIMCOMBINATION1=19;
    public static final int DIRECTION=20;
    public static final int SELECTION_THRESHOLD=21;
    public static final int V1=22;
    public static final int SELECTION_MAXDELTA=23;
    public static final int SELECTION_MAXN=24;
    public static final int SELECTION_MULTIPLE=25;
    public static final int SIMCOMBINATION2=26;

    // delegates
    // delegators

    public ComaWorkFlowLexer() {;} 
    public ComaWorkFlowLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ComaWorkFlowLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g"; }

    // $ANTLR start "CHAR_BRACE_LEFT"
    public final void mCHAR_BRACE_LEFT() throws RecognitionException {
        try {
            int _type = CHAR_BRACE_LEFT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:3:17: ( '(' )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:3:19: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHAR_BRACE_LEFT"

    // $ANTLR start "CHAR_BRACE_RIGHT"
    public final void mCHAR_BRACE_RIGHT() throws RecognitionException {
        try {
            int _type = CHAR_BRACE_RIGHT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:4:18: ( ')' )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:4:20: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHAR_BRACE_RIGHT"

    // $ANTLR start "CHAR_DOT"
    public final void mCHAR_DOT() throws RecognitionException {
        try {
            int _type = CHAR_DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:5:10: ( '.' )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:5:12: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHAR_DOT"

    // $ANTLR start "CHAR_COMMA"
    public final void mCHAR_COMMA() throws RecognitionException {
        try {
            int _type = CHAR_COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:6:12: ( ',' )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:6:14: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHAR_COMMA"

    // $ANTLR start "CHAR_SEMICOLON"
    public final void mCHAR_SEMICOLON() throws RecognitionException {
        try {
            int _type = CHAR_SEMICOLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:7:16: ( ';' )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:7:18: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHAR_SEMICOLON"

    // $ANTLR start "RESOLUTION_1"
    public final void mRESOLUTION_1() throws RecognitionException {
        try {
            int _type = RESOLUTION_1;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:14: ( ( 'paths' | 'innerpaths' | 'leafpaths' | 'nodes' | 'innernodes' | 'leafnodes' | 'roots' | 'shared' | 'uppaths' | 'downpaths' | 'user' | 'sharedpaths' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:17: ( 'paths' | 'innerpaths' | 'leafpaths' | 'nodes' | 'innernodes' | 'leafnodes' | 'roots' | 'shared' | 'uppaths' | 'downpaths' | 'user' | 'sharedpaths' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:17: ( 'paths' | 'innerpaths' | 'leafpaths' | 'nodes' | 'innernodes' | 'leafnodes' | 'roots' | 'shared' | 'uppaths' | 'downpaths' | 'user' | 'sharedpaths' )
            int alt1=12;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:19: 'paths'
                    {
                    match("paths"); 


                    }
                    break;
                case 2 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:29: 'innerpaths'
                    {
                    match("innerpaths"); 


                    }
                    break;
                case 3 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:44: 'leafpaths'
                    {
                    match("leafpaths"); 


                    }
                    break;
                case 4 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:58: 'nodes'
                    {
                    match("nodes"); 


                    }
                    break;
                case 5 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:68: 'innernodes'
                    {
                    match("innernodes"); 


                    }
                    break;
                case 6 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:83: 'leafnodes'
                    {
                    match("leafnodes"); 


                    }
                    break;
                case 7 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:97: 'roots'
                    {
                    match("roots"); 


                    }
                    break;
                case 8 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:107: 'shared'
                    {
                    match("shared"); 


                    }
                    break;
                case 9 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:118: 'uppaths'
                    {
                    match("uppaths"); 


                    }
                    break;
                case 10 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:130: 'downpaths'
                    {
                    match("downpaths"); 


                    }
                    break;
                case 11 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:144: 'user'
                    {
                    match("user"); 


                    }
                    break;
                case 12 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:139:153: 'sharedpaths'
                    {
                    match("sharedpaths"); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RESOLUTION_1"

    // $ANTLR start "RESOLUTION_2"
    public final void mRESOLUTION_2() throws RecognitionException {
        try {
            int _type = RESOLUTION_2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:140:14: ( ( 'selfpath' | 'selfnode' | 'parents' | 'siblings' | 'children' | 'leaves' | 'allnodes' | 'successors' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:140:17: ( 'selfpath' | 'selfnode' | 'parents' | 'siblings' | 'children' | 'leaves' | 'allnodes' | 'successors' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:140:17: ( 'selfpath' | 'selfnode' | 'parents' | 'siblings' | 'children' | 'leaves' | 'allnodes' | 'successors' )
            int alt2=8;
            alt2 = dfa2.predict(input);
            switch (alt2) {
                case 1 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:140:19: 'selfpath'
                    {
                    match("selfpath"); 


                    }
                    break;
                case 2 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:140:32: 'selfnode'
                    {
                    match("selfnode"); 


                    }
                    break;
                case 3 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:140:45: 'parents'
                    {
                    match("parents"); 


                    }
                    break;
                case 4 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:140:57: 'siblings'
                    {
                    match("siblings"); 


                    }
                    break;
                case 5 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:140:70: 'children'
                    {
                    match("children"); 


                    }
                    break;
                case 6 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:140:83: 'leaves'
                    {
                    match("leaves"); 


                    }
                    break;
                case 7 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:140:94: 'allnodes'
                    {
                    match("allnodes"); 


                    }
                    break;
                case 8 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:140:107: 'successors'
                    {
                    match("successors"); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RESOLUTION_2"

    // $ANTLR start "RESOLUTION_3"
    public final void mRESOLUTION_3() throws RecognitionException {
        try {
            int _type = RESOLUTION_3;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:141:14: ( ( 'name' | 'nametoken' | 'path' | 'pathtoken' | 'pathsyn' | 'comment' | 'commenttoken' | 'datatype' | 'statistics' | 'synonyms' | 'instance_constraints' | 'instance_content' | 'instance_content_indirect' | 'instance_all' | 'nameandsynonyms' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:141:17: ( 'name' | 'nametoken' | 'path' | 'pathtoken' | 'pathsyn' | 'comment' | 'commenttoken' | 'datatype' | 'statistics' | 'synonyms' | 'instance_constraints' | 'instance_content' | 'instance_content_indirect' | 'instance_all' | 'nameandsynonyms' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:141:17: ( 'name' | 'nametoken' | 'path' | 'pathtoken' | 'pathsyn' | 'comment' | 'commenttoken' | 'datatype' | 'statistics' | 'synonyms' | 'instance_constraints' | 'instance_content' | 'instance_content_indirect' | 'instance_all' | 'nameandsynonyms' )
            int alt3=15;
            alt3 = dfa3.predict(input);
            switch (alt3) {
                case 1 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:141:19: 'name'
                    {
                    match("name"); 


                    }
                    break;
                case 2 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:141:28: 'nametoken'
                    {
                    match("nametoken"); 


                    }
                    break;
                case 3 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:141:42: 'path'
                    {
                    match("path"); 


                    }
                    break;
                case 4 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:141:51: 'pathtoken'
                    {
                    match("pathtoken"); 


                    }
                    break;
                case 5 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:141:65: 'pathsyn'
                    {
                    match("pathsyn"); 


                    }
                    break;
                case 6 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:141:77: 'comment'
                    {
                    match("comment"); 


                    }
                    break;
                case 7 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:141:89: 'commenttoken'
                    {
                    match("commenttoken"); 


                    }
                    break;
                case 8 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:141:106: 'datatype'
                    {
                    match("datatype"); 


                    }
                    break;
                case 9 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:141:119: 'statistics'
                    {
                    match("statistics"); 


                    }
                    break;
                case 10 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:142:5: 'synonyms'
                    {
                    match("synonyms"); 


                    }
                    break;
                case 11 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:142:18: 'instance_constraints'
                    {
                    match("instance_constraints"); 


                    }
                    break;
                case 12 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:142:43: 'instance_content'
                    {
                    match("instance_content"); 


                    }
                    break;
                case 13 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:142:63: 'instance_content_indirect'
                    {
                    match("instance_content_indirect"); 


                    }
                    break;
                case 14 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:142:92: 'instance_all'
                    {
                    match("instance_all"); 


                    }
                    break;
                case 15 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:142:109: 'nameandsynonyms'
                    {
                    match("nameandsynonyms"); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RESOLUTION_3"

    // $ANTLR start "SIMMEASURE"
    public final void mSIMMEASURE() throws RecognitionException {
        try {
            int _type = SIMMEASURE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:145:11: ( ( 'trigram' | 'soundex' | 'editdist' | 'datatypesimilarity' | 'featvect' | 'tfidf' | 'usersyn' | 'cosine' | 'jaccard' | 'jarowinkler' | 'sim_equal' | 'trigramcoma' | 'trigramifuice' | 'trigramlowmem' | 'trigramopt' | 'edjoin' | 'levenshteinlucene' | 'levenshteinsecondstring' | 'levenshteinlimes' | 'cosineppjoin+fullycached' | 'cosineppjoin' | 'cosinesimmetrics' | 'jaccardppjoin+fullycached' | 'jaccardppjoin+' | 'jaccardsecondstring' | 'jaccardsimmetrics' | 'jarowinklerlucene' | 'tfidflucenefullycached' | 'tfidflucenefullycachedalternative' | 'tfidfsecondstring' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:145:14: ( 'trigram' | 'soundex' | 'editdist' | 'datatypesimilarity' | 'featvect' | 'tfidf' | 'usersyn' | 'cosine' | 'jaccard' | 'jarowinkler' | 'sim_equal' | 'trigramcoma' | 'trigramifuice' | 'trigramlowmem' | 'trigramopt' | 'edjoin' | 'levenshteinlucene' | 'levenshteinsecondstring' | 'levenshteinlimes' | 'cosineppjoin+fullycached' | 'cosineppjoin' | 'cosinesimmetrics' | 'jaccardppjoin+fullycached' | 'jaccardppjoin+' | 'jaccardsecondstring' | 'jaccardsimmetrics' | 'jarowinklerlucene' | 'tfidflucenefullycached' | 'tfidflucenefullycachedalternative' | 'tfidfsecondstring' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:145:14: ( 'trigram' | 'soundex' | 'editdist' | 'datatypesimilarity' | 'featvect' | 'tfidf' | 'usersyn' | 'cosine' | 'jaccard' | 'jarowinkler' | 'sim_equal' | 'trigramcoma' | 'trigramifuice' | 'trigramlowmem' | 'trigramopt' | 'edjoin' | 'levenshteinlucene' | 'levenshteinsecondstring' | 'levenshteinlimes' | 'cosineppjoin+fullycached' | 'cosineppjoin' | 'cosinesimmetrics' | 'jaccardppjoin+fullycached' | 'jaccardppjoin+' | 'jaccardsecondstring' | 'jaccardsimmetrics' | 'jarowinklerlucene' | 'tfidflucenefullycached' | 'tfidflucenefullycachedalternative' | 'tfidfsecondstring' )
            int alt4=30;
            alt4 = dfa4.predict(input);
            switch (alt4) {
                case 1 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:145:15: 'trigram'
                    {
                    match("trigram"); 


                    }
                    break;
                case 2 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:145:27: 'soundex'
                    {
                    match("soundex"); 


                    }
                    break;
                case 3 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:145:39: 'editdist'
                    {
                    match("editdist"); 


                    }
                    break;
                case 4 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:145:52: 'datatypesimilarity'
                    {
                    match("datatypesimilarity"); 


                    }
                    break;
                case 5 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:145:75: 'featvect'
                    {
                    match("featvect"); 


                    }
                    break;
                case 6 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:145:88: 'tfidf'
                    {
                    match("tfidf"); 


                    }
                    break;
                case 7 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:146:5: 'usersyn'
                    {
                    match("usersyn"); 


                    }
                    break;
                case 8 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:146:17: 'cosine'
                    {
                    match("cosine"); 


                    }
                    break;
                case 9 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:146:28: 'jaccard'
                    {
                    match("jaccard"); 


                    }
                    break;
                case 10 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:146:40: 'jarowinkler'
                    {
                    match("jarowinkler"); 


                    }
                    break;
                case 11 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:146:56: 'sim_equal'
                    {
                    match("sim_equal"); 


                    }
                    break;
                case 12 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:147:5: 'trigramcoma'
                    {
                    match("trigramcoma"); 


                    }
                    break;
                case 13 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:147:21: 'trigramifuice'
                    {
                    match("trigramifuice"); 


                    }
                    break;
                case 14 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:147:39: 'trigramlowmem'
                    {
                    match("trigramlowmem"); 


                    }
                    break;
                case 15 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:147:57: 'trigramopt'
                    {
                    match("trigramopt"); 


                    }
                    break;
                case 16 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:147:72: 'edjoin'
                    {
                    match("edjoin"); 


                    }
                    break;
                case 17 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:148:5: 'levenshteinlucene'
                    {
                    match("levenshteinlucene"); 


                    }
                    break;
                case 18 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:148:27: 'levenshteinsecondstring'
                    {
                    match("levenshteinsecondstring"); 


                    }
                    break;
                case 19 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:148:55: 'levenshteinlimes'
                    {
                    match("levenshteinlimes"); 


                    }
                    break;
                case 20 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:148:75: 'cosineppjoin+fullycached'
                    {
                    match("cosineppjoin+fullycached"); 


                    }
                    break;
                case 21 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:148:104: 'cosineppjoin'
                    {
                    match("cosineppjoin"); 


                    }
                    break;
                case 22 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:148:121: 'cosinesimmetrics'
                    {
                    match("cosinesimmetrics"); 


                    }
                    break;
                case 23 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:149:5: 'jaccardppjoin+fullycached'
                    {
                    match("jaccardppjoin+fullycached"); 


                    }
                    break;
                case 24 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:149:35: 'jaccardppjoin+'
                    {
                    match("jaccardppjoin+"); 


                    }
                    break;
                case 25 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:149:54: 'jaccardsecondstring'
                    {
                    match("jaccardsecondstring"); 


                    }
                    break;
                case 26 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:149:78: 'jaccardsimmetrics'
                    {
                    match("jaccardsimmetrics"); 


                    }
                    break;
                case 27 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:149:100: 'jarowinklerlucene'
                    {
                    match("jarowinklerlucene"); 


                    }
                    break;
                case 28 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:150:5: 'tfidflucenefullycached'
                    {
                    match("tfidflucenefullycached"); 


                    }
                    break;
                case 29 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:150:32: 'tfidflucenefullycachedalternative'
                    {
                    match("tfidflucenefullycachedalternative"); 


                    }
                    break;
                case 30 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:150:70: 'tfidfsecondstring'
                    {
                    match("tfidfsecondstring"); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SIMMEASURE"

    // $ANTLR start "SETCOMBINATION"
    public final void mSETCOMBINATION() throws RecognitionException {
        try {
            int _type = SETCOMBINATION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:152:15: ( ( 'set_average' | 'set_dice' | 'set_max' | 'set_min' | 'set_highest' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:152:18: ( 'set_average' | 'set_dice' | 'set_max' | 'set_min' | 'set_highest' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:152:18: ( 'set_average' | 'set_dice' | 'set_max' | 'set_min' | 'set_highest' )
            int alt5=5;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:152:19: 'set_average'
                    {
                    match("set_average"); 


                    }
                    break;
                case 2 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:152:35: 'set_dice'
                    {
                    match("set_dice"); 


                    }
                    break;
                case 3 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:152:48: 'set_max'
                    {
                    match("set_max"); 


                    }
                    break;
                case 4 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:152:60: 'set_min'
                    {
                    match("set_min"); 


                    }
                    break;
                case 5 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:152:72: 'set_highest'
                    {
                    match("set_highest"); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SETCOMBINATION"

    // $ANTLR start "COMPOSITION"
    public final void mCOMPOSITION() throws RecognitionException {
        try {
            int _type = COMPOSITION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:154:12: ( ( 'com_average' | 'com_max' | 'com_min' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:154:16: ( 'com_average' | 'com_max' | 'com_min' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:154:16: ( 'com_average' | 'com_max' | 'com_min' )
            int alt6=3;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='c') ) {
                int LA6_1 = input.LA(2);

                if ( (LA6_1=='o') ) {
                    int LA6_2 = input.LA(3);

                    if ( (LA6_2=='m') ) {
                        int LA6_3 = input.LA(4);

                        if ( (LA6_3=='_') ) {
                            int LA6_4 = input.LA(5);

                            if ( (LA6_4=='a') ) {
                                alt6=1;
                            }
                            else if ( (LA6_4=='m') ) {
                                int LA6_6 = input.LA(6);

                                if ( (LA6_6=='a') ) {
                                    alt6=2;
                                }
                                else if ( (LA6_6=='i') ) {
                                    alt6=3;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 6, 6, input);

                                    throw nvae;
                                }
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 6, 4, input);

                                throw nvae;
                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 6, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:154:17: 'com_average'
                    {
                    match("com_average"); 


                    }
                    break;
                case 2 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:154:33: 'com_max'
                    {
                    match("com_max"); 


                    }
                    break;
                case 3 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:154:45: 'com_min'
                    {
                    match("com_min"); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMPOSITION"

    // $ANTLR start "SIMCOMBINATION1"
    public final void mSIMCOMBINATION1() throws RecognitionException {
        try {
            int _type = SIMCOMBINATION1;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:17: ( ( 'max' | 'min' | 'average' | 'nonlinear' | 'openii' | 'harmony' | 'sigmoid' | 'owa' | 'owa_most' | 'weighted2' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:20: ( 'max' | 'min' | 'average' | 'nonlinear' | 'openii' | 'harmony' | 'sigmoid' | 'owa' | 'owa_most' | 'weighted2' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:20: ( 'max' | 'min' | 'average' | 'nonlinear' | 'openii' | 'harmony' | 'sigmoid' | 'owa' | 'owa_most' | 'weighted2' )
            int alt7=10;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:21: 'max'
                    {
                    match("max"); 


                    }
                    break;
                case 2 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:29: 'min'
                    {
                    match("min"); 


                    }
                    break;
                case 3 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:37: 'average'
                    {
                    match("average"); 


                    }
                    break;
                case 4 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:49: 'nonlinear'
                    {
                    match("nonlinear"); 


                    }
                    break;
                case 5 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:63: 'openii'
                    {
                    match("openii"); 


                    }
                    break;
                case 6 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:74: 'harmony'
                    {
                    match("harmony"); 


                    }
                    break;
                case 7 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:86: 'sigmoid'
                    {
                    match("sigmoid"); 


                    }
                    break;
                case 8 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:98: 'owa'
                    {
                    match("owa"); 


                    }
                    break;
                case 9 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:106: 'owa_most'
                    {
                    match("owa_most"); 


                    }
                    break;
                case 10 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:156:119: 'weighted2'
                    {
                    match("weighted2"); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SIMCOMBINATION1"

    // $ANTLR start "SIMCOMBINATION2"
    public final void mSIMCOMBINATION2() throws RecognitionException {
        try {
            int _type = SIMCOMBINATION2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:157:17: ( ( 'weighted' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:157:19: ( 'weighted' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:157:19: ( 'weighted' )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:157:20: 'weighted'
            {
            match("weighted"); 


            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SIMCOMBINATION2"

    // $ANTLR start "RESULT_COMBINATION"
    public final void mRESULT_COMBINATION() throws RecognitionException {
        try {
            int _type = RESULT_COMBINATION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:159:20: ( ( 'intersect' | 'diff' | 'merge' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:159:23: ( 'intersect' | 'diff' | 'merge' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:159:23: ( 'intersect' | 'diff' | 'merge' )
            int alt8=3;
            switch ( input.LA(1) ) {
            case 'i':
                {
                alt8=1;
                }
                break;
            case 'd':
                {
                alt8=2;
                }
                break;
            case 'm':
                {
                alt8=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:159:25: 'intersect'
                    {
                    match("intersect"); 


                    }
                    break;
                case 2 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:159:39: 'diff'
                    {
                    match("diff"); 


                    }
                    break;
                case 3 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:159:48: 'merge'
                    {
                    match("merge"); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RESULT_COMBINATION"

    // $ANTLR start "DIRECTION"
    public final void mDIRECTION() throws RecognitionException {
        try {
            int _type = DIRECTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:162:11: ( ( 'both' | 'forward' | 'backward' | 'simple' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:162:14: ( 'both' | 'forward' | 'backward' | 'simple' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:162:14: ( 'both' | 'forward' | 'backward' | 'simple' )
            int alt9=4;
            switch ( input.LA(1) ) {
            case 'b':
                {
                int LA9_1 = input.LA(2);

                if ( (LA9_1=='o') ) {
                    alt9=1;
                }
                else if ( (LA9_1=='a') ) {
                    alt9=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 1, input);

                    throw nvae;
                }
                }
                break;
            case 'f':
                {
                alt9=2;
                }
                break;
            case 's':
                {
                alt9=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:162:15: 'both'
                    {
                    match("both"); 


                    }
                    break;
                case 2 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:162:24: 'forward'
                    {
                    match("forward"); 


                    }
                    break;
                case 3 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:162:36: 'backward'
                    {
                    match("backward"); 


                    }
                    break;
                case 4 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:162:49: 'simple'
                    {
                    match("simple"); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIRECTION"

    // $ANTLR start "SELECTION_THRESHOLD"
    public final void mSELECTION_THRESHOLD() throws RecognitionException {
        try {
            int _type = SELECTION_THRESHOLD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:164:21: ( ( 'threshold' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:164:24: ( 'threshold' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:164:24: ( 'threshold' )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:164:25: 'threshold'
            {
            match("threshold"); 


            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SELECTION_THRESHOLD"

    // $ANTLR start "SELECTION_MAXDELTA"
    public final void mSELECTION_MAXDELTA() throws RecognitionException {
        try {
            int _type = SELECTION_MAXDELTA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:165:20: ( ( 'maxdelta' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:165:23: ( 'maxdelta' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:165:23: ( 'maxdelta' )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:165:24: 'maxdelta'
            {
            match("maxdelta"); 


            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SELECTION_MAXDELTA"

    // $ANTLR start "SELECTION_MAXN"
    public final void mSELECTION_MAXN() throws RecognitionException {
        try {
            int _type = SELECTION_MAXN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:166:16: ( ( 'maxn' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:166:19: ( 'maxn' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:166:19: ( 'maxn' )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:166:20: 'maxn'
            {
            match("maxn"); 


            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SELECTION_MAXN"

    // $ANTLR start "SELECTION_MULTIPLE"
    public final void mSELECTION_MULTIPLE() throws RecognitionException {
        try {
            int _type = SELECTION_MULTIPLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:167:20: ( ( 'multiple' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:167:23: ( 'multiple' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:167:23: ( 'multiple' )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:167:24: 'multiple'
            {
            match("multiple"); 


            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SELECTION_MULTIPLE"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            int _type = DIGIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:170:7: ( '0' .. '9' )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:170:11: '0' .. '9'
            {
            matchRange('0','9'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "V2"
    public final void mV2() throws RecognitionException {
        try {
            int _type = V2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:171:4: ( ( DIGIT )+ )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:171:8: ( DIGIT )+
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:171:8: ( DIGIT )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>='0' && LA10_0<='9')) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:171:8: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "V2"

    // $ANTLR start "V1"
    public final void mV1() throws RecognitionException {
        try {
            int _type = V1;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:172:4: ( ( ( '0' CHAR_DOT ( DIGIT )+ ) | '1.0' | '1' | '0' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:172:9: ( ( '0' CHAR_DOT ( DIGIT )+ ) | '1.0' | '1' | '0' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:172:9: ( ( '0' CHAR_DOT ( DIGIT )+ ) | '1.0' | '1' | '0' )
            int alt12=4;
            int LA12_0 = input.LA(1);

            if ( (LA12_0=='0') ) {
                int LA12_1 = input.LA(2);

                if ( (LA12_1=='.') ) {
                    alt12=1;
                }
                else {
                    alt12=4;}
            }
            else if ( (LA12_0=='1') ) {
                int LA12_2 = input.LA(2);

                if ( (LA12_2=='.') ) {
                    alt12=2;
                }
                else {
                    alt12=3;}
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:172:11: ( '0' CHAR_DOT ( DIGIT )+ )
                    {
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:172:11: ( '0' CHAR_DOT ( DIGIT )+ )
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:172:12: '0' CHAR_DOT ( DIGIT )+
                    {
                    match('0'); 
                    mCHAR_DOT(); 
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:172:25: ( DIGIT )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( ((LA11_0>='0' && LA11_0<='9')) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:172:25: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:172:35: '1.0'
                    {
                    match("1.0"); 


                    }
                    break;
                case 3 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:172:43: '1'
                    {
                    match('1'); 

                    }
                    break;
                case 4 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:172:49: '0'
                    {
                    match('0'); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "V1"

    // $ANTLR start "BOOLEAN"
    public final void mBOOLEAN() throws RecognitionException {
        try {
            int _type = BOOLEAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:173:8: ( ( 'true' | 'false' ) )
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:173:11: ( 'true' | 'false' )
            {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:173:11: ( 'true' | 'false' )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0=='t') ) {
                alt13=1;
            }
            else if ( (LA13_0=='f') ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:173:12: 'true'
                    {
                    match("true"); 


                    }
                    break;
                case 2 :
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:173:21: 'false'
                    {
                    match("false"); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BOOLEAN"

    public void mTokens() throws RecognitionException {
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:8: ( CHAR_BRACE_LEFT | CHAR_BRACE_RIGHT | CHAR_DOT | CHAR_COMMA | CHAR_SEMICOLON | RESOLUTION_1 | RESOLUTION_2 | RESOLUTION_3 | SIMMEASURE | SETCOMBINATION | COMPOSITION | SIMCOMBINATION1 | SIMCOMBINATION2 | RESULT_COMBINATION | DIRECTION | SELECTION_THRESHOLD | SELECTION_MAXDELTA | SELECTION_MAXN | SELECTION_MULTIPLE | DIGIT | V2 | V1 | BOOLEAN )
        int alt14=23;
        alt14 = dfa14.predict(input);
        switch (alt14) {
            case 1 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:10: CHAR_BRACE_LEFT
                {
                mCHAR_BRACE_LEFT(); 

                }
                break;
            case 2 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:26: CHAR_BRACE_RIGHT
                {
                mCHAR_BRACE_RIGHT(); 

                }
                break;
            case 3 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:43: CHAR_DOT
                {
                mCHAR_DOT(); 

                }
                break;
            case 4 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:52: CHAR_COMMA
                {
                mCHAR_COMMA(); 

                }
                break;
            case 5 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:63: CHAR_SEMICOLON
                {
                mCHAR_SEMICOLON(); 

                }
                break;
            case 6 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:78: RESOLUTION_1
                {
                mRESOLUTION_1(); 

                }
                break;
            case 7 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:91: RESOLUTION_2
                {
                mRESOLUTION_2(); 

                }
                break;
            case 8 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:104: RESOLUTION_3
                {
                mRESOLUTION_3(); 

                }
                break;
            case 9 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:117: SIMMEASURE
                {
                mSIMMEASURE(); 

                }
                break;
            case 10 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:128: SETCOMBINATION
                {
                mSETCOMBINATION(); 

                }
                break;
            case 11 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:143: COMPOSITION
                {
                mCOMPOSITION(); 

                }
                break;
            case 12 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:155: SIMCOMBINATION1
                {
                mSIMCOMBINATION1(); 

                }
                break;
            case 13 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:171: SIMCOMBINATION2
                {
                mSIMCOMBINATION2(); 

                }
                break;
            case 14 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:187: RESULT_COMBINATION
                {
                mRESULT_COMBINATION(); 

                }
                break;
            case 15 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:206: DIRECTION
                {
                mDIRECTION(); 

                }
                break;
            case 16 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:216: SELECTION_THRESHOLD
                {
                mSELECTION_THRESHOLD(); 

                }
                break;
            case 17 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:236: SELECTION_MAXDELTA
                {
                mSELECTION_MAXDELTA(); 

                }
                break;
            case 18 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:255: SELECTION_MAXN
                {
                mSELECTION_MAXN(); 

                }
                break;
            case 19 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:270: SELECTION_MULTIPLE
                {
                mSELECTION_MULTIPLE(); 

                }
                break;
            case 20 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:289: DIGIT
                {
                mDIGIT(); 

                }
                break;
            case 21 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:295: V2
                {
                mV2(); 

                }
                break;
            case 22 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:298: V1
                {
                mV1(); 

                }
                break;
            case 23 :
                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:1:301: BOOLEAN
                {
                mBOOLEAN(); 

                }
                break;

        }

    }


    protected DFA1 dfa1 = new DFA1(this);
    protected DFA2 dfa2 = new DFA2(this);
    protected DFA3 dfa3 = new DFA3(this);
    protected DFA4 dfa4 = new DFA4(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA14 dfa14 = new DFA14(this);
    static final String DFA1_eotS =
        "\32\uffff\1\34\2\uffff";
    static final String DFA1_eofS =
        "\35\uffff";
    static final String DFA1_minS =
        "\1\144\1\uffff\1\156\1\145\2\uffff\1\150\1\160\1\uffff\1\156\2"+
        "\141\2\uffff\1\145\1\146\2\162\1\156\1\145\1\156\2\uffff\1\144\2"+
        "\uffff\1\160\2\uffff";
    static final String DFA1_maxS =
        "\1\165\1\uffff\1\156\1\145\2\uffff\1\150\1\163\1\uffff\1\156\2"+
        "\141\2\uffff\1\145\1\146\2\162\1\160\1\145\1\160\2\uffff\1\144\2"+
        "\uffff\1\160\2\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\1\2\uffff\1\4\1\7\2\uffff\1\12\3\uffff\1\11\1\13\7\uffff"+
        "\1\3\1\6\1\uffff\1\2\1\5\1\uffff\1\14\1\10";
    static final String DFA1_specialS =
        "\35\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\10\4\uffff\1\2\2\uffff\1\3\1\uffff\1\4\1\uffff\1\1\1\uffff"+
            "\1\5\1\6\1\uffff\1\7",
            "",
            "\1\11",
            "\1\12",
            "",
            "",
            "\1\13",
            "\1\14\2\uffff\1\15",
            "",
            "\1\16",
            "\1\17",
            "\1\20",
            "",
            "",
            "\1\21",
            "\1\22",
            "\1\23",
            "\1\24",
            "\1\26\1\uffff\1\25",
            "\1\27",
            "\1\31\1\uffff\1\30",
            "",
            "",
            "\1\32",
            "",
            "",
            "\1\33",
            "",
            ""
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "139:17: ( 'paths' | 'innerpaths' | 'leafpaths' | 'nodes' | 'innernodes' | 'leafnodes' | 'roots' | 'shared' | 'uppaths' | 'downpaths' | 'user' | 'sharedpaths' )";
        }
    }
    static final String DFA2_eotS =
        "\15\uffff";
    static final String DFA2_eofS =
        "\15\uffff";
    static final String DFA2_minS =
        "\1\141\1\145\4\uffff\1\154\2\uffff\1\146\1\156\2\uffff";
    static final String DFA2_maxS =
        "\1\163\1\165\4\uffff\1\154\2\uffff\1\146\1\160\2\uffff";
    static final String DFA2_acceptS =
        "\2\uffff\1\3\1\5\1\6\1\7\1\uffff\1\4\1\10\2\uffff\1\1\1\2";
    static final String DFA2_specialS =
        "\15\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\5\1\uffff\1\3\10\uffff\1\4\3\uffff\1\2\2\uffff\1\1",
            "\1\6\3\uffff\1\7\13\uffff\1\10",
            "",
            "",
            "",
            "",
            "\1\11",
            "",
            "",
            "\1\12",
            "\1\14\1\uffff\1\13",
            "",
            ""
    };

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }
        public String getDescription() {
            return "140:17: ( 'selfpath' | 'selfnode' | 'parents' | 'siblings' | 'children' | 'leaves' | 'allnodes' | 'successors' )";
        }
    }
    static final String DFA3_eotS =
        "\21\uffff\1\27\1\32\14\uffff\1\42\15\uffff\1\57\2\uffff";
    static final String DFA3_eofS =
        "\60\uffff";
    static final String DFA3_minS =
        "\1\143\2\141\1\157\1\uffff\1\164\1\156\1\155\1\164\1\155\2\uffff"+
        "\1\163\1\145\1\150\1\155\1\164\1\141\1\163\1\145\1\141\6\uffff\2"+
        "\156\1\164\1\143\1\164\1\145\2\uffff\1\137\1\141\1\157\1\uffff\1"+
        "\156\1\163\1\uffff\1\145\1\156\1\164\1\137\2\uffff";
    static final String DFA3_maxS =
        "\1\163\2\141\1\157\1\uffff\1\171\1\156\1\155\1\164\1\155\2\uffff"+
        "\1\163\1\145\1\150\1\155\3\164\1\145\1\141\6\uffff\2\156\1\164\1"+
        "\143\1\164\1\145\2\uffff\1\137\1\143\1\157\1\uffff\1\156\1\164\1"+
        "\uffff\1\145\1\156\1\164\1\137\2\uffff";
    static final String DFA3_acceptS =
        "\4\uffff\1\10\5\uffff\1\11\1\12\11\uffff\1\2\1\17\1\1\1\4\1\5\1"+
        "\3\6\uffff\1\7\1\6\3\uffff\1\16\2\uffff\1\13\4\uffff\1\15\1\14";
    static final String DFA3_specialS =
        "\60\uffff}>";
    static final String[] DFA3_transitionS = {
            "\1\3\1\4\4\uffff\1\6\4\uffff\1\1\1\uffff\1\2\2\uffff\1\5",
            "\1\7",
            "\1\10",
            "\1\11",
            "",
            "\1\12\4\uffff\1\13",
            "\1\14",
            "\1\15",
            "\1\16",
            "\1\17",
            "",
            "",
            "\1\20",
            "\1\21",
            "\1\22",
            "\1\23",
            "\1\24",
            "\1\26\22\uffff\1\25",
            "\1\31\1\30",
            "\1\33",
            "\1\34",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\35",
            "\1\36",
            "\1\37",
            "\1\40",
            "\1\41",
            "\1\43",
            "",
            "",
            "\1\44",
            "\1\46\1\uffff\1\45",
            "\1\47",
            "",
            "\1\50",
            "\1\51\1\52",
            "",
            "\1\53",
            "\1\54",
            "\1\55",
            "\1\56",
            "",
            ""
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "141:17: ( 'name' | 'nametoken' | 'path' | 'pathtoken' | 'pathsyn' | 'comment' | 'commenttoken' | 'datatype' | 'statistics' | 'synonyms' | 'instance_constraints' | 'instance_content' | 'instance_content_indirect' | 'instance_all' | 'nameandsynonyms' )";
        }
    }
    static final String DFA4_eotS =
        "\41\uffff\1\51\10\uffff\1\62\3\uffff\1\72\4\uffff\1\77\35\uffff"+
        "\1\127\2\uffff\1\134\14\uffff\1\144\11\uffff\1\155\2\uffff";
    static final String DFA4_eofS =
        "\156\uffff";
    static final String DFA4_minS =
        "\1\143\1\146\1\151\1\144\3\uffff\1\157\1\141\1\145\2\151\2\uffff"+
        "\1\151\1\163\1\143\1\166\1\147\1\144\2\uffff\1\151\1\143\1\157\1"+
        "\145\1\162\1\146\1\156\1\141\1\167\1\156\1\141\1\154\1\145\1\162"+
        "\1\151\1\163\1\155\1\165\2\uffff\1\160\1\144\1\156\1\150\2\143\1"+
        "\160\2\uffff\1\160\1\153\1\164\5\uffff\1\145\1\152\1\160\1\145\1"+
        "\uffff\1\154\1\145\1\156\1\157\1\152\2\uffff\1\145\1\151\1\145\1"+
        "\151\1\157\1\162\1\156\1\146\1\156\1\151\2\154\1\165\1\53\1\156"+
        "\2\uffff\1\151\1\uffff\1\154\2\uffff\1\53\2\uffff\1\154\1\146\1"+
        "\171\2\uffff\1\143\1\141\1\143\1\150\1\145\1\144\1\141\2\uffff";
    static final String DFA4_maxS =
        "\1\165\1\162\1\157\1\144\3\uffff\1\157\1\141\1\145\2\151\2\uffff"+
        "\1\152\1\163\1\162\1\166\1\147\1\144\2\uffff\1\151\1\143\1\157\1"+
        "\145\1\162\1\146\1\156\1\141\1\167\1\156\1\141\1\163\1\145\1\162"+
        "\1\151\1\163\1\155\1\165\2\uffff\1\163\1\144\1\156\1\150\1\157\1"+
        "\143\1\160\2\uffff\1\163\1\153\1\164\5\uffff\1\145\1\152\1\160\1"+
        "\151\1\uffff\1\154\1\145\1\156\1\157\1\152\2\uffff\1\145\1\151\1"+
        "\145\1\151\1\157\1\162\1\156\1\146\1\156\1\151\1\154\1\163\1\165"+
        "\1\53\1\156\2\uffff\1\165\1\uffff\1\154\2\uffff\1\53\2\uffff\1\154"+
        "\1\146\1\171\2\uffff\1\143\1\141\1\143\1\150\1\145\1\144\1\141\2"+
        "\uffff";
    static final String DFA4_acceptS =
        "\4\uffff\1\4\1\5\1\7\5\uffff\1\2\1\13\6\uffff\1\3\1\20\22\uffff"+
        "\1\36\1\6\7\uffff\1\26\1\10\3\uffff\1\14\1\15\1\16\1\17\1\1\4\uffff"+
        "\1\11\5\uffff\1\31\1\32\17\uffff\1\33\1\12\1\uffff\1\22\1\uffff"+
        "\1\24\1\25\1\uffff\1\21\1\23\3\uffff\1\27\1\30\7\uffff\1\35\1\34";
    static final String DFA4_specialS =
        "\156\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\7\1\4\1\3\1\5\3\uffff\1\10\1\uffff\1\11\6\uffff\1\2\1\1"+
            "\1\6",
            "\1\13\13\uffff\1\12",
            "\1\15\5\uffff\1\14",
            "\1\16",
            "",
            "",
            "",
            "\1\17",
            "\1\20",
            "\1\21",
            "\1\22",
            "\1\23",
            "",
            "",
            "\1\24\1\25",
            "\1\26",
            "\1\27\16\uffff\1\30",
            "\1\31",
            "\1\32",
            "\1\33",
            "",
            "",
            "\1\34",
            "\1\35",
            "\1\36",
            "\1\37",
            "\1\40",
            "\1\41",
            "\1\42",
            "\1\43",
            "\1\44",
            "\1\45",
            "\1\46",
            "\1\47\6\uffff\1\50",
            "\1\52",
            "\1\53",
            "\1\54",
            "\1\55",
            "\1\56",
            "\1\57",
            "",
            "",
            "\1\60\2\uffff\1\61",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\66\5\uffff\1\67\2\uffff\1\70\2\uffff\1\71",
            "\1\73",
            "\1\74",
            "",
            "",
            "\1\75\2\uffff\1\76",
            "\1\100",
            "\1\101",
            "",
            "",
            "",
            "",
            "",
            "\1\102",
            "\1\103",
            "\1\104",
            "\1\105\3\uffff\1\106",
            "",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\112",
            "\1\113",
            "",
            "",
            "\1\114",
            "\1\115",
            "\1\116",
            "\1\117",
            "\1\120",
            "\1\121",
            "\1\122",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\126",
            "\1\130\6\uffff\1\131",
            "\1\132",
            "\1\133",
            "\1\135",
            "",
            "",
            "\1\137\13\uffff\1\136",
            "",
            "\1\140",
            "",
            "",
            "\1\141",
            "",
            "",
            "\1\142",
            "\1\143",
            "\1\145",
            "",
            "",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\154",
            "",
            ""
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "145:14: ( 'trigram' | 'soundex' | 'editdist' | 'datatypesimilarity' | 'featvect' | 'tfidf' | 'usersyn' | 'cosine' | 'jaccard' | 'jarowinkler' | 'sim_equal' | 'trigramcoma' | 'trigramifuice' | 'trigramlowmem' | 'trigramopt' | 'edjoin' | 'levenshteinlucene' | 'levenshteinsecondstring' | 'levenshteinlimes' | 'cosineppjoin+fullycached' | 'cosineppjoin' | 'cosinesimmetrics' | 'jaccardppjoin+fullycached' | 'jaccardppjoin+' | 'jaccardsecondstring' | 'jaccardsimmetrics' | 'jarowinklerlucene' | 'tfidflucenefullycached' | 'tfidflucenefullycachedalternative' | 'tfidfsecondstring' )";
        }
    }
    static final String DFA5_eotS =
        "\13\uffff";
    static final String DFA5_eofS =
        "\13\uffff";
    static final String DFA5_minS =
        "\1\163\1\145\1\164\1\137\1\141\2\uffff\1\141\3\uffff";
    static final String DFA5_maxS =
        "\1\163\1\145\1\164\1\137\1\155\2\uffff\1\151\3\uffff";
    static final String DFA5_acceptS =
        "\5\uffff\1\1\1\2\1\uffff\1\5\1\3\1\4";
    static final String DFA5_specialS =
        "\13\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1",
            "\1\2",
            "\1\3",
            "\1\4",
            "\1\5\2\uffff\1\6\3\uffff\1\10\4\uffff\1\7",
            "",
            "",
            "\1\11\7\uffff\1\12",
            "",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "152:18: ( 'set_average' | 'set_dice' | 'set_max' | 'set_min' | 'set_highest' )";
        }
    }
    static final String DFA7_eotS =
        "\14\uffff\1\16\2\uffff";
    static final String DFA7_eofS =
        "\17\uffff";
    static final String DFA7_minS =
        "\2\141\2\uffff\1\160\6\uffff\1\141\1\137\2\uffff";
    static final String DFA7_maxS =
        "\1\167\1\151\2\uffff\1\167\6\uffff\1\141\1\137\2\uffff";
    static final String DFA7_acceptS =
        "\2\uffff\1\3\1\4\1\uffff\1\6\1\7\1\12\1\1\1\2\1\5\2\uffff\1\11"+
        "\1\10";
    static final String DFA7_specialS =
        "\17\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\2\6\uffff\1\5\4\uffff\1\1\1\3\1\4\3\uffff\1\6\3\uffff\1"+
            "\7",
            "\1\10\7\uffff\1\11",
            "",
            "",
            "\1\12\6\uffff\1\13",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\14",
            "\1\15",
            "",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "156:20: ( 'max' | 'min' | 'average' | 'nonlinear' | 'openii' | 'harmony' | 'sigmoid' | 'owa' | 'owa_most' | 'weighted2' )";
        }
    }
    static final String DFA14_eotS =
        "\27\uffff\3\54\34\uffff\1\24\1\uffff\1\36\1\12\5\uffff\1\12\6\uffff"+
        "\1\36\1\110\1\uffff";
    static final String DFA14_eofS =
        "\111\uffff";
    static final String DFA14_minS =
        "\1\50\5\uffff\1\141\1\156\1\145\1\141\1\uffff\1\145\1\160\1\141"+
        "\1\150\1\154\1\146\1\uffff\2\141\1\uffff\1\145\1\uffff\2\56\1\60"+
        "\1\162\1\156\1\141\1\144\1\uffff\1\154\1\142\1\uffff\1\145\1\164"+
        "\1\uffff\1\155\1\151\2\uffff\1\170\1\uffff\1\151\3\uffff\1\150\1"+
        "\146\1\uffff\1\137\1\162\1\141\1\137\1\144\1\147\2\163\1\164\3\uffff"+
        "\1\150\2\171\1\164\1\160\2\145\1\144\1\163\1\62\1\uffff";
    static final String DFA14_maxS =
        "\1\167\5\uffff\1\141\1\156\1\145\1\157\1\uffff\1\171\1\163\2\157"+
        "\1\166\1\162\1\uffff\1\157\1\165\1\uffff\1\145\1\uffff\3\71\2\164"+
        "\1\166\1\156\1\uffff\1\164\1\155\1\uffff\1\145\1\164\1\uffff\1\163"+
        "\1\165\2\uffff\1\170\1\uffff\1\151\3\uffff\1\150\1\166\1\uffff\1"+
        "\160\1\162\1\141\1\155\1\156\1\147\2\163\1\164\3\uffff\1\150\2\171"+
        "\1\164\1\160\2\145\1\144\1\163\1\62\1\uffff";
    static final String DFA14_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\4\uffff\1\6\6\uffff\1\11\2\uffff\1"+
        "\14\1\uffff\1\17\7\uffff\1\10\2\uffff\1\7\2\uffff\1\16\2\uffff\1"+
        "\20\1\27\1\uffff\1\23\1\uffff\1\24\1\25\1\26\2\uffff\1\12\11\uffff"+
        "\1\13\1\21\1\22\12\uffff\1\15";
    static final String DFA14_specialS =
        "\111\uffff}>";
    static final String[] DFA14_transitionS = {
            "\1\1\1\2\2\uffff\1\4\1\uffff\1\3\1\uffff\1\27\1\30\10\31\1"+
            "\uffff\1\5\45\uffff\1\17\1\26\1\16\1\15\1\21\1\22\1\uffff\1"+
            "\24\1\7\1\21\1\uffff\1\10\1\23\1\11\1\24\1\6\1\uffff\1\12\1"+
            "\13\1\20\1\14\1\uffff\1\25",
            "",
            "",
            "",
            "",
            "",
            "\1\32",
            "\1\33",
            "\1\34",
            "\1\36\15\uffff\1\35",
            "",
            "\1\37\2\uffff\1\12\1\40\5\uffff\1\21\4\uffff\1\36\1\41\3\uffff"+
            "\1\36",
            "\1\12\2\uffff\1\42",
            "\1\43\7\uffff\1\44\5\uffff\1\12",
            "\1\41\6\uffff\1\45",
            "\1\41\11\uffff\1\24",
            "\1\21\1\uffff\1\47\11\uffff\1\46",
            "",
            "\1\50\3\uffff\1\21\11\uffff\1\26",
            "\1\51\3\uffff\1\44\3\uffff\1\24\13\uffff\1\52",
            "",
            "\1\53",
            "",
            "\1\56\1\uffff\12\55",
            "\1\56\1\uffff\12\55",
            "\12\55",
            "\1\41\1\uffff\1\57",
            "\1\12\4\uffff\1\36\1\44",
            "\1\60\24\uffff\1\21",
            "\1\12\11\uffff\1\24",
            "",
            "\1\41\7\uffff\1\61",
            "\1\41\4\uffff\1\24\5\uffff\1\62",
            "",
            "\1\63",
            "\1\64",
            "",
            "\1\65\5\uffff\1\21",
            "\1\21\13\uffff\1\50",
            "",
            "",
            "\1\66",
            "",
            "\1\67",
            "",
            "",
            "",
            "\1\70",
            "\1\12\17\uffff\1\41",
            "",
            "\1\21\20\uffff\1\26",
            "\1\71",
            "\1\72",
            "\1\73\15\uffff\1\36",
            "\1\74\11\uffff\1\75",
            "\1\76",
            "\1\77",
            "\1\21",
            "\1\100",
            "",
            "",
            "",
            "\1\101",
            "\1\36",
            "\1\102",
            "\1\103",
            "\1\104",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\21",
            "\1\24",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( CHAR_BRACE_LEFT | CHAR_BRACE_RIGHT | CHAR_DOT | CHAR_COMMA | CHAR_SEMICOLON | RESOLUTION_1 | RESOLUTION_2 | RESOLUTION_3 | SIMMEASURE | SETCOMBINATION | COMPOSITION | SIMCOMBINATION1 | SIMCOMBINATION2 | RESULT_COMBINATION | DIRECTION | SELECTION_THRESHOLD | SELECTION_MAXDELTA | SELECTION_MAXN | SELECTION_MULTIPLE | DIGIT | V2 | V1 | BOOLEAN );";
        }
    }
 

}