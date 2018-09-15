package atomicity;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressMeta;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;
import org.openjdk.jcstress.infra.results.J_Result;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is testing atomicity of an increment operation.
 * Each nested class represents one increment from actor 1 and one increment from actor 2
 *
 * @author Pavel.Krizskiy
 */
@Outcome(id = "2", expect = Expect.ACCEPTABLE, desc = "Both actors incremented the value")
@Outcome(expect = Expect.FORBIDDEN, desc = "Atomicity violated!")
public class AtomicityIncrementTests {

    @JCStressTest
    @JCStressMeta(AtomicityIncrementTests.class)
    @State
    public static class AtomicityIntIncrementTest {

        int value;

        @Actor
        public void actor1() {
            value++;
        }

        @Actor
        public void actor2() {
            value++;
        }

        @Arbiter
        public void arbiter(I_Result r) {
            r.r1 = value;
        }
    }

    @JCStressTest
    @JCStressMeta(AtomicityIncrementTests.class)
    @State
    public static class AtomicityLongIncrementTest {

        long value;

        @Actor
        public void actor1() {
            value++;
        }

        @Actor
        public void actor2() {
            value++;
        }

        @Arbiter
        public void arbiter(J_Result r) {
            r.r1 = value;
        }
    }

    @JCStressTest
    @JCStressMeta(AtomicityIncrementTests.class)
    @State
    public static class AtomicityVolatileIntIncrementTest {

        volatile int value;

        @Actor
        public void actor1() {
            value++;
        }

        @Actor
        public void actor2() {
            value++;
        }

        @Arbiter
        public void arbiter(I_Result r) {
            r.r1 = value;
        }
    }

    @JCStressTest
    @JCStressMeta(AtomicityIncrementTests.class)
    @State
    public static class AtomicityAtomicIntegerIncrementTest {

        AtomicInteger value = new AtomicInteger(0);

        @Actor
        public void actor1() {
            value.incrementAndGet();
        }

        @Actor
        public void actor2() {
            value.incrementAndGet();
        }

        @Arbiter
        public void arbiter(I_Result r) {
            r.r1 = value.get();
        }
    }

    @JCStressTest
    @JCStressMeta(AtomicityIncrementTests.class)
    public static class AtomicitySynchronizedIntIncrementTest {

        @State
        public static class TestingState {
            int value = 0;

            public synchronized int getValue() {
                return value;
            }

            public synchronized void incrementValue() {
                value++;
            }
        }

        @Actor
        public void actor1(TestingState testingState) {
            testingState.incrementValue();
        }

        @Actor
        public void actor2(TestingState testingState) {
            testingState.incrementValue();
        }

        @Arbiter
        public void arbiter(TestingState testingState, I_Result r) {
            r.r1 = testingState.getValue();
        }
    }
}
