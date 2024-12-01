package pku;

import pascal.taie.analysis.MethodAnalysis;
import pascal.taie.config.AnalysisConfig;
import pascal.taie.ir.IR;
import pku.abs.AbstractVarDomain;

public class MethodConstraintAnalysis extends MethodAnalysis<MethodConstraintResult> {
    public static final String ID = "pku-pta-method-constraint";
    public final PreprocessResult preprocess;
    public final AbstractVarDomain staticDomain;

    public MethodConstraintAnalysis(AnalysisConfig config,
            PreprocessResult preprocess, AbstractVarDomain staticDomain) {
        super(config);
        this.preprocess = preprocess;
        this.staticDomain = staticDomain;
    }

    @Override
    public MethodConstraintResult analyze(IR ir) {
        var result = new MethodConstraintResult(preprocess, staticDomain);
        Context context = new Context(null, ir, -1);
        result.analysis(context);
        return result;
    }

}
