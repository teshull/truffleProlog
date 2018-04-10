package cs598ga.shull.prolog.nodes;

import cs598ga.shull.prolog.execution.ExecutionEnvironment;
import cs598ga.shull.prolog.execution.LocalEnvironment;
import cs598ga.shull.prolog.execution.error.InvalidArithmeticOperationError;

public class IsNode extends LogicalNode{

	@Override
	public BaseNode firstStep(ExecutionEnvironment env){
		boolean foundLeft = false;
		boolean foundRight = false;
		IntegerNode leftVal = null;
		IntegerNode rightVal = null;
		try{
			if(right instanceof ComputeNode){
				ComputeNode compute = (ComputeNode) right;
				rightVal = compute.computeValue(env);
				foundRight = true;
			}
		} catch(InvalidArithmeticOperationError e){
			
		}
		try{
			if(left instanceof ComputeNode){
				ComputeNode compute = (ComputeNode) left;
				leftVal = compute.computeValue(env);
				foundLeft = true;
			}
		} catch(InvalidArithmeticOperationError e){

		}
		LocalEnvironment local = env.getCurrentLocalEnv();
		if(foundLeft && foundRight){
			return IntegerNode.isEqual(leftVal, rightVal)? SpecialNode.FINISHED : SpecialNode.DEADEND;
		} else if(foundRight){
			if(left instanceof VariableNode){
				//need to set the variable, and make sure it doesn't already exist
				VariableNode var = (VariableNode) left;
				if(var.base.isSourceCurrentlyVariable(local)){
					local.setSourceMatch(var.base.getName(), rightVal);
					return SpecialNode.FINISHED;
				}
			}
		}else {
			if(right instanceof VariableNode){
				//need to set the variable, and make sure it doesn't already exist
				VariableNode var = (VariableNode) right;
				if(var.base.isSourceCurrentlyVariable(local)){
					local.setSourceMatch(var.base.getName(), leftVal);
					return SpecialNode.FINISHED;
				}
			}
			
		}
		return SpecialNode.DEADEND;
	}
	
	@Override
	public String toString(){
		String message = "Is Node: " + left + " is " + right;
		return message;
	}
}
