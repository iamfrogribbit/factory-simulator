package factorysim.model;
import factorysim.config.MachineConfig;
import factorysim.config.PortConfig;
import java.util.ArrayList;
import java.util.List;

/**
 * Simulation model for a machine.
 */
public class Machine implements Tickable, StatResettable {

    private final String name;
    private final int cooldown;
    private final List<InputPort> inputs;
    private final List<OutputPort> outputs;
    private int remainingCooldown;
    private int ticks;
    private int utilisedTicks;

    /**
     * This is a suggested design for the Machine class if you are stuck
     * 
     * You do not need to follow this design, if you have a different
     * idea in mind, as long as what you do is compatible with the 
     * Sink and FactoryNetwork classes!
     * 
     * Design considerations:
     * - The spec states a machine should have a name and a cooldown period.
     * - It also needs to have some way of tracking its input and output ports (their capacity, what they currently hold, and what items they store), and its state (e.g. is it in cooldown? For how many moreticks?)
     * - A machine constructor could either take in a MachineConfig object, or 
     * these fields individually.
     * - A machine activates on a tick, so what provided interface could be useful here?
     * - On a tick, a machine will need to check things like if it can activate/if it is blocked/if it is currently in a cooldown state, and then implement activation (take items from input port, and produce a batch of output), or decrement cooldown remaining etc. 
     * - remember you can use private methods and other classes (e.g. to represent sub-components of a machine) to break down the machine component and the activation logic!
     * - You will also need to track machine statistics. You might need some more class fields to help! Read the section of the spec around machine statistics.
     * - The Sink class had a method for calculating avg items consumed. What is the equivalent here?
     * - If we're tracking statistics, what other interface should this class perhaps implement?
     * 
     */
    public Machine(MachineConfig machine) {
        name = machine.getName();
        cooldown = machine.getCooldown();
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        for (PortConfig input : machine.getInputConfigs()) {
            inputs.add(new InputPort(input));
        }
        for (PortConfig output : machine.getOutputConfigs()) {
            outputs.add(new OutputPort(output));
        }
        remainingCooldown = 0;
    }

    public String getName() {
        return name;
    }

    public double getUtilisation() {
        if (ticks == 0) return 0.0;
        return utilisedTicks / (double) ticks;
    }

    public boolean canActivate() {

        if (!inputs.isEmpty()) {
            for (InputPort input : inputs) {
                if (!input.isFull()) return false;
            }
        }

        for (OutputPort output : outputs) {
            if (!output.isEmpty()) return false;
        }

        return remainingCooldown == 0;
    }

    private void consume() {
        for (InputPort input : inputs) {
            while (input.canPush()) input.push();
        }
    }

    private void produce() {
        for (OutputPort output : outputs) {
            while (output.canPull()) output.pullItem();
        }
    }

    public List<OutputPort> getOutputs() {
        return outputs;
    }

    public List<InputPort> getInputs() {
        return inputs;
    }

    @Override
    public void tick() {
        ticks++;
        if (remainingCooldown > 0) {
            remainingCooldown--;
            utilisedTicks++;
        } else if (canActivate()) {
            consume();
            produce();
            remainingCooldown = cooldown;
            utilisedTicks++;
        }
    }

    @Override
    public void resetStatistics() {
        ticks = 0;
        utilisedTicks = 0;
    }

}
