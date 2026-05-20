package factorysim.model;

import java.util.ArrayList;
import java.util.List;

public class BeltBalancer implements Tockable, StatResettable {

    private final String name;
    private final String itemType;
    private final List<OutputPort> sources;
    private final List<InputPort> destinations;

    private int tocks;
    private long itemsTransferred;

    private int nextSourceIndex;
    private int nextDestinationIndex;

    public BeltBalancer(String name, String itemType) {
        this.name = name;
        this.itemType = itemType;
        sources = new ArrayList<>();
        destinations = new ArrayList<>();
        nextSourceIndex = 0;
        nextDestinationIndex = 0;
    }

    public String getName() {
        return name;
    }

    public void addSource(OutputPort newSource) throws BeltValidationException {
        if (newSource.itemType().equals(itemType)) {
            sources.add(newSource);
        } else {
            throw new BeltValidationException("Sources must output the same item type as the belt's item type");
        }
    }

    public void addDestination(InputPort newOutput) throws BeltValidationException {
        if (newOutput.getItemName().equals(itemType)) {
            destinations.add(newOutput);
        } else {
            throw new BeltValidationException("destinations must take the same item type that the belt outputs");
        }
    }

    private int calculateNextSourceIndex() {
        nextSourceIndex = (nextSourceIndex + 1) % sources.size();
        while (sources.get(nextSourceIndex).isEmpty()) {
            nextSourceIndex = (nextSourceIndex + 1) % sources.size();
        }
        return nextSourceIndex;
    }

    private int calculateNextDestinationIndex() {
        nextDestinationIndex = (nextDestinationIndex + 1) % destinations.size();
        while (sources.get(nextDestinationIndex).isFull()) {
            nextDestinationIndex = (nextDestinationIndex + 1) % destinations.size();
        }
        return nextDestinationIndex;
    }

    @Override
    public void tock() {
        tocks++;
        OutputPort newSource = sources.get(calculateNextSourceIndex());
        InputPort newDestination = destinations.get(calculateNextDestinationIndex());
        
        if (newSource.canPull() && newDestination.canReceive()) {
            newSource.pullItem();
            newDestination.push();
            itemsTransferred++;
        }
    }

    public double getItemsPerMinute() {
        if (tocks == 0) return 0.0;
        return (itemsTransferred / (double) tocks) * 60;
    }

    public String getItemType() {
        return itemType;
    }

    @Override
    public void resetStatistics() {
        tocks = 0;
        itemsTransferred = 0;
    }
}