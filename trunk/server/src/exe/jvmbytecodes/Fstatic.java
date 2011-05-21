class Fstatic {
	public static void main(String[] args) {
		f();
	}

	static void f() {
		int factorial = 1;
		for (int i = 2; i <= 4; i++)
			factorial *= i;
	}
}
