package pku;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses(
        {
                GraphTestPKU.class,
                solveConstraintTest.class
        }
)

public class PKUTestSuite {
}
