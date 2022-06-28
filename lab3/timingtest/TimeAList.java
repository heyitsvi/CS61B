package timingtest;
import edu.princeton.cs.algs4.Stopwatch;


/**
 * Created by hug.
 */
public class TimeAList {
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
    }

    public static void timeAListConstruction() {
        int[] sizes = new int[]{1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000};
        AList<Double> timeList = new AList<>();
        AList<Integer> opCount = new AList<>();
        AList<Integer> N = new AList<>();

        for (int i = 0; i < sizes.length; i++) {
            int iter = sizes[i];
            AList<Integer> List = new AList<>();

            Stopwatch sw = new Stopwatch();
            while (iter > 0) {
                List.addLast(1);
                iter -= 1;
            }
            double timeInSeconds = sw.elapsedTime();
            //System.out.println("Time for " + sizes[i] + " is " + timeInSeconds);
            timeList.addLast(timeInSeconds);
            N.addLast(sizes[i]);
            opCount.addLast(sizes[i]);
        }

        printTimingTable(N, timeList, opCount);
    }
}
