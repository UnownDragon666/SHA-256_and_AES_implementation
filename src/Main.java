// Imports
import secureHashAlgorithm.SHA2;

public class Main {
    public static void main(String[] args) {
        SHA2 test = new SHA2("Hello World");
        test.hash();
    }
}