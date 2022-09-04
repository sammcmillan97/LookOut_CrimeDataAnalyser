package seng202.group7.analyses;

/**
 *
 * Tuple class used for converting hash table into sorted list for the "GraphUtil" data class.
 *
 * @param <X> The string key the value that is being measured
 * @param <Y> The int value for occurrence in the data of x
 * @author Sam McMillan
 * @author Shaylin Simadari
 */
public class Tuple<X, Y> {
    public X x;
    public Y y;

    /**
     * The constructor for the tuple class.
     * @param x     The value being stored as type X.
     * @param y     The value being stored as type Y.
     */
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}
