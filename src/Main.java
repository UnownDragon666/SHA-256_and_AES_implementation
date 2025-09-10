// Imports
import secureHashAlgorithm.SHA2;

public class Main {
    public static void main(String[] args) {
        SHA2 test = new SHA2("hello world");
        test.hash();

        SHA2 test2 = new SHA2("To be or not to be, that is the question. Whether 'tis nobler in mind to suffer the slings and arrows of outrageous fortune or to take arms against a sea of troubles, and by opposing, to end them. To die, to sleep. No more. And by a sleep to say we end, the heart ache and thousand natural shocks that flesh is heir to. 'Tis a consummation, devoutly to be wished. To die, to sleep. To sleep, perchance to dream. Ay, there's the rub.");
        test2.hash();
    }
}