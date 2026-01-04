package linq;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static linq.Linq.*;
import static org.junit.jupiter.api.Assertions.*;

public class JavaTest {

    // ========== 测试数据和辅助方法 ==========

    private Enumerable<Integer> getIntEnumerable() {
        return of(1, 2, 3, 4, 5);
    }

    private Enumerable<String> getStringEnumerable() {
        return of("apple", "banana", "cherry", "date", "elderberry");
    }

    private Enumerable<Person> getPersonEnumerable() {
        return of(
                new Person("Alice", 25, 50000.0),
                new Person("Bob", 30, 60000.0),
                new Person("Charlie", 25, 55000.0),
                new Person("David", 35, 70000.0),
                new Person("Eve", 30, 65000.0)
        );
    }

    static class Person {
        String name;
        int age;
        double salary;

        Person(String name, int age, double salary) {
            this.name = name;
            this.age = age;
            this.salary = salary;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public double getSalary() {
            return salary;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return age == person.age &&
                    Double.compare(person.salary, salary) == 0 &&
                    Objects.equals(name, person.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age, salary);
        }
    }

    // ========== 测试 Iterable 接口 ==========

    @Test
    public void testIterator() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        List<Integer> result = new ArrayList<>();
        for (Integer i : enumerable) {
            result.add(i);
        }
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result);
    }

    // ========== 测试 aggregate 方法 ==========

    @Test
    public void testAggregate() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        int result = enumerable.aggregate(Integer::sum);
        assertEquals(15, result);
    }

    @Test
    public void testAggregateEmptyThrowsException() {
        Enumerable<Integer> empty = of();
        assertThrows(UnsupportedOperationException.class, () ->
                empty.aggregate(Integer::sum));
    }

    @Test
    public void testAggregateWithSeed() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        int result = enumerable.aggregate(10, Integer::sum);
        assertEquals(25, result);
    }

    @Test
    public void testAggregateWithSeedAndResultSelector() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        String result = enumerable.aggregate(0,
                Integer::sum,
                total -> "Sum: " + total);
        assertEquals("Sum: 15", result);
    }

    @Test
    public void testAggregateBy() {
        Enumerable<Person> persons = getPersonEnumerable();
        Enumerable<Map.Entry<Integer, Double>> result = persons.aggregateBy(
                Person::getAge,
                0.0,
                (acc, p) -> acc + p.getSalary()
        );

        Map<Integer, Double> map = result.toList().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(105000.0, map.get(25), 0.001);
        assertEquals(125000.0, map.get(30), 0.001);
        assertEquals(70000.0, map.get(35), 0.001);
    }

    @Test
    public void testAggregateByWithSeedSelector() {
        Enumerable<Person> persons = getPersonEnumerable();
        Enumerable<Map.Entry<Integer, List<String>>> result = persons.aggregateBy(
                Person::getAge,
                (_, _) -> new ArrayList<>(),
                (_, list, p) -> {
                    list.add(p.getName());
                    return list;
                }
        );

        Map<Integer, List<String>> map = result.toList().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertTrue(map.get(25).containsAll(Arrays.asList("Alice", "Charlie")));
        assertTrue(map.get(30).containsAll(Arrays.asList("Bob", "Eve")));
        assertEquals(1, map.get(35).size());
        assertEquals("David", map.get(35).getFirst());
    }

    // ========== 测试 any 和 all 方法 ==========

    @Test
    public void testAny() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertTrue(enumerable.any());

        Enumerable<Integer> empty = of();
        assertFalse(empty.any());
    }

    @Test
    public void testAnyWithPredicate() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertTrue(enumerable.any(x -> x > 3));
        assertFalse(enumerable.any(x -> x > 10));
    }

    @Test
    public void testAll() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertTrue(enumerable.all(x -> x > 0));
        assertFalse(enumerable.all(x -> x > 3));

        Enumerable<Integer> empty = of();
        assertTrue(empty.all(x -> x > 0)); // 空集合返回 true
    }

    // ========== 测试 average 方法 ==========

    @Test
    public void testAverageInt() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(6.0, enumerable.averageInt(x -> x * 2), 0.001);
    }

    @Test
    public void testAverageLong() {
        Enumerable<Long> enumerable = of(1L, 2L, 3L, 4L, 5L);
        assertEquals(6.0, enumerable.averageLong(x -> x * 2), 0.001);
    }

    @Test
    public void testAverageFloat() {
        Enumerable<Float> enumerable = of(1.0f, 2.0f, 3.0f, 4.0f, 5.0f);
        assertEquals(6.0f, enumerable.averageFloat(x -> x * 2), 0.001f);
    }

    @Test
    public void testAverageDouble() {
        Enumerable<Double> enumerable = of(1.0, 2.0, 3.0, 4.0, 5.0);
        assertEquals(6.0, enumerable.averageDouble(x -> x * 2), 0.001);
    }

    @Test
    public void testAverageBigDecimal() {
        Enumerable<BigDecimal> enumerable = of(
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(4),
                BigDecimal.valueOf(5)
        );
        assertEquals(BigDecimal.valueOf(6), enumerable.averageBigDecimal(x -> x.multiply(BigDecimal.valueOf(2))));
    }

    @Test
    public void testAverageBigDecimalEmptyThrowsException() {
        Enumerable<BigDecimal> empty = of();
        assertThrows(ArithmeticException.class, () ->
                empty.averageBigDecimal(x -> x));
    }

    // ========== 测试 cast 和 ofType 方法 ==========

    @Test
    public void testCast() {
        List<Object> mixedList = Arrays.asList("a", "b", "c");
        Enumerable<Object> enumerable = of(mixedList);
        Enumerable<String> casted = enumerable.cast(String.class);

        assertEquals(3, casted.count());
        assertTrue(casted.contains("a"));
    }

    @Test
    public void testCastThrowsClassCastException() {
        List<Object> mixedList = Arrays.asList("a", 1, "c");
        Enumerable<Object> enumerable = of(mixedList);

        assertThrows(ClassCastException.class, () ->
                enumerable.cast(String.class).toList());
    }

    @Test
    public void testOfType() {
        List<Object> mixedList = Arrays.asList("a", 1, "b", 2, "c");
        Enumerable<Object> enumerable = of(mixedList);
        Enumerable<String> strings = enumerable.ofType(String.class);

        assertEquals(3, strings.count());
        assertEquals(Arrays.asList("a", "b", "c"), strings.toList());
    }

    // ========== 测试 chunk 方法 ==========

    @Test
    public void testChunk() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        Enumerable<List<Integer>> chunks = enumerable.chunk(2);

        List<List<Integer>> result = chunks.toList();
        assertEquals(3, result.size());
        assertEquals(Arrays.asList(1, 2), result.get(0));
        assertEquals(Arrays.asList(3, 4), result.get(1));
        assertEquals(List.of(5), result.get(2));
    }

    @Test
    public void testChunkThrowsIllegalArgumentException() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertThrows(IllegalArgumentException.class, () ->
                enumerable.chunk(0));
    }

    // ========== 测试 concat 方法 ==========

    @Test
    public void testConcat() {
        Enumerable<Integer> first = of(1, 2, 3);
        Enumerable<Integer> second = of(4, 5, 6);

        Enumerable<Integer> result = first.concat(second);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), result.toList());
    }

    // ========== 测试 contains 方法 ==========

    @Test
    public void testContains() {
        Enumerable<String> enumerable = getStringEnumerable();
        assertTrue(enumerable.contains("banana"));
        assertFalse(enumerable.contains("grape"));
    }

    // ========== 测试 count 方法 ==========

    @Test
    public void testCount() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(5, enumerable.count());

        assertEquals(2, enumerable.count(x -> x > 3));
        assertEquals(0, enumerable.count(x -> x > 10));
    }

    @Test
    public void testLongCount() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(5L, enumerable.longCount());

        assertEquals(2L, enumerable.longCount(x -> x > 3));
    }

    @Test
    public void testCountBy() {
        Enumerable<String> enumerable = of("apple", "banana", "apple", "cherry", "banana", "apple");
        Enumerable<Map.Entry<String, Integer>> result = enumerable.countBy(s -> s);

        Map<String, Integer> map = result.toList().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(3, map.get("apple"));
        assertEquals(2, map.get("banana"));
        assertEquals(1, map.get("cherry"));
    }

    // ========== 测试 distinct 方法 ==========

    @Test
    public void testDistinct() {
        Enumerable<Integer> enumerable = of(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        Enumerable<Integer> distinct = enumerable.distinct();

        assertEquals(Arrays.asList(1, 2, 3, 4), distinct.toList());
    }

    @Test
    public void testDistinctBy() {
        Enumerable<Person> persons = getPersonEnumerable();
        Enumerable<Person> distinctByAge = persons.distinctBy(Person::getAge);

        assertEquals(3, distinctByAge.count()); // 3个不同的年龄：25, 30, 35
    }

    // ========== 测试 elementAt 方法 ==========

    @Test
    public void testElementAt() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(3, enumerable.elementAt(2));
    }

    @Test
    public void testElementAtThrowsIndexOutOfBoundsException() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertThrows(IndexOutOfBoundsException.class, () ->
                enumerable.elementAt(10));
    }

    @Test
    public void testElementAtOrDefault() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(3, enumerable.elementAtOrDefault(2));
        assertNull(enumerable.elementAtOrDefault(10));
    }

    // ========== 测试 except 方法 ==========

    @Test
    public void testExcept() {
        Enumerable<Integer> first = of(1, 2, 3, 4, 5);
        Enumerable<Integer> second = of(4, 5, 6, 7, 8);

        Enumerable<Integer> result = first.except(second);
        assertEquals(Arrays.asList(1, 2, 3), result.toList());
    }

    @Test
    public void testExceptBy() {
        Enumerable<Person> persons = getPersonEnumerable();
        Enumerable<Integer> agesToExclude = of(25, 35);

        Enumerable<Person> result = persons.exceptBy(agesToExclude, Person::getAge);

        assertEquals(1, result.count()); // 只有年龄为30的人
        assertTrue(result.all(p -> p.getAge() == 30));
    }

    // ========== 测试 first 和 firstOrDefault 方法 ==========

    @Test
    public void testFirst() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(1, enumerable.first());
    }

    @Test
    public void testFirstThrowsNoSuchElementException() {
        Enumerable<Integer> empty = of();
        assertThrows(NoSuchElementException.class, empty::first);
    }

    @Test
    public void testFirstWithPredicate() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(4, enumerable.first(x -> x > 3));
    }

    @Test
    public void testFirstWithPredicateThrowsNoSuchElementException() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertThrows(NoSuchElementException.class, () ->
                enumerable.first(x -> x > 10));
    }

    @Test
    public void testFirstOrDefault() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(1, enumerable.firstOrDefault());

        Enumerable<Integer> empty = of();
        assertNull(empty.firstOrDefault());
        assertEquals(100, empty.firstOrDefault(100));
    }

    @Test
    public void testFirstOrDefaultWithPredicate() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(4, enumerable.firstOrDefault(x -> x > 3));
        assertNull(enumerable.firstOrDefault(x -> x > 10));
        assertEquals(100, enumerable.firstOrDefault(x -> x > 10, 100));
    }

    // ========== 测试 groupJoin 方法 ==========

    @Test
    public void testGroupJoin() {
        Enumerable<Person> persons = getPersonEnumerable();
        Enumerable<Department> departments = of(
                new Department("HR", 25),
                new Department("IT", 30),
                new Department("Sales", 40)
        );

        Enumerable<String> result = persons.groupJoin(
                departments,
                Person::getAge,
                Department::getRequiredAge,
                (person, deptEnum) -> {
                    String deptNames = deptEnum.select(Department::getName).toList().toString();
                    return person.getName() + " can join: " + deptNames;
                }
        );

        // 验证结果
        assertTrue(result.any(s -> s.contains("Alice can join: [HR]")));
        assertTrue(result.any(s -> s.contains("Bob can join: [IT]")));
    }

    static class Department {
        String name;
        int requiredAge;

        Department(String name, int requiredAge) {
            this.name = name;
            this.requiredAge = requiredAge;
        }

        public String getName() {
            return name;
        }

        public int getRequiredAge() {
            return requiredAge;
        }
    }

    // ========== 测试 groupBy 方法 ==========

    @Test
    public void testGroupBy() {
        Enumerable<Person> persons = getPersonEnumerable();
        Enumerable<Group<Integer, Person>> groups = persons.groupBy(Person::getAge);

        Map<Integer, List<Person>> groupMap = new HashMap<>();
        for (Group<Integer, Person> group : groups) {
            groupMap.put(group.getKey(), group.getElements().toList());
        }

        assertEquals(2, groupMap.get(25).size());
        assertEquals(2, groupMap.get(30).size());
        assertEquals(1, groupMap.get(35).size());
    }

    @Test
    public void testGroupByWithElementSelector() {
        Enumerable<Person> persons = getPersonEnumerable();
        Enumerable<Group<Integer, String>> groups = persons.groupBy(
                Person::getAge,
                Person::getName
        );

        Map<Integer, List<String>> groupMap = new HashMap<>();
        for (Group<Integer, String> group : groups) {
            groupMap.put(group.getKey(), group.getElements().toList());
        }

        assertTrue(groupMap.get(25).containsAll(Arrays.asList("Alice", "Charlie")));
        assertTrue(groupMap.get(30).containsAll(Arrays.asList("Bob", "Eve")));
    }

    // ========== 测试 index 方法 ==========

    @Test
    public void testIndex() {
        Enumerable<String> enumerable = getStringEnumerable();
        Enumerable<Tuple<Integer, String>> indexed = enumerable.index();

        List<Tuple<Integer, String>> result = indexed.toList();
        assertEquals(5, result.size());
        assertEquals(0, result.get(0).getFirst());
        assertEquals("apple", result.get(0).getSecond());
        assertEquals(4, result.get(4).getFirst());
        assertEquals("elderberry", result.get(4).getSecond());
    }

    // ========== 测试 intersect 方法 ==========

    @Test
    public void testIntersect() {
        Enumerable<Integer> first = of(1, 2, 3, 4, 5);
        Enumerable<Integer> second = of(4, 5, 6, 7, 8);

        Enumerable<Integer> result = first.intersect(second);
        assertEquals(Arrays.asList(4, 5), result.toList());
    }

    @Test
    public void testIntersectBy() {
        Enumerable<Person> persons = getPersonEnumerable();
        Enumerable<Integer> agesToIntersect = of(25, 30);

        Enumerable<Person> result = persons.intersectBy(agesToIntersect, Person::getAge);

        assertEquals(2, result.count()); // 年龄为25和30的人
        assertTrue(result.all(p -> p.getAge() == 25 || p.getAge() == 30));
    }

    // ========== 测试 join 方法 ==========

    @Test
    public void testJoin() {
        Enumerable<Person> persons = getPersonEnumerable();
        Enumerable<Department> departments = of(
                new Department("HR", 25),
                new Department("IT", 30),
                new Department("Sales", 40)
        );

        Enumerable<String> result = persons.join(
                departments,
                Person::getAge,
                Department::getRequiredAge,
                (person, dept) -> person.getName() + " works in " + dept.getName()
        );

        assertEquals(4, result.count()); // 4个人有匹配的部门
        assertTrue(result.any(s -> s.contains("Alice works in HR")));
        assertTrue(result.any(s -> s.contains("Bob works in IT")));
    }

    // ========== 测试 last 和 lastOrDefault 方法 ==========

    @Test
    public void testLast() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(5, enumerable.last());
    }

    @Test
    public void testLastThrowsNoSuchElementException() {
        Enumerable<Integer> empty = of();
        assertThrows(NoSuchElementException.class, empty::last);
    }

    @Test
    public void testLastWithPredicate() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(5, enumerable.last(x -> x < 6));
    }

    @Test
    public void testLastOrDefault() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(5, enumerable.lastOrDefault());

        Enumerable<Integer> empty = of();
        assertNull(empty.lastOrDefault());
        assertEquals(100, empty.lastOrDefault(100));
    }

    @Test
    public void testLastOrDefaultWithPredicate() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(5, enumerable.lastOrDefault(x -> x > 3));
        assertNull(enumerable.lastOrDefault(x -> x > 10));
        assertEquals(100, enumerable.lastOrDefault(x -> x > 10, 100));
    }

    // ========== 测试 leftJoin 方法 ==========

    @Test
    public void testLeftJoin() {
        Enumerable<Person> persons = of(
                new Person("Alice", 25, 50000.0),
                new Person("Bob", 30, 60000.0),
                new Person("Charlie", 40, 70000.0) // 没有匹配的部门
        );

        Enumerable<Department> departments = of(
                new Department("HR", 25),
                new Department("IT", 30),
                new Department("Sales", 35)
        );

        Enumerable<String> result = persons.leftJoin(
                departments,
                Person::getAge,
                Department::getRequiredAge,
                (person, dept) -> person.getName() + " -> " + (dept != null ? dept.getName() : "No Department")
        );

        assertEquals(3, result.count());
        assertTrue(result.any(s -> s.contains("Alice -> HR")));
        assertTrue(result.any(s -> s.contains("Bob -> IT")));
        assertTrue(result.any(s -> s.contains("Charlie -> No Department")));
    }

    // ========== 测试 max 和 min 方法 ==========

    @Test
    public void testMaxWithComparer() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(5, enumerable.max(Comparator.naturalOrder()));
    }

    @Test
    public void testMaxWithSelector() {
        Enumerable<Person> persons = getPersonEnumerable();
        assertEquals(70000.0, persons.max(Person::getSalary), 0.001);
    }

    @Test
    public void testMaxWithSelectorAndComparer() {
        Enumerable<String> enumerable = getStringEnumerable();
        // 按字符串长度比较
        String longest = enumerable.max(s -> s, Comparator.comparingInt(String::length));
        assertEquals("elderberry", longest);
    }

    @Test
    public void testMaxBy() {
        Enumerable<Person> persons = getPersonEnumerable();
        Person oldest = persons.maxBy(Person::getAge);
        assertEquals(35, oldest.getAge());
        assertEquals("David", oldest.getName());
    }

    @Test
    public void testMaxByThrowsNoSuchElementException() {
        Enumerable<Person> empty = of();
        assertThrows(NoSuchElementException.class, () ->
                empty.maxBy(Person::getAge));
    }

    @Test
    public void testMinWithComparer() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(1, enumerable.min(Comparator.naturalOrder()));
    }

    @Test
    public void testMinWithSelector() {
        Enumerable<Person> persons = getPersonEnumerable();
        assertEquals(50000.0, persons.min(Person::getSalary), 0.001);
    }

    @Test
    public void testMinBy() {
        Enumerable<Person> persons = getPersonEnumerable();
        Person youngest = persons.minBy(Person::getAge);
        assertEquals(25, youngest.getAge());
        assertEquals("Alice", youngest.getName());
    }

    @Test
    public void testMinByThrowsNoSuchElementException() {
        Enumerable<Person> empty = of();
        assertThrows(NoSuchElementException.class, () ->
                empty.minBy(Person::getAge));
    }

    // ========== 测试 order 和 orderBy 方法 ==========

    @Test
    public void testOrder() {
        Enumerable<Integer> enumerable = of(5, 3, 1, 4, 2);
        Enumerable<Integer> ordered = enumerable.order(Comparator.naturalOrder());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), ordered.toList());
    }

    @Test
    public void testOrderBy() {
        Enumerable<Person> persons = getPersonEnumerable();
        Enumerable<Person> ordered = persons.orderBy(Person::getAge);

        List<Integer> ages = ordered.select(Person::getAge).toList();
        assertEquals(Arrays.asList(25, 25, 30, 30, 35), ages);
    }

    @Test
    public void testOrderByDescending() {
        Enumerable<Person> persons = getPersonEnumerable();
        Enumerable<Person> ordered = persons.orderByDescending(Person::getSalary);

        List<Double> salaries = ordered.select(Person::getSalary).toList();
        assertEquals(70000.0, salaries.get(0), 0.001);
        assertEquals(50000.0, salaries.get(4), 0.001);
    }

    // ========== 测试 reverse 方法 ==========

    @Test
    public void testReverse() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        Enumerable<Integer> reversed = enumerable.reverse();
        assertEquals(Arrays.asList(5, 4, 3, 2, 1), reversed.toList());
    }

    // ========== 测试 rightJoin 方法 ==========

    @Test
    public void testRightJoin() {
        Enumerable<Person> persons = of(
                new Person("Alice", 25, 50000.0),
                new Person("Bob", 30, 60000.0)
        );

        Enumerable<Department> departments = of(
                new Department("HR", 25),
                new Department("IT", 30),
                new Department("Sales", 35) // 没有匹配的人
        );

        Enumerable<String> result = persons.rightJoin(
                departments,
                Person::getAge,
                Department::getRequiredAge,
                (person, dept) -> (person != null ? person.getName() : "No Person") + " -> " + dept.getName()
        );

        assertEquals(3, result.count());
        assertTrue(result.any(s -> s.contains("Alice -> HR")));
        assertTrue(result.any(s -> s.contains("Bob -> IT")));
        assertTrue(result.any(s -> s.contains("No Person -> Sales")));
    }

    // ========== 测试 select 和 selectMany 方法 ==========

    @Test
    public void testSelect() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        Enumerable<String> result = enumerable.select(x -> "Number: " + x);

        assertEquals(5, result.count());
        assertEquals("Number: 1", result.first());
    }

    @Test
    public void testSelectWithIndex() {
        Enumerable<String> enumerable = getStringEnumerable();
        Enumerable<String> result = enumerable.select((s, i) -> i + ": " + s);

        assertEquals("0: apple", result.first());
        assertEquals("4: elderberry", result.last());
    }

    @Test
    public void testSelectMany() {
        Enumerable<String> enumerable = of("apple,banana", "cherry,date");
        Enumerable<String> result = enumerable.selectMany(s -> Arrays.asList(s.split(",")));

        assertEquals(4, result.count());
        assertTrue(result.contains("apple"));
        assertTrue(result.contains("banana"));
        assertTrue(result.contains("cherry"));
        assertTrue(result.contains("date"));
    }

    @Test
    public void testSelectManyWithResultSelector() {
        Enumerable<String> enumerable = of("apple", "banana");
        Enumerable<String> result = enumerable.selectMany(
                s -> Arrays.asList(s.toUpperCase(), s.toLowerCase()),
                (original, transformed) -> original + " -> " + transformed
        );

        assertEquals(4, result.count());
        assertTrue(result.any(s -> s.contains("apple -> APPLE")));
        assertTrue(result.any(s -> s.contains("apple -> apple")));
    }

    // ========== 测试 shuffle 方法 ==========

    @Test
    public void testShuffle() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        Enumerable<Integer> shuffled = enumerable.shuffle();

        // 验证元素相同但顺序可能不同
        assertEquals(5, shuffled.count());
        assertTrue(shuffled.contains(1));
        assertTrue(shuffled.contains(2));
        assertTrue(shuffled.contains(3));
        assertTrue(shuffled.contains(4));
        assertTrue(shuffled.contains(5));
    }

    // ========== 测试 single 和 singleOrDefault 方法 ==========

    @Test
    public void testSingle() {
        Enumerable<Integer> singleElement = of(42);
        assertEquals(42, singleElement.single());
    }

    @Test
    public void testSingleThrowsExceptionForMultipleElements() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertThrows(IllegalArgumentException.class, enumerable::single);
    }

    @Test
    public void testSingleWithPredicate() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(3, enumerable.single(x -> x == 3));
    }

    @Test
    public void testSingleWithPredicateThrowsExceptionForMultipleMatches() {
        Enumerable<Integer> enumerable = of(1, 2, 2, 3);
        assertThrows(IllegalArgumentException.class, () ->
                enumerable.single(x -> x == 2));
    }

    @Test
    public void testSingleOrDefault() {
        Enumerable<Integer> singleElement = of(42);
        assertEquals(42, singleElement.singleOrDefault());

        Enumerable<Integer> empty = of();
        assertNull(empty.singleOrDefault());
        assertEquals(100, empty.singleOrDefault(100));
    }

    @Test
    public void testSingleOrDefaultWithPredicate() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(3, enumerable.singleOrDefault(x -> x == 3));
        assertNull(enumerable.singleOrDefault(x -> x == 10));
        assertEquals(100, enumerable.singleOrDefault(100, x -> x == 10));
    }

    // ========== 测试 skip 和 take 方法 ==========

    @Test
    public void testSkip() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        Enumerable<Integer> result = enumerable.skip(2);
        assertEquals(Arrays.asList(3, 4, 5), result.toList());
    }

    @Test
    public void testSkipWhile() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        Enumerable<Integer> result = enumerable.skipWhile(x -> x < 3);
        assertEquals(Arrays.asList(3, 4, 5), result.toList());
    }

    @Test
    public void testSkipLast() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        Enumerable<Integer> result = enumerable.skipLast(2);
        assertEquals(Arrays.asList(1, 2, 3), result.toList());
    }

    @Test
    public void testTake() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        Enumerable<Integer> result = enumerable.take(3);
        assertEquals(Arrays.asList(1, 2, 3), result.toList());
    }

    @Test
    public void testTakeWithRange() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        Enumerable<Integer> result = enumerable.take(1, 4); // 索引1到3（不包括4）
        assertEquals(Arrays.asList(2, 3, 4), result.toList());
    }

    @Test
    public void testTakeWhile() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        Enumerable<Integer> result = enumerable.takeWhile(x -> x < 4);
        assertEquals(Arrays.asList(1, 2, 3), result.toList());
    }

    @Test
    public void testTakeLast() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        Enumerable<Integer> result = enumerable.takeLast(2);
        assertEquals(Arrays.asList(4, 5), result.toList());
    }

    // ========== 测试 sum 方法 ==========

    @Test
    public void testSumInt() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        assertEquals(15, enumerable.sumInt(x -> x));
    }

    @Test
    public void testSumLong() {
        Enumerable<Long> enumerable = of(1L, 2L, 3L, 4L, 5L);
        assertEquals(15L, enumerable.sumLong(x -> x));
    }

    @Test
    public void testSumFloat() {
        Enumerable<Float> enumerable = of(1.0f, 2.0f, 3.0f, 4.0f, 5.0f);
        assertEquals(15.0f, enumerable.sumFloat(x -> x), 0.001f);
    }

    @Test
    public void testSumDouble() {
        Enumerable<Double> enumerable = of(1.0, 2.0, 3.0, 4.0, 5.0);
        assertEquals(15.0, enumerable.sumDouble(x -> x), 0.001);
    }

    @Test
    public void testSumBigDecimal() {
        Enumerable<BigDecimal> enumerable = of(
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(4),
                BigDecimal.valueOf(5)
        );
        assertEquals(BigDecimal.valueOf(15), enumerable.sumBigDecimal(x -> x));
    }

    // ========== 测试 union 方法 ==========

    @Test
    public void testUnion() {
        Enumerable<Integer> first = of(1, 2, 3);
        Enumerable<Integer> second = of(3, 4, 5);

        Enumerable<Integer> result = first.union(second);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result.toList());
    }

    @Test
    public void testUnionBy() {
        Enumerable<Person> persons1 = of(
                new Person("Alice", 25, 50000.0),
                new Person("Bob", 30, 60000.0)
        );

        Enumerable<Person> persons2 = of(
                new Person("Charlie", 25, 55000.0), // 年龄重复
                new Person("David", 35, 70000.0)
        );

        Enumerable<Person> result = persons1.unionBy(persons2, Person::getAge);

        assertEquals(3, result.count()); // 只有3个不同的年龄
        assertEquals(3, result.select(Person::getAge).distinct().count());
    }

    // ========== 测试 where 方法 ==========

    @Test
    public void testWhere() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        Enumerable<Integer> result = enumerable.where(x -> x % 2 == 0);
        assertEquals(Arrays.asList(2, 4), result.toList());
    }

    @Test
    public void testWhereWithIndex() {
        Enumerable<String> enumerable = getStringEnumerable();
        Enumerable<String> result = enumerable.where((_, i) -> i % 2 == 0);

        assertEquals(3, result.count()); // 索引0, 2, 4
        assertTrue(result.contains("apple"));
        assertTrue(result.contains("cherry"));
        assertTrue(result.contains("elderberry"));
    }

    // ========== 测试 zip 方法 ==========

    @Test
    public void testZip() {
        Enumerable<Integer> first = of(1, 2, 3);
        Enumerable<String> second = of("A", "B", "C");

        Enumerable<Tuple<Integer, String>> result = first.zip(second);

        List<Tuple<Integer, String>> tuples = result.toList();
        assertEquals(3, tuples.size());
        assertEquals(1, tuples.getFirst().getFirst());
        assertEquals("A", tuples.getFirst().getSecond());
    }

    @Test
    public void testZipWithResultSelector() {
        Enumerable<Integer> first = of(1, 2, 3);
        Enumerable<String> second = of("A", "B", "C");

        Enumerable<String> result = first.zip(second, (num, str) -> num + str);

        assertEquals(Arrays.asList("1A", "2B", "3C"), result.toList());
    }

    // ========== 测试 toList, toMap, toHashSet 方法 ==========

    @Test
    public void testToList() {
        Enumerable<Integer> enumerable = getIntEnumerable();
        List<Integer> list = enumerable.toList();
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
    }

    @Test
    public void testToMap() {
        Enumerable<Person> persons = getPersonEnumerable();
        Map<String, Person> map = persons.toMap(Person::getName);

        assertEquals(5, map.size());
        assertEquals("Alice", map.get("Alice").getName());
        assertEquals(25, map.get("Alice").getAge());
    }

    @Test
    public void testToMapWithElementSelector() {
        Enumerable<Person> persons = getPersonEnumerable();
        Map<String, Double> map = persons.toMap(Person::getName, Person::getSalary);

        assertEquals(5, map.size());
        assertEquals(50000.0, map.get("Alice"), 0.001);
    }

    @Test
    public void testToHashSet() {
        Enumerable<Integer> enumerable = of(1, 2, 2, 3, 3, 3);
        Set<Integer> set = enumerable.toHashSet();

        assertEquals(3, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
    }


    @Test
    public void testMaxByWithComparer() {
        Enumerable<Person> persons = getPersonEnumerable();
        Person result = persons.maxBy(
                Person::getName,
                Comparator.comparingInt(String::length)
        );
        // 名字最长的应该是 "elderberry" 但 Person 中没有，这里找名字最长的
        // 实际上我们的 Person 名字长度: Alice(5), Bob(3), Charlie(7), David(5), Eve(3)
        assertEquals("Charlie", result.getName());
    }

    @Test
    public void testMaxByEmptyThrowsException() {
        Enumerable<Person> empty = of();
        assertThrows(NoSuchElementException.class, () ->
                empty.maxBy(Person::getName, Comparator.naturalOrder()));
    }

    @Test
    public void testMinWithSelectorAndComparer() {
        Enumerable<Person> persons = getPersonEnumerable();
        // 找到名字长度最短的人
        String result = persons.min(
                Person::getName,
                Comparator.comparingInt(String::length)
        );
        // Bob(3) 和 Eve(3) 都是最短的，maxOfWith 会返回第一个遇到的
        assertEquals(3, result.length());
    }

    @Test
    public void testMinWithSelectorReturnsComparable() {
        Enumerable<Person> persons = getPersonEnumerable();
        Double minSalary = persons.min(Person::getSalary);
        assertEquals(50000.0, minSalary, 0.001);
    }

    // ========== 测试 minBy 重载方法 ==========

    @Test
    public void testMinByWithComparer() {
        Enumerable<Person> persons = getPersonEnumerable();
        // 按名字长度找到名字最短的人
        Person result = persons.minBy(
                Person::getName,
                Comparator.comparingInt(String::length)
        );
        // Bob(3) 或 Eve(3) - 会返回第一个遇到的
        assertEquals(3, result.getName().length());
    }

    @Test
    public void testMinByWithComparerEmptyThrowsException() {
        Enumerable<Person> empty = of();
        assertThrows(NoSuchElementException.class, () ->
                empty.minBy(Person::getName, Comparator.naturalOrder()));
    }

    // ========== 测试 orderBy 重载方法 ==========

    @Test
    public void testOrderByWithComparer() {
        Enumerable<String> enumerable = of("apple", "Banana", "cherry", "Date");
        // 不区分大小写排序
        Enumerable<String> result = enumerable.orderBy(
                s -> s,
                String.CASE_INSENSITIVE_ORDER
        );

        List<String> sorted = result.toList();
        // 期望顺序: apple, Banana, cherry, Date (字母顺序，不区分大小写)
        // 实际字母顺序: a, B, c, D -> apple, Banana, cherry, Date
        assertEquals("apple", sorted.get(0));
        assertEquals("Banana", sorted.get(1));
        assertEquals("cherry", sorted.get(2));
        assertEquals("Date", sorted.get(3));
    }

    // ========== 测试 selectMany 重载方法 ==========

    @Test
    public void testSelectManyWithIndex() {
        Enumerable<List<String>> enumerable = of(
                Arrays.asList("a", "b"),
                Arrays.asList("c", "d", "e"),
                List.of("f")
        );

        // 使用带索引的 selectMany
        Enumerable<String> result = enumerable.selectMany(
                (list, index) -> list.stream()
                        .map(s -> index + ":" + s)
                        .collect(Collectors.toList())
        );

        List<String> expected = Arrays.asList(
                "0:a", "0:b",
                "1:c", "1:d", "1:e",
                "2:f"
        );

        assertEquals(expected, result.toList());
    }

    @Test
    public void testSelectManyWithCollectionAndResultSelector() {
        Enumerable<Integer> numbers = of(1, 2, 3);
        Enumerable<String> letters = of("A", "B");

        // 使用两个序列的笛卡尔积
        Enumerable<String> result = numbers.selectMany(
                _ -> letters,
                (n, l) -> n + l
        );

        List<String> expected = Arrays.asList(
                "1A", "1B",
                "2A", "2B",
                "3A", "3B"
        );

        assertEquals(expected, result.toList());
    }

    @Test
    public void testSelectManyWithIndexAndResultSelector() {
        Enumerable<List<Integer>> enumerable = of(
                Arrays.asList(1, 2),
                Arrays.asList(3, 4, 5)
        );

        Enumerable<String> result = enumerable.selectMany(
                (list, _) -> list,
                (list, element) -> "list" + list.size() + "->" + element
        );

        List<String> expected = Arrays.asList(
                "list2->1", "list2->2",
                "list3->3", "list3->4", "list3->5"
        );

        assertEquals(expected, result.toList());
    }

    // ========== 测试 skipWhile 重载方法 ==========

    @Test
    public void testSkipWhileWithIndex() {
        Enumerable<Integer> enumerable = of(1, 2, 3, 4, 5, 1, 2, 3);

        // 跳过前3个元素（索引0,1,2）
        Enumerable<Integer> result = enumerable.skipWhile(
                (_, index) -> index < 3
        );

        assertEquals(Arrays.asList(4, 5, 1, 2, 3), result.toList());
    }

    @Test
    public void testSkipWhileWithIndexComplex() {
        Enumerable<Integer> enumerable = of(10, 20, 30, 5, 15, 25);

        // 跳过值大于索引*10的元素
        Enumerable<Integer> result = enumerable.skipWhile(
                (value, index) -> value > index * 10
        );

        // 检查过程：
        // index=0, value=10 > 0*10=0 -> 跳过
        // index=1, value=20 > 1*10=10 -> 跳过
        // index=2, value=30 > 2*10=20 -> 跳过
        // index=3, value=5 > 3*10=30? false -> 停止跳过
        assertEquals(Arrays.asList(5, 15, 25), result.toList());
    }

    // ========== 测试 takeWhile 重载方法 ==========

    @Test
    public void testTakeWhileWithIndex() {
        Enumerable<Integer> enumerable = of(1, 2, 3, 4, 5, 1, 2, 3);

        // 取前3个元素（索引0,1,2）
        Enumerable<Integer> result = enumerable.takeWhile(
                (_, index) -> index < 3
        );

        assertEquals(Arrays.asList(1, 2, 3), result.toList());
    }

    @Test
    public void testTakeWhileWithIndexComplex() {
        Enumerable<Integer> enumerable = of(10, 20, 30, 5, 15, 25);

        // 取值大于索引*10的元素，直到条件不满足
        Enumerable<Integer> result = enumerable.takeWhile(
                (value, index) -> value > index * 10
        );

        // 检查过程：
        // index=0, value=10 > 0*10=0 -> 取
        // index=1, value=20 > 1*10=10 -> 取
        // index=2, value=30 > 2*10=20 -> 取
        // index=3, value=5 > 3*10=30? false -> 停止
        assertEquals(Arrays.asList(10, 20, 30), result.toList());
    }

    // ========== 测试 toLookUp 方法 ==========

    @Test
    public void testToLookUp() {
        Enumerable<Person> persons = getPersonEnumerable();
        LookUp<Integer, Person> lookUp = persons.toLookUp(Person::getAge);

        // 测试 contains 方法
        assertTrue(lookUp.contains(25));
        assertTrue(lookUp.contains(30));
        assertTrue(lookUp.contains(35));
        assertFalse(lookUp.contains(40));

        // 测试 get 方法
        Enumerable<Person> age25 = lookUp.get(25);
        assertEquals(2, age25.count());
        assertTrue(age25.any(p -> p.getName().equals("Alice")));
        assertTrue(age25.any(p -> p.getName().equals("Charlie")));

        // 测试迭代器
        List<Group<Integer, Person>> groups = new ArrayList<>();
        for (Group<Integer, Person> group : lookUp) {
            groups.add(group);
        }

        assertEquals(3, groups.size());

        // 找到年龄为30的组
        Group<Integer, Person> age30Group = groups.stream()
                .filter(g -> g.getKey() == 30)
                .findFirst()
                .orElse(null);

        assertNotNull(age30Group);
        assertEquals(2, age30Group.getElements().count());
    }

    @Test
    public void testToLookUpWithElementSelector() {
        Enumerable<Person> persons = getPersonEnumerable();
        LookUp<Integer, String> lookUp = persons.toLookUp(
                Person::getAge,
                Person::getName
        );

        // 测试 get 方法返回的是名称而不是 Person 对象
        Enumerable<String> age30Names = lookUp.get(30);
        assertEquals(2, age30Names.count());
        assertTrue(age30Names.contains("Bob"));
        assertTrue(age30Names.contains("Eve"));

        // 测试 contains 方法
        assertTrue(lookUp.contains(25));
        assertFalse(lookUp.contains(99));

        // 测试迭代
        int totalNames = 0;
        for (Group<Integer, String> group : lookUp) {
            totalNames += group.getElements().count();
        }
        assertEquals(5, totalNames); // 总共5个人
    }

    @Test
    public void testToLookUpWithNullKeys() {
        // 创建一个包含null键的数据
        List<String> items = Arrays.asList("apple", null, "banana", null, "cherry");
        Enumerable<String> enumerable = of(items);

        // 使用自身作为键（null会作为键）
        LookUp<String, String> lookUp = enumerable.toLookUp(s -> s);

        // 应该包含null键
        assertTrue(lookUp.contains(null));

        // null键对应的组应该有2个元素
        Enumerable<String> nullGroup = lookUp.get(null);
        assertEquals(2, nullGroup.count());

        // "apple" 键对应的组应该有1个元素
        Enumerable<String> appleGroup = lookUp.get("apple");
        assertEquals(1, appleGroup.count());
        assertEquals("apple", appleGroup.first());
    }

    @Test
    public void testToLookUpEmpty() {
        Enumerable<Person> empty = of();
        LookUp<Integer, Person> lookUp = empty.toLookUp(Person::getAge);

        // 应该不包含任何键
        assertFalse(lookUp.contains(25));

        // get 方法应该返回空 Enumerable
        Enumerable<Person> result = lookUp.get(25);
        assertEquals(0, result.count());

        // 迭代器应该为空
        assertFalse(lookUp.iterator().hasNext());
    }

    // ========== 测试边界情况和异常 ==========

    @Test
    public void testMaxByWithComparerSingleElement() {
        Enumerable<Person> single = of(new Person("Alice", 25, 50000.0));
        Person result = single.maxBy(Person::getName, Comparator.reverseOrder());
        assertEquals("Alice", result.getName());
    }

    @Test
    public void testMinByWithComparerAllEqual() {
        Enumerable<Person> persons = of(
                new Person("Alice", 25, 50000.0),
                new Person("Bob", 25, 50000.0),
                new Person("Charlie", 25, 50000.0)
        );

        // 所有属性都相同，应该返回第一个
        Person result = persons.minBy(Person::getAge, Comparator.naturalOrder());
        assertEquals("Alice", result.getName());
    }

    @Test
    public void testOrderByWithNullValues() {
        Enumerable<String> enumerable = of("banana", null, "apple", null, "cherry");

        // 使用处理null的比较器（null放在前面）
        Comparator<String> nullsFirst = Comparator.nullsFirst(Comparator.naturalOrder());
        Enumerable<String> result = enumerable.orderBy(s -> s, nullsFirst);

        List<String> sorted = result.toList();
        // null 应该在前，然后是 "apple", "banana", "cherry"
        assertNull(sorted.get(0));
        assertNull(sorted.get(1));
        assertEquals("apple", sorted.get(2));
        assertEquals("banana", sorted.get(3));
        assertEquals("cherry", sorted.get(4));
    }

    @Test
    public void testSelectManyWithEmptyCollections() {
        Enumerable<List<String>> enumerable = of(
                Arrays.asList("a", "b"),
                Collections.emptyList(),
                List.of("c")
        );

        Enumerable<String> result = enumerable.selectMany(list -> list);

        assertEquals(Arrays.asList("a", "b", "c"), result.toList());
    }

    @Test
    public void testSkipWhileWithIndexAllSkipped() {
        Enumerable<Integer> enumerable = of(1, 2, 3, 4, 5);

        // 跳过所有元素
        Enumerable<Integer> result = enumerable.skipWhile((_, _) -> true);

        assertEquals(0, result.count());
    }

    @Test
    public void testTakeWhileWithIndexNoneTaken() {
        Enumerable<Integer> enumerable = of(1, 2, 3, 4, 5);

        // 不取任何元素
        Enumerable<Integer> result = enumerable.takeWhile((_, _) -> false);

        assertEquals(0, result.count());
    }

    @Test
    public void testToLookUpDuplicateKeysDifferentValues() {
        Enumerable<Person> persons = of(
                new Person("Alice", 25, 50000.0),
                new Person("Bob", 25, 60000.0), // 相同年龄，不同薪水
                new Person("Charlie", 30, 55000.0)
        );

        LookUp<Integer, Person> lookUp = persons.toLookUp(Person::getAge);

        Enumerable<Person> age25Group = lookUp.get(25);
        assertEquals(2, age25Group.count());

        // 验证两个人都被包含
        assertTrue(age25Group.any(p -> p.getName().equals("Alice")));
        assertTrue(age25Group.any(p -> p.getName().equals("Bob")));
    }
}

