package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {

    private static double calcFrequencyOfNote(int index) {
        return 440 * Math.pow(2, (index - 24) / 12.0);
    }
    private static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    private static final int numOfKEYS = keyboard.length();

    public static void main(String[] args) {
        GuitarString[] arrGS  = new GuitarString[numOfKEYS];

        for (int i = 0; i < numOfKEYS; i++) {
            double freq = calcFrequencyOfNote(i);
            arrGS[i] = new GuitarString(freq);
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int keyIndex = keyboard.indexOf(key);
                if (keyIndex > 0) {
                    arrGS[keyIndex].pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = 0.0;
            for (GuitarString s : arrGS) {
                sample += s.sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (GuitarString s : arrGS) {
                s.tic();
            }
        }
    }
}
