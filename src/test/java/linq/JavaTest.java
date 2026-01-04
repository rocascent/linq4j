package linq;

import kotlin.ranges.IntRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JavaTest {

    private List<Integer> numbers;
    private List<String> strings;
    private List<Person> people;
    private List<Object> mixedList;

    @BeforeEach
    void setUp() {
        numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        strings = Arrays.asList("apple", "banana", "cherry", "date", "elderberry", "fig");
        people = Arrays.asList(
                new Person("Alice", 25, "New York"),
                new Person("Bob", 30, "Los Angeles"),
                new Person("Charlie", 35, "New York"),
                new Person("David", 28, "Chicago"),
                new Person("Eve", 32, "Los Angeles")
        );
        mixedList = Arrays.asList(1, "two", 3.0, "four", 5);
    }

    static class Person {
        String name;
        int age;
        String city;

        Person(String name, int age, String city) {
            this.name = name;
            this.age = age;
            this.city = city;
        }

        String getName() {
            return name;
        }

        int getAge() {
            return age;
        }

        String getCity() {
            return city;
        }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }

    // ==================== Aggregate 方法测试 ====================

    @Test
    @DisplayName("aggregate - 简单聚合")
    void testAggregateSimple() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        int sum = enumerable.aggregate((a, b) -> a + b);
        assertEquals(55, sum);

        int product = enumerable.aggregate((a, b) -> a * b);
        assertEquals(3628800, product);
    }

    @Test
    @DisplayName("aggregate - 带初始值")
    void testAggregateWithSeed() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        int sum = enumerable.aggregate(10, (acc, item) -> acc + item);
        assertEquals(65, sum); // 10 + 55

        String concat = enumerable.aggregate("", (acc, item) -> acc + item);
        assertEquals("12345678910", concat);
    }

    @Test
    @DisplayName("aggregate - 带结果选择器")
    void testAggregateWithResultSelector() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        String result = enumerable.aggregate(
                0,
                (acc, item) -> acc + item,
                sum -> "Sum is: " + sum
        );
        assertEquals("Sum is: 55", result);
    }

    @Test
    @DisplayName("aggregateBy - 按键分组聚合")
    void testAggregateBy() {
        Enumerable<Person> enumerable = Linq.of(people);

        var result = enumerable.aggregateBy(
                p -> p.getCity(),
                0,
                (sum, p) -> sum + p.getAge()
        ).toList();

        // 检查每个城市的年龄总和
        for (var entry : result) {
            if ("New York".equals(entry.getKey())) {
                assertEquals(60, entry.getValue()); // 25 + 35
            } else if ("Los Angeles".equals(entry.getKey())) {
                assertEquals(62, entry.getValue()); // 30 + 32
            } else if ("Chicago".equals(entry.getKey())) {
                assertEquals(28, entry.getValue());
            }
        }
    }

    // ==================== Any/All 方法测试 ====================

    @Test
    @DisplayName("any - 检查序列是否包含元素")
    void testAny() {
        Enumerable<Integer> empty = Linq.of(Collections.emptyList());
        Enumerable<Integer> nonEmpty = Linq.of(numbers);

        assertFalse(empty.any());
        assertTrue(nonEmpty.any());
    }

    @Test
    @DisplayName("any - 带条件检查")
    void testAnyWithPredicate() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        assertTrue(enumerable.any(n -> n > 5));
        assertTrue(enumerable.any(n -> n == 5));
        assertFalse(enumerable.any(n -> n > 20));
    }

    @Test
    @DisplayName("all - 检查所有元素是否满足条件")
    void testAll() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        assertTrue(enumerable.all(n -> n > 0));
        assertFalse(enumerable.all(n -> n > 5));

        // 空序列应该返回 true
        Enumerable<Integer> empty = Linq.of(Collections.emptyList());
        assertTrue(empty.all(n -> n > 100));
    }

    // ==================== Average 方法测试 ====================

    @Test
    @DisplayName("averageInt - 计算整数平均值")
    void testAverageInt() {
        Enumerable<Person> enumerable = Linq.of(people);

        double averageAge = enumerable.averageInt(Person::getAge);
        assertEquals(30.0, averageAge, 0.001); // (25+30+35+28+32)/5 = 30

        // 测试空序列异常
        Enumerable<Person> empty = Linq.of(Collections.emptyList());
        assertTrue(Double.isNaN(empty.averageInt(Person::getAge)));
    }

    @Test
    @DisplayName("averageLong - 计算长整型平均值")
    void testAverageLong() {
        List<Long> longs = Arrays.asList(100L, 200L, 300L);
        Enumerable<Long> enumerable = Linq.of(longs);

        double average = enumerable.averageLong(l -> l);
        assertEquals(200.0, average, 0.001);
    }

    @Test
    @DisplayName("averageFloat - 计算浮点数平均值")
    void testAverageFloat() {
        List<Float> floats = Arrays.asList(1.5f, 2.5f, 3.5f);
        Enumerable<Float> enumerable = Linq.of(floats);

        float average = enumerable.averageFloat(f -> f);
        assertEquals(2.5f, average, 0.001);
    }

    @Test
    @DisplayName("averageDouble - 计算双精度平均值")
    void testAverageDouble() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        double average = enumerable.averageDouble(n -> n * 1.0);
        assertEquals(5.5, average, 0.001);
    }

    @Test
    @DisplayName("averageBigDecimal - 计算BigDecimal平均值")
    void testAverageBigDecimal() {
        List<BigDecimal> decimals = Arrays.asList(
                new BigDecimal("10.5"),
                new BigDecimal("20.5"),
                new BigDecimal("30.5")
        );
        Enumerable<BigDecimal> enumerable = Linq.of(decimals);

        BigDecimal average = enumerable.averageBigDecimal(bd -> bd);
        assertEquals(new BigDecimal("20.5"), average);
    }

    // ==================== Cast 方法测试 ====================

    @Test
    @DisplayName("cast - 类型转换")
    void testCast() {
        Enumerable<Object> enumerable = Linq.of(mixedList);

        // 尝试转换会抛出异常
        assertThrows(ClassCastException.class, () -> {
            Enumerable<Integer> integersOnly = enumerable.cast(Integer.class);
            integersOnly.toList(); // 这里会触发遍历和转换
        });
    }

    // ==================== Chunk 方法测试 ====================

    @Test
    @DisplayName("chunk - 分块")
    void testChunk() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        List<List<Integer>> chunks = enumerable.chunk(3).toList();

        assertEquals(4, chunks.size()); // 10个元素分成4块
        assertEquals(Arrays.asList(1, 2, 3), chunks.get(0));
        assertEquals(Arrays.asList(4, 5, 6), chunks.get(1));
        assertEquals(Arrays.asList(7, 8, 9), chunks.get(2));
        assertEquals(Arrays.asList(10), chunks.get(3));

        // 测试异常情况
        assertThrows(IllegalArgumentException.class, () -> enumerable.chunk(0));
    }

    // ==================== Concat 方法测试 ====================

    @Test
    @DisplayName("concat - 连接序列")
    void testConcat() {
        Enumerable<Integer> first = new Enumerable<>(Arrays.asList(1, 2, 3).stream()::iterator);
        Enumerable<Integer> second = new Enumerable<>(Arrays.asList(4, 5, 6).stream()::iterator);

        List<Integer> result = first.concat(second).toList();
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), result);
    }

    // ==================== Contains 方法测试 ====================

    @Test
    @DisplayName("contains - 检查包含元素")
    void testContains() {
        Enumerable<String> enumerable = Linq.of(strings);

        assertTrue(enumerable.contains("banana"));
        assertFalse(enumerable.contains("grape"));

        // 使用自定义比较器
        assertTrue(enumerable.contains("BANANA", (a, b) -> a.equalsIgnoreCase(b)));
        assertFalse(enumerable.contains("GRAPE", (a, b) -> a.equalsIgnoreCase(b)));
    }

    // ==================== Count 方法测试 ====================

    @Test
    @DisplayName("count - 计数")
    void testCount() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        assertEquals(10, enumerable.count());
        assertEquals(5, enumerable.count(n -> n > 5));
        assertEquals(10L, enumerable.longCount());
        assertEquals(5L, enumerable.longCount(n -> n > 5));
    }

    @Test
    @DisplayName("countBy - 按键计数")
    void testCountBy() {
        Enumerable<Person> enumerable = Linq.of(people);

        List<Map.Entry<String, Integer>> cityCounts = enumerable.countBy(Person::getCity).toList();

        // 验证计数
        for (var entry : cityCounts) {
            if ("New York".equals(entry.getKey())) {
                assertEquals(2, entry.getValue());
            } else if ("Los Angeles".equals(entry.getKey())) {
                assertEquals(2, entry.getValue());
            } else if ("Chicago".equals(entry.getKey())) {
                assertEquals(1, entry.getValue());
            }
        }
    }

    // ==================== Distinct 方法测试 ====================

    @Test
    @DisplayName("distinct - 去重")
    void testDistinct() {
        List<Integer> withDuplicates = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 5);
        Enumerable<Integer> enumerable = Linq.of(withDuplicates);

        List<Integer> distinct = enumerable.distinct().toList();
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), distinct);
    }

    // ==================== ElementAt 方法测试 ====================

    @Test
    @DisplayName("elementAt - 获取指定位置的元素")
    void testElementAt() {
        Enumerable<String> enumerable = Linq.of(strings);

        assertEquals("apple", enumerable.elementAt(0));
        assertEquals("cherry", enumerable.elementAt(2));
        assertEquals("fig", enumerable.elementAt(5));

        // 测试异常
        assertThrows(IndexOutOfBoundsException.class, () -> enumerable.elementAt(10));
        assertThrows(IndexOutOfBoundsException.class, () -> enumerable.elementAt(-1));

        // elementAtOrDefault
        assertEquals("cherry", enumerable.elementAtOrDefault(2));
        assertNull(enumerable.elementAtOrDefault(10));
    }

    // ==================== First 方法测试 ====================

    @Test
    @DisplayName("first - 获取第一个元素")
    void testFirst() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        assertEquals(1, enumerable.first());
        assertEquals(3, enumerable.first(n -> n > 2));

        // firstOrDefault
        assertEquals(1, enumerable.firstOrDefault());
        assertEquals(3, enumerable.firstOrDefault(n -> n > 2));
        assertNull(enumerable.firstOrDefault(n -> n > 100));
        assertEquals(-1, enumerable.firstOrDefault(n -> n > 100, -1));

        var empty = Linq.of();
        assertNull(empty.firstOrDefault());
        assertEquals(5, empty.firstOrDefault(5));
    }

    // ==================== GroupJoin 方法测试 ====================

    @Test
    @DisplayName("groupJoin - 分组连接")
    void testGroupJoin() {
        List<Person> customers = Arrays.asList(
                new Person("Alice", 25, "NY"),
                new Person("Bob", 30, "LA"),
                new Person("Charlie", 35, "NY")
        );

        List<Order> orders = Arrays.asList(
                new Order("Alice", "Book"),
                new Order("Alice", "Pen"),
                new Order("Bob", "Laptop"),
                new Order("David", "Phone") // David不在customers中
        );

        Enumerable<Person> customerEnum = Linq.of(customers);

        List<CustomerOrders> result = customerEnum.groupJoin(
                orders,
                customer -> customer.getName(),
                order -> order.customerName,
                (customer, orderEnum) -> new CustomerOrders(
                        customer.getName(),
                        orderEnum.select(o -> o.product).toList()
                )
        ).toList();

        // 验证结果
        for (CustomerOrders co : result) {
            if ("Alice".equals(co.customerName)) {
                assertEquals(2, co.products.size());
                assertTrue(co.products.contains("Book"));
                assertTrue(co.products.contains("Pen"));
            } else if ("Bob".equals(co.customerName)) {
                assertEquals(1, co.products.size());
                assertEquals("Laptop", co.products.get(0));
            } else if ("Charlie".equals(co.customerName)) {
                assertTrue(co.products.isEmpty());
            }
        }
    }

    static class Order {
        String customerName;
        String product;

        Order(String customerName, String product) {
            this.customerName = customerName;
            this.product = product;
        }
    }

    static class CustomerOrders {
        String customerName;
        List<String> products;

        CustomerOrders(String customerName, List<String> products) {
            this.customerName = customerName;
            this.products = products;
        }
    }

    // ==================== GroupBy 方法测试 ====================

    @Test
    @DisplayName("groupBy - 分组")
    void testGroupBy() {
        Enumerable<Person> enumerable = Linq.of(people);

        List<Group<String, Person>> groups = enumerable.groupBy(Person::getCity).toList();

        // 验证分组
        for (Group<String, Person> group : groups) {
            if ("New York".equals(group.getKey())) {
                assertEquals(2, group.getElements().count());
            } else if ("Los Angeles".equals(group.getKey())) {
                assertEquals(2, group.getElements().count());
            }
        }

        // 带元素选择器的分组
        var nameGroups = enumerable.groupBy(Person::getCity, Person::getName).toList();
        for (var group : nameGroups) {
            if ("New York".equals(group.getKey())) {
                assertTrue(group.getElements().contains("Alice"));
                assertTrue(group.getElements().contains("Charlie"));
            }
        }
    }

    // ==================== Select 方法测试 ====================

    @Test
    @DisplayName("select - 投影")
    void testSelect() {
        Enumerable<Person> enumerable = Linq.of(people);

        // 简单选择
        List<String> names = enumerable.select(Person::getName).toList();
        assertEquals(5, names.size());
        assertTrue(names.contains("Alice"));

        // 带索引的选择
        List<String> namesWithIndex = enumerable.select((p, i) -> i + ": " + p.getName()).toList();
        assertEquals("0: Alice", namesWithIndex.get(0));
        assertEquals("1: Bob", namesWithIndex.get(1));
    }

    @Test
    @DisplayName("selectMany - 展开选择")
    void testSelectMany() {
        List<Department> departments = Arrays.asList(
                new Department("Engineering", Arrays.asList("Alice", "Bob")),
                new Department("Sales", Arrays.asList("Charlie", "David"))
        );

        Enumerable<Department> enumerable = Linq.of(departments);

        // 简单的selectMany
        List<String> allEmployees = enumerable.selectMany(d -> d.getEmployees()).toList();
        assertEquals(4, allEmployees.size());
        assertTrue(allEmployees.contains("Alice"));
        assertTrue(allEmployees.contains("David"));

        // 带索引的selectMany
        List<String> employeesWithDeptIndex = enumerable.selectMany(
                (d, i) -> d.getEmployees().stream()
                        .map(e -> i + "-" + e)
                        .toList()
        ).toList();

        // 带结果选择器的selectMany
        List<String> deptEmployeePairs = enumerable.selectMany(
                d -> d.getEmployees(),
                (d, e) -> d.getName() + ": " + e
        ).toList();
        assertTrue(deptEmployeePairs.contains("Engineering: Alice"));

        // 带结果选择器的selectMany
        List<String> deptEmployeePairsIndex = enumerable.selectMany(
                (d, i) -> d.getEmployees(),
                (d, e) -> d.getName() + ": " + e
        ).toList();
        assertTrue(deptEmployeePairs.contains("Engineering: Alice"));
    }

    static class Department {
        String name;
        List<String> employees;

        Department(String name, List<String> employees) {
            this.name = name;
            this.employees = employees;
        }

        String getName() {
            return name;
        }

        List<String> getEmployees() {
            return employees;
        }
    }

    // ==================== Skip 方法测试 ====================

    @Test
    @DisplayName("skip - 跳过元素")
    void testSkip() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        // skip
        List<Integer> skipped = enumerable.skip(5).toList();
        assertEquals(Arrays.asList(6, 7, 8, 9, 10), skipped);

        // skipWhile
        List<Integer> skippedWhile = enumerable.skipWhile(n -> n < 5).toList();
        assertEquals(Arrays.asList(5, 6, 7, 8, 9, 10), skippedWhile);

        // skipWhile带索引
        List<Integer> skippedWhileWithIndex = enumerable.skipWhile((n, i) -> i < 3).toList();
        assertEquals(Arrays.asList(4, 5, 6, 7, 8, 9, 10), skippedWhileWithIndex);

        // skipLast
        List<Integer> skippedLast = enumerable.skipLast(3).toList();
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7), skippedLast);
    }

    // ==================== Sum 方法测试 ====================

    @Test
    @DisplayName("sum - 求和")
    void testSum() {
        Enumerable<Person> enumerable = Linq.of(people);

        // sumInt
        int intSum = enumerable.sumInt(Person::getAge);
        assertEquals(150, intSum); // 25+30+35+28+32

        // sumLong
        List<Long> longs = Arrays.asList(100L, 200L, 300L);
        Enumerable<Long> longEnum = Linq.of(longs);
        long longSum = longEnum.sumLong(l -> l);
        assertEquals(600L, longSum);

        // sumFloat
        List<Float> floats = Arrays.asList(1.5f, 2.5f, 3.5f);
        Enumerable<Float> floatEnum = Linq.of(floats);
        float floatSum = floatEnum.sumFloat(f -> f);
        assertEquals(7.5f, floatSum, 0.001);

        // sumDouble
        double doubleSum = enumerable.sumDouble(p -> p.getAge() * 1.0);
        assertEquals(150.0, doubleSum, 0.001);

        // sumBigDecimal
        List<BigDecimal> decimals = Arrays.asList(
                new BigDecimal("10.5"),
                new BigDecimal("20.5")
        );
        Enumerable<BigDecimal> decimalEnum = Linq.of(decimals);
        BigDecimal decimalSum = decimalEnum.sumBigDecimal(bd -> bd);
        assertEquals(new BigDecimal("31.0"), decimalSum);
    }

    // ==================== Take 方法测试 ====================

    @Test
    @DisplayName("take - 获取前N个元素")
    void testTake() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        List<Integer> firstThree = enumerable.take(3).toList();
        assertEquals(Arrays.asList(1, 2, 3), firstThree);

        // 取0个
        assertTrue(enumerable.take(0).toList().isEmpty());

        // 取超过元素个数
        assertEquals(10, enumerable.take(20).count());
    }

    // ==================== Where 方法测试 ====================

    @Test
    @DisplayName("where - 筛选")
    void testWhere() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        // where带简单谓词
        List<Integer> evens = enumerable.where(n -> n % 2 == 0).toList();
        assertEquals(Arrays.asList(2, 4, 6, 8, 10), evens);

        // where带索引谓词
        List<Integer> evenIndexed = enumerable.where((n, i) -> i % 2 == 0).toList();
        assertEquals(Arrays.asList(1, 3, 5, 7, 9), evenIndexed);
    }

    // ==================== ToList 方法测试 ====================

    @Test
    @DisplayName("toList - 转换为列表")
    void testToList() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        List<Integer> list = enumerable.toList();
        assertEquals(10, list.size());
        assertEquals(numbers, list);
    }

    // ==================== ToLookup 方法测试 ====================

    @Test
    @DisplayName("toLookUp - 创建查找表")
    void testToLookup() {
        Enumerable<Person> enumerable = Linq.of(people);

        LookUp<String, Person> lookup = enumerable.toLookUp(Person::getCity);

        // 验证查找表
        assertTrue(lookup.contains("New York"));
        assertEquals(2, lookup.get("New York").count());

        assertTrue(lookup.contains("Los Angeles"));
        assertEquals(2, lookup.get("Los Angeles").count());

        assertTrue(lookup.contains("Chicago"));
        assertEquals(1, lookup.get("Chicago").count());
    }

    // ==================== 迭代器测试 ====================

    @Test
    @DisplayName("iterator - 迭代器功能")
    void testIterator() {
        Enumerable<Integer> enumerable = Linq.of(numbers);

        int sum = 0;
        for (int num : enumerable) {
            sum += num;
        }
        assertEquals(55, sum);
    }

    // ==================== 边界情况和异常测试 ====================

    @Test
    @DisplayName("空序列测试")
    void testEmptySequence() {
        Enumerable<Integer> empty = Linq.of(Collections.emptyList());

        assertFalse(empty.any());
        assertTrue(empty.all(n -> true));
        assertEquals(0, empty.count());
        assertNull(empty.firstOrDefault());
        assertTrue(empty.toList().isEmpty());

        // 空序列的聚合应该抛出异常
        assertThrows(UnsupportedOperationException.class, () -> empty.aggregate((a, b) -> a + b));
    }

    @Test
    @DisplayName("null值处理测试")
    void testNullHandling() {
        List<String> listWithNulls = Arrays.asList("a", null, "b", null, "c");
        Enumerable<String> enumerable = Linq.of(listWithNulls);

        // 检查包含null
        assertTrue(enumerable.contains(null));

        // 过滤掉null
        List<String> withoutNulls = enumerable.where(s -> s != null).toList();
        assertEquals(Arrays.asList("a", "b", "c"), withoutNulls);
    }

    @Test
    @DisplayName("大数据集测试")
    void testLargeDataset() {
        // 创建包含大量数据的序列
        List<Integer> largeList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            largeList.add(i);
        }

        Enumerable<Integer> enumerable = Linq.of(largeList);

        // 测试各种操作
        assertEquals(10000, enumerable.count());
        assertEquals(49995000, enumerable.sumInt(n -> n)); // 0+1+2+...+9999
        assertEquals(9999, enumerable.elementAt(9999));

        // 测试并行处理（如果支持的话）
        int sum = enumerable.where(n -> n % 2 == 0).sumInt(n -> n);
        assertTrue(sum > 0);
    }

    @Test
    @DisplayName("惰性求值测试")
    void testLazyEvaluation() {
        // 测试序列操作是否是惰性的
        List<Integer> source = Arrays.asList(1, 2, 3, 4, 5);
        final int[] operationCount = {0};

        Enumerable<Integer> enumerable = new Enumerable<>(source.stream()
                .map(n -> {
                    operationCount[0]++;
                    return n;
                })::iterator);

        // 应用转换但不触发求值
        Enumerable<Integer> transformed = enumerable
                .where(n -> n > 2)
                .select(n -> n * 2);

        // 此时应该还没有执行任何操作
        assertEquals(0, operationCount[0]);

        // 触发求值
        List<Integer> result = transformed.toList();

        // 现在操作应该被执行了
        assertEquals(5, operationCount[0]); // 5个元素都被处理了
        assertEquals(Arrays.asList(6, 8, 10), result);
    }
}