// ========== 测试 Linq 静态方法 ==========

class LinqTest {

    @Test
    public void testOfEmpty() {
        Enumerable<String> empty = Linq.of();
        assertNotNull(empty);
        assertEquals(0, empty.count());
    }

    @Test
    public void testOfVarargs() {
        Enumerable<Integer> enumerable = Linq.of(1, 2, 3, 4, 5);
        assertEquals(5, enumerable.count());
        assertEquals(1, enumerable.first());
    }

    @Test
    public void testOfByteArray() {
        byte[] bytes = {1, 2, 3, 4, 5};
        Enumerable<Byte> enumerable = Linq.of(bytes);

        List<Byte> result = enumerable.toList();
        assertEquals(5, result.size());
        assertEquals((byte) 1, result.getFirst());
    }

    @Test
    public void testOfShortArray() {
        short[] shorts = {1, 2, 3, 4, 5};
        Enumerable<Short> enumerable = Linq.of(shorts);

        List<Short> result = enumerable.toList();
        assertEquals(5, result.size());
        assertEquals((short) 1, result.getFirst());
    }

    @Test
    public void testOfIntArray() {
        int[] ints = {1, 2, 3, 4, 5};
        Enumerable<Integer> enumerable = Linq.of(ints);

        List<Integer> result = enumerable.toList();
        assertEquals(5, result.size());
        assertEquals(1, result.getFirst());
    }

    @Test
    public void testOfLongArray() {
        long[] longs = {1L, 2L, 3L, 4L, 5L};
        Enumerable<Long> enumerable = Linq.of(longs);

        List<Long> result = enumerable.toList();
        assertEquals(5, result.size());
        assertEquals(1L, result.getFirst());
    }

    @Test
    public void testOfIterable() {
        List<String> list = Arrays.asList("a", "b", "c");
        Enumerable<String> enumerable = Linq.of(list);

        assertEquals(3, enumerable.count());
        assertEquals("a", enumerable.first());
    }
}