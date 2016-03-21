package at.ac.wu.infobiz.projectmining.test;
import java.io.StringReader;
import java.util.List;

import util.LblTree;
import distance.APTED;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;


public class TestWithTED {

	public static void main(String[] args) {
		LexicalizedParser lp = LexicalizedParser.loadModel();
		demoAPI(lp);
	}
	
	public static void demoAPI(LexicalizedParser lp) {

	    // This option shows loading and using an explicit tokenizer
		String sent1 = "This is one sentence";
	    String sent2 = "This is another sentence";
	    
	    TokenizerFactory<CoreLabel> tokenizerFactory =
	        PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	    
	    Tokenizer<CoreLabel> tok1 = tokenizerFactory.getTokenizer(new StringReader(sent1));
	    
	    Tokenizer<CoreLabel> tok2 =
	        tokenizerFactory.getTokenizer(new StringReader(sent2));
	    List<CoreLabel> rawWords1 = tok1.tokenize();
	    List<CoreLabel> rawWords2 = tok2.tokenize();
	    Tree parseSent1 = lp.apply(rawWords1);
	    Tree parseSent2 = lp.apply(rawWords2);

	    // You can also use a TreePrint object to print trees and dependencies
	    TreePrint tp = new TreePrint(
	    		"oneline, "
//	    		+ "penn, "
//	    		+ "latexTree, "
//	    		+ "xmlTree, "
//	    		+ "words," 
//	    		+ "wordsAndTags, "
//	    		+ "rootSymbolOnly,"
//	    		+ "dependencies," 
//	    		+ "typedDependencies, "
//	    		+ "typedDependenciesCollapsed," 
//	    		+ "collocations, "
//	    		+ "semanticGraph, "
//	    		+ "conllStyleDependencies," 
//	    		+ "conll2007"
	    		);
	    tp.printTree(parseSent2);
//	    System.out.println(parseSent1.pennString());
//	    String treeString1 = toTreeString(parseSent1.pennString());
	    String tree1 = parseSent1.toString().replaceAll("\\(", "\\{").replaceAll("\\)", "\\}").replaceAll("\\s+", ""); 
	    String tree2 = parseSent2.toString().replaceAll("\\(", "\\{").replaceAll("\\)", "\\}").replaceAll("\\s+", "");
	    System.out.println(tree1+"\n"+tree2);
	    
	    APTED ted = new APTED(1f,1f,1f);
	    LblTree t1 = LblTree.fromString(tree1);
	    LblTree t2 = LblTree.fromString(tree2);
	    t1.prettyPrint();
	    t2.prettyPrint();
	    
	    float dist = ted.nonNormalizedTreeDist(t1, t2);
	    System.out.println(dist);
	  }

//	private static String toTreeString(String pennString) {
//		String[] parts = pennString.split("\\n");
//		String res = "";
//		for (String string : parts) {
//			res+=""
//		}
//		return null;
//	}


}
