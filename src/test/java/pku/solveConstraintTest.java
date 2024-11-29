package pku;

import org.junit.jupiter.api.Test;
import pku.StandardConstraintSet;

public class solveConstraintTest {
    @Test
    public void testSolver(){
        // 指针包括 o -> 0, p -> 1, q -> 2
        // 值包括 v -> 0, p -> 1, w -> 2
        // 由于接口修改，暂时无法测试
//        SimpleConstraint sc_o = new SimpleConstraint(0);
//        sc_o.addElement(0); // v in o
//        SimpleConstraint sc_p = new SimpleConstraint(1);
//        sc_p.addSubset(0);  // o subset p
//        SimpleConstraint sc_q = new SimpleConstraint(2);
//        sc_q.addElement(1);  // p in q
//        AllInConstraint aic = new AllInConstraint(2, 1);    // forall x in q, x subset p
//        AllHasConstraint ahc = new AllHasConstraint(2);
//        ahc.addElement(2); // forall v in q, w in v
//        System.out.println(sc_o);
//        System.out.println(sc_p);
//        System.out.println(aic);
//        System.out.println(ahc);
//        StandardConstraintSet scs = new StandardConstraintSet();
//        scs.addSimpleConstraint(sc_o);
//        scs.addSimpleConstraint(sc_p);
//        scs.addSimpleConstraint(sc_q);
//        scs.addAllInConstraint(aic);
//        scs.addAllHasConstraint(ahc);
//        scs.solve();
//        System.out.println("graph index of o is :" + scs.get_node_index_from_var_index(0));
//        System.out.println("graph index of p is :" + scs.get_node_index_from_var_index(1));
//        System.out.println("graph index of q is :" + scs.get_node_index_from_var_index(2));
//
//        System.out.println("o: " + scs.getInfo(0));
//        System.out.println("p: " + scs.getInfo(1));
//        System.out.println("q: " + scs.getInfo(2));
    }
}
