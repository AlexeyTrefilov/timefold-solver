package ai.timefold.solver.examples.nqueens.moves;

import ai.timefold.solver.core.api.score.director.ScoreDirector;
import ai.timefold.solver.core.impl.heuristic.move.AbstractMove;
import ai.timefold.solver.examples.nqueens.domain.NQueens;
import ai.timefold.solver.examples.nqueens.domain.Queen;
import ai.timefold.solver.examples.nqueens.domain.Row;

import static ai.timefold.solver.examples.nqueens.app.NQueensHelloWorld.toDisplayString;

public class ChangeMove extends AbstractMove<NQueens> {
    Queen queen;
    Row row;
    Row originalRow;
    public ChangeMove(Queen queen, Row row){
        this.queen = queen;
        this.row = row;
        originalRow = queen.getRow();
    }

    @Override
    protected AbstractMove<NQueens> createUndoMove(ScoreDirector<NQueens> scoreDirector) {
        return new ChangeMove(queen, originalRow);
    }


    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<NQueens> scoreDirector) {
        scoreDirector.beforeVariableChanged(queen, "row");
        queen.setRow(row);
        scoreDirector.afterVariableChanged(queen, "row");
        scoreDirector.triggerVariableListeners();

    }

    @Override
    public boolean isMoveDoable(ScoreDirector<NQueens> scoreDirector) {
        System.out.println(this+": "+scoreDirector.getWorkingSolution().getScore());
        //System.out.println(toDisplayString(scoreDirector.getWorkingSolution()));
        return queen.getRow()!= row;
    }

    public String toString(){
        return queen+ ": "+queen.getRow()+" -> "+ row;
    }
}
