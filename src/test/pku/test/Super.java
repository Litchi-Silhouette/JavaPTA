package test;

import benchmark.internal.Benchmark;
// import benchmark.objects.B;
// By Zhiyi Li

class A {
    A() {
    }
}

class B extends A {
    A f;
    B() {
        super();
        f = new A();
    }
}
public class Super {
    public static void main(String[] args) {
        B b = new B();
        A a = new A();
    }
}
