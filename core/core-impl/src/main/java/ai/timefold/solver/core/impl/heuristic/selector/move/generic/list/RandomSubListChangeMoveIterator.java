package ai.timefold.solver.core.impl.heuristic.selector.move.generic.list;

import java.util.Iterator;
import java.util.Random;

import ai.timefold.solver.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import ai.timefold.solver.core.impl.heuristic.move.Move;
import ai.timefold.solver.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import ai.timefold.solver.core.impl.heuristic.selector.list.DestinationSelector;
import ai.timefold.solver.core.impl.heuristic.selector.list.ElementRef;
import ai.timefold.solver.core.impl.heuristic.selector.list.SubList;
import ai.timefold.solver.core.impl.heuristic.selector.list.SubListSelector;

class RandomSubListChangeMoveIterator<Solution_> extends UpcomingSelectionIterator<Move<Solution_>> {

    private final Iterator<SubList> subListIterator;
    private final Iterator<ElementRef> destinationIterator;
    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final Random workingRandom;
    private final boolean selectReversingMoveToo;

    RandomSubListChangeMoveIterator(
            SubListSelector<Solution_> subListSelector,
            DestinationSelector<Solution_> destinationSelector,
            Random workingRandom,
            boolean selectReversingMoveToo) {
        this.subListIterator = subListSelector.iterator();
        this.destinationIterator = destinationSelector.iterator();
        this.listVariableDescriptor = subListSelector.getVariableDescriptor();
        this.workingRandom = workingRandom;
        this.selectReversingMoveToo = selectReversingMoveToo;
    }

    @Override
    protected Move<Solution_> createUpcomingSelection() {
        if (!subListIterator.hasNext() || !destinationIterator.hasNext()) {
            return noUpcomingSelection();
        }

        SubList subList = subListIterator.next();
        ElementRef destination = destinationIterator.next();
        boolean reversing = selectReversingMoveToo && workingRandom.nextBoolean();

        return new SubListChangeMove<>(
                listVariableDescriptor,
                subList,
                destination.entity(),
                destination.index(),
                reversing);
    }
}
