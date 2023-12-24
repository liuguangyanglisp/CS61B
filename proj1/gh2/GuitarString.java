package gh2;

// TODO: uncomment the following import once you're ready to start this portion
    import deque.ArrayDeque;
    import deque.Deque;
    import deque.LinkedListDeque;
    import deque.MaxArrayDeque;
    import edu.princeton.cs.algs4.StdAudio;

    import java.util.Comparator;
// TODO: maybe more imports

//Note: This file will not compile until you complete the Deque implementations
public class GuitarString {
    /** Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    private Deque<Double> buffer;

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        int capacity = (int) Math.round(SR / frequency);
        buffer = new LinkedListDeque<>();
        while (buffer.size() < capacity){
            buffer.addLast(0.0);
        }
    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        int capacity = buffer.size();
        while (capacity > 0){
            buffer.removeLast();
            buffer.addFirst(Math.random() - 0.5);
            capacity -= 1;
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double newDouble = ((buffer.get(1) + buffer.removeFirst() ) / 2) * DECAY;
        buffer.addLast(newDouble);
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        return buffer.get(0);
    }
}
