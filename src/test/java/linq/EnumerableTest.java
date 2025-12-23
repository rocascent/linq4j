package linq;

import linq.collections.enumerable.ArrayEnumerable;
import linq.collections.enumerable.EmptyEnumerable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EnumerableTest {

    // ============= 测试数据准备 =============

    private Enumerable<Integer> getIntegerEnumerable() {
        return Enumerable.of(1, 2, 3, 4, 5);
    }

    private Enumerable<String> getStringEnumerable() {
        return Enumerable.of("apple", "banana", "cherry", "date", "elderberry");
    }

    private Enumerable<User> getUserEnumerable() {
        return Enumerable.of(
                new User(1, "Alice", 25),
                new User(2, "Bob", 30),
                new User(3, "Charlie", 35),
                new User(4, "David", 40)
        );
    }

    static class User {
        private int id;
        private String name;
        private int age;

        public User(int id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof User user)) return false;
            return id == user.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    // ============= 静态方法测试 =============

    @Test
    void testEmpty() {
        // Arrange
        Enumerable<String> empty = Enumerable.empty();

        // Act & Assert
        assertEquals(0, empty.count());
        assertFalse(empty.iterator().hasNext());
        assertInstanceOf(EmptyEnumerable.class, empty);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5})
    void testOfWithArray(int size) {
        // Arrange
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }

        // Act
        Enumerable<Integer> enumerable = Enumerable.of(array);

        // Assert
        assertEquals(size, enumerable.count());
        if (size > 0) {
            assertInstanceOf(ArrayEnumerable.class, enumerable);
        }
    }

    @Test
    void testOfWithNullArray() {
        // Act
        Enumerable<Integer> enumerable = Enumerable.of((Integer[]) null);

        // Assert
        assertEquals(0, enumerable.count());
        assertInstanceOf(EmptyEnumerable.class, enumerable);
    }

    @Test
    void testOfWithIterable() {
        // Arrange
        List<String> list = Arrays.asList("a", "b", "c");

        // Act
        Enumerable<String> enumerable = Enumerable.of(list);

        // Assert
        assertEquals(3, enumerable.count());
        assertTrue(enumerable.iterator().hasNext());
    }

    @Test
    void testOfWithNullIterable() {
        // Act
        Enumerable<String> enumerable = Enumerable.of((Iterable<String>) null);

        // Assert
        assertEquals(0, enumerable.count());
        assertInstanceOf(EmptyEnumerable.class, enumerable);
    }

    @Test
    void testIsEmptyArray() {
        // Arrange
        Integer[] emptyArray = new Integer[0];
        Integer[] nonEmptyArray = new Integer[]{1, 2, 3};
        ArrayEnumerable<Integer> emptyEnumerable = new ArrayEnumerable<>(emptyArray);
        ArrayEnumerable<Integer> nonEmptyEnumerable = new ArrayEnumerable<>(nonEmptyArray);

        // Act & Assert
        assertTrue(Enumerable.isEmptyArray(emptyEnumerable));
        assertFalse(Enumerable.isEmptyArray(nonEmptyEnumerable));
    }

    // ============= count() 方法测试 =============

    @Test
    void testCount() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act
        int count = enumerable.count();

        // Assert
        assertEquals(5, count);
    }

    @Test
    void testCountWithPredicate() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();
        Predicate<Integer> evenPredicate = x -> x % 2 == 0;

        // Act
        int count = enumerable.count(evenPredicate);

        // Assert
        assertEquals(2, count); // 2, 4
    }

    @Test
    void testCountWithPredicate_NoMatches() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();
        Predicate<Integer> greaterThan10 = x -> x > 10;

        // Act
        int count = enumerable.count(greaterThan10);

        // Assert
        assertEquals(0, count);
    }

    @Test
    void testLongCount() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act
        long count = enumerable.longCount();

        // Assert
        assertEquals(5L, count);
    }

    // ============= first() 方法测试 =============

    @Test
    void testFirst() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act
        Integer first = enumerable.first();

        // Assert
        assertEquals(Integer.valueOf(1), first);
    }

    @Test
    void testFirst_EmptySequence() {
        // Arrange
        Enumerable<Integer> enumerable = Enumerable.empty();

        // Act & Assert
        assertThrows(IllegalStateException.class, enumerable::first);
    }

    @Test
    void testFirstWithPredicate() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();
        Predicate<Integer> greaterThan3 = x -> x > 3;

        // Act
        Integer first = enumerable.first(greaterThan3);

        // Assert
        assertEquals(Integer.valueOf(4), first);
    }

    @Test
    void testFirstWithPredicate_NoMatches() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();
        Predicate<Integer> greaterThan10 = x -> x > 10;

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> enumerable.first(greaterThan10));
    }

    @Test
    void testFirstWithPredicate_NullPredicate() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> enumerable.first(null));
    }

    // ============= firstOrDefault() 方法测试 =============

    @Test
    void testFirstOrDefault() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act
        Integer first = enumerable.firstOrDefault();

        // Assert
        assertEquals(Integer.valueOf(1), first);
    }

    @Test
    void testFirstOrDefault_EmptySequence() {
        // Arrange
        Enumerable<Integer> enumerable = Enumerable.empty();

        // Act
        Integer result = enumerable.firstOrDefault();

        // Assert
        assertNull(result);
    }

    @Test
    void testFirstOrDefaultWithDefaultValue() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act
        Integer result = enumerable.firstOrDefault(99);

        // Assert
        assertEquals(Integer.valueOf(1), result);
    }

    @Test
    void testFirstOrDefaultWithDefaultValue_EmptySequence() {
        // Arrange
        Enumerable<Integer> enumerable = Enumerable.empty();

        // Act
        Integer result = enumerable.firstOrDefault(99);

        // Assert
        assertEquals(Integer.valueOf(99), result);
    }

    @Test
    void testFirstOrDefaultWithPredicate() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();
        Predicate<Integer> evenPredicate = x -> x % 2 == 0;

        // Act
        Integer result = enumerable.firstOrDefault(evenPredicate);

        // Assert
        assertEquals(Integer.valueOf(2), result);
    }

    @Test
    void testFirstOrDefaultWithPredicate_NoMatches() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();
        Predicate<Integer> greaterThan10 = x -> x > 10;

        // Act
        Integer result = enumerable.firstOrDefault(greaterThan10);

        // Assert
        assertNull(result);
    }

    @Test
    void testFirstOrDefaultWithPredicateAndDefaultValue() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();
        Predicate<Integer> greaterThan3 = x -> x > 3;

        // Act
        Integer result = enumerable.firstOrDefault(greaterThan3, 99);

        // Assert
        assertEquals(Integer.valueOf(4), result);
    }

    @Test
    void testFirstOrDefaultWithPredicateAndDefaultValue_NoMatches() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();
        Predicate<Integer> greaterThan10 = x -> x > 10;

        // Act
        Integer result = enumerable.firstOrDefault(greaterThan10, 99);

        // Assert
        assertEquals(Integer.valueOf(99), result);
    }

    // ============= select() 方法测试 =============

    @Test
    void testSelect() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();
        Function<Integer, String> toString = Object::toString;

        // Act
        Enumerable<String> result = enumerable.select(toString);

        // Assert
        assertEquals(5, result.count());
        assertEquals("1", result.first());
        assertEquals("5", result.toList().get(4));
    }

    @Test
    void testSelectWithIndex() {
        // Arrange
        Enumerable<String> enumerable = getStringEnumerable();

        // Act
        Enumerable<String> result = enumerable.select((item, index) ->
                String.format("%d: %s", index, item.toUpperCase()));

        // Assert
        List<String> list = result.toList();
        assertEquals("0: APPLE", list.get(0));
        assertEquals("4: ELDERBERRY", list.get(4));
    }

    @Test
    void testSelect_NullSelector() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> enumerable.select((Function<Integer, String>) null));
    }

    // ============= selectMany() 方法测试 =============

    @Test
    void testSelectMany() {
        // Arrange
        Enumerable<String> enumerable = Enumerable.of("abc", "def");
        Function<String, Iterable<Character>> charSelector =
                s -> Enumerable.of(s.chars().mapToObj(c -> (char) c).toArray(Character[]::new));

        // Act
        Enumerable<Character> result = enumerable.selectMany(charSelector);

        // Assert
        assertEquals(6, result.count());
        List<Character> chars = result.toList();
        assertEquals('a', chars.get(0));
        assertEquals('f', chars.get(5));
    }

    @Test
    void testSelectManyWithResultSelector() {
        // Arrange
        Enumerable<String> enumerable = Enumerable.of("abc", "def");
        Function<String, Iterable<Character>> collectionSelector =
                s -> Enumerable.of(s.chars().mapToObj(c -> (char) c).toArray(Character[]::new));

        // Act
        Enumerable<String> result = enumerable.selectMany(
                collectionSelector,
                (source, character) -> source + "-" + character
        );

        // Assert
        List<String> list = result.toList();
        assertTrue(list.contains("abc-a"));
        assertTrue(list.contains("def-f"));
        assertEquals(6, list.size());
    }

    @Test
    void testSelectMany_EmptyCollection() {
        // Arrange
        Enumerable<String> enumerable = Enumerable.of("test");
        Function<String, Iterable<Integer>> emptySelector = s -> Enumerable.empty();

        // Act
        Enumerable<Integer> result = enumerable.selectMany(emptySelector);

        // Assert
        assertEquals(0, result.count());
    }

    // ============= skip() 方法测试 =============

    @ParameterizedTest
    @MethodSource("skipTestData")
    void testSkip(int skipCount, int expectedCount, Integer expectedFirst) {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act
        Enumerable<Integer> result = enumerable.skip(skipCount);

        // Assert
        assertEquals(expectedCount, result.count());
        if (expectedFirst != null) {
            assertEquals(expectedFirst, result.first());
        }
    }

    private static Stream<Arguments> skipTestData() {
        return Stream.of(
                Arguments.of(0, 5, 1),   // 不跳过
                Arguments.of(2, 3, 3),   // 跳过前2个
                Arguments.of(4, 1, 5),   // 跳过前4个
                Arguments.of(5, 0, null), // 跳过所有
                Arguments.of(10, 0, null) // 跳过超过总数
        );
    }

    @Test
    void testSkip_NegativeCount() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act
        Enumerable<Integer> result = enumerable.skip(-1);

        // Assert
        assertEquals(5, result.count()); // 负值应该被当作0处理
    }

    // ============= sum 方法测试 =============

    @Test
    void testSumInt() {
        // Arrange
        Enumerable<User> enumerable = getUserEnumerable();
        Function<User, Integer> ageSelector = User::getAge;

        // Act
        int sum = enumerable.sumInt(ageSelector);

        // Assert
        assertEquals(25 + 30 + 35 + 40, sum);
    }

    @Test
    void testSumInt_Empty() {
        // Arrange
        Enumerable<User> enumerable = Enumerable.empty();
        Function<User, Integer> ageSelector = User::getAge;

        // Act
        int sum = enumerable.sumInt(ageSelector);

        // Assert
        assertEquals(0, sum);
    }

    @Test
    void testSumLong() {
        // Arrange
        Enumerable<Long> enumerable = Enumerable.of(100L, 200L, 300L);
        Function<Long, Long> selector = x -> x * 2;

        // Act
        long sum = enumerable.sumLong(selector);

        // Assert
        assertEquals(1200L, sum); // (200 + 400 + 600)
    }

    @Test
    void testSumDouble() {
        // Arrange
        Enumerable<Double> enumerable = Enumerable.of(1.5, 2.5, 3.5);
        Function<Double, Double> selector = x -> x * 2;

        // Act
        double sum = enumerable.sumDouble(selector);

        // Assert
        assertEquals(15.0, sum, 0.0001); // (3.0 + 5.0 + 7.0)
    }

    @Test
    void testSumBigDecimal() {
        // Arrange
        Enumerable<BigDecimal> enumerable = Enumerable.of(
                new BigDecimal("10.5"),
                new BigDecimal("20.5"),
                new BigDecimal("30.5")
        );
        Function<BigDecimal, BigDecimal> selector = x -> x.multiply(new BigDecimal("2"));

        // Act
        BigDecimal sum = enumerable.sumBigDecimal(selector);

        // Assert
        assertEquals(new BigDecimal("123.0"), sum); // (21.0 + 41.0 + 61.0)
    }

    // ============= where() 方法测试 =============

    @Test
    void testWhere() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();
        Predicate<Integer> evenPredicate = x -> x % 2 == 0;

        // Act
        Enumerable<Integer> result = enumerable.where(evenPredicate);

        // Assert
        assertEquals(2, result.count());
        assertTrue(result.toList().containsAll(Arrays.asList(2, 4)));
    }

    @Test
    void testWhereWithIndex() {
        // Arrange
        Enumerable<String> enumerable = getStringEnumerable();

        // Act
        Enumerable<String> result = enumerable.where((item, index) -> index % 2 == 0);

        // Assert
        List<String> list = result.toList();
        assertEquals(3, list.size()); // 索引0,2,4
        assertEquals("apple", list.get(0));
        assertEquals("cherry", list.get(1));
        assertEquals("elderberry", list.get(2));
    }

    @Test
    void testWhere_NoMatches() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();
        Predicate<Integer> greaterThan10 = x -> x > 10;

        // Act
        Enumerable<Integer> result = enumerable.where(greaterThan10);

        // Assert
        assertEquals(0, result.count());
    }

    @Test
    void testWhere_NullPredicate() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> enumerable.where((Predicate<Integer>) null));
    }

    // ============= toList() 方法测试 =============

    @Test
    void testToList() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act
        List<Integer> list = enumerable.toList();

        // Assert
        assertEquals(5, list.size());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
    }

    @Test
    void testToList_Empty() {
        // Arrange
        Enumerable<Integer> enumerable = Enumerable.empty();

        // Act
        List<Integer> list = enumerable.toList();

        // Assert
        assertTrue(list.isEmpty());
    }

    @Test
    void testToList_MaintainsOrder() {
        // Arrange
        Enumerable<Integer> enumerable = Enumerable.of(3, 1, 4, 1, 5);

        // Act
        List<Integer> list = enumerable.toList();

        // Assert
        assertEquals(Arrays.asList(3, 1, 4, 1, 5), list);
    }

    @Test
    void testToList_ImmutableCopy() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act
        List<Integer> list = enumerable.toList();

        // 修改返回的列表不应该影响原始Enumerable
        list.add(6);

        // Assert
        assertEquals(5, enumerable.count()); // 原始不变
        assertEquals(6, list.size()); // 修改的副本
    }

    // ============= 迭代器相关测试 =============

    @Test
    void testIterator() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act
        Iterator<Integer> iterator = enumerable.iterator();

        // Assert
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(1), iterator.next());
    }

    @Test
    void testForEachLoop() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();
        List<Integer> collected = new ArrayList<>();

        // Act
        for (Integer num : enumerable) {
            collected.add(num);
        }

        // Assert
        assertEquals(5, collected.size());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), collected);
    }

    @Test
    void testEnumerator() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act
        Enumerator<Integer> enumerator = enumerable.enumerator();

        // Assert
        assertNotNull(enumerator);
        assertTrue(enumerator.moveNext());
        assertEquals(Integer.valueOf(1), enumerator.current());
    }

    // ============= 边界条件测试 =============

    @Test
    void testLargeCollection() {
        // Arrange
        List<Integer> largeList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            largeList.add(i);
        }
        Enumerable<Integer> enumerable = Enumerable.of(largeList);

        // Act
        int count = enumerable.count();
        Enumerable<Integer> filtered = enumerable.where(x -> x % 2 == 0);

        // Assert
        assertEquals(10000, count);
        assertEquals(5000, filtered.count());
    }

    @Test
    void testNullElements() {
        // Arrange
        Enumerable<String> enumerable = Enumerable.of("a", null, "c", null, "e");

        // Act
        int count = enumerable.count();
        int nonNullCount = enumerable.count(Objects::nonNull);

        // Assert
        assertEquals(5, count);
        assertEquals(3, nonNullCount);
    }

    // ============= 集成测试 =============

    @Test
    void testMethodChaining() {
        // Arrange
        Enumerable<Integer> enumerable = getIntegerEnumerable();

        // Act
        List<Integer> result = enumerable
                .where(x -> x > 2)            // 3, 4, 5
                .skip(1)                      // 4, 5
                .select(x -> x * 10)          // 40, 50
                .toList();

        // Assert
        assertEquals(Arrays.asList(40, 50), result);
    }

    @Test
    void testComplexQuery() {
        // Arrange
        Enumerable<User> users = getUserEnumerable();

        // Act
        List<String> result = users
                .where(user -> user.getAge() >= 30)      // Bob(30), Charlie(35), David(40)
                .select(user -> user.getName())          // "Bob", "Charlie", "David"
                .where(name -> name.length() > 3)        // "Charlie", "David"
                .select(name -> name.toUpperCase())      // "CHARLIE", "DAVID"
                .toList();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("CHARLIE"));
        assertTrue(result.contains("DAVID"));
    }

    // ============= 性能测试 =============

