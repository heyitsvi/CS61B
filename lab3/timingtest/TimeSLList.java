package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
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
        timeGetLast();
    }

    public static void timeGetLast() {
        int[] sizes = new int[]{1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000};
        AList<Double> timeList = new AList<>();
        AList<Integer> opCount = new AList<>();
        AList<Integer> N = new AList<>();

        for(int i = 0; i < sizes.length; i++){
            SLList<Integer> L =  new SLList<>();
            int iter = sizes[i];

            while (iter > 0) {
                L.addLast(1);
                iter -= 1;
            }

            Stopwatch sw = new Stopwatch();

            for(int j = 0; j < 10000; j++){
                L.getLast();
            }

            double timeInSeconds = sw.elapsedTime();
            timeList.addLast(timeInSeconds);
            opCount.addLast(1000);
            N.addLast(sizes[i]);
        }

        printTimingTable(N, timeList, opCount);
    }

}
