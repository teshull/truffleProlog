package cs598ga.shull.test.ast;

import java.util.ArrayList;

import cs598ga.shull.test.execution.*;
import cs598ga.shull.test.parser.*;
import cs598ga.shull.test.parser.PrologParser.*;
import cs598ga.shull.test.runtime.PrologRuntime;
import cs598ga.shull.test.nodes.*;
import cs598ga.shull.test.nodecreation.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class NodeGeneratorListener extends PrologBaseListener{
	public NodeScope currentScope = NodeScope.EMPTY;
	final private GlobalEnvironment env = GlobalEnvironment.globalEnv;

	@Override 
	public void enterEveryRule(ParserRuleContext ctx) { 
		//System.out.println("enter every rule " + ctx.getText());
		currentScope = currentScope.transferToChildScope();
		
	}

	@Override 
	public void exitEveryRule(ParserRuleContext ctx) { 
		//System.out.println("exit every rule " + ctx.getText());
		currentScope = currentScope.transferToParentScope();
	}

	@Override 
	public void exitP_text(PrologParser.P_textContext ctx) { 
		currentScope.printTree();
		env.printEnvironment();
	}

	@Override 
	public void exitClause(PrologParser.ClauseContext ctx) { 
		System.out.println("exit clause term " + ctx.getText());
		ArrayList<BaseNode> children = currentScope.getChildren();
		assert children.size() == 1 : "should only have one here";
		assert children.get(0) instanceof ClauseNode : "also surprising";
		ClauseNode child = (ClauseNode) children.get(0);
		// maybe should switch this to using an enum...
		if(child.isFact()){
			FactNode fact = (FactNode) child;
			env.addFactNode(fact);
			
		} else if(child.isRule()) {
			RuleNode rule = (RuleNode) child;
			env.addRuleNode(rule);
			
		} else if(child.isQuery()){
			QueryNode rule = (QueryNode) child;
			env.addQueryNode(rule);
			
		} else {
			PrologRuntime.programError("unexpected item passed to clause");
		}
		// is finished with this node
		currentScope.releaseChildren();
	}

	@Override 
	public void exitSupported_unary_operator(PrologParser.Supported_unary_operatorContext ctx) { 
		System.out.println("exit supported unary operator term " + ctx.getText());
		ArrayList<BaseNode> children = currentScope.getChildren();
		assert children.size() == 2 : "will need to handle more cases soon";
		BaseNode operator = children.get(0);
		if(operator instanceof QueryNode){
			QueryNode query = (QueryNode) operator;
			BaseNode temp = children.get(1);
			System.out.println("temp " + temp + " class " + temp.getClass());
			ExecutableNode node = (ExecutableNode) children.get(1);
			query.setChild(node);
			currentScope.addNode(query);
		} else if (operator instanceof RuleNode){
			assert false : "rule node is coming soon";
		} else {
			assert false : "am not supporting this yet";
		}
		
	}

	@Override public void exitSupported_binary_operator(PrologParser.Supported_binary_operatorContext ctx) { 
		System.out.println("exit supported binary operator term " + ctx.getText());
		ArrayList<BaseNode> children = currentScope.getChildren();
		if(children.size() == 3){
			if(children.get(1) instanceof RuleNode){
				RuleNode rule = (RuleNode) children.get(1);
				rule.addPredicate((PredicateNode) children.get(0));
				rule.addCondition(children.get(2));
				currentScope.addNode(rule);
			}
		}
		
	}

	@Override 
	public void exitRule_operator(PrologParser.Rule_operatorContext ctx) { 
		System.out.println("exit rule operator " + ctx.getText());
		ArrayList<BaseNode> children = currentScope.getChildren();
		assert children == SpecialNode.NONODES : "well, I am confused";
		RuleNode rule = new RuleNode();
		currentScope.addNode(rule);
	}

	@Override 
	public void exitQuerey_operator(PrologParser.Querey_operatorContext ctx) { 
		System.out.println("exit querey term " + ctx.getText());
		ArrayList<BaseNode> children = currentScope.getChildren();
		assert children == SpecialNode.NONODES : "well, I am confused";
		QueryNode query = new QueryNode();
		currentScope.addNode(query);
	}


	@Override public void exitAtom_term(PrologParser.Atom_termContext ctx) { 
		System.out.println("exit atom term " + ctx.getText());
		BaseNode node = NodeFactory.createAtom(ctx.getText());
		currentScope.addNode(node);
	}

	
	@Override public void exitCompound_term(PrologParser.Compound_termContext ctx) { 
		System.out.println("exit compound term " + ctx.getText());
		System.out.println("tree " + ctx.toStringTree());
		ArrayList<BaseNode> children = currentScope.getChildren();
		BaseNode base = children.get(0);
		assert base instanceof AtomNode : "didn't expect this";
		assert children.size() > 1 : "well, crap";
		ArrayList<PredicateNode> terms = new ArrayList<>();
		for(int i = 1; i < children.size(); i++){
			terms.add((PredicateNode) children.get(i));
		}
		BaseNode node = NodeFactory.createCompound((AtomNode) base, terms);
		currentScope.addNode(node);
	}

	@Override 
	public void exitVariable(PrologParser.VariableContext ctx) { 
		System.out.println("exit variable term " + ctx.getText());
		BaseNode node = NodeFactory.createVariable(ctx.getText());
		currentScope.addNode(node);
	}


}
