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

    @Override
    public void tock() {
        tocks++;

        if (sources.isEmpty() || destinations.isEmpty()) return;
        
        int checked = 0;
        while (checked < sources.size() && !sources.get(nextSourceIndex).canPull()) {
            nextSourceIndex = (nextSourceIndex + 1) % sources.size();
            checked++;
        }

        int destChecked = 0;
        while (destChecked < destinations.size() && !destinations.get(nextDestinationIndex).canReceive()) {
            nextDestinationIndex = (nextDestinationIndex + 1) % destinations.size();
            destChecked++;
        }

        if (sources.get(nextSourceIndex).canPull() 
                && destinations.get(nextDestinationIndex).canReceive()) {
            sources.get(nextSourceIndex).pullItem();
            destinations.get(nextDestinationIndex).receiveItem();
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