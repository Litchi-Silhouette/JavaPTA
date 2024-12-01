package cornerCases;

import benchmark.internal.Benchmark;
import benchmark.objects.A;
import benchmark.objects.B;

/*
 * @testcase AccessPath1
 * 
 * @version 1.0
 * 
 * @author Johannes Späth, Nguyen Quang Do Lisa (Secure Software Engineering Group, Fraunhofer
 * Institute SIT)
 * 
 * @description Query for access paths
 */
public class AccessPath1 {

  public static void main(String[] args) {

    Benchmark.alloc(1);
    B s = new B();
    A a = new A();
    A b = new A();
    b.f = s;
    a.f = b.f;
    B d = a.f;
    Benchmark.test(1, d);
  }
}
