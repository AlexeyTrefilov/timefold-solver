package ai.timefold.solver.examples.nqueens.moves;

import ai.timefold.solver.core.api.score.director.ScoreDirector;
import ai.timefold.solver.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import ai.timefold.solver.examples.nqueens.domain.NQueens;

import java.util.Iterator;
import java.util.Random;

import static org.apache.commons.math3.util.ArithmeticUtils.pow;

public class ChangeMoveFactory implements MoveIteratorFactory<NQueens, ChangeMove> {
    @Override
    public long getSize(ScoreDirector<NQueens> scoreDirector) {
        return pow(scoreDirector.getWorkingSolution().getN(), 2);
    }

    @Override
    public Iterator<ChangeMove> createOriginalMoveIterator(ScoreDirector<NQueens> scoreDirector) {
        NQueens nqueens = scoreDirector.getWorkingSolution();
        return nqueens.getQueenList().stream().flatMap(queen ->
                nqueens.getRowList().stream().map(row -> new ChangeMove(queen, row))).iterator();
    }

    @Override
    public Iterator<ChangeMove> createRandomMoveIterator(ScoreDirector<NQueens> scoreDirector, Random workingRandom) {
        return createOriginalMoveIterator(scoreDirector);
    }
}
