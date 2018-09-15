package map;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;
import org.openjdk.jcstress.infra.results.LL_Result;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represents is testing put operations of different {@link Map} implementations.
 *
 * @author Pavel.Krizskiy
 */
public class MapTests {

    /**
     * Tests for {@link HashMap}
     */
    public static class HashMapTests {

        @State
        public static class HashMapState {
            public Map<String, String> map = new HashMap<>();
        }

        @JCStressTest
        @Outcome(id = "2", expect = Expect.ACCEPTABLE, desc = "Map has correct size")
        @Outcome(expect = Expect.FORBIDDEN, desc = "Error: Map size is incorrect! Race condition occurred!")
        public static class HashMapSizeTest {

            @Actor
            public void actor1(HashMapState hashMapState) {
                hashMapState.map.put("A", "Val1");
            }

            @Actor
            public void actor2(HashMapState hashMapState) {
                hashMapState.map.put("B", "Val2");
            }

            @Arbiter
            public void arbiter(HashMapState hashMapState, I_Result r) {
                r.r1 = hashMapState.map.size();
            }
        }

        @JCStressTest
        @Outcome(id = "Val1, Val2", expect = Expect.ACCEPTABLE, desc = "Map was filled correctly")
        @Outcome(id = "Val1, null", expect = Expect.FORBIDDEN, desc = "Error: Second value missed")
        @Outcome(id = "null, Val2", expect = Expect.FORBIDDEN, desc = "Error: First value missed")
        @Outcome(expect = Expect.FORBIDDEN, desc = "Error")
        public static class HashMapValueTest {

            @Actor
            public void actor1(HashMapState hashMapState, LL_Result r) {
                hashMapState.map.put("A", "Val1");
                r.r1 = hashMapState.map.get("A");
            }

            @Actor
            public void actor2(HashMapState hashMapState, LL_Result r) {
                hashMapState.map.put("B", "Val2");
                r.r2 = hashMapState.map.get("B");
            }
        }
    }

    /**
     * Test for {@link ConcurrentHashMap}
     */
    @JCStressTest
    @Outcome(id = "Val1, Val2", expect = Expect.ACCEPTABLE, desc = "Map was filled correctly")
    @Outcome(id = "Val1, null", expect = Expect.FORBIDDEN, desc = "Error: Second value missed")
    @Outcome(id = "null, Val2", expect = Expect.FORBIDDEN, desc = "Error: First value missed")
    @Outcome(expect = Expect.FORBIDDEN, desc = "Error")
    public static class ConcurrentHashMapValueTest {

        @State
        public static class ConcurrentMapState {
            public Map<String, String> map = new ConcurrentHashMap<>();
        }

        @Actor
        public void actor1(ConcurrentMapState concurrentMapState, LL_Result r) {
            concurrentMapState.map.put("A", "Val1");
            r.r1 = concurrentMapState.map.get("A");
        }

        @Actor
        public void actor2(ConcurrentMapState concurrentMapState, LL_Result r) {
            concurrentMapState.map.put("B", "Val2");
            r.r2 = concurrentMapState.map.get("B");
        }
    }
}
