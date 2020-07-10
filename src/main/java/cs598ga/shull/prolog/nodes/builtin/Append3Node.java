package cs598ga.shull.prolog.nodes.builtin;

import cs598ga.shull.prolog.execution.ExecutionEnvironment;
import cs598ga.shull.prolog.execution.LocalEnvironment;
import cs598ga.shull.prolog.nodes.NameNode;
import cs598ga.shull.prolog.nodes.SpecialNode;

public class Append3Node extends BuiltinNode {
    public Append3Node(){
        super(3);
        this.base = new NameNode("append", false);
    }

    @Override
    public SpecialNode executeBuiltin(ExecutionEnvironment env, LocalEnvironment localEnv) {
        assert false : "still working on implementing this";
        System.out.println("in progress");
        return SpecialNode.FINISHED;
    }

    @Override
    public String getName() {
        return "append3";
    }
}
