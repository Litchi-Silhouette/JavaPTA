package pku;

import java.util.List;
import java.util.ArrayList;

// 可以自己定义Constraint类, 最后集合成标准形式
public class StandardConstraint {
    // left = left and subsets and {elements} and secSubsets and judgeInclude
    private int left;
    private List<Integer> subsets;
    private List<Integer> elements;
    private List<Integer> secSubsets;
    private List<JudgeConstraint> judgeInclude;

    public StandardConstraint(int left) {
        this.left = left;
        this.subsets = new ArrayList<>();
        this.elements = new ArrayList<>();
        this.secSubsets = new ArrayList<>();
        this.judgeInclude = new ArrayList<>();
    }

    public void addSubset(int subset) {
        this.subsets.add(subset);
    }

    public void addElement(int element) {
        this.elements.add(element);
    }

    public void addSecSubset(int secSubset) {
        this.secSubsets.add(secSubset);
    }

    public void addJudgeInclude(JudgeConstraint judgeConstraint) {
        this.judgeInclude.add(judgeConstraint);
    }

    public void calculate(AbstractDomain domain) {
        for (JudgeConstraint judgeConstraint : judgeInclude) {
            int result = judgeConstraint.calculate(domain);
            if (result >= 0) {
                domain.merge(left, domain.get(result));
            }
        }
        for (int subset : subsets) {
            domain.merge(left, domain.get(subset));
        }
        for (int element : elements) {
            domain.get(left).set(element);
        }
        for (int secSubset : secSubsets) {
            List<Integer> elements = domain.getElementIndex(secSubset);
            for (int element : elements) {
                domain.merge(left, domain.get(element));
            }
        }
    }
}
