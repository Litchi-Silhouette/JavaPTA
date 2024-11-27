package pku;

import pascal.taie.analysis.MethodAnalysis;
import pascal.taie.config.AnalysisConfig;
import pascal.taie.ir.IR;

public class MethodConstraintAnalysis extends MethodAnalysis<MethodConstraintResult> {
    public static final String ID = "pku-pta-method-constraint";
    public AbstractMallocDomain mallocDomain;

    public MethodConstraintAnalysis(AnalysisConfig config, AbstractMallocDomain mallocDomain) {
        super(config);
        this.mallocDomain = mallocDomain;
    }

    @Override
    public MethodConstraintResult analyze(IR ir) {
        var result = new MethodConstraintResult(mallocDomain);
        result.analysis(ir);
        return result;
    }

}
