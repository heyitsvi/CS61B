package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {

    private static double calcFrequencyOfNote(int index){
        return 440 * Math.pow(2, (index - 24) / 12.0);
    }

    public static GuitarString GS;

    //public static GuitarString[] arrGS;

    public static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static final double CONCERT_A = 440.0;
    public static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);

    public static void main(String[] args) {
        GuitarString[] arrGS  = new GuitarString[37];

        for (int i = 0; i < 37; i++){
            GS = new GuitarString(calcFrequencyOfNote(i));
            arrGS[i] = GS;
        }
        /* create two guitar strings, for concert A and C */
        //GuitarString stringA = new GuitarString(CONCERT_A);
        //GuitarString stringC = new GuitarString(CONCERT_C);

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                /*if (key == 'a') {
                    stringA.pluck();
                } else if (key == 'c') {
                    stringC.pluck();
                }*/
                arrGS[keyboard.indexOf(key)].pluck();
            }

            /* compute the superposition of samples */
            //double sample = stringA.sample() + stringC.sample();

            double sample = 0;
            for(int i = 0; i < 37; i++){
                sample += arrGS[i].sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            //stringA.tic();
            //stringC.tic();

            for(int i = 0; i < 37; i++){
                arrGS[i].tic();
            }
        }
    }
}
