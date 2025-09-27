class MainEasy {
  public static void main(String[] args) {
    A a;
    a = new A();
    System.out.println(a.getValue(3));
  }
}

class A {
  public int getValue(int x) {
    return x + 1;
  }
}

