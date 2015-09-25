package de.wdilab.coma.matching.validation;

// $ANTLR 3.3 Nov 30, 2010 12:45:30 C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g 2012-06-29 09:44:49

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.antlr.runtime.debug.*;
import java.io.IOException;

import org.antlr.runtime.tree.*;

public class ComaWorkFlowParser extends DebugParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "CHAR_BRACE_LEFT", "CHAR_BRACE_RIGHT", "CHAR_DOT", "CHAR_COMMA", "CHAR_SEMICOLON", "RESULT_COMBINATION", "RESOLUTION_1", "RESOLUTION_2", "SETCOMBINATION", "RESOLUTION_3", "SIMMEASURE", "DIGIT", "V2", "BOOLEAN", "COMPOSITION", "SIMCOMBINATION1", "DIRECTION", "SELECTION_THRESHOLD", "V1", "SELECTION_MAXDELTA", "SELECTION_MAXN", "SELECTION_MULTIPLE", "SIMCOMBINATION2"
    };
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

    public static final String[] ruleNames = new String[] {
        "invalidRule", "synpred1_ComaWorkFlow", "synpred39_ComaWorkFlow", 
        "synpred38_ComaWorkFlow", "synpred8_ComaWorkFlow", "reuse", "synpred17_ComaWorkFlow", 
        "synpred28_ComaWorkFlow", "synpred5_ComaWorkFlow", "synpred10_ComaWorkFlow", 
        "synpred11_ComaWorkFlow", "synpred24_ComaWorkFlow", "synpred6_ComaWorkFlow", 
        "synpred13_ComaWorkFlow", "allowedToken", "strategy", "synpred20_ComaWorkFlow", 
        "matcher", "synpred3_ComaWorkFlow", "complexMatcher", "synpred35_ComaWorkFlow", 
        "selection", "synpred23_ComaWorkFlow", "synpred25_ComaWorkFlow", 
        "coma", "synpred33_ComaWorkFlow", "synpred18_ComaWorkFlow", "synpred26_ComaWorkFlow", 
        "synpred30_ComaWorkFlow", "synpred21_ComaWorkFlow", "synpred34_ComaWorkFlow", 
        "synpred32_ComaWorkFlow", "synpred40_ComaWorkFlow", "similarityCombination", 
        "synpred12_ComaWorkFlow", "synpred27_ComaWorkFlow", "synpred2_ComaWorkFlow", 
        "synpred16_ComaWorkFlow", "synpred7_ComaWorkFlow", "synpred19_ComaWorkFlow", 
        "synpred15_ComaWorkFlow", "synpred37_ComaWorkFlow", "synpred22_ComaWorkFlow", 
        "synpred9_ComaWorkFlow", "synpred31_ComaWorkFlow", "synpred14_ComaWorkFlow", 
        "selectionParameter", "synpred4_ComaWorkFlow", "workflow", "synpred29_ComaWorkFlow", 
        "synpred36_ComaWorkFlow"
    };
    public static final boolean[] decisionCanBacktrack = new boolean[] {
        false, // invalid decision
        false, false, false, false, true, false, true, false, false, false, 
            false, false, true, false, false, false, false, false, false, 
            false, false, false, false, false, false, false
    };

     
        public int ruleLevel = 0;
        public int getRuleLevel() { return ruleLevel; }
        public void incRuleLevel() { ruleLevel++; }
        public void decRuleLevel() { ruleLevel--; }
        public ComaWorkFlowParser(TokenStream input) {
            this(input, DebugEventSocketProxy.DEFAULT_DEBUGGER_PORT, new RecognizerSharedState());
        }
        public ComaWorkFlowParser(TokenStream input, int port, RecognizerSharedState state) {
            super(input, state);
            DebugEventSocketProxy proxy =
                new DebugEventSocketProxy(this,port,adaptor);
            setDebugListener(proxy);
            setTokenStream(new DebugTokenStream(input,proxy));
            try {
                proxy.handshake();
            }
            catch (IOException ioe) {
                reportError(ioe);
            }
            TreeAdaptor adap = new CommonTreeAdaptor();
            setTreeAdaptor(adap);
            proxy.setTreeAdaptor(adap);
        }
    public ComaWorkFlowParser(TokenStream input, DebugEventListener dbg) {
        super(input, dbg);

         
        TreeAdaptor adap = new CommonTreeAdaptor();
        setTreeAdaptor(adap);

    }
    protected boolean evalPredicate(boolean result, String predicate) {
        dbg.semanticPredicate(result, predicate);
        return result;
    }

    protected DebugTreeAdaptor adaptor;
    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = new DebugTreeAdaptor(dbg,adaptor);

    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }


    public String[] getTokenNames() { return ComaWorkFlowParser.tokenNames; }
    public String getGrammarFileName() { return "C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g"; }


    public static class coma_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "coma"
    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:37:2: coma : ( workflow | strategy | complexMatcher | matcher );
    public final ComaWorkFlowParser.coma_return coma() throws RecognitionException {
        ComaWorkFlowParser.coma_return retval = new ComaWorkFlowParser.coma_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        ComaWorkFlowParser.workflow_return workflow1 = null;

        ComaWorkFlowParser.strategy_return strategy2 = null;

        ComaWorkFlowParser.complexMatcher_return complexMatcher3 = null;

        ComaWorkFlowParser.matcher_return matcher4 = null;



        try { dbg.enterRule(getGrammarFileName(), "coma");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(37, 2);

        try {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:37:7: ( workflow | strategy | complexMatcher | matcher )
            int alt1=4;
            try { dbg.enterDecision(1, decisionCanBacktrack[1]);

            int LA1_0 = input.LA(1);

            if ( (LA1_0==CHAR_BRACE_LEFT) ) {
                switch ( input.LA(2) ) {
                case CHAR_BRACE_LEFT:
                case DIGIT:
                case V2:
                    {
                    alt1=1;
                    }
                    break;
                case RESOLUTION_1:
                    {
                    alt1=2;
                    }
                    break;
                case RESOLUTION_2:
                    {
                    alt1=3;
                    }
                    break;
                case RESOLUTION_3:
                    {
                    alt1=4;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 1, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }

            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(1);}

            switch (alt1) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:37:11: workflow
                    {
                    root_0 = (Object)adaptor.nil();

                    dbg.location(37,11);
                    pushFollow(FOLLOW_workflow_in_coma120);
                    workflow1=workflow();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, workflow1.getTree());

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:37:22: strategy
                    {
                    root_0 = (Object)adaptor.nil();

                    dbg.location(37,22);
                    pushFollow(FOLLOW_strategy_in_coma124);
                    strategy2=strategy();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, strategy2.getTree());

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:37:33: complexMatcher
                    {
                    root_0 = (Object)adaptor.nil();

                    dbg.location(37,33);
                    pushFollow(FOLLOW_complexMatcher_in_coma128);
                    complexMatcher3=complexMatcher();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, complexMatcher3.getTree());

                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:37:50: matcher
                    {
                    root_0 = (Object)adaptor.nil();

                    dbg.location(37,50);
                    pushFollow(FOLLOW_matcher_in_coma132);
                    matcher4=matcher();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, matcher4.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        dbg.location(37, 57);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "coma");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "coma"

    public static class workflow_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "workflow"
    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:39:2: workflow : CHAR_BRACE_LEFT ( ( strategy CHAR_SEMICOLON strategy ( CHAR_SEMICOLON selection )? ) | ( strategy ( CHAR_COMMA strategy )+ ( CHAR_SEMICOLON RESULT_COMBINATION )? ) | ( strategy ) | ( reuse ) ) CHAR_BRACE_RIGHT ;
    public final ComaWorkFlowParser.workflow_return workflow() throws RecognitionException {
        ComaWorkFlowParser.workflow_return retval = new ComaWorkFlowParser.workflow_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CHAR_BRACE_LEFT5=null;
        Token CHAR_SEMICOLON7=null;
        Token CHAR_SEMICOLON9=null;
        Token CHAR_COMMA12=null;
        Token CHAR_SEMICOLON14=null;
        Token RESULT_COMBINATION15=null;
        Token CHAR_BRACE_RIGHT18=null;
        ComaWorkFlowParser.strategy_return strategy6 = null;

        ComaWorkFlowParser.strategy_return strategy8 = null;

        ComaWorkFlowParser.selection_return selection10 = null;

        ComaWorkFlowParser.strategy_return strategy11 = null;

        ComaWorkFlowParser.strategy_return strategy13 = null;

        ComaWorkFlowParser.strategy_return strategy16 = null;

        ComaWorkFlowParser.reuse_return reuse17 = null;


        Object CHAR_BRACE_LEFT5_tree=null;
        Object CHAR_SEMICOLON7_tree=null;
        Object CHAR_SEMICOLON9_tree=null;
        Object CHAR_COMMA12_tree=null;
        Object CHAR_SEMICOLON14_tree=null;
        Object RESULT_COMBINATION15_tree=null;
        Object CHAR_BRACE_RIGHT18_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "workflow");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(39, 2);

        try {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:39:11: ( CHAR_BRACE_LEFT ( ( strategy CHAR_SEMICOLON strategy ( CHAR_SEMICOLON selection )? ) | ( strategy ( CHAR_COMMA strategy )+ ( CHAR_SEMICOLON RESULT_COMBINATION )? ) | ( strategy ) | ( reuse ) ) CHAR_BRACE_RIGHT )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:40:5: CHAR_BRACE_LEFT ( ( strategy CHAR_SEMICOLON strategy ( CHAR_SEMICOLON selection )? ) | ( strategy ( CHAR_COMMA strategy )+ ( CHAR_SEMICOLON RESULT_COMBINATION )? ) | ( strategy ) | ( reuse ) ) CHAR_BRACE_RIGHT
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(40,5);
            CHAR_BRACE_LEFT5=(Token)match(input,CHAR_BRACE_LEFT,FOLLOW_CHAR_BRACE_LEFT_in_workflow149); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_BRACE_LEFT5_tree = (Object)adaptor.create(CHAR_BRACE_LEFT5);
            adaptor.addChild(root_0, CHAR_BRACE_LEFT5_tree);
            }
            dbg.location(41,6);
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:41:6: ( ( strategy CHAR_SEMICOLON strategy ( CHAR_SEMICOLON selection )? ) | ( strategy ( CHAR_COMMA strategy )+ ( CHAR_SEMICOLON RESULT_COMBINATION )? ) | ( strategy ) | ( reuse ) )
            int alt5=4;
            try { dbg.enterSubRule(5);
            try { dbg.enterDecision(5, decisionCanBacktrack[5]);

            int LA5_0 = input.LA(1);

            if ( (LA5_0==CHAR_BRACE_LEFT) ) {
                int LA5_1 = input.LA(2);

                if ( (synpred5_ComaWorkFlow()) ) {
                    alt5=1;
                }
                else if ( (synpred8_ComaWorkFlow()) ) {
                    alt5=2;
                }
                else if ( (synpred9_ComaWorkFlow()) ) {
                    alt5=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else if ( ((LA5_0>=DIGIT && LA5_0<=V2)) ) {
                alt5=4;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(5);}

            switch (alt5) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:42:7: ( strategy CHAR_SEMICOLON strategy ( CHAR_SEMICOLON selection )? )
                    {
                    dbg.location(42,7);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:42:7: ( strategy CHAR_SEMICOLON strategy ( CHAR_SEMICOLON selection )? )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:42:9: strategy CHAR_SEMICOLON strategy ( CHAR_SEMICOLON selection )?
                    {
                    dbg.location(42,9);
                    pushFollow(FOLLOW_strategy_in_workflow169);
                    strategy6=strategy();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, strategy6.getTree());
                    dbg.location(42,19);
                    CHAR_SEMICOLON7=(Token)match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_workflow172); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_SEMICOLON7_tree = (Object)adaptor.create(CHAR_SEMICOLON7);
                    adaptor.addChild(root_0, CHAR_SEMICOLON7_tree);
                    }
                    dbg.location(42,34);
                    pushFollow(FOLLOW_strategy_in_workflow174);
                    strategy8=strategy();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, strategy8.getTree());
                    dbg.location(42,43);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:42:43: ( CHAR_SEMICOLON selection )?
                    int alt2=2;
                    try { dbg.enterSubRule(2);
                    try { dbg.enterDecision(2, decisionCanBacktrack[2]);

                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==CHAR_SEMICOLON) ) {
                        alt2=1;
                    }
                    } finally {dbg.exitDecision(2);}

                    switch (alt2) {
                        case 1 :
                            dbg.enterAlt(1);

                            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:42:44: CHAR_SEMICOLON selection
                            {
                            dbg.location(42,44);
                            CHAR_SEMICOLON9=(Token)match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_workflow177); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            CHAR_SEMICOLON9_tree = (Object)adaptor.create(CHAR_SEMICOLON9);
                            adaptor.addChild(root_0, CHAR_SEMICOLON9_tree);
                            }
                            dbg.location(42,59);
                            pushFollow(FOLLOW_selection_in_workflow179);
                            selection10=selection();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, selection10.getTree());

                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(2);}


                    }


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:7: ( strategy ( CHAR_COMMA strategy )+ ( CHAR_SEMICOLON RESULT_COMBINATION )? )
                    {
                    dbg.location(44,7);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:7: ( strategy ( CHAR_COMMA strategy )+ ( CHAR_SEMICOLON RESULT_COMBINATION )? )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:9: strategy ( CHAR_COMMA strategy )+ ( CHAR_SEMICOLON RESULT_COMBINATION )?
                    {
                    dbg.location(44,9);
                    pushFollow(FOLLOW_strategy_in_workflow200);
                    strategy11=strategy();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, strategy11.getTree());
                    dbg.location(44,18);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:18: ( CHAR_COMMA strategy )+
                    int cnt3=0;
                    try { dbg.enterSubRule(3);

                    loop3:
                    do {
                        int alt3=2;
                        try { dbg.enterDecision(3, decisionCanBacktrack[3]);

                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==CHAR_COMMA) ) {
                            alt3=1;
                        }


                        } finally {dbg.exitDecision(3);}

                        switch (alt3) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:19: CHAR_COMMA strategy
                    	    {
                    	    dbg.location(44,19);
                    	    CHAR_COMMA12=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_workflow203); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    CHAR_COMMA12_tree = (Object)adaptor.create(CHAR_COMMA12);
                    	    adaptor.addChild(root_0, CHAR_COMMA12_tree);
                    	    }
                    	    dbg.location(44,30);
                    	    pushFollow(FOLLOW_strategy_in_workflow205);
                    	    strategy13=strategy();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, strategy13.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt3 >= 1 ) break loop3;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(3, input);
                                dbg.recognitionException(eee);

                                throw eee;
                        }
                        cnt3++;
                    } while (true);
                    } finally {dbg.exitSubRule(3);}

                    dbg.location(44,41);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:41: ( CHAR_SEMICOLON RESULT_COMBINATION )?
                    int alt4=2;
                    try { dbg.enterSubRule(4);
                    try { dbg.enterDecision(4, decisionCanBacktrack[4]);

                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==CHAR_SEMICOLON) ) {
                        alt4=1;
                    }
                    } finally {dbg.exitDecision(4);}

                    switch (alt4) {
                        case 1 :
                            dbg.enterAlt(1);

                            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:42: CHAR_SEMICOLON RESULT_COMBINATION
                            {
                            dbg.location(44,42);
                            CHAR_SEMICOLON14=(Token)match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_workflow210); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            CHAR_SEMICOLON14_tree = (Object)adaptor.create(CHAR_SEMICOLON14);
                            adaptor.addChild(root_0, CHAR_SEMICOLON14_tree);
                            }
                            dbg.location(44,57);
                            RESULT_COMBINATION15=(Token)match(input,RESULT_COMBINATION,FOLLOW_RESULT_COMBINATION_in_workflow212); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            RESULT_COMBINATION15_tree = (Object)adaptor.create(RESULT_COMBINATION15);
                            adaptor.addChild(root_0, RESULT_COMBINATION15_tree);
                            }

                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(4);}


                    }


                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:46:7: ( strategy )
                    {
                    dbg.location(46,7);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:46:7: ( strategy )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:46:8: strategy
                    {
                    dbg.location(46,8);
                    pushFollow(FOLLOW_strategy_in_workflow234);
                    strategy16=strategy();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, strategy16.getTree());

                    }


                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:48:7: ( reuse )
                    {
                    dbg.location(48,7);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:48:7: ( reuse )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:48:8: reuse
                    {
                    dbg.location(48,8);
                    pushFollow(FOLLOW_reuse_in_workflow259);
                    reuse17=reuse();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, reuse17.getTree());

                    }


                    }
                    break;

            }
            } finally {dbg.exitSubRule(5);}

            dbg.location(50,5);
            CHAR_BRACE_RIGHT18=(Token)match(input,CHAR_BRACE_RIGHT,FOLLOW_CHAR_BRACE_RIGHT_in_workflow273); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_BRACE_RIGHT18_tree = (Object)adaptor.create(CHAR_BRACE_RIGHT18);
            adaptor.addChild(root_0, CHAR_BRACE_RIGHT18_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        dbg.location(50, 21);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "workflow");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "workflow"

    public static class strategy_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "strategy"
    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:56:2: strategy : CHAR_BRACE_LEFT ( RESOLUTION_1 CHAR_SEMICOLON ( ( ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) ) | ( complexMatcher ) ) ( CHAR_SEMICOLON selection )? ) ) CHAR_BRACE_RIGHT ;
    public final ComaWorkFlowParser.strategy_return strategy() throws RecognitionException {
        ComaWorkFlowParser.strategy_return retval = new ComaWorkFlowParser.strategy_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CHAR_BRACE_LEFT19=null;
        Token RESOLUTION_120=null;
        Token CHAR_SEMICOLON21=null;
        Token CHAR_COMMA23=null;
        Token CHAR_SEMICOLON25=null;
        Token CHAR_SEMICOLON28=null;
        Token CHAR_BRACE_RIGHT30=null;
        ComaWorkFlowParser.complexMatcher_return complexMatcher22 = null;

        ComaWorkFlowParser.complexMatcher_return complexMatcher24 = null;

        ComaWorkFlowParser.similarityCombination_return similarityCombination26 = null;

        ComaWorkFlowParser.complexMatcher_return complexMatcher27 = null;

        ComaWorkFlowParser.selection_return selection29 = null;


        Object CHAR_BRACE_LEFT19_tree=null;
        Object RESOLUTION_120_tree=null;
        Object CHAR_SEMICOLON21_tree=null;
        Object CHAR_COMMA23_tree=null;
        Object CHAR_SEMICOLON25_tree=null;
        Object CHAR_SEMICOLON28_tree=null;
        Object CHAR_BRACE_RIGHT30_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "strategy");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(56, 2);

        try {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:56:11: ( CHAR_BRACE_LEFT ( RESOLUTION_1 CHAR_SEMICOLON ( ( ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) ) | ( complexMatcher ) ) ( CHAR_SEMICOLON selection )? ) ) CHAR_BRACE_RIGHT )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:58:5: CHAR_BRACE_LEFT ( RESOLUTION_1 CHAR_SEMICOLON ( ( ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) ) | ( complexMatcher ) ) ( CHAR_SEMICOLON selection )? ) ) CHAR_BRACE_RIGHT
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(58,5);
            CHAR_BRACE_LEFT19=(Token)match(input,CHAR_BRACE_LEFT,FOLLOW_CHAR_BRACE_LEFT_in_strategy307); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_BRACE_LEFT19_tree = (Object)adaptor.create(CHAR_BRACE_LEFT19);
            adaptor.addChild(root_0, CHAR_BRACE_LEFT19_tree);
            }
            dbg.location(59,6);
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:59:6: ( RESOLUTION_1 CHAR_SEMICOLON ( ( ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) ) | ( complexMatcher ) ) ( CHAR_SEMICOLON selection )? ) )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:59:8: RESOLUTION_1 CHAR_SEMICOLON ( ( ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) ) | ( complexMatcher ) ) ( CHAR_SEMICOLON selection )? )
            {
            dbg.location(59,8);
            RESOLUTION_120=(Token)match(input,RESOLUTION_1,FOLLOW_RESOLUTION_1_in_strategy318); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RESOLUTION_120_tree = (Object)adaptor.create(RESOLUTION_120);
            adaptor.addChild(root_0, RESOLUTION_120_tree);
            }
            dbg.location(60,7);
            CHAR_SEMICOLON21=(Token)match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_strategy327); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_SEMICOLON21_tree = (Object)adaptor.create(CHAR_SEMICOLON21);
            adaptor.addChild(root_0, CHAR_SEMICOLON21_tree);
            }
            dbg.location(61,7);
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:61:7: ( ( ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) ) | ( complexMatcher ) ) ( CHAR_SEMICOLON selection )? )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:62:8: ( ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) ) | ( complexMatcher ) ) ( CHAR_SEMICOLON selection )?
            {
            dbg.location(62,8);
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:62:8: ( ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) ) | ( complexMatcher ) )
            int alt7=2;
            try { dbg.enterSubRule(7);
            try { dbg.enterDecision(7, decisionCanBacktrack[7]);

            int LA7_0 = input.LA(1);

            if ( (LA7_0==CHAR_BRACE_LEFT) ) {
                int LA7_1 = input.LA(2);

                if ( (synpred11_ComaWorkFlow()) ) {
                    alt7=1;
                }
                else if ( (true) ) {
                    alt7=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(7);}

            switch (alt7) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:9: ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) )
                    {
                    dbg.location(63,9);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:9: ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:10: complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination )
                    {
                    dbg.location(63,10);
                    pushFollow(FOLLOW_complexMatcher_in_strategy357);
                    complexMatcher22=complexMatcher();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, complexMatcher22.getTree());
                    dbg.location(63,25);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:25: ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:26: ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination
                    {
                    dbg.location(63,26);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:26: ( CHAR_COMMA complexMatcher )+
                    int cnt6=0;
                    try { dbg.enterSubRule(6);

                    loop6:
                    do {
                        int alt6=2;
                        try { dbg.enterDecision(6, decisionCanBacktrack[6]);

                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==CHAR_COMMA) ) {
                            alt6=1;
                        }


                        } finally {dbg.exitDecision(6);}

                        switch (alt6) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:27: CHAR_COMMA complexMatcher
                    	    {
                    	    dbg.location(63,27);
                    	    CHAR_COMMA23=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_strategy361); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    CHAR_COMMA23_tree = (Object)adaptor.create(CHAR_COMMA23);
                    	    adaptor.addChild(root_0, CHAR_COMMA23_tree);
                    	    }
                    	    dbg.location(63,38);
                    	    pushFollow(FOLLOW_complexMatcher_in_strategy363);
                    	    complexMatcher24=complexMatcher();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, complexMatcher24.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                dbg.recognitionException(eee);

                                throw eee;
                        }
                        cnt6++;
                    } while (true);
                    } finally {dbg.exitSubRule(6);}

                    dbg.location(63,55);
                    CHAR_SEMICOLON25=(Token)match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_strategy367); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_SEMICOLON25_tree = (Object)adaptor.create(CHAR_SEMICOLON25);
                    adaptor.addChild(root_0, CHAR_SEMICOLON25_tree);
                    }
                    dbg.location(63,70);
                    pushFollow(FOLLOW_similarityCombination_in_strategy369);
                    similarityCombination26=similarityCombination();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, similarityCombination26.getTree());

                    }


                    }


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:65:9: ( complexMatcher )
                    {
                    dbg.location(65,9);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:65:9: ( complexMatcher )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:65:10: complexMatcher
                    {
                    dbg.location(65,10);
                    pushFollow(FOLLOW_complexMatcher_in_strategy393);
                    complexMatcher27=complexMatcher();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, complexMatcher27.getTree());

                    }


                    }
                    break;

            }
            } finally {dbg.exitSubRule(7);}

            dbg.location(67,8);
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:67:8: ( CHAR_SEMICOLON selection )?
            int alt8=2;
            try { dbg.enterSubRule(8);
            try { dbg.enterDecision(8, decisionCanBacktrack[8]);

            int LA8_0 = input.LA(1);

            if ( (LA8_0==CHAR_SEMICOLON) ) {
                alt8=1;
            }
            } finally {dbg.exitDecision(8);}

            switch (alt8) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:67:9: CHAR_SEMICOLON selection
                    {
                    dbg.location(67,9);
                    CHAR_SEMICOLON28=(Token)match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_strategy417); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_SEMICOLON28_tree = (Object)adaptor.create(CHAR_SEMICOLON28);
                    adaptor.addChild(root_0, CHAR_SEMICOLON28_tree);
                    }
                    dbg.location(67,24);
                    pushFollow(FOLLOW_selection_in_strategy419);
                    selection29=selection();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, selection29.getTree());

                    }
                    break;

            }
            } finally {dbg.exitSubRule(8);}


            }


            }

            dbg.location(70,5);
            CHAR_BRACE_RIGHT30=(Token)match(input,CHAR_BRACE_RIGHT,FOLLOW_CHAR_BRACE_RIGHT_in_strategy447); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_BRACE_RIGHT30_tree = (Object)adaptor.create(CHAR_BRACE_RIGHT30);
            adaptor.addChild(root_0, CHAR_BRACE_RIGHT30_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        dbg.location(70, 21);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "strategy");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "strategy"

    public static class complexMatcher_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "complexMatcher"
    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:75:2: complexMatcher : CHAR_BRACE_LEFT ( RESOLUTION_2 CHAR_SEMICOLON ( ( ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) ) | ( matcher | complexMatcher ) ) CHAR_SEMICOLON SETCOMBINATION ) ) CHAR_BRACE_RIGHT ;
    public final ComaWorkFlowParser.complexMatcher_return complexMatcher() throws RecognitionException {
        ComaWorkFlowParser.complexMatcher_return retval = new ComaWorkFlowParser.complexMatcher_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CHAR_BRACE_LEFT31=null;
        Token RESOLUTION_232=null;
        Token CHAR_SEMICOLON33=null;
        Token CHAR_COMMA36=null;
        Token CHAR_SEMICOLON39=null;
        Token CHAR_SEMICOLON43=null;
        Token SETCOMBINATION44=null;
        Token CHAR_BRACE_RIGHT45=null;
        ComaWorkFlowParser.matcher_return matcher34 = null;

        ComaWorkFlowParser.complexMatcher_return complexMatcher35 = null;

        ComaWorkFlowParser.matcher_return matcher37 = null;

        ComaWorkFlowParser.complexMatcher_return complexMatcher38 = null;

        ComaWorkFlowParser.similarityCombination_return similarityCombination40 = null;

        ComaWorkFlowParser.matcher_return matcher41 = null;

        ComaWorkFlowParser.complexMatcher_return complexMatcher42 = null;


        Object CHAR_BRACE_LEFT31_tree=null;
        Object RESOLUTION_232_tree=null;
        Object CHAR_SEMICOLON33_tree=null;
        Object CHAR_COMMA36_tree=null;
        Object CHAR_SEMICOLON39_tree=null;
        Object CHAR_SEMICOLON43_tree=null;
        Object SETCOMBINATION44_tree=null;
        Object CHAR_BRACE_RIGHT45_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "complexMatcher");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(75, 2);

        try {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:75:16: ( CHAR_BRACE_LEFT ( RESOLUTION_2 CHAR_SEMICOLON ( ( ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) ) | ( matcher | complexMatcher ) ) CHAR_SEMICOLON SETCOMBINATION ) ) CHAR_BRACE_RIGHT )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:78:5: CHAR_BRACE_LEFT ( RESOLUTION_2 CHAR_SEMICOLON ( ( ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) ) | ( matcher | complexMatcher ) ) CHAR_SEMICOLON SETCOMBINATION ) ) CHAR_BRACE_RIGHT
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(78,5);
            CHAR_BRACE_LEFT31=(Token)match(input,CHAR_BRACE_LEFT,FOLLOW_CHAR_BRACE_LEFT_in_complexMatcher476); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_BRACE_LEFT31_tree = (Object)adaptor.create(CHAR_BRACE_LEFT31);
            adaptor.addChild(root_0, CHAR_BRACE_LEFT31_tree);
            }
            dbg.location(79,6);
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:79:6: ( RESOLUTION_2 CHAR_SEMICOLON ( ( ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) ) | ( matcher | complexMatcher ) ) CHAR_SEMICOLON SETCOMBINATION ) )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:79:8: RESOLUTION_2 CHAR_SEMICOLON ( ( ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) ) | ( matcher | complexMatcher ) ) CHAR_SEMICOLON SETCOMBINATION )
            {
            dbg.location(79,8);
            RESOLUTION_232=(Token)match(input,RESOLUTION_2,FOLLOW_RESOLUTION_2_in_complexMatcher487); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RESOLUTION_232_tree = (Object)adaptor.create(RESOLUTION_232);
            adaptor.addChild(root_0, RESOLUTION_232_tree);
            }
            dbg.location(80,7);
            CHAR_SEMICOLON33=(Token)match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_complexMatcher496); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_SEMICOLON33_tree = (Object)adaptor.create(CHAR_SEMICOLON33);
            adaptor.addChild(root_0, CHAR_SEMICOLON33_tree);
            }
            dbg.location(81,7);
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:81:7: ( ( ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) ) | ( matcher | complexMatcher ) ) CHAR_SEMICOLON SETCOMBINATION )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:82:8: ( ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) ) | ( matcher | complexMatcher ) ) CHAR_SEMICOLON SETCOMBINATION
            {
            dbg.location(82,8);
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:82:8: ( ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) ) | ( matcher | complexMatcher ) )
            int alt13=2;
            try { dbg.enterSubRule(13);
            try { dbg.enterDecision(13, decisionCanBacktrack[13]);

            int LA13_0 = input.LA(1);

            if ( (LA13_0==CHAR_BRACE_LEFT) ) {
                int LA13_1 = input.LA(2);

                if ( (synpred16_ComaWorkFlow()) ) {
                    alt13=1;
                }
                else if ( (true) ) {
                    alt13=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(13);}

            switch (alt13) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:9: ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) )
                    {
                    dbg.location(83,9);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:9: ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:11: ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination )
                    {
                    dbg.location(83,11);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:11: ( matcher | complexMatcher )
                    int alt9=2;
                    try { dbg.enterSubRule(9);
                    try { dbg.enterDecision(9, decisionCanBacktrack[9]);

                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==CHAR_BRACE_LEFT) ) {
                        int LA9_1 = input.LA(2);

                        if ( (LA9_1==RESOLUTION_3) ) {
                            alt9=1;
                        }
                        else if ( (LA9_1==RESOLUTION_2) ) {
                            alt9=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 9, 1, input);

                            dbg.recognitionException(nvae);
                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 0, input);

                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                    } finally {dbg.exitDecision(9);}

                    switch (alt9) {
                        case 1 :
                            dbg.enterAlt(1);

                            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:12: matcher
                            {
                            dbg.location(83,12);
                            pushFollow(FOLLOW_matcher_in_complexMatcher529);
                            matcher34=matcher();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, matcher34.getTree());

                            }
                            break;
                        case 2 :
                            dbg.enterAlt(2);

                            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:22: complexMatcher
                            {
                            dbg.location(83,22);
                            pushFollow(FOLLOW_complexMatcher_in_complexMatcher533);
                            complexMatcher35=complexMatcher();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, complexMatcher35.getTree());

                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(9);}

                    dbg.location(83,38);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:38: ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:39: ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination
                    {
                    dbg.location(83,39);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:39: ( CHAR_COMMA ( matcher | complexMatcher ) )+
                    int cnt11=0;
                    try { dbg.enterSubRule(11);

                    loop11:
                    do {
                        int alt11=2;
                        try { dbg.enterDecision(11, decisionCanBacktrack[11]);

                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==CHAR_COMMA) ) {
                            alt11=1;
                        }


                        } finally {dbg.exitDecision(11);}

                        switch (alt11) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:40: CHAR_COMMA ( matcher | complexMatcher )
                    	    {
                    	    dbg.location(83,40);
                    	    CHAR_COMMA36=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_complexMatcher538); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    CHAR_COMMA36_tree = (Object)adaptor.create(CHAR_COMMA36);
                    	    adaptor.addChild(root_0, CHAR_COMMA36_tree);
                    	    }
                    	    dbg.location(83,51);
                    	    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:51: ( matcher | complexMatcher )
                    	    int alt10=2;
                    	    try { dbg.enterSubRule(10);
                    	    try { dbg.enterDecision(10, decisionCanBacktrack[10]);

                    	    int LA10_0 = input.LA(1);

                    	    if ( (LA10_0==CHAR_BRACE_LEFT) ) {
                    	        int LA10_1 = input.LA(2);

                    	        if ( (LA10_1==RESOLUTION_3) ) {
                    	            alt10=1;
                    	        }
                    	        else if ( (LA10_1==RESOLUTION_2) ) {
                    	            alt10=2;
                    	        }
                    	        else {
                    	            if (state.backtracking>0) {state.failed=true; return retval;}
                    	            NoViableAltException nvae =
                    	                new NoViableAltException("", 10, 1, input);

                    	            dbg.recognitionException(nvae);
                    	            throw nvae;
                    	        }
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return retval;}
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("", 10, 0, input);

                    	        dbg.recognitionException(nvae);
                    	        throw nvae;
                    	    }
                    	    } finally {dbg.exitDecision(10);}

                    	    switch (alt10) {
                    	        case 1 :
                    	            dbg.enterAlt(1);

                    	            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:52: matcher
                    	            {
                    	            dbg.location(83,52);
                    	            pushFollow(FOLLOW_matcher_in_complexMatcher541);
                    	            matcher37=matcher();

                    	            state._fsp--;
                    	            if (state.failed) return retval;
                    	            if ( state.backtracking==0 ) adaptor.addChild(root_0, matcher37.getTree());

                    	            }
                    	            break;
                    	        case 2 :
                    	            dbg.enterAlt(2);

                    	            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:62: complexMatcher
                    	            {
                    	            dbg.location(83,62);
                    	            pushFollow(FOLLOW_complexMatcher_in_complexMatcher545);
                    	            complexMatcher38=complexMatcher();

                    	            state._fsp--;
                    	            if (state.failed) return retval;
                    	            if ( state.backtracking==0 ) adaptor.addChild(root_0, complexMatcher38.getTree());

                    	            }
                    	            break;

                    	    }
                    	    } finally {dbg.exitSubRule(10);}


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                dbg.recognitionException(eee);

                                throw eee;
                        }
                        cnt11++;
                    } while (true);
                    } finally {dbg.exitSubRule(11);}

                    dbg.location(83,80);
                    CHAR_SEMICOLON39=(Token)match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_complexMatcher550); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_SEMICOLON39_tree = (Object)adaptor.create(CHAR_SEMICOLON39);
                    adaptor.addChild(root_0, CHAR_SEMICOLON39_tree);
                    }
                    dbg.location(83,95);
                    pushFollow(FOLLOW_similarityCombination_in_complexMatcher552);
                    similarityCombination40=similarityCombination();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, similarityCombination40.getTree());

                    }


                    }


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:85:9: ( matcher | complexMatcher )
                    {
                    dbg.location(85,9);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:85:9: ( matcher | complexMatcher )
                    int alt12=2;
                    try { dbg.enterSubRule(12);
                    try { dbg.enterDecision(12, decisionCanBacktrack[12]);

                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==CHAR_BRACE_LEFT) ) {
                        int LA12_1 = input.LA(2);

                        if ( (LA12_1==RESOLUTION_3) ) {
                            alt12=1;
                        }
                        else if ( (LA12_1==RESOLUTION_2) ) {
                            alt12=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 12, 1, input);

                            dbg.recognitionException(nvae);
                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 12, 0, input);

                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                    } finally {dbg.exitDecision(12);}

                    switch (alt12) {
                        case 1 :
                            dbg.enterAlt(1);

                            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:85:10: matcher
                            {
                            dbg.location(85,10);
                            pushFollow(FOLLOW_matcher_in_complexMatcher575);
                            matcher41=matcher();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, matcher41.getTree());

                            }
                            break;
                        case 2 :
                            dbg.enterAlt(2);

                            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:85:20: complexMatcher
                            {
                            dbg.location(85,20);
                            pushFollow(FOLLOW_complexMatcher_in_complexMatcher579);
                            complexMatcher42=complexMatcher();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, complexMatcher42.getTree());

                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(12);}


                    }
                    break;

            }
            } finally {dbg.exitSubRule(13);}

            dbg.location(87,8);
            CHAR_SEMICOLON43=(Token)match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_complexMatcher601); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_SEMICOLON43_tree = (Object)adaptor.create(CHAR_SEMICOLON43);
            adaptor.addChild(root_0, CHAR_SEMICOLON43_tree);
            }
            dbg.location(87,23);
            SETCOMBINATION44=(Token)match(input,SETCOMBINATION,FOLLOW_SETCOMBINATION_in_complexMatcher603); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            SETCOMBINATION44_tree = (Object)adaptor.create(SETCOMBINATION44);
            adaptor.addChild(root_0, SETCOMBINATION44_tree);
            }

            }


            }

            dbg.location(90,5);
            CHAR_BRACE_RIGHT45=(Token)match(input,CHAR_BRACE_RIGHT,FOLLOW_CHAR_BRACE_RIGHT_in_complexMatcher627); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_BRACE_RIGHT45_tree = (Object)adaptor.create(CHAR_BRACE_RIGHT45);
            adaptor.addChild(root_0, CHAR_BRACE_RIGHT45_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        dbg.location(90, 21);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "complexMatcher");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "complexMatcher"

    public static class matcher_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "matcher"
    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:95:2: matcher : CHAR_BRACE_LEFT ( RESOLUTION_3 CHAR_SEMICOLON ( ( SIMMEASURE | ( SIMMEASURE ( ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination ) ) ) CHAR_SEMICOLON SETCOMBINATION ) ) CHAR_BRACE_RIGHT ;
    public final ComaWorkFlowParser.matcher_return matcher() throws RecognitionException {
        ComaWorkFlowParser.matcher_return retval = new ComaWorkFlowParser.matcher_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CHAR_BRACE_LEFT46=null;
        Token RESOLUTION_347=null;
        Token CHAR_SEMICOLON48=null;
        Token SIMMEASURE49=null;
        Token SIMMEASURE50=null;
        Token CHAR_COMMA51=null;
        Token SIMMEASURE52=null;
        Token CHAR_SEMICOLON53=null;
        Token CHAR_SEMICOLON55=null;
        Token SETCOMBINATION56=null;
        Token CHAR_BRACE_RIGHT57=null;
        ComaWorkFlowParser.similarityCombination_return similarityCombination54 = null;


        Object CHAR_BRACE_LEFT46_tree=null;
        Object RESOLUTION_347_tree=null;
        Object CHAR_SEMICOLON48_tree=null;
        Object SIMMEASURE49_tree=null;
        Object SIMMEASURE50_tree=null;
        Object CHAR_COMMA51_tree=null;
        Object SIMMEASURE52_tree=null;
        Object CHAR_SEMICOLON53_tree=null;
        Object CHAR_SEMICOLON55_tree=null;
        Object SETCOMBINATION56_tree=null;
        Object CHAR_BRACE_RIGHT57_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "matcher");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(95, 2);

        try {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:95:10: ( CHAR_BRACE_LEFT ( RESOLUTION_3 CHAR_SEMICOLON ( ( SIMMEASURE | ( SIMMEASURE ( ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination ) ) ) CHAR_SEMICOLON SETCOMBINATION ) ) CHAR_BRACE_RIGHT )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:95:13: CHAR_BRACE_LEFT ( RESOLUTION_3 CHAR_SEMICOLON ( ( SIMMEASURE | ( SIMMEASURE ( ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination ) ) ) CHAR_SEMICOLON SETCOMBINATION ) ) CHAR_BRACE_RIGHT
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(95,13);
            CHAR_BRACE_LEFT46=(Token)match(input,CHAR_BRACE_LEFT,FOLLOW_CHAR_BRACE_LEFT_in_matcher644); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_BRACE_LEFT46_tree = (Object)adaptor.create(CHAR_BRACE_LEFT46);
            adaptor.addChild(root_0, CHAR_BRACE_LEFT46_tree);
            }
            dbg.location(96,6);
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:96:6: ( RESOLUTION_3 CHAR_SEMICOLON ( ( SIMMEASURE | ( SIMMEASURE ( ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination ) ) ) CHAR_SEMICOLON SETCOMBINATION ) )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:96:8: RESOLUTION_3 CHAR_SEMICOLON ( ( SIMMEASURE | ( SIMMEASURE ( ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination ) ) ) CHAR_SEMICOLON SETCOMBINATION )
            {
            dbg.location(96,8);
            RESOLUTION_347=(Token)match(input,RESOLUTION_3,FOLLOW_RESOLUTION_3_in_matcher655); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RESOLUTION_347_tree = (Object)adaptor.create(RESOLUTION_347);
            adaptor.addChild(root_0, RESOLUTION_347_tree);
            }
            dbg.location(97,7);
            CHAR_SEMICOLON48=(Token)match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_matcher664); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_SEMICOLON48_tree = (Object)adaptor.create(CHAR_SEMICOLON48);
            adaptor.addChild(root_0, CHAR_SEMICOLON48_tree);
            }
            dbg.location(98,7);
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:98:7: ( ( SIMMEASURE | ( SIMMEASURE ( ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination ) ) ) CHAR_SEMICOLON SETCOMBINATION )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:99:8: ( SIMMEASURE | ( SIMMEASURE ( ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination ) ) ) CHAR_SEMICOLON SETCOMBINATION
            {
            dbg.location(99,8);
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:99:8: ( SIMMEASURE | ( SIMMEASURE ( ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination ) ) )
            int alt15=2;
            try { dbg.enterSubRule(15);
            try { dbg.enterDecision(15, decisionCanBacktrack[15]);

            int LA15_0 = input.LA(1);

            if ( (LA15_0==SIMMEASURE) ) {
                int LA15_1 = input.LA(2);

                if ( (LA15_1==CHAR_SEMICOLON) ) {
                    alt15=1;
                }
                else if ( (LA15_1==CHAR_COMMA) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(15);}

            switch (alt15) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:100:9: SIMMEASURE
                    {
                    dbg.location(100,9);
                    SIMMEASURE49=(Token)match(input,SIMMEASURE,FOLLOW_SIMMEASURE_in_matcher693); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SIMMEASURE49_tree = (Object)adaptor.create(SIMMEASURE49);
                    adaptor.addChild(root_0, SIMMEASURE49_tree);
                    }

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:102:9: ( SIMMEASURE ( ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination ) )
                    {
                    dbg.location(102,9);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:102:9: ( SIMMEASURE ( ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination ) )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:102:10: SIMMEASURE ( ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination )
                    {
                    dbg.location(102,10);
                    SIMMEASURE50=(Token)match(input,SIMMEASURE,FOLLOW_SIMMEASURE_in_matcher716); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SIMMEASURE50_tree = (Object)adaptor.create(SIMMEASURE50);
                    adaptor.addChild(root_0, SIMMEASURE50_tree);
                    }
                    dbg.location(102,21);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:102:21: ( ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:102:22: ( CHAR_COMMA SIMMEASURE )+ CHAR_SEMICOLON similarityCombination
                    {
                    dbg.location(102,22);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:102:22: ( CHAR_COMMA SIMMEASURE )+
                    int cnt14=0;
                    try { dbg.enterSubRule(14);

                    loop14:
                    do {
                        int alt14=2;
                        try { dbg.enterDecision(14, decisionCanBacktrack[14]);

                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==CHAR_COMMA) ) {
                            alt14=1;
                        }


                        } finally {dbg.exitDecision(14);}

                        switch (alt14) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:102:23: CHAR_COMMA SIMMEASURE
                    	    {
                    	    dbg.location(102,23);
                    	    CHAR_COMMA51=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_matcher720); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    CHAR_COMMA51_tree = (Object)adaptor.create(CHAR_COMMA51);
                    	    adaptor.addChild(root_0, CHAR_COMMA51_tree);
                    	    }
                    	    dbg.location(102,34);
                    	    SIMMEASURE52=(Token)match(input,SIMMEASURE,FOLLOW_SIMMEASURE_in_matcher722); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    SIMMEASURE52_tree = (Object)adaptor.create(SIMMEASURE52);
                    	    adaptor.addChild(root_0, SIMMEASURE52_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt14 >= 1 ) break loop14;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(14, input);
                                dbg.recognitionException(eee);

                                throw eee;
                        }
                        cnt14++;
                    } while (true);
                    } finally {dbg.exitSubRule(14);}

                    dbg.location(102,47);
                    CHAR_SEMICOLON53=(Token)match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_matcher726); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_SEMICOLON53_tree = (Object)adaptor.create(CHAR_SEMICOLON53);
                    adaptor.addChild(root_0, CHAR_SEMICOLON53_tree);
                    }
                    dbg.location(102,62);
                    pushFollow(FOLLOW_similarityCombination_in_matcher728);
                    similarityCombination54=similarityCombination();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, similarityCombination54.getTree());

                    }


                    }


                    }
                    break;

            }
            } finally {dbg.exitSubRule(15);}

            dbg.location(104,7);
            CHAR_SEMICOLON55=(Token)match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_matcher748); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_SEMICOLON55_tree = (Object)adaptor.create(CHAR_SEMICOLON55);
            adaptor.addChild(root_0, CHAR_SEMICOLON55_tree);
            }
            dbg.location(104,22);
            SETCOMBINATION56=(Token)match(input,SETCOMBINATION,FOLLOW_SETCOMBINATION_in_matcher750); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            SETCOMBINATION56_tree = (Object)adaptor.create(SETCOMBINATION56);
            adaptor.addChild(root_0, SETCOMBINATION56_tree);
            }

            }


            }

            dbg.location(107,5);
            CHAR_BRACE_RIGHT57=(Token)match(input,CHAR_BRACE_RIGHT,FOLLOW_CHAR_BRACE_RIGHT_in_matcher776); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_BRACE_RIGHT57_tree = (Object)adaptor.create(CHAR_BRACE_RIGHT57);
            adaptor.addChild(root_0, CHAR_BRACE_RIGHT57_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        dbg.location(107, 21);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "matcher");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "matcher"

    public static class reuse_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "reuse"
    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:110:2: reuse : ( DIGIT | V2 ) CHAR_COMMA ( DIGIT | V2 ) CHAR_COMMA BOOLEAN CHAR_COMMA COMPOSITION CHAR_COMMA SIMCOMBINATION1 CHAR_COMMA BOOLEAN ;
    public final ComaWorkFlowParser.reuse_return reuse() throws RecognitionException {
        ComaWorkFlowParser.reuse_return retval = new ComaWorkFlowParser.reuse_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set58=null;
        Token CHAR_COMMA59=null;
        Token set60=null;
        Token CHAR_COMMA61=null;
        Token BOOLEAN62=null;
        Token CHAR_COMMA63=null;
        Token COMPOSITION64=null;
        Token CHAR_COMMA65=null;
        Token SIMCOMBINATION166=null;
        Token CHAR_COMMA67=null;
        Token BOOLEAN68=null;

        Object set58_tree=null;
        Object CHAR_COMMA59_tree=null;
        Object set60_tree=null;
        Object CHAR_COMMA61_tree=null;
        Object BOOLEAN62_tree=null;
        Object CHAR_COMMA63_tree=null;
        Object COMPOSITION64_tree=null;
        Object CHAR_COMMA65_tree=null;
        Object SIMCOMBINATION166_tree=null;
        Object CHAR_COMMA67_tree=null;
        Object BOOLEAN68_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "reuse");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(110, 2);

        try {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:110:8: ( ( DIGIT | V2 ) CHAR_COMMA ( DIGIT | V2 ) CHAR_COMMA BOOLEAN CHAR_COMMA COMPOSITION CHAR_COMMA SIMCOMBINATION1 CHAR_COMMA BOOLEAN )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:110:11: ( DIGIT | V2 ) CHAR_COMMA ( DIGIT | V2 ) CHAR_COMMA BOOLEAN CHAR_COMMA COMPOSITION CHAR_COMMA SIMCOMBINATION1 CHAR_COMMA BOOLEAN
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(110,11);
            set58=(Token)input.LT(1);
            if ( (input.LA(1)>=DIGIT && input.LA(1)<=V2) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set58));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                dbg.recognitionException(mse);
                throw mse;
            }

            dbg.location(110,24);
            CHAR_COMMA59=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_reuse798); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_COMMA59_tree = (Object)adaptor.create(CHAR_COMMA59);
            adaptor.addChild(root_0, CHAR_COMMA59_tree);
            }
            dbg.location(110,35);
            set60=(Token)input.LT(1);
            if ( (input.LA(1)>=DIGIT && input.LA(1)<=V2) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set60));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                dbg.recognitionException(mse);
                throw mse;
            }

            dbg.location(110,48);
            CHAR_COMMA61=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_reuse808); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_COMMA61_tree = (Object)adaptor.create(CHAR_COMMA61);
            adaptor.addChild(root_0, CHAR_COMMA61_tree);
            }
            dbg.location(110,59);
            BOOLEAN62=(Token)match(input,BOOLEAN,FOLLOW_BOOLEAN_in_reuse810); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            BOOLEAN62_tree = (Object)adaptor.create(BOOLEAN62);
            adaptor.addChild(root_0, BOOLEAN62_tree);
            }
            dbg.location(110,67);
            CHAR_COMMA63=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_reuse812); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_COMMA63_tree = (Object)adaptor.create(CHAR_COMMA63);
            adaptor.addChild(root_0, CHAR_COMMA63_tree);
            }
            dbg.location(110,78);
            COMPOSITION64=(Token)match(input,COMPOSITION,FOLLOW_COMPOSITION_in_reuse814); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            COMPOSITION64_tree = (Object)adaptor.create(COMPOSITION64);
            adaptor.addChild(root_0, COMPOSITION64_tree);
            }
            dbg.location(110,90);
            CHAR_COMMA65=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_reuse816); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_COMMA65_tree = (Object)adaptor.create(CHAR_COMMA65);
            adaptor.addChild(root_0, CHAR_COMMA65_tree);
            }
            dbg.location(110,101);
            SIMCOMBINATION166=(Token)match(input,SIMCOMBINATION1,FOLLOW_SIMCOMBINATION1_in_reuse818); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            SIMCOMBINATION166_tree = (Object)adaptor.create(SIMCOMBINATION166);
            adaptor.addChild(root_0, SIMCOMBINATION166_tree);
            }
            dbg.location(110,117);
            CHAR_COMMA67=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_reuse820); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_COMMA67_tree = (Object)adaptor.create(CHAR_COMMA67);
            adaptor.addChild(root_0, CHAR_COMMA67_tree);
            }
            dbg.location(110,128);
            BOOLEAN68=(Token)match(input,BOOLEAN,FOLLOW_BOOLEAN_in_reuse822); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            BOOLEAN68_tree = (Object)adaptor.create(BOOLEAN68);
            adaptor.addChild(root_0, BOOLEAN68_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        dbg.location(110, 135);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "reuse");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "reuse"

    public static class selection_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "selection"
    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:119:2: selection : CHAR_BRACE_LEFT DIRECTION CHAR_COMMA selectionParameter CHAR_BRACE_RIGHT ;
    public final ComaWorkFlowParser.selection_return selection() throws RecognitionException {
        ComaWorkFlowParser.selection_return retval = new ComaWorkFlowParser.selection_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CHAR_BRACE_LEFT69=null;
        Token DIRECTION70=null;
        Token CHAR_COMMA71=null;
        Token CHAR_BRACE_RIGHT73=null;
        ComaWorkFlowParser.selectionParameter_return selectionParameter72 = null;


        Object CHAR_BRACE_LEFT69_tree=null;
        Object DIRECTION70_tree=null;
        Object CHAR_COMMA71_tree=null;
        Object CHAR_BRACE_RIGHT73_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "selection");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(119, 2);

        try {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:119:12: ( CHAR_BRACE_LEFT DIRECTION CHAR_COMMA selectionParameter CHAR_BRACE_RIGHT )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:119:15: CHAR_BRACE_LEFT DIRECTION CHAR_COMMA selectionParameter CHAR_BRACE_RIGHT
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(119,15);
            CHAR_BRACE_LEFT69=(Token)match(input,CHAR_BRACE_LEFT,FOLLOW_CHAR_BRACE_LEFT_in_selection844); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_BRACE_LEFT69_tree = (Object)adaptor.create(CHAR_BRACE_LEFT69);
            adaptor.addChild(root_0, CHAR_BRACE_LEFT69_tree);
            }
            dbg.location(119,31);
            DIRECTION70=(Token)match(input,DIRECTION,FOLLOW_DIRECTION_in_selection846); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            DIRECTION70_tree = (Object)adaptor.create(DIRECTION70);
            adaptor.addChild(root_0, DIRECTION70_tree);
            }
            dbg.location(119,41);
            CHAR_COMMA71=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_selection848); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_COMMA71_tree = (Object)adaptor.create(CHAR_COMMA71);
            adaptor.addChild(root_0, CHAR_COMMA71_tree);
            }
            dbg.location(119,52);
            pushFollow(FOLLOW_selectionParameter_in_selection850);
            selectionParameter72=selectionParameter();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, selectionParameter72.getTree());
            dbg.location(119,71);
            CHAR_BRACE_RIGHT73=(Token)match(input,CHAR_BRACE_RIGHT,FOLLOW_CHAR_BRACE_RIGHT_in_selection852); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CHAR_BRACE_RIGHT73_tree = (Object)adaptor.create(CHAR_BRACE_RIGHT73);
            adaptor.addChild(root_0, CHAR_BRACE_RIGHT73_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        dbg.location(119, 87);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "selection");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "selection"

    public static class selectionParameter_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "selectionParameter"
    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:121:2: selectionParameter : ( ( SELECTION_THRESHOLD CHAR_BRACE_LEFT V1 CHAR_BRACE_RIGHT ) | ( SELECTION_MAXDELTA CHAR_BRACE_LEFT V1 CHAR_BRACE_RIGHT ) | ( SELECTION_MAXN CHAR_BRACE_LEFT ( DIGIT | V2 ) CHAR_BRACE_RIGHT ) | ( SELECTION_MULTIPLE CHAR_BRACE_LEFT ( DIGIT )+ CHAR_COMMA V1 CHAR_COMMA V1 CHAR_BRACE_RIGHT ) );
    public final ComaWorkFlowParser.selectionParameter_return selectionParameter() throws RecognitionException {
        ComaWorkFlowParser.selectionParameter_return retval = new ComaWorkFlowParser.selectionParameter_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SELECTION_THRESHOLD74=null;
        Token CHAR_BRACE_LEFT75=null;
        Token V176=null;
        Token CHAR_BRACE_RIGHT77=null;
        Token SELECTION_MAXDELTA78=null;
        Token CHAR_BRACE_LEFT79=null;
        Token V180=null;
        Token CHAR_BRACE_RIGHT81=null;
        Token SELECTION_MAXN82=null;
        Token CHAR_BRACE_LEFT83=null;
        Token set84=null;
        Token CHAR_BRACE_RIGHT85=null;
        Token SELECTION_MULTIPLE86=null;
        Token CHAR_BRACE_LEFT87=null;
        Token DIGIT88=null;
        Token CHAR_COMMA89=null;
        Token V190=null;
        Token CHAR_COMMA91=null;
        Token V192=null;
        Token CHAR_BRACE_RIGHT93=null;

        Object SELECTION_THRESHOLD74_tree=null;
        Object CHAR_BRACE_LEFT75_tree=null;
        Object V176_tree=null;
        Object CHAR_BRACE_RIGHT77_tree=null;
        Object SELECTION_MAXDELTA78_tree=null;
        Object CHAR_BRACE_LEFT79_tree=null;
        Object V180_tree=null;
        Object CHAR_BRACE_RIGHT81_tree=null;
        Object SELECTION_MAXN82_tree=null;
        Object CHAR_BRACE_LEFT83_tree=null;
        Object set84_tree=null;
        Object CHAR_BRACE_RIGHT85_tree=null;
        Object SELECTION_MULTIPLE86_tree=null;
        Object CHAR_BRACE_LEFT87_tree=null;
        Object DIGIT88_tree=null;
        Object CHAR_COMMA89_tree=null;
        Object V190_tree=null;
        Object CHAR_COMMA91_tree=null;
        Object V192_tree=null;
        Object CHAR_BRACE_RIGHT93_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "selectionParameter");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(121, 2);

        try {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:121:21: ( ( SELECTION_THRESHOLD CHAR_BRACE_LEFT V1 CHAR_BRACE_RIGHT ) | ( SELECTION_MAXDELTA CHAR_BRACE_LEFT V1 CHAR_BRACE_RIGHT ) | ( SELECTION_MAXN CHAR_BRACE_LEFT ( DIGIT | V2 ) CHAR_BRACE_RIGHT ) | ( SELECTION_MULTIPLE CHAR_BRACE_LEFT ( DIGIT )+ CHAR_COMMA V1 CHAR_COMMA V1 CHAR_BRACE_RIGHT ) )
            int alt17=4;
            try { dbg.enterDecision(17, decisionCanBacktrack[17]);

            switch ( input.LA(1) ) {
            case SELECTION_THRESHOLD:
                {
                alt17=1;
                }
                break;
            case SELECTION_MAXDELTA:
                {
                alt17=2;
                }
                break;
            case SELECTION_MAXN:
                {
                alt17=3;
                }
                break;
            case SELECTION_MULTIPLE:
                {
                alt17=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(17);}

            switch (alt17) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:121:23: ( SELECTION_THRESHOLD CHAR_BRACE_LEFT V1 CHAR_BRACE_RIGHT )
                    {
                    root_0 = (Object)adaptor.nil();

                    dbg.location(121,23);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:121:23: ( SELECTION_THRESHOLD CHAR_BRACE_LEFT V1 CHAR_BRACE_RIGHT )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:121:25: SELECTION_THRESHOLD CHAR_BRACE_LEFT V1 CHAR_BRACE_RIGHT
                    {
                    dbg.location(121,25);
                    SELECTION_THRESHOLD74=(Token)match(input,SELECTION_THRESHOLD,FOLLOW_SELECTION_THRESHOLD_in_selectionParameter864); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SELECTION_THRESHOLD74_tree = (Object)adaptor.create(SELECTION_THRESHOLD74);
                    adaptor.addChild(root_0, SELECTION_THRESHOLD74_tree);
                    }
                    dbg.location(121,45);
                    CHAR_BRACE_LEFT75=(Token)match(input,CHAR_BRACE_LEFT,FOLLOW_CHAR_BRACE_LEFT_in_selectionParameter866); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_BRACE_LEFT75_tree = (Object)adaptor.create(CHAR_BRACE_LEFT75);
                    adaptor.addChild(root_0, CHAR_BRACE_LEFT75_tree);
                    }
                    dbg.location(121,61);
                    V176=(Token)match(input,V1,FOLLOW_V1_in_selectionParameter868); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    V176_tree = (Object)adaptor.create(V176);
                    adaptor.addChild(root_0, V176_tree);
                    }
                    dbg.location(121,64);
                    CHAR_BRACE_RIGHT77=(Token)match(input,CHAR_BRACE_RIGHT,FOLLOW_CHAR_BRACE_RIGHT_in_selectionParameter870); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_BRACE_RIGHT77_tree = (Object)adaptor.create(CHAR_BRACE_RIGHT77);
                    adaptor.addChild(root_0, CHAR_BRACE_RIGHT77_tree);
                    }

                    }


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:122:5: ( SELECTION_MAXDELTA CHAR_BRACE_LEFT V1 CHAR_BRACE_RIGHT )
                    {
                    root_0 = (Object)adaptor.nil();

                    dbg.location(122,5);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:122:5: ( SELECTION_MAXDELTA CHAR_BRACE_LEFT V1 CHAR_BRACE_RIGHT )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:122:7: SELECTION_MAXDELTA CHAR_BRACE_LEFT V1 CHAR_BRACE_RIGHT
                    {
                    dbg.location(122,7);
                    SELECTION_MAXDELTA78=(Token)match(input,SELECTION_MAXDELTA,FOLLOW_SELECTION_MAXDELTA_in_selectionParameter882); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SELECTION_MAXDELTA78_tree = (Object)adaptor.create(SELECTION_MAXDELTA78);
                    adaptor.addChild(root_0, SELECTION_MAXDELTA78_tree);
                    }
                    dbg.location(122,26);
                    CHAR_BRACE_LEFT79=(Token)match(input,CHAR_BRACE_LEFT,FOLLOW_CHAR_BRACE_LEFT_in_selectionParameter884); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_BRACE_LEFT79_tree = (Object)adaptor.create(CHAR_BRACE_LEFT79);
                    adaptor.addChild(root_0, CHAR_BRACE_LEFT79_tree);
                    }
                    dbg.location(122,42);
                    V180=(Token)match(input,V1,FOLLOW_V1_in_selectionParameter886); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    V180_tree = (Object)adaptor.create(V180);
                    adaptor.addChild(root_0, V180_tree);
                    }
                    dbg.location(122,45);
                    CHAR_BRACE_RIGHT81=(Token)match(input,CHAR_BRACE_RIGHT,FOLLOW_CHAR_BRACE_RIGHT_in_selectionParameter888); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_BRACE_RIGHT81_tree = (Object)adaptor.create(CHAR_BRACE_RIGHT81);
                    adaptor.addChild(root_0, CHAR_BRACE_RIGHT81_tree);
                    }

                    }


                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:123:5: ( SELECTION_MAXN CHAR_BRACE_LEFT ( DIGIT | V2 ) CHAR_BRACE_RIGHT )
                    {
                    root_0 = (Object)adaptor.nil();

                    dbg.location(123,5);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:123:5: ( SELECTION_MAXN CHAR_BRACE_LEFT ( DIGIT | V2 ) CHAR_BRACE_RIGHT )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:123:7: SELECTION_MAXN CHAR_BRACE_LEFT ( DIGIT | V2 ) CHAR_BRACE_RIGHT
                    {
                    dbg.location(123,7);
                    SELECTION_MAXN82=(Token)match(input,SELECTION_MAXN,FOLLOW_SELECTION_MAXN_in_selectionParameter900); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SELECTION_MAXN82_tree = (Object)adaptor.create(SELECTION_MAXN82);
                    adaptor.addChild(root_0, SELECTION_MAXN82_tree);
                    }
                    dbg.location(123,22);
                    CHAR_BRACE_LEFT83=(Token)match(input,CHAR_BRACE_LEFT,FOLLOW_CHAR_BRACE_LEFT_in_selectionParameter902); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_BRACE_LEFT83_tree = (Object)adaptor.create(CHAR_BRACE_LEFT83);
                    adaptor.addChild(root_0, CHAR_BRACE_LEFT83_tree);
                    }
                    dbg.location(123,38);
                    set84=(Token)input.LT(1);
                    if ( (input.LA(1)>=DIGIT && input.LA(1)<=V2) ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set84));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        dbg.recognitionException(mse);
                        throw mse;
                    }

                    dbg.location(123,51);
                    CHAR_BRACE_RIGHT85=(Token)match(input,CHAR_BRACE_RIGHT,FOLLOW_CHAR_BRACE_RIGHT_in_selectionParameter912); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_BRACE_RIGHT85_tree = (Object)adaptor.create(CHAR_BRACE_RIGHT85);
                    adaptor.addChild(root_0, CHAR_BRACE_RIGHT85_tree);
                    }

                    }


                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:124:5: ( SELECTION_MULTIPLE CHAR_BRACE_LEFT ( DIGIT )+ CHAR_COMMA V1 CHAR_COMMA V1 CHAR_BRACE_RIGHT )
                    {
                    root_0 = (Object)adaptor.nil();

                    dbg.location(124,5);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:124:5: ( SELECTION_MULTIPLE CHAR_BRACE_LEFT ( DIGIT )+ CHAR_COMMA V1 CHAR_COMMA V1 CHAR_BRACE_RIGHT )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:124:7: SELECTION_MULTIPLE CHAR_BRACE_LEFT ( DIGIT )+ CHAR_COMMA V1 CHAR_COMMA V1 CHAR_BRACE_RIGHT
                    {
                    dbg.location(124,7);
                    SELECTION_MULTIPLE86=(Token)match(input,SELECTION_MULTIPLE,FOLLOW_SELECTION_MULTIPLE_in_selectionParameter924); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SELECTION_MULTIPLE86_tree = (Object)adaptor.create(SELECTION_MULTIPLE86);
                    adaptor.addChild(root_0, SELECTION_MULTIPLE86_tree);
                    }
                    dbg.location(124,26);
                    CHAR_BRACE_LEFT87=(Token)match(input,CHAR_BRACE_LEFT,FOLLOW_CHAR_BRACE_LEFT_in_selectionParameter926); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_BRACE_LEFT87_tree = (Object)adaptor.create(CHAR_BRACE_LEFT87);
                    adaptor.addChild(root_0, CHAR_BRACE_LEFT87_tree);
                    }
                    dbg.location(124,42);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:124:42: ( DIGIT )+
                    int cnt16=0;
                    try { dbg.enterSubRule(16);

                    loop16:
                    do {
                        int alt16=2;
                        try { dbg.enterDecision(16, decisionCanBacktrack[16]);

                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==DIGIT) ) {
                            alt16=1;
                        }


                        } finally {dbg.exitDecision(16);}

                        switch (alt16) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:0:0: DIGIT
                    	    {
                    	    dbg.location(124,42);
                    	    DIGIT88=(Token)match(input,DIGIT,FOLLOW_DIGIT_in_selectionParameter928); if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) {
                    	    DIGIT88_tree = (Object)adaptor.create(DIGIT88);
                    	    adaptor.addChild(root_0, DIGIT88_tree);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt16 >= 1 ) break loop16;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(16, input);
                                dbg.recognitionException(eee);

                                throw eee;
                        }
                        cnt16++;
                    } while (true);
                    } finally {dbg.exitSubRule(16);}

                    dbg.location(124,49);
                    CHAR_COMMA89=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_selectionParameter931); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_COMMA89_tree = (Object)adaptor.create(CHAR_COMMA89);
                    adaptor.addChild(root_0, CHAR_COMMA89_tree);
                    }
                    dbg.location(124,60);
                    V190=(Token)match(input,V1,FOLLOW_V1_in_selectionParameter933); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    V190_tree = (Object)adaptor.create(V190);
                    adaptor.addChild(root_0, V190_tree);
                    }
                    dbg.location(124,63);
                    CHAR_COMMA91=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_selectionParameter935); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_COMMA91_tree = (Object)adaptor.create(CHAR_COMMA91);
                    adaptor.addChild(root_0, CHAR_COMMA91_tree);
                    }
                    dbg.location(124,74);
                    V192=(Token)match(input,V1,FOLLOW_V1_in_selectionParameter937); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    V192_tree = (Object)adaptor.create(V192);
                    adaptor.addChild(root_0, V192_tree);
                    }
                    dbg.location(124,77);
                    CHAR_BRACE_RIGHT93=(Token)match(input,CHAR_BRACE_RIGHT,FOLLOW_CHAR_BRACE_RIGHT_in_selectionParameter939); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_BRACE_RIGHT93_tree = (Object)adaptor.create(CHAR_BRACE_RIGHT93);
                    adaptor.addChild(root_0, CHAR_BRACE_RIGHT93_tree);
                    }

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        dbg.location(124, 94);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "selectionParameter");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "selectionParameter"

    public static class similarityCombination_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "similarityCombination"
    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:126:2: similarityCombination : ( SIMCOMBINATION1 | ( SIMCOMBINATION2 CHAR_BRACE_LEFT V1 CHAR_COMMA V1 CHAR_BRACE_RIGHT ) );
    public final ComaWorkFlowParser.similarityCombination_return similarityCombination() throws RecognitionException {
        ComaWorkFlowParser.similarityCombination_return retval = new ComaWorkFlowParser.similarityCombination_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SIMCOMBINATION194=null;
        Token SIMCOMBINATION295=null;
        Token CHAR_BRACE_LEFT96=null;
        Token V197=null;
        Token CHAR_COMMA98=null;
        Token V199=null;
        Token CHAR_BRACE_RIGHT100=null;

        Object SIMCOMBINATION194_tree=null;
        Object SIMCOMBINATION295_tree=null;
        Object CHAR_BRACE_LEFT96_tree=null;
        Object V197_tree=null;
        Object CHAR_COMMA98_tree=null;
        Object V199_tree=null;
        Object CHAR_BRACE_RIGHT100_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "similarityCombination");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(126, 2);

        try {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:126:24: ( SIMCOMBINATION1 | ( SIMCOMBINATION2 CHAR_BRACE_LEFT V1 CHAR_COMMA V1 CHAR_BRACE_RIGHT ) )
            int alt18=2;
            try { dbg.enterDecision(18, decisionCanBacktrack[18]);

            int LA18_0 = input.LA(1);

            if ( (LA18_0==SIMCOMBINATION1) ) {
                alt18=1;
            }
            else if ( (LA18_0==SIMCOMBINATION2) ) {
                alt18=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(18);}

            switch (alt18) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:126:26: SIMCOMBINATION1
                    {
                    root_0 = (Object)adaptor.nil();

                    dbg.location(126,26);
                    SIMCOMBINATION194=(Token)match(input,SIMCOMBINATION1,FOLLOW_SIMCOMBINATION1_in_similarityCombination954); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SIMCOMBINATION194_tree = (Object)adaptor.create(SIMCOMBINATION194);
                    adaptor.addChild(root_0, SIMCOMBINATION194_tree);
                    }

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:126:44: ( SIMCOMBINATION2 CHAR_BRACE_LEFT V1 CHAR_COMMA V1 CHAR_BRACE_RIGHT )
                    {
                    root_0 = (Object)adaptor.nil();

                    dbg.location(126,44);
                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:126:44: ( SIMCOMBINATION2 CHAR_BRACE_LEFT V1 CHAR_COMMA V1 CHAR_BRACE_RIGHT )
                    dbg.enterAlt(1);

                    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:126:46: SIMCOMBINATION2 CHAR_BRACE_LEFT V1 CHAR_COMMA V1 CHAR_BRACE_RIGHT
                    {
                    dbg.location(126,46);
                    SIMCOMBINATION295=(Token)match(input,SIMCOMBINATION2,FOLLOW_SIMCOMBINATION2_in_similarityCombination960); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SIMCOMBINATION295_tree = (Object)adaptor.create(SIMCOMBINATION295);
                    adaptor.addChild(root_0, SIMCOMBINATION295_tree);
                    }
                    dbg.location(126,62);
                    CHAR_BRACE_LEFT96=(Token)match(input,CHAR_BRACE_LEFT,FOLLOW_CHAR_BRACE_LEFT_in_similarityCombination962); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_BRACE_LEFT96_tree = (Object)adaptor.create(CHAR_BRACE_LEFT96);
                    adaptor.addChild(root_0, CHAR_BRACE_LEFT96_tree);
                    }
                    dbg.location(126,78);
                    V197=(Token)match(input,V1,FOLLOW_V1_in_similarityCombination964); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    V197_tree = (Object)adaptor.create(V197);
                    adaptor.addChild(root_0, V197_tree);
                    }
                    dbg.location(126,81);
                    CHAR_COMMA98=(Token)match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_similarityCombination966); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_COMMA98_tree = (Object)adaptor.create(CHAR_COMMA98);
                    adaptor.addChild(root_0, CHAR_COMMA98_tree);
                    }
                    dbg.location(126,92);
                    V199=(Token)match(input,V1,FOLLOW_V1_in_similarityCombination968); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    V199_tree = (Object)adaptor.create(V199);
                    adaptor.addChild(root_0, V199_tree);
                    }
                    dbg.location(126,95);
                    CHAR_BRACE_RIGHT100=(Token)match(input,CHAR_BRACE_RIGHT,FOLLOW_CHAR_BRACE_RIGHT_in_similarityCombination970); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHAR_BRACE_RIGHT100_tree = (Object)adaptor.create(CHAR_BRACE_RIGHT100);
                    adaptor.addChild(root_0, CHAR_BRACE_RIGHT100_tree);
                    }

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        dbg.location(126, 112);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "similarityCombination");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "similarityCombination"

    public static class allowedToken_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "allowedToken"
    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:135:2: allowedToken : ( RESOLUTION_1 | RESOLUTION_2 | RESOLUTION_3 | SIMMEASURE | SETCOMBINATION | COMPOSITION | SIMCOMBINATION1 | SIMCOMBINATION2 | RESULT_COMBINATION | DIRECTION | SELECTION_THRESHOLD | SELECTION_MAXDELTA | SELECTION_MAXN | SELECTION_MULTIPLE );
    public final ComaWorkFlowParser.allowedToken_return allowedToken() throws RecognitionException {
        ComaWorkFlowParser.allowedToken_return retval = new ComaWorkFlowParser.allowedToken_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set101=null;

        Object set101_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "allowedToken");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(135, 2);

        try {
            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:135:15: ( RESOLUTION_1 | RESOLUTION_2 | RESOLUTION_3 | SIMMEASURE | SETCOMBINATION | COMPOSITION | SIMCOMBINATION1 | SIMCOMBINATION2 | RESULT_COMBINATION | DIRECTION | SELECTION_THRESHOLD | SELECTION_MAXDELTA | SELECTION_MAXN | SELECTION_MULTIPLE )
            dbg.enterAlt(1);

            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(135,15);
            set101=(Token)input.LT(1);
            if ( (input.LA(1)>=RESULT_COMBINATION && input.LA(1)<=SIMMEASURE)||(input.LA(1)>=COMPOSITION && input.LA(1)<=SELECTION_THRESHOLD)||(input.LA(1)>=SELECTION_MAXDELTA && input.LA(1)<=SIMCOMBINATION2) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set101));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                dbg.recognitionException(mse);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        dbg.location(137, 96);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "allowedToken");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "allowedToken"

    // $ANTLR start synpred5_ComaWorkFlow
    public final void synpred5_ComaWorkFlow_fragment() throws RecognitionException {   
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:42:7: ( ( strategy CHAR_SEMICOLON strategy ( CHAR_SEMICOLON selection )? ) )
        dbg.enterAlt(1);

        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:42:7: ( strategy CHAR_SEMICOLON strategy ( CHAR_SEMICOLON selection )? )
        {
        dbg.location(42,7);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:42:7: ( strategy CHAR_SEMICOLON strategy ( CHAR_SEMICOLON selection )? )
        dbg.enterAlt(1);

        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:42:9: strategy CHAR_SEMICOLON strategy ( CHAR_SEMICOLON selection )?
        {
        dbg.location(42,9);
        pushFollow(FOLLOW_strategy_in_synpred5_ComaWorkFlow169);
        strategy();

        state._fsp--;
        if (state.failed) return ;
        dbg.location(42,19);
        match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_synpred5_ComaWorkFlow172); if (state.failed) return ;
        dbg.location(42,34);
        pushFollow(FOLLOW_strategy_in_synpred5_ComaWorkFlow174);
        strategy();

        state._fsp--;
        if (state.failed) return ;
        dbg.location(42,43);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:42:43: ( CHAR_SEMICOLON selection )?
        int alt19=2;
        try { dbg.enterSubRule(19);
        try { dbg.enterDecision(19, decisionCanBacktrack[19]);

        int LA19_0 = input.LA(1);

        if ( (LA19_0==CHAR_SEMICOLON) ) {
            alt19=1;
        }
        } finally {dbg.exitDecision(19);}

        switch (alt19) {
            case 1 :
                dbg.enterAlt(1);

                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:42:44: CHAR_SEMICOLON selection
                {
                dbg.location(42,44);
                match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_synpred5_ComaWorkFlow177); if (state.failed) return ;
                dbg.location(42,59);
                pushFollow(FOLLOW_selection_in_synpred5_ComaWorkFlow179);
                selection();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }
        } finally {dbg.exitSubRule(19);}


        }


        }
    }
    // $ANTLR end synpred5_ComaWorkFlow

    // $ANTLR start synpred8_ComaWorkFlow
    public final void synpred8_ComaWorkFlow_fragment() throws RecognitionException {   
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:7: ( ( strategy ( CHAR_COMMA strategy )+ ( CHAR_SEMICOLON RESULT_COMBINATION )? ) )
        dbg.enterAlt(1);

        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:7: ( strategy ( CHAR_COMMA strategy )+ ( CHAR_SEMICOLON RESULT_COMBINATION )? )
        {
        dbg.location(44,7);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:7: ( strategy ( CHAR_COMMA strategy )+ ( CHAR_SEMICOLON RESULT_COMBINATION )? )
        dbg.enterAlt(1);

        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:9: strategy ( CHAR_COMMA strategy )+ ( CHAR_SEMICOLON RESULT_COMBINATION )?
        {
        dbg.location(44,9);
        pushFollow(FOLLOW_strategy_in_synpred8_ComaWorkFlow200);
        strategy();

        state._fsp--;
        if (state.failed) return ;
        dbg.location(44,18);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:18: ( CHAR_COMMA strategy )+
        int cnt20=0;
        try { dbg.enterSubRule(20);

        loop20:
        do {
            int alt20=2;
            try { dbg.enterDecision(20, decisionCanBacktrack[20]);

            int LA20_0 = input.LA(1);

            if ( (LA20_0==CHAR_COMMA) ) {
                alt20=1;
            }


            } finally {dbg.exitDecision(20);}

            switch (alt20) {
        	case 1 :
        	    dbg.enterAlt(1);

        	    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:19: CHAR_COMMA strategy
        	    {
        	    dbg.location(44,19);
        	    match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_synpred8_ComaWorkFlow203); if (state.failed) return ;
        	    dbg.location(44,30);
        	    pushFollow(FOLLOW_strategy_in_synpred8_ComaWorkFlow205);
        	    strategy();

        	    state._fsp--;
        	    if (state.failed) return ;

        	    }
        	    break;

        	default :
        	    if ( cnt20 >= 1 ) break loop20;
        	    if (state.backtracking>0) {state.failed=true; return ;}
                    EarlyExitException eee =
                        new EarlyExitException(20, input);
                    dbg.recognitionException(eee);

                    throw eee;
            }
            cnt20++;
        } while (true);
        } finally {dbg.exitSubRule(20);}

        dbg.location(44,41);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:41: ( CHAR_SEMICOLON RESULT_COMBINATION )?
        int alt21=2;
        try { dbg.enterSubRule(21);
        try { dbg.enterDecision(21, decisionCanBacktrack[21]);

        int LA21_0 = input.LA(1);

        if ( (LA21_0==CHAR_SEMICOLON) ) {
            alt21=1;
        }
        } finally {dbg.exitDecision(21);}

        switch (alt21) {
            case 1 :
                dbg.enterAlt(1);

                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:44:42: CHAR_SEMICOLON RESULT_COMBINATION
                {
                dbg.location(44,42);
                match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_synpred8_ComaWorkFlow210); if (state.failed) return ;
                dbg.location(44,57);
                match(input,RESULT_COMBINATION,FOLLOW_RESULT_COMBINATION_in_synpred8_ComaWorkFlow212); if (state.failed) return ;

                }
                break;

        }
        } finally {dbg.exitSubRule(21);}


        }


        }
    }
    // $ANTLR end synpred8_ComaWorkFlow

    // $ANTLR start synpred9_ComaWorkFlow
    public final void synpred9_ComaWorkFlow_fragment() throws RecognitionException {   
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:46:7: ( ( strategy ) )
        dbg.enterAlt(1);

        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:46:7: ( strategy )
        {
        dbg.location(46,7);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:46:7: ( strategy )
        dbg.enterAlt(1);

        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:46:8: strategy
        {
        dbg.location(46,8);
        pushFollow(FOLLOW_strategy_in_synpred9_ComaWorkFlow234);
        strategy();

        state._fsp--;
        if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred9_ComaWorkFlow

    // $ANTLR start synpred11_ComaWorkFlow
    public final void synpred11_ComaWorkFlow_fragment() throws RecognitionException {   
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:9: ( ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) ) )
        dbg.enterAlt(1);

        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:9: ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) )
        {
        dbg.location(63,9);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:9: ( complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination ) )
        dbg.enterAlt(1);

        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:10: complexMatcher ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination )
        {
        dbg.location(63,10);
        pushFollow(FOLLOW_complexMatcher_in_synpred11_ComaWorkFlow357);
        complexMatcher();

        state._fsp--;
        if (state.failed) return ;
        dbg.location(63,25);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:25: ( ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination )
        dbg.enterAlt(1);

        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:26: ( CHAR_COMMA complexMatcher )+ CHAR_SEMICOLON similarityCombination
        {
        dbg.location(63,26);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:26: ( CHAR_COMMA complexMatcher )+
        int cnt22=0;
        try { dbg.enterSubRule(22);

        loop22:
        do {
            int alt22=2;
            try { dbg.enterDecision(22, decisionCanBacktrack[22]);

            int LA22_0 = input.LA(1);

            if ( (LA22_0==CHAR_COMMA) ) {
                alt22=1;
            }


            } finally {dbg.exitDecision(22);}

            switch (alt22) {
        	case 1 :
        	    dbg.enterAlt(1);

        	    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:63:27: CHAR_COMMA complexMatcher
        	    {
        	    dbg.location(63,27);
        	    match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_synpred11_ComaWorkFlow361); if (state.failed) return ;
        	    dbg.location(63,38);
        	    pushFollow(FOLLOW_complexMatcher_in_synpred11_ComaWorkFlow363);
        	    complexMatcher();

        	    state._fsp--;
        	    if (state.failed) return ;

        	    }
        	    break;

        	default :
        	    if ( cnt22 >= 1 ) break loop22;
        	    if (state.backtracking>0) {state.failed=true; return ;}
                    EarlyExitException eee =
                        new EarlyExitException(22, input);
                    dbg.recognitionException(eee);

                    throw eee;
            }
            cnt22++;
        } while (true);
        } finally {dbg.exitSubRule(22);}

        dbg.location(63,55);
        match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_synpred11_ComaWorkFlow367); if (state.failed) return ;
        dbg.location(63,70);
        pushFollow(FOLLOW_similarityCombination_in_synpred11_ComaWorkFlow369);
        similarityCombination();

        state._fsp--;
        if (state.failed) return ;

        }


        }


        }
    }
    // $ANTLR end synpred11_ComaWorkFlow

    // $ANTLR start synpred16_ComaWorkFlow
    public final void synpred16_ComaWorkFlow_fragment() throws RecognitionException {   
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:9: ( ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) ) )
        dbg.enterAlt(1);

        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:9: ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) )
        {
        dbg.location(83,9);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:9: ( ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination ) )
        dbg.enterAlt(1);

        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:11: ( matcher | complexMatcher ) ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination )
        {
        dbg.location(83,11);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:11: ( matcher | complexMatcher )
        int alt24=2;
        try { dbg.enterSubRule(24);
        try { dbg.enterDecision(24, decisionCanBacktrack[24]);

        int LA24_0 = input.LA(1);

        if ( (LA24_0==CHAR_BRACE_LEFT) ) {
            int LA24_1 = input.LA(2);

            if ( (LA24_1==RESOLUTION_3) ) {
                alt24=1;
            }
            else if ( (LA24_1==RESOLUTION_2) ) {
                alt24=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 1, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 24, 0, input);

            dbg.recognitionException(nvae);
            throw nvae;
        }
        } finally {dbg.exitDecision(24);}

        switch (alt24) {
            case 1 :
                dbg.enterAlt(1);

                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:12: matcher
                {
                dbg.location(83,12);
                pushFollow(FOLLOW_matcher_in_synpred16_ComaWorkFlow529);
                matcher();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                dbg.enterAlt(2);

                // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:22: complexMatcher
                {
                dbg.location(83,22);
                pushFollow(FOLLOW_complexMatcher_in_synpred16_ComaWorkFlow533);
                complexMatcher();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }
        } finally {dbg.exitSubRule(24);}

        dbg.location(83,38);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:38: ( ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination )
        dbg.enterAlt(1);

        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:39: ( CHAR_COMMA ( matcher | complexMatcher ) )+ CHAR_SEMICOLON similarityCombination
        {
        dbg.location(83,39);
        // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:39: ( CHAR_COMMA ( matcher | complexMatcher ) )+
        int cnt26=0;
        try { dbg.enterSubRule(26);

        loop26:
        do {
            int alt26=2;
            try { dbg.enterDecision(26, decisionCanBacktrack[26]);

            int LA26_0 = input.LA(1);

            if ( (LA26_0==CHAR_COMMA) ) {
                alt26=1;
            }


            } finally {dbg.exitDecision(26);}

            switch (alt26) {
        	case 1 :
        	    dbg.enterAlt(1);

        	    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:40: CHAR_COMMA ( matcher | complexMatcher )
        	    {
        	    dbg.location(83,40);
        	    match(input,CHAR_COMMA,FOLLOW_CHAR_COMMA_in_synpred16_ComaWorkFlow538); if (state.failed) return ;
        	    dbg.location(83,51);
        	    // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:51: ( matcher | complexMatcher )
        	    int alt25=2;
        	    try { dbg.enterSubRule(25);
        	    try { dbg.enterDecision(25, decisionCanBacktrack[25]);

        	    int LA25_0 = input.LA(1);

        	    if ( (LA25_0==CHAR_BRACE_LEFT) ) {
        	        int LA25_1 = input.LA(2);

        	        if ( (LA25_1==RESOLUTION_3) ) {
        	            alt25=1;
        	        }
        	        else if ( (LA25_1==RESOLUTION_2) ) {
        	            alt25=2;
        	        }
        	        else {
        	            if (state.backtracking>0) {state.failed=true; return ;}
        	            NoViableAltException nvae =
        	                new NoViableAltException("", 25, 1, input);

        	            dbg.recognitionException(nvae);
        	            throw nvae;
        	        }
        	    }
        	    else {
        	        if (state.backtracking>0) {state.failed=true; return ;}
        	        NoViableAltException nvae =
        	            new NoViableAltException("", 25, 0, input);

        	        dbg.recognitionException(nvae);
        	        throw nvae;
        	    }
        	    } finally {dbg.exitDecision(25);}

        	    switch (alt25) {
        	        case 1 :
        	            dbg.enterAlt(1);

        	            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:52: matcher
        	            {
        	            dbg.location(83,52);
        	            pushFollow(FOLLOW_matcher_in_synpred16_ComaWorkFlow541);
        	            matcher();

        	            state._fsp--;
        	            if (state.failed) return ;

        	            }
        	            break;
        	        case 2 :
        	            dbg.enterAlt(2);

        	            // C:\\Users\\arnold\\Documents\\grammar\\ComaWorkFlow.g:83:62: complexMatcher
        	            {
        	            dbg.location(83,62);
        	            pushFollow(FOLLOW_complexMatcher_in_synpred16_ComaWorkFlow545);
        	            complexMatcher();

        	            state._fsp--;
        	            if (state.failed) return ;

        	            }
        	            break;

        	    }
        	    } finally {dbg.exitSubRule(25);}


        	    }
        	    break;

        	default :
        	    if ( cnt26 >= 1 ) break loop26;
        	    if (state.backtracking>0) {state.failed=true; return ;}
                    EarlyExitException eee =
                        new EarlyExitException(26, input);
                    dbg.recognitionException(eee);

                    throw eee;
            }
            cnt26++;
        } while (true);
        } finally {dbg.exitSubRule(26);}

        dbg.location(83,80);
        match(input,CHAR_SEMICOLON,FOLLOW_CHAR_SEMICOLON_in_synpred16_ComaWorkFlow550); if (state.failed) return ;
        dbg.location(83,95);
        pushFollow(FOLLOW_similarityCombination_in_synpred16_ComaWorkFlow552);
        similarityCombination();

        state._fsp--;
        if (state.failed) return ;

        }


        }


        }
    }
    // $ANTLR end synpred16_ComaWorkFlow

    // Delegated rules

    public final boolean synpred16_ComaWorkFlow() {
        state.backtracking++;
        dbg.beginBacktrack(state.backtracking);
        int start = input.mark();
        try {
            synpred16_ComaWorkFlow_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        dbg.endBacktrack(state.backtracking, success);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred9_ComaWorkFlow() {
        state.backtracking++;
        dbg.beginBacktrack(state.backtracking);
        int start = input.mark();
        try {
            synpred9_ComaWorkFlow_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        dbg.endBacktrack(state.backtracking, success);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_ComaWorkFlow() {
        state.backtracking++;
        dbg.beginBacktrack(state.backtracking);
        int start = input.mark();
        try {
            synpred5_ComaWorkFlow_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        dbg.endBacktrack(state.backtracking, success);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_ComaWorkFlow() {
        state.backtracking++;
        dbg.beginBacktrack(state.backtracking);
        int start = input.mark();
        try {
            synpred8_ComaWorkFlow_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        dbg.endBacktrack(state.backtracking, success);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred11_ComaWorkFlow() {
        state.backtracking++;
        dbg.beginBacktrack(state.backtracking);
        int start = input.mark();
        try {
            synpred11_ComaWorkFlow_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        dbg.endBacktrack(state.backtracking, success);
        state.backtracking--;
        state.failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_workflow_in_coma120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_strategy_in_coma124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_complexMatcher_in_coma128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_matcher_in_coma132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHAR_BRACE_LEFT_in_workflow149 = new BitSet(new long[]{0x0000000000018010L});
    public static final BitSet FOLLOW_strategy_in_workflow169 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_workflow172 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_strategy_in_workflow174 = new BitSet(new long[]{0x0000000000000120L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_workflow177 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_selection_in_workflow179 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_strategy_in_workflow200 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_workflow203 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_strategy_in_workflow205 = new BitSet(new long[]{0x00000000000001A0L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_workflow210 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_RESULT_COMBINATION_in_workflow212 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_strategy_in_workflow234 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_reuse_in_workflow259 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CHAR_BRACE_RIGHT_in_workflow273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHAR_BRACE_LEFT_in_strategy307 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_RESOLUTION_1_in_strategy318 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_strategy327 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_complexMatcher_in_strategy357 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_strategy361 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_complexMatcher_in_strategy363 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_strategy367 = new BitSet(new long[]{0x0000000004080000L});
    public static final BitSet FOLLOW_similarityCombination_in_strategy369 = new BitSet(new long[]{0x0000000000000120L});
    public static final BitSet FOLLOW_complexMatcher_in_strategy393 = new BitSet(new long[]{0x0000000000000120L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_strategy417 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_selection_in_strategy419 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CHAR_BRACE_RIGHT_in_strategy447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHAR_BRACE_LEFT_in_complexMatcher476 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_RESOLUTION_2_in_complexMatcher487 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_complexMatcher496 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_matcher_in_complexMatcher529 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_complexMatcher_in_complexMatcher533 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_complexMatcher538 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_matcher_in_complexMatcher541 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_complexMatcher_in_complexMatcher545 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_complexMatcher550 = new BitSet(new long[]{0x0000000004080000L});
    public static final BitSet FOLLOW_similarityCombination_in_complexMatcher552 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_matcher_in_complexMatcher575 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_complexMatcher_in_complexMatcher579 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_complexMatcher601 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_SETCOMBINATION_in_complexMatcher603 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CHAR_BRACE_RIGHT_in_complexMatcher627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHAR_BRACE_LEFT_in_matcher644 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RESOLUTION_3_in_matcher655 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_matcher664 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_SIMMEASURE_in_matcher693 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_SIMMEASURE_in_matcher716 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_matcher720 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_SIMMEASURE_in_matcher722 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_matcher726 = new BitSet(new long[]{0x0000000004080000L});
    public static final BitSet FOLLOW_similarityCombination_in_matcher728 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_matcher748 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_SETCOMBINATION_in_matcher750 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CHAR_BRACE_RIGHT_in_matcher776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_reuse790 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_reuse798 = new BitSet(new long[]{0x0000000000018000L});
    public static final BitSet FOLLOW_set_in_reuse800 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_reuse808 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_BOOLEAN_in_reuse810 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_reuse812 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_COMPOSITION_in_reuse814 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_reuse816 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_SIMCOMBINATION1_in_reuse818 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_reuse820 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_BOOLEAN_in_reuse822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHAR_BRACE_LEFT_in_selection844 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_DIRECTION_in_selection846 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_selection848 = new BitSet(new long[]{0x0000000003A00000L});
    public static final BitSet FOLLOW_selectionParameter_in_selection850 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CHAR_BRACE_RIGHT_in_selection852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECTION_THRESHOLD_in_selectionParameter864 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_CHAR_BRACE_LEFT_in_selectionParameter866 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_V1_in_selectionParameter868 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CHAR_BRACE_RIGHT_in_selectionParameter870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECTION_MAXDELTA_in_selectionParameter882 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_CHAR_BRACE_LEFT_in_selectionParameter884 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_V1_in_selectionParameter886 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CHAR_BRACE_RIGHT_in_selectionParameter888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECTION_MAXN_in_selectionParameter900 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_CHAR_BRACE_LEFT_in_selectionParameter902 = new BitSet(new long[]{0x0000000000018000L});
    public static final BitSet FOLLOW_set_in_selectionParameter904 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CHAR_BRACE_RIGHT_in_selectionParameter912 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECTION_MULTIPLE_in_selectionParameter924 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_CHAR_BRACE_LEFT_in_selectionParameter926 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_DIGIT_in_selectionParameter928 = new BitSet(new long[]{0x0000000000008080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_selectionParameter931 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_V1_in_selectionParameter933 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_selectionParameter935 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_V1_in_selectionParameter937 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CHAR_BRACE_RIGHT_in_selectionParameter939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIMCOMBINATION1_in_similarityCombination954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIMCOMBINATION2_in_similarityCombination960 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_CHAR_BRACE_LEFT_in_similarityCombination962 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_V1_in_similarityCombination964 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_similarityCombination966 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_V1_in_similarityCombination968 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CHAR_BRACE_RIGHT_in_similarityCombination970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_allowedToken0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_strategy_in_synpred5_ComaWorkFlow169 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_synpred5_ComaWorkFlow172 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_strategy_in_synpred5_ComaWorkFlow174 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_synpred5_ComaWorkFlow177 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_selection_in_synpred5_ComaWorkFlow179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_strategy_in_synpred8_ComaWorkFlow200 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_synpred8_ComaWorkFlow203 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_strategy_in_synpred8_ComaWorkFlow205 = new BitSet(new long[]{0x0000000000000182L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_synpred8_ComaWorkFlow210 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_RESULT_COMBINATION_in_synpred8_ComaWorkFlow212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_strategy_in_synpred9_ComaWorkFlow234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_complexMatcher_in_synpred11_ComaWorkFlow357 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_synpred11_ComaWorkFlow361 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_complexMatcher_in_synpred11_ComaWorkFlow363 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_synpred11_ComaWorkFlow367 = new BitSet(new long[]{0x0000000004080000L});
    public static final BitSet FOLLOW_similarityCombination_in_synpred11_ComaWorkFlow369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_matcher_in_synpred16_ComaWorkFlow529 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_complexMatcher_in_synpred16_ComaWorkFlow533 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_CHAR_COMMA_in_synpred16_ComaWorkFlow538 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_matcher_in_synpred16_ComaWorkFlow541 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_complexMatcher_in_synpred16_ComaWorkFlow545 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_CHAR_SEMICOLON_in_synpred16_ComaWorkFlow550 = new BitSet(new long[]{0x0000000004080000L});
    public static final BitSet FOLLOW_similarityCombination_in_synpred16_ComaWorkFlow552 = new BitSet(new long[]{0x0000000000000002L});

}