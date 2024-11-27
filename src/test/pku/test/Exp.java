package test;

import benchmark.internal.Benchmark;
import benchmark.objects.B;
import benchmark.objects.N;

public class Exp {

  public static void main(String[] args) {
    Benchmark.alloc(1);
    B b1 = new B();
    Benchmark.alloc(2);
    B b2 = new B();
    Benchmark.alloc(3);
    N n1 = new N();
    Benchmark.alloc(4);
    N n2 = new N();

    n1.next = n2;
    n1.next.next = n1;

    try {
      int i = 0;
      int j = 1;
      j = j / i;
      b1 = b2;
    } catch (Exception e) {
      System.out.println("Exception");
    }
    Benchmark.test(1, b1);
    Benchmark.test(2, b2);
    Benchmark.test(3, n1);
    Benchmark.test(4, n2);
  }
}
/*
 * Answer:
 * 1 : 1 2 3 4
 * 2 : 2 3 4
 * 3 : 3 4
 * 4 : 4
 */
