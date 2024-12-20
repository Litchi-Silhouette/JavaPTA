package cornerCases;

import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;

/*
 * @testcase ObjectSensitivity2
 * 
 * @version 1.0
 * 
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 * 
 * @description Object sensitive alias from parameter object
 */
public class ObjectSensitivity2 {

  public static void main(String[] args) {

    B b1 = new B();
    Benchmark.alloc(1);
    B b2 = new B();

    A a = new A();

    B b3 = a.id(b1);
    B b4 = a.id(b2);

    Benchmark.test(1, b3);
    Benchmark.test(2, b4);
  }
}
