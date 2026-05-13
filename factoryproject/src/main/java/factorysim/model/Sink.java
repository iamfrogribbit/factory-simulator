package factorysim.model;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * Simulation model for a sink: a special consumer belt that accepts unlimited
 * items of any type.
 * 
 * Implements:
 * Tockable: On every tock, the sink should pull all available 
 * items from any connected machine output sources 
 * StatResettable: The sink should be able to reset any data tracking statistics.
 * 
 * Project note: You should replace any empty methods with your implementation as indicated (YOUR CODE HERE). 
 * The comments clearly specify what each method should do. You might also find it useful to refer 
 * to the specification and SinkTest.java file for more details on the expected behaviour of the Sink class.
 * 
 * You may add your own fields and methods as you see fit, but you should not 
 * alter the method signatures of the provided public methods, or the tests will fail!
 */
public class Sink implements Tockable, StatResettable {

    // A static String that stores the "name" used to identify the sink belt in the configuration files.
    public static final String BELT_NAME = "sink";

    //YOUR FIELDS GO HERE!

    int tocks = 0;
    HashMap<String, Integer> itemQuantities;
    ArrayList<OutputSource> sources;

    /** Initialise a Sink object. This takes no parameters.
     * This should set up any fields you decide the class should have to default values if relevant.
     * 
     * For example, you'll probably want to have a way of tracking the type and count of each type of item consumed.
     * You might find a Map (e.g. LinkedHashMap) useful for this, but you can choose your own data structure(s) as you see fit
     */
    public Sink() {
        itemQuantities = new HashMap<>();
        sources = new ArrayList<OutputSource>();
    }

    /** 
    * You should have a method addSource that adds a machine output port the 
    * sink is connected to, to the class. 
    * 
    * It is up to you how you represent an 
    * Output Port, but it will need to implement the OutputSource interface provided!
    * 
    * @param source Any object that implements the provided OutputSource interface.
    */
    public void addSource(OutputSource source) {
        if (itemQuantities.get(source.itemType()) == null) {
            itemQuantities.put(source.itemType(), 0);
            sources.add(source);
        }
    }

    /**
     * Performs the tock phase: pulls all available items from all connected 
     * machine output ports (sources)
     */
    @Override
    public void tock() {
        tocks += 1;
        for (int i = 0; i < sources.size(); i++) {
            OutputSource source = sources.get(i);
            int newQuantity = itemQuantities.get(source.itemType());
            if (source.canPull()){
                while (source.canPull()) {
                    newQuantity++;
                    source.pullItem();
                }
                itemQuantities.put(source.itemType(), newQuantity);
            }
        }
    }

    /**
     * This should return a list of all item types that have been consumed by 
     * the sink since the last reset. Order does not matter.
     * @return A List of Strings representing the item types consumed since last reset.
     * Remember that an ArrayList is an implementation of List!
     */
    public List<String> getItemTypes() {
        List<String> result = new ArrayList(sources.size());
        for (int i = 0; i< sources.size(); i++) {
            if (itemQuantities.get(sources.get(i).itemType()) != 0) {
                result.add(i, sources.get(i).itemType());
            }
        }
        return result;
    }

    /**
     * For a given item type, this should return the average number of items of that type 
     * consumed per minute since the last reset.
     * 
     * This should be calculated as follows: 
     * (count of itemType objects consumed / total number of seconds (i.e. tocks) since last reset) * 60
     * 
     * Remember only one tock occurs per step (1 step = 1 second), so the number of tocks since last reset = number of seconds!
     * 
     * @param itemType The type of item to calculate the average items per minute for.
     * @return a double representing the average number of items of the given type consumed per minute since last reset.
     */
    public double getAvgItemsPerMinute(String itemType) {
        double result = (itemQuantities.get(itemType) / tocks) * 60;
        return result;
    }

    /** 
     * Returns the total items consumed per type since last reset. 
     * 
     * @param itemType The type of item to get the total count for.
     * @return a long representing the total number of items of type itemType consumed
     * since the last reset. 
     * */
    public long getItemsConsumed(String itemType) {
        long result = itemQuantities.get(itemType);
        return result;
    }

    /**
     * Checks if the sink has consumed any items since the last reset.
     * 
     * @return boolean true if no items have been consumed since last reset, false otherwise.
     */
    public boolean isEmpty() {
        if (itemQuantities.isEmpty()) {
            return true;
        }
        Object[] amountConsumed = itemQuantities.values().toArray();
        for (Object amount : amountConsumed) {
            if ((Integer) amount > 0) {
                return false;
            }
        }
        return true;
    }

    /** 
     * Reset any fields tracking data used to calculate the statistics to their initial values.
     */
    @Override
    public void resetStatistics() {
        for (OutputSource source : sources) {
            itemQuantities.put(source.itemType(), 0);
        }
    }
}
