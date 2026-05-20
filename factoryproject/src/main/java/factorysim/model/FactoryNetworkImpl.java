package factorysim.model;
import factorysim.config.MachineConfig;
import factorysim.stats.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * FactoryNetwork models the overall factory, including connections 
 * between machines, belts, and sinks.
 * Implements the FactoryNetwork interface.
 */
public final class FactoryNetworkImpl implements FactoryNetwork {

    private final Sink sink;
    private final List<Machine> machines;
    private final List<BeltBalancer> belts;
    private final Map<String, BeltBalancer> beltRegistry;

    /**
     * Establish a factory network.
     * Most of the logic for this class is in the constructor
     * (though you might find private methods useful to break it up)
     * 
     * @param machineConfigs a list of machine configs from the 
     * configuration file (in the same order they were originally in
     * i.e. machineConfigs.get(0) would have been the first listed machine in the config file)
     * @throws BeltValidationException If the list of machineConfigs tries to wire a sink to a
     * machine input port, or tries to connect a belt to two ports with different types, 
     * the constructor should throw a BeltValidationException with an informative error message.
     */
    public FactoryNetworkImpl(List<MachineConfig> machineConfigs) throws BeltValidationException {

        // The constructor should initialise your factory components (Sink has been initialised for you below). 
        // It should "wire" the components together (i.e. connect machine output ports to belts/sinks etc), 
        // based on the layout specified in the machine configs.
        // It should also check that the provided config doesn't suggest wiring a belt in 
        // an incorrect way (i.e. you can't  wire a sink to a machine input port)
        // a BeltValidationException. You might even find it useful to do this first,
        // It is up to you how to approach this/ what data structures you use.
        
        sink = new Sink();
        machines = new ArrayList<>();
        belts = new ArrayList<>();
        beltRegistry = new LinkedHashMap<>();

        for (MachineConfig machineConfig : machineConfigs) {
            Machine machine = new Machine(machineConfig);
            machines.add(machine);

            for (OutputPort outputPort : machine.getOutputs()) {
                String beltName = outputPort.getBeltName();
                if (beltName.equals("sink")) {
                    sink.addSource(outputPort);
                } else {
                    BeltBalancer belt = getOrCreateBelt(beltName, outputPort.itemType());
                    belt.addSource(outputPort);
                }
            }

            for (InputPort inputPort : machine.getInputs()) {
                String beltName = inputPort.getBeltName();
                if (beltName.equals("sink")) {
                    throw new BeltValidationException("Belt 'sink' cannot connect to an input port");
                } else{
                    BeltBalancer belt = getOrCreateBelt(beltName, inputPort.getItemName());
                    belt.addDestination(inputPort);
                }
            }
        }
    }

    private BeltBalancer getOrCreateBelt(String name, String itemType) throws BeltValidationException {
        if (beltRegistry.keySet().contains(name)) {
            BeltBalancer belt = beltRegistry.get(name);
            if (!belt.getItemType().equals(itemType)) {
                throw new BeltValidationException("Belt " + name + " cannot carry both" + belt.getItemType() + " and " + itemType);
            }
            return belt;
        } else {
            BeltBalancer belt = new BeltBalancer(name, itemType);
            beltRegistry.put(name, belt);
            belts.add(belt);
            return belt;
        }
    }

    @Override
    public void tick() {
        for (Machine machine : machines) {
            machine.tick();
        }
    }

    @Override
    public void tock() {
        for (BeltBalancer belt : belts) {
            belt.tock();
        }

        sink.tock();
    }

    @Override
    public List<SinkEntry> getSinkStats() {
        List<SinkEntry> result = new ArrayList<>();
        for (String itemType : sink.getItemTypes()) {
            result.add(new SinkEntry(itemType, sink.getAvgItemsPerMinute(itemType)));
        }
        return result;
    }

    @Override
    public List<MachineStats> getMachineStats() {
        List<MachineStats> result = new ArrayList<>();
        for (Machine machine : machines) {
            result.add(new MachineStats(machine.getName(), machine.getUtilisation()));
        }
        return result;
    }

    @Override
    public List<BeltStats> getBeltStats() {
        List<BeltStats> result = new ArrayList<>();
        for (BeltBalancer belt : belts) {
            result.add(new BeltStats(belt.getName(), belt.getItemType(), belt.getItemsPerMinute()));
        }
        return result;
    }

    @Override
    public void resetStatistics() {
        sink.resetStatistics();

        for (Machine machine : machines) {
            machine.resetStatistics();
        }

        for (BeltBalancer belt : belts) {
            belt.resetStatistics();
        }
    }

    // This class should implement the FactoryNetwork specification.
    // You should think about what methods this involves.
    // If you've used a good object oriented design on factory components, these methods might be quite simple.

}
