grammar ComaWorkFlow;

options {output=AST;backtrack=true;}


	/*
	 * Grammar for the Matcher Workflow in Coma
	 * @author Patrick Arnold, Sabine Massmann
	 * @date Feb 10, 2011
	 * @date recursive August 28, 2011
	 * @date final check March 13, 2012
	 */

	// ===============================================================================================
	//     PART I - TOKENS
	// ===============================================================================================

	tokens {
	
		CHAR_BRACE_LEFT  =	'(';
		CHAR_BRACE_RIGHT =	')';	
		
		CHAR_DOT = 		'.';
		CHAR_COMMA =		',';
		CHAR_SEMICOLON =	';';
		
	}
	
	
	// ===============================================================================================
	//     PART II - PARSER RULES: MAIN RULES
	// ===============================================================================================
	 
	 
	// *** Level 1 - WORKFLOW DEFINITION ***

	coma :			workflow | strategy | complexMatcher | matcher;  // Top Level - Can be changed to test another level, e.g. strategy, matcher etc.	

	workflow :		
				CHAR_BRACE_LEFT 
					( 	
						( strategy  CHAR_SEMICOLON strategy (CHAR_SEMICOLON selection)?)
						|
						( strategy (CHAR_COMMA strategy)+ (CHAR_SEMICOLON RESULT_COMBINATION)? ) 
						|
						(strategy)						
						| 
						(reuse)
					)
				CHAR_BRACE_RIGHT; 
	
	 
	
	// *** Level 2 - COMPLEX STRATEGY DEFINITION ***
	
	strategy :					
				
				CHAR_BRACE_LEFT  
					( RESOLUTION_1 
						CHAR_SEMICOLON 
						( 
							(
								(complexMatcher ((CHAR_COMMA complexMatcher)+ CHAR_SEMICOLON similarityCombination))
								| 
								(complexMatcher )		
							) 
							(CHAR_SEMICOLON selection)?
						) 		
					)  
				CHAR_BRACE_RIGHT;
	
	
	// ***  Level 3 and Level 4 together - COMPLEX MATCHER and STRATEGY DEFINITION ***
	
	complexMatcher:				

				
				CHAR_BRACE_LEFT  
					( RESOLUTION_2 
						CHAR_SEMICOLON 
						(		
							(
								( (matcher | complexMatcher) ((CHAR_COMMA (matcher | complexMatcher))+ CHAR_SEMICOLON similarityCombination))
								|
								(matcher | complexMatcher ) 
							)	
							CHAR_SEMICOLON SETCOMBINATION	
						)
					)  
				CHAR_BRACE_RIGHT;
	
	
	// *** LEVEL 5 - MATCHER DEFINITION ***
	
	matcher :		CHAR_BRACE_LEFT  
					( RESOLUTION_3 
						CHAR_SEMICOLON 
						( 
							(
								SIMMEASURE 
								| 
								(SIMMEASURE ((CHAR_COMMA SIMMEASURE)+ CHAR_SEMICOLON similarityCombination))
							) 
						CHAR_SEMICOLON SETCOMBINATION
						) 		
					)  
				CHAR_BRACE_RIGHT;
	
		
	reuse :		(DIGIT | V2) CHAR_COMMA (DIGIT | V2) CHAR_COMMA BOOLEAN CHAR_COMMA COMPOSITION CHAR_COMMA SIMCOMBINATION1 CHAR_COMMA BOOLEAN;
	


	// ===============================================================================================
	//     PART III - PARSER RULES: ADDITIONAL RULES
	// ===============================================================================================

	
	selection :		CHAR_BRACE_LEFT DIRECTION CHAR_COMMA selectionParameter CHAR_BRACE_RIGHT;
	
	selectionParameter :	( SELECTION_THRESHOLD CHAR_BRACE_LEFT V1 CHAR_BRACE_RIGHT) | 
				( SELECTION_MAXDELTA CHAR_BRACE_LEFT V1 CHAR_BRACE_RIGHT) | 
				( SELECTION_MAXN CHAR_BRACE_LEFT (DIGIT | V2) CHAR_BRACE_RIGHT) | 
				( SELECTION_MULTIPLE CHAR_BRACE_LEFT DIGIT+ CHAR_COMMA V1 CHAR_COMMA V1 CHAR_BRACE_RIGHT);	
				
	similarityCombination :	SIMCOMBINATION1 | ( SIMCOMBINATION2 CHAR_BRACE_LEFT V1 CHAR_COMMA V1 CHAR_BRACE_RIGHT);	
	
	
		
		
	// ===============================================================================================
	//     PART IV - LEXER RULES
	// ===============================================================================================

	allowedToken :			RESOLUTION_1 | RESOLUTION_2 | RESOLUTION_3 | SIMMEASURE |
					SETCOMBINATION | COMPOSITION | SIMCOMBINATION1 | SIMCOMBINATION2 | RESULT_COMBINATION |
					DIRECTION | SELECTION_THRESHOLD | SELECTION_MAXDELTA | SELECTION_MAXN | SELECTION_MULTIPLE;

	RESOLUTION_1 :		( 'paths' | 'innerpaths' | 'leafpaths' | 'nodes' | 'innernodes' | 'leafnodes' | 'roots' | 'shared' | 'uppaths' | 'downpaths' | 'user' | 'sharedpaths');
	RESOLUTION_2 :		( 'selfpath' | 'selfnode' | 'parents' | 'siblings' | 'children' | 'leaves' | 'allnodes' | 'successors');
	RESOLUTION_3 :		( 'name' | 'nametoken' | 'path' | 'pathtoken' | 'pathsyn' | 'comment' | 'commenttoken' | 'datatype' | 'statistics' | 
				'synonyms' | 'instance_constraints' | 'instance_content' |'instance_content_indirect' |'instance_all' | 'nameandsynonyms' );


	SIMMEASURE:		('trigram' | 'soundex' | 'editdist' | 'datatypesimilarity' | 'featvect' | 'tfidf' | 
				'usersyn' | 'cosine' | 'jaccard' | 'jarowinkler' | 'sim_equal' | 
				'trigramcoma' | 'trigramifuice' | 'trigramlowmem' | 'trigramopt' | 'edjoin' | 
				'levenshteinlucene' | 'levenshteinsecondstring' | 'levenshteinlimes' |'cosineppjoin+fullycached' | 'cosineppjoin' | 'cosinesimmetrics' | 
				'jaccardppjoin+fullycached' | 'jaccardppjoin+' | 'jaccardsecondstring' | 'jaccardsimmetrics' | 'jarowinklerlucene' | 
				'tfidflucenefullycached' | 'tfidflucenefullycachedalternative' | 'tfidfsecondstring' );	
	
	SETCOMBINATION: 	('set_average' | 'set_dice' | 'set_max' | 'set_min' | 'set_highest');
	
	COMPOSITION: 		('com_average' | 'com_max' | 'com_min');
	
	SIMCOMBINATION1 : 	('max' | 'min' | 'average' | 'nonlinear' | 'openii' | 'harmony' | 'sigmoid' | 'owa' | 'owa_most' | 'weighted2');
	SIMCOMBINATION2 :	('weighted');
	
	RESULT_COMBINATION : 	( 'intersect' | 'diff' | 'merge');


	DIRECTION :		('both' | 'forward' | 'backward' | 'simple');
	
	SELECTION_THRESHOLD : 	('threshold');
	SELECTION_MAXDELTA : 	('maxdelta');
	SELECTION_MAXN : 	('maxn');
	SELECTION_MULTIPLE : 	('multiple'); 
	
	
	DIGIT : 		'0'..'9'; 
	V2 :			DIGIT+; 
	V1 : 			( ('0' CHAR_DOT DIGIT+) | '1.0' | '1' | '0'); 
	BOOLEAN:		('true' | 'false');
	
	