//    @Test
//    void testLazyEvaluation() {
//        // Arrange
//        Enumerable<Integer> source = mock(Enumerable.class);
//        when(source.enumerator()).thenReturn(new TestEnumerator<>(Arrays.asList(1, 2, 3, 4, 5)));
//
//        // 创建模拟的selector和predicate
//        Function<Integer, Integer> mockSelector = mock(Function.class);
//        when(mockSelector.apply(any())).thenReturn(0); // 总是返回0
//
//        Predicate<Integer> mockPredicate = mock(Predicate.class);
//        when(mockPredicate.test(any())).thenReturn(true); // 总是返回true
//
//        // Act
//        Enumerable<Integer> result = source
//                .where(mockPredicate)
//                .select(mockSelector);
//
//        // 此时不应该调用任何方法（延迟执行）
//        verify(source, never()).enumerator();
//        verify(mockPredicate, never()).test(any());
//        verify(mockSelector, never()).apply(any());
//
//        // 触发执行
//        result.first();
//
//        // Assert - 验证方法被调用
//        verify(source).enumerator();
//        verify(mockPredicate, atLeastOnce()).test(any());
//        verify(mockSelector, atLeastOnce()).apply(any());
//    }
//
//    // 测试用的Enumerator实现
//    private static class TestEnumerator<T> implements Enumerator<T> {
//        private final Iterator<T> iterator;
//        private T current;
//
//        TestEnumerator(Iterable<T> source) {
//            this.iterator = source.iterator();
//        }
//
//        @Override
//        public boolean moveNext() {
//            if (iterator.hasNext()) {
//                current = iterator.next();
//                return true;
//            }
//            return false;
//        }
//
//        @Override
//        public T current() {
//            return current;
//        }
//
//        @Override
//        public void reset() {
//            throw new UnsupportedOperationException();
//        }
//    }
}