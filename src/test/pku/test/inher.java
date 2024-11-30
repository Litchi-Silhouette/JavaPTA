package test;

import benchmark.internal.Benchmark;

// By Guo ZiXun

public class A {
    public int a;
}

public class B {
    public A createA(int a) {
        Benchmark.alloc(1);
        A obj = new A();
        obj.a = a;
        return obj;
    }
}

public class C extends B {
    public A createA(int a) {
        Benchmark.alloc(2);
        A obj = new A();
        obj.a = a + 1;
        return obj;
    }
}

public class D extends C {
    public A createA(int a) {
        Benchmark.alloc(3);
        A obj = new A();
        obj.a = a + 2;
        return obj;
    }
}

public class inher {
  public static void main(String[] args) {
      Benchmark.alloc(4);
      C c = new C();
      Benchmark.alloc(5);
      D d = new D();
      Benchmark.alloc(6);
      B c1 = new B();
      try {
          for (int i = 0; i < 10; i++) {
              c1 = c;
          }
      }
      catch (Exception e) {
          System.out.println("Exception");
      }
      A a = c1.createA(1);
      Benchmark.test(1, a);
      /*
        Answer:
         1 : 1 2
       */

  }
}
