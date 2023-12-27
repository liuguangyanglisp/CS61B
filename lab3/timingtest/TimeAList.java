package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    public static AList variousTestSize;
    public static AList testRuntime;
    public static AList operations;
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
        printTimingTable(variousTestSize, testRuntime, operations);
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> testAlist = new AList<>();
        variousTestSize = new AList<>();
        testRuntime = new AList<>();
        operations = new AList<>();
        int testSize = 0;
        while (testAlist.size() < 128000) {
            if (testSize < 1000) {
                testSize += 1000;
            } else {
                testSize *= 2;
            }
            Stopwatch sw = new Stopwatch();
            while (testAlist.size() < testSize) {
                testAlist.addLast(0);
            }
            double timeInSeconds = sw.elapsedTime();

            variousTestSize.addLast(testAlist.size());
            testRuntime.addLast(timeInSeconds);
            operations.addLast(testAlist.size());
        }
    }
}
