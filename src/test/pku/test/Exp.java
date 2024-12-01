package test;

import benchmark.internal.Benchmark;
import benchmark.objects.B;
import benchmark.objects.A;

public class Exp {
  public static void assign(A a, A b) {
    b.f = a.f;
  }

  public static void main(String[] args) {
    Benchmark.alloc(1);
    B b = new B();
    A a = new A(b);
    A c = new A();
    assign(a, c);
    B d = c.f;
    Benchmark.test(1, d);
    B e = a.f;
    Benchmark.test(2, e);
  }
}
