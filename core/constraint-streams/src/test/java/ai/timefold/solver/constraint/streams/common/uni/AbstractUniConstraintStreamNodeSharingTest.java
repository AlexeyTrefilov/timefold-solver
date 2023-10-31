package ai.timefold.solver.constraint.streams.common.uni;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.Predicate;

import ai.timefold.solver.constraint.streams.common.AbstractConstraintStreamTest;
import ai.timefold.solver.constraint.streams.common.ConstraintStreamImplSupport;
import ai.timefold.solver.constraint.streams.common.ConstraintStreamNodeSharingTest;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import ai.timefold.solver.core.api.score.stream.bi.BiJoiner;
import ai.timefold.solver.core.api.score.stream.uni.UniConstraintStream;
import ai.timefold.solver.core.impl.testdata.domain.TestdataEntity;
import ai.timefold.solver.core.impl.testdata.domain.TestdataSolution;
import ai.timefold.solver.core.impl.testdata.domain.TestdataValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;

public abstract class AbstractUniConstraintStreamNodeSharingTest extends AbstractConstraintStreamTest implements
        ConstraintStreamNodeSharingTest {

    private ConstraintFactory constraintFactory;
    private UniConstraintStream<TestdataEntity> baseStream;

    protected AbstractUniConstraintStreamNodeSharingTest(
            ConstraintStreamImplSupport implSupport) {
        super(implSupport);
    }

    @BeforeEach
    public void setup() {
        constraintFactory = buildConstraintFactory(TestdataSolution.buildSolutionDescriptor());
        baseStream = constraintFactory.forEach(TestdataEntity.class);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************
    @Override
    @TestTemplate
    public void differentParentSameFilter() {
        Predicate<TestdataEntity> filter1 = a -> true;
        Predicate<TestdataEntity> filter2 = a -> false;

        assertThat(baseStream.filter(filter1))
                .isNotSameAs(baseStream.filter(filter2)
                        .filter(filter1));
    }

    @Override
    @TestTemplate
    public void sameParentDifferentFilter() {
        Predicate<TestdataEntity> filter1 = a -> true;
        Predicate<TestdataEntity> filter2 = a -> false;

        assertThat(baseStream.filter(filter1))
                .isNotSameAs(baseStream.filter(filter2));
    }

    @Override
    @TestTemplate
    public void sameParentSameFilter() {
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream.filter(filter1))
                .isSameAs(baseStream.filter(filter1));
    }

    // ************************************************************************
    // Join
    // ************************************************************************
    @Override
    @TestTemplate
    public void differentLeftParentJoin() {
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream.join(TestdataValue.class))
                .isNotSameAs(baseStream.filter(filter1).join(TestdataValue.class));
    }

    @Override
    @TestTemplate
    public void differentRightParentJoin() {
        Predicate<TestdataValue> filter1 = a -> true;

        assertThat(baseStream.join(TestdataValue.class))
                .isNotSameAs(baseStream.join(constraintFactory
                        .forEach(TestdataValue.class)
                        .filter(filter1)));
    }

    @Override
    @TestTemplate
    public void sameParentsDifferentIndexerJoin() {
        BiJoiner<TestdataEntity, TestdataValue> indexedJoiner1 = Joiners.equal(TestdataEntity::getCode, TestdataValue::getCode);
        BiJoiner<TestdataEntity, TestdataValue> indexedJoiner2 =
                Joiners.equal(TestdataEntity::toString, TestdataValue::toString);

        assertThat(baseStream.join(TestdataValue.class, indexedJoiner1))
                .isNotSameAs(baseStream.join(TestdataValue.class, indexedJoiner2));
    }

    @Override
    @TestTemplate
    public void sameParentsDifferentFilteringJoin() {
        BiJoiner<TestdataEntity, TestdataValue> filteringJoiner1 = Joiners.filtering((a, b) -> false);
        BiJoiner<TestdataEntity, TestdataValue> filteringJoiner2 = Joiners.filtering((a, b) -> true);

        assertThat(baseStream.join(TestdataValue.class, filteringJoiner1))
                .isNotSameAs(baseStream.join(TestdataValue.class, filteringJoiner2));
    }

    @Override
    @TestTemplate
    public void sameParentsJoin() {
        assertThat(baseStream.join(TestdataValue.class))
                .isSameAs(baseStream.join(TestdataValue.class));
    }

    @Override
    @TestTemplate
    public void sameParentsSameIndexerJoin() {
        BiJoiner<TestdataEntity, TestdataValue> indexedJoiner = Joiners.equal(TestdataEntity::getCode, TestdataValue::getCode);

        assertThat(baseStream.join(TestdataValue.class, indexedJoiner))
                .isSameAs(baseStream.join(TestdataValue.class, indexedJoiner));
    }

    @Override
    @TestTemplate
    public void sameParentsSameFilteringJoin() {
        BiJoiner<TestdataEntity, TestdataValue> filteringJoiner = Joiners.filtering((a, b) -> true);

        assertThat(baseStream.join(TestdataValue.class, filteringJoiner))
                .isSameAs(baseStream.join(TestdataValue.class, filteringJoiner));
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    @Override
    @TestTemplate
    public void ifExistsOtherDifferentParent() {
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream.ifExistsOther(TestdataEntity.class))
                .isNotSameAs(baseStream.filter(filter1).ifExistsOther(TestdataEntity.class));
    }

    @Override
    @TestTemplate
    public void ifExistsOtherSameParentDifferentIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.equal();
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.equal(TestdataEntity::getCode);

        assertThat(baseStream.ifExistsOther(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifExistsOther(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifExistsOtherSameParentDifferentFilter() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.filtering((a, b) -> a == b);
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.filtering((a, b) -> a != b);

        assertThat(baseStream.ifExistsOther(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifExistsOther(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifExistsOtherSameParentSameIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner = Joiners.equal();

        assertThat(baseStream.ifExistsOther(TestdataEntity.class, joiner))
                .isSameAs(baseStream.ifExistsOther(TestdataEntity.class, joiner));
    }

    // Cannot test same filter since ifExistsOther will create a new filtering predicate

    @Override
    @TestTemplate
    public void ifNotExistsOtherDifferentParent() {
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream.ifExistsOther(TestdataEntity.class))
                .isNotSameAs(baseStream.filter(filter1).ifNotExistsOther(TestdataEntity.class));
    }

    @Override
    @TestTemplate
    public void ifNotExistsOtherSameParentDifferentIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.equal();
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.equal(TestdataEntity::getCode);

        assertThat(baseStream.ifNotExistsOther(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifNotExistsOther(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifNotExistsOtherSameParentDifferentFilter() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.filtering((a, b) -> a == b);
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.filtering((a, b) -> a != b);

        assertThat(baseStream.ifNotExistsOther(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifNotExistsOther(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifNotExistsOtherSameParentSameIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner = Joiners.equal();

        assertThat(baseStream.ifNotExistsOther(TestdataEntity.class, joiner))
                .isSameAs(baseStream.ifNotExistsOther(TestdataEntity.class, joiner));
    }

    // Cannot test same filter since ifExistsOther will create a new filtering predicate

    @Override
    @TestTemplate
    public void ifExistsOtherIncludingNullVarsDifferentParent() {
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream.ifExistsOtherIncludingNullVars(TestdataEntity.class))
                .isNotSameAs(baseStream.filter(filter1).ifExistsOtherIncludingNullVars(TestdataEntity.class));
    }

    @Override
    @TestTemplate
    public void ifExistsOtherIncludingNullVarsSameParentDifferentIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.equal();
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.equal(TestdataEntity::getCode);

        assertThat(baseStream.ifExistsOtherIncludingNullVars(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifExistsOtherIncludingNullVars(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifExistsOtherIncludingNullVarsSameParentDifferentFilter() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.filtering((a, b) -> a == b);
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.filtering((a, b) -> a != b);

        assertThat(baseStream.ifExistsOtherIncludingNullVars(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifExistsOtherIncludingNullVars(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifExistsOtherIncludingNullVarsSameParentSameIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner = Joiners.equal();

        assertThat(baseStream.ifExistsOtherIncludingNullVars(TestdataEntity.class, joiner))
                .isSameAs(baseStream.ifExistsOtherIncludingNullVars(TestdataEntity.class, joiner));
    }

    @Override
    @TestTemplate
    public void ifNotExistsOtherIncludingNullVarsDifferentParent() {
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream.ifNotExistsOtherIncludingNullVars(TestdataEntity.class))
                .isNotSameAs(baseStream.filter(filter1).ifNotExistsOtherIncludingNullVars(TestdataEntity.class));
    }

    @Override
    @TestTemplate
    public void ifNotExistsOtherIncludingNullVarsSameParentDifferentIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.equal();
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.equal(TestdataEntity::getCode);

        assertThat(baseStream.ifNotExistsOtherIncludingNullVars(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifNotExistsOtherIncludingNullVars(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifNotExistsOtherIncludingNullVarsSameParentDifferentFilter() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.filtering((a, b) -> a == b);
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.filtering((a, b) -> a != b);

        assertThat(baseStream.ifNotExistsOtherIncludingNullVars(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifNotExistsOtherIncludingNullVars(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifNotExistsOtherIncludingNullVarsSameParentSameIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner = Joiners.equal();

        assertThat(baseStream.ifNotExistsOtherIncludingNullVars(TestdataEntity.class, joiner))
                .isSameAs(baseStream.ifNotExistsOtherIncludingNullVars(TestdataEntity.class, joiner));
    }

    @Override
    @TestTemplate
    @SuppressWarnings("unchecked")
    public void ifExistsDifferentParent() {
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream.ifExists(TestdataEntity.class))
                .isNotSameAs(baseStream.filter(filter1).ifExists(TestdataEntity.class));
    }

    @Override
    @TestTemplate
    @SuppressWarnings("unchecked")
    public void ifNotExistsDifferentParent() {
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream.ifNotExists(TestdataEntity.class))
                .isNotSameAs(baseStream.filter(filter1).ifNotExists(TestdataEntity.class));
    }

    @Override
    @TestTemplate
    @SuppressWarnings("unchecked")
    public void ifExistsIncludingNullVarsDifferentParent() {
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream.ifExistsIncludingNullVars(TestdataEntity.class))
                .isNotSameAs(baseStream.filter(filter1).ifExistsIncludingNullVars(TestdataEntity.class));
    }

    @Override
    @TestTemplate
    @SuppressWarnings("unchecked")
    public void ifNotExistsIncludingNullVarsDifferentParent() {
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream.ifNotExistsIncludingNullVars(TestdataEntity.class))
                .isNotSameAs(baseStream.filter(filter1).ifNotExistsIncludingNullVars(TestdataEntity.class));
    }

    @Override
    @TestTemplate
    public void ifExistsSameParentSameIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner = Joiners.equal();

        assertThat(baseStream.ifExists(TestdataEntity.class, joiner))
                .isSameAs(baseStream.ifExists(TestdataEntity.class, joiner));
    }

    @Override
    @TestTemplate
    public void ifExistsSameParentSameFilter() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner = Joiners.filtering((a, b) -> a == b);

        assertThat(baseStream.ifExists(TestdataEntity.class, joiner))
                .isSameAs(baseStream.ifExists(TestdataEntity.class, joiner));
    }

    @Override
    @TestTemplate
    public void ifNotExistsSameParentSameIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner = Joiners.equal();

        assertThat(baseStream.ifNotExists(TestdataEntity.class, joiner))
                .isSameAs(baseStream.ifNotExists(TestdataEntity.class, joiner));
    }

    @Override
    @TestTemplate
    public void ifNotExistsSameParentSameFilter() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner = Joiners.filtering((a, b) -> a == b);

        assertThat(baseStream.ifNotExists(TestdataEntity.class, joiner))
                .isSameAs(baseStream.ifNotExists(TestdataEntity.class, joiner));
    }

    @Override
    @TestTemplate
    public void ifExistsIncludingNullVarsSameParentSameIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner = Joiners.equal();

        assertThat(baseStream.ifExistsIncludingNullVars(TestdataEntity.class, joiner))
                .isSameAs(baseStream.ifExistsIncludingNullVars(TestdataEntity.class, joiner));
    }

    @Override
    @TestTemplate
    public void ifExistsIncludingNullVarsSameParentSameFilter() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner = Joiners.filtering((a, b) -> a == b);

        assertThat(baseStream.ifExistsIncludingNullVars(TestdataEntity.class, joiner))
                .isSameAs(baseStream.ifExistsIncludingNullVars(TestdataEntity.class, joiner));
    }

    @Override
    @TestTemplate
    public void ifNotExistsIncludingNullVarsSameParentSameIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner = Joiners.equal();

        assertThat(baseStream.ifNotExistsIncludingNullVars(TestdataEntity.class, joiner))
                .isSameAs(baseStream.ifNotExistsIncludingNullVars(TestdataEntity.class, joiner));
    }

    @Override
    @TestTemplate
    public void ifNotExistsIncludingNullVarsSameParentSameFilter() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner = Joiners.filtering((a, b) -> a == b);

        assertThat(baseStream.ifNotExistsIncludingNullVars(TestdataEntity.class, joiner))
                .isSameAs(baseStream.ifNotExistsIncludingNullVars(TestdataEntity.class, joiner));
    }

    @Override
    @TestTemplate
    public void ifExistsSameParentDifferentIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.equal();
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.equal(TestdataEntity::getCode);

        assertThat(baseStream.ifExists(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifExists(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifExistsSameParentDifferentFilter() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.filtering((a, b) -> a == b);
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.filtering((a, b) -> a != b);

        assertThat(baseStream.ifExists(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifExists(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifNotExistsSameParentDifferentIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.equal();
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.equal(TestdataEntity::getCode);

        assertThat(baseStream.ifNotExists(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifNotExists(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifNotExistsSameParentDifferentFilter() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.filtering((a, b) -> a == b);
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.filtering((a, b) -> a != b);

        assertThat(baseStream.ifNotExists(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifNotExists(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifExistsIncludingNullVarsSameParentDifferentIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.equal();
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.equal(TestdataEntity::getCode);

        assertThat(baseStream.ifExistsIncludingNullVars(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifExistsIncludingNullVars(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifExistsIncludingNullVarsSameParentDifferentFilter() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.filtering((a, b) -> a == b);
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.filtering((a, b) -> a != b);

        assertThat(baseStream.ifExistsIncludingNullVars(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifExistsIncludingNullVars(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifNotExistsIncludingNullVarsSameParentDifferentIndexer() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.equal();
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.equal(TestdataEntity::getCode);

        assertThat(baseStream.ifNotExistsIncludingNullVars(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifNotExistsIncludingNullVars(TestdataEntity.class, joiner2));
    }

    @Override
    @TestTemplate
    public void ifNotExistsIncludingNullVarsSameParentDifferentFilter() {
        BiJoiner<TestdataEntity, TestdataEntity> joiner1 = Joiners.filtering((a, b) -> a == b);
        BiJoiner<TestdataEntity, TestdataEntity> joiner2 = Joiners.filtering((a, b) -> a != b);

        assertThat(baseStream.ifNotExistsIncludingNullVars(TestdataEntity.class, joiner1))
                .isNotSameAs(baseStream.ifNotExistsIncludingNullVars(TestdataEntity.class, joiner2));
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    // TODO

    // ************************************************************************
    // Map/expand/flatten/distinct/concat
    // ************************************************************************

    // TODO Map/expand/flatten

    @Override
    @TestTemplate
    public void differentParentDistinct() {
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream.distinct())
                .isNotSameAs(baseStream.filter(filter1).distinct());
    }

    @Override
    @TestTemplate
    public void sameParentDistinct() {
        assertThat(baseStream.distinct())
                .isSameAs(baseStream.distinct());
    }

    @Override
    @TestTemplate
    public void differentFirstSourceConcat() {
        Predicate<TestdataEntity> sourceFilter = Objects::nonNull;
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream
                .concat(baseStream.filter(filter1)))
                .isNotSameAs(baseStream.filter(sourceFilter)
                        .concat(baseStream.filter(filter1)));
    }

    @Override
    @TestTemplate
    public void differentSecondSourceConcat() {
        Predicate<TestdataEntity> sourceFilter = Objects::nonNull;
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream
                .filter(filter1)
                .concat(baseStream))
                .isNotSameAs(baseStream
                        .filter(filter1)
                        .concat(baseStream.filter(sourceFilter)));
    }

    @Override
    @TestTemplate
    public void sameSourcesConcat() {
        Predicate<TestdataEntity> filter1 = a -> true;

        assertThat(baseStream
                .concat(baseStream.filter(filter1)))
                .isSameAs(baseStream.concat(baseStream.filter(filter1)));
    }
}