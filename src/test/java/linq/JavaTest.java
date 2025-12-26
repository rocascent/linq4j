package linq;

import kotlin.jvm.functions.Function1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class JavaTest {

    @Test
    public void testEmptyEnumerable() {
        // 测试 Kotlin 伴生对象的静态方法
        Enumerable<String> empty = Enumerable.of();
        Assertions.assertEquals(0, empty.count());
        Assertions.assertFalse(empty.iterator().hasNext());
    }

    @Test
    public void testArrayEnumerable() {
        // 测试 vararg 工厂方法
        Enumerable<Integer> numbers = Enumerable.of(1, 2, 3, 4, 5);
        Assertions.assertEquals(5, numbers.count());

        // 测试迭代
        int sum = 0;
        for (Integer num : numbers) {
            sum += num;
        }
        Assertions.assertEquals(15, sum);
    }

    @Test
    public void testIterableEnumerable() {
        // 测试 Iterable 工厂方法
        List<String> list = Arrays.asList("A", "B", "C");
        Enumerable<String> enumerable = Enumerable.of(list);

        Assertions.assertEquals(3, enumerable.count());
        Assertions.assertEquals("A", enumerable.first());
    }

    @Test
    public void testFirstMethod() {
        Enumerable<Integer> numbers = Enumerable.of(1, 2, 3, 4, 5);

        // 测试 first() 方法
        Assertions.assertEquals(Integer.valueOf(1), numbers.first());

        // 测试带谓词的 first()
        Integer firstEven = numbers.first(new Function1<Integer, Boolean>() {
            @Override
            public Boolean invoke(Integer integer) {
                return integer % 2 == 0;
            }
        });
        Assertions.assertEquals(Integer.valueOf(2), firstEven);
    }

    @Test
    public void testFirstOrDefault() {
        Enumerable<Integer> numbers = Enumerable.of(1, 2, 3);

        // 测试 firstOrDefault() - 无参版本
        Assertions.assertEquals(Integer.valueOf(1), numbers.firstOrDefault());

        // 测试带默认值的 firstOrDefault()
        Integer result = numbers.firstOrDefault(999);
        Assertions.assertEquals(Integer.valueOf(1), result);

        // 测试空序列的 firstOrDefault()
        Enumerable<Integer> empty = Enumerable.of();
        Assertions.assertNull(empty.firstOrDefault());
        Assertions.assertEquals(Integer.valueOf(999), empty.firstOrDefault(999));
    }

    @Test
    public void testFirstOrDefaultWithPredicate() {
        Enumerable<Integer> numbers = Enumerable.of(1, 3, 5, 7);

        // 使用 lambda 表达式（Java 8+）
        Integer firstEven = numbers.firstOrDefault(n -> n % 2 == 0);
        Assertions.assertNull(firstEven);

        // 带默认值的版本
        Integer firstEvenWithDefault = numbers.firstOrDefault(
                n -> n % 2 == 0,
                999
        );
        Assertions.assertEquals(Integer.valueOf(999), firstEvenWithDefault);
    }

    @Test
    public void testSelectMethod() {
        Enumerable<Integer> numbers = Enumerable.of(1, 2, 3);

        // 测试 select() 方法 - Java 中使用 lambda
        Enumerable<String> strings = numbers.select(new Function1<Integer, String>() {
            @Override
            public String invoke(Integer n) {
                return "Number: " + n;
            }
        });

        // Java 8+ 可以使用 lambda
        Enumerable<String> stringsLambda = numbers.select(n -> "Number: " + n);

        List<String> result = stringsLambda.toList();
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("Number: 1", result.get(0));
        Assertions.assertEquals("Number: 3", result.get(2));
    }

    @Test
    public void testSelectWithIndex() {
        Enumerable<String> items = Enumerable.of("A", "B", "C");

        // 测试带索引的 select
        Enumerable<String> indexed = items.select((item, index) -> item + "-" + index);

        List<String> result = indexed.toList();
        Assertions.assertEquals("A-0", result.get(0));
        Assertions.assertEquals("B-1", result.get(1));
        Assertions.assertEquals("C-2", result.get(2));
    }

    @Test
    public void testSelectMany() {
        Enumerable<String> phrases = Enumerable.of("Hello World", "Kotlin Java");

        // 测试 selectMany
        Enumerable<String> words = phrases.selectMany(new Function1<String, Iterable<String>>() {
            @Override
            public Iterable<String> invoke(String phrase) {
                return Arrays.asList(phrase.split(" "));
            }
        });

        List<String> result = words.toList();
        Assertions.assertEquals(4, result.size());
        Assertions.assertTrue(result.contains("Hello"));
        Assertions.assertTrue(result.contains("World"));
        Assertions.assertTrue(result.contains("Kotlin"));
        Assertions.assertTrue(result.contains("Java"));
    }

    @Test
    public void testWhereMethod() {
        Enumerable<Integer> numbers = Enumerable.of(1, 2, 3, 4, 5, 6);

        // 测试 where() 方法 - 过滤偶数
        Enumerable<Integer> evens = numbers.where(new Function1<Integer, Boolean>() {
            @Override
            public Boolean invoke(Integer n) {
                return n % 2 == 0;
            }
        });

        // Java 8+ lambda
        Enumerable<Integer> evensLambda = numbers.where(n -> n % 2 == 0);

        List<Integer> result = evensLambda.toList();
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(Integer.valueOf(2), result.get(0));
        Assertions.assertEquals(Integer.valueOf(4), result.get(1));
        Assertions.assertEquals(Integer.valueOf(6), result.get(2));
    }

    @Test
    public void testWhereWithIndex() {
        Enumerable<String> items = Enumerable.of("A", "B", "C", "D", "E");

        // 测试带索引的 where
        Enumerable<String> result = items.where((item, index) -> index % 2 == 0);

        List<String> filtered = result.toList();
        Assertions.assertEquals(3, filtered.size());
        Assertions.assertEquals("A", filtered.get(0));
        Assertions.assertEquals("C", filtered.get(1));
        Assertions.assertEquals("E", filtered.get(2));
    }

    @Test
    public void testSkipAndTake() {
        Enumerable<Integer> numbers = Enumerable.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 测试 skip
        Enumerable<Integer> skipped = numbers.skip(3);
        Assertions.assertEquals(Integer.valueOf(4), skipped.first());
        Assertions.assertEquals(7, skipped.count());

        // 测试 take
        Enumerable<Integer> taken = numbers.take(3);
        Assertions.assertEquals(3, taken.count());
        Assertions.assertEquals(Integer.valueOf(1), taken.first());

        // 测试链式调用
        List<Integer> result = numbers.skip(2).take(3).toList();
        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(Integer.valueOf(3), result.get(0));
        Assertions.assertEquals(Integer.valueOf(4), result.get(1));
        Assertions.assertEquals(Integer.valueOf(5), result.get(2));
    }

    @Test
    public void testSkipWhile() {
        Enumerable<Integer> numbers = Enumerable.of(1, 2, 3, 4, 5, 1, 2, 3);

        // 测试 skipWhile
        Enumerable<Integer> skipped = numbers.skipWhile(n -> n < 3);

        List<Integer> result = skipped.toList();
        Assertions.assertEquals(6, result.size());
        Assertions.assertEquals(Integer.valueOf(3), result.get(0));
        Assertions.assertEquals(Integer.valueOf(4), result.get(1));
        Assertions.assertEquals(Integer.valueOf(5), result.get(2));
        Assertions.assertEquals(Integer.valueOf(1), result.get(3));
        Assertions.assertEquals(Integer.valueOf(2), result.get(4));
    }

    @Test
    public void testCountMethods() {
        Enumerable<Integer> numbers = Enumerable.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 测试 count()
        Assertions.assertEquals(10, numbers.count());

        // 测试带谓词的 count()
        int evenCount = numbers.count(n -> n % 2 == 0);
        Assertions.assertEquals(5, evenCount);

        // 测试 longCount()
        long total = numbers.longCount();
        Assertions.assertEquals(10L, total);

        // 测试带谓词的 longCount()
        long oddLongCount = numbers.longCount(n -> n % 2 != 0);
        Assertions.assertEquals(5L, oddLongCount);
    }

    @Test
    public void testSumMethods() {
        Enumerable<Item> items = Enumerable.of(
                new Item("A", 10),
                new Item("B", 20),
                new Item("C", 30)
        );

        // 测试 sumInt
        int intSum = items.sumInt(Item::getValue);
        Assertions.assertEquals(60, intSum);

        // 测试 sumLong
        long longSum = items.sumLong(item -> (long) item.getValue());
        Assertions.assertEquals(60L, longSum);

        // 测试 sumDouble
        double doubleSum = items.sumDouble(item -> (double) item.getValue() * 1.5);
        Assertions.assertEquals(90.0, doubleSum, 0.001);

        // 测试 sumFloat
        float floatSum = items.sumFloat(item -> (float) item.getValue() * 0.5f);
        Assertions.assertEquals(30.0f, floatSum, 0.001);
    }

    @Test
    public void testToList() {
        // 测试 toList 方法
        Enumerable<Integer> numbers = Enumerable.of(1, 2, 3, 4, 5);
        List<Integer> list = numbers.toList();

        Assertions.assertEquals(5, list.size());
        Assertions.assertEquals(Integer.valueOf(1), list.get(0));
        Assertions.assertEquals(Integer.valueOf(5), list.get(4));

        // 确保返回的是可修改的列表（如果实现是可修改的）
        list.add(6);
        Assertions.assertEquals(6, list.size());
    }

    @Test
    public void testChainedOperations() {
        // 测试链式操作 - 模拟典型的 LINQ 查询
        Enumerable<Integer> numbers = Enumerable.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<String> result = numbers
                .where(n -> n % 2 == 0)           // 取偶数
                .skip(1)                          // 跳过第一个
                .take(3)                          // 取前三个
                .select(n -> "Number: " + n * 2)  // 乘以2并转换为字符串
                .toList();

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("Number: 8", result.get(0));  // 4*2
        Assertions.assertEquals("Number: 12", result.get(1)); // 6*2
        Assertions.assertEquals("Number: 16", result.get(2)); // 8*2
    }

    // 辅助类用于测试
    private static class Item {
        private final String name;
        private final int value;

        public Item(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    @Test
    public void testMethodReferences() {
        // 测试 Java 方法引用与 Kotlin 的互操作性
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        Enumerable<String> enumerable = Enumerable.of(names);

        // 使用方法引用
        List<Integer> lengths = enumerable.select(String::length).toList();

        Assertions.assertEquals(3, lengths.size());
        Assertions.assertEquals(Integer.valueOf(5), lengths.get(0));
        Assertions.assertEquals(Integer.valueOf(3), lengths.get(1));
        Assertions.assertEquals(Integer.valueOf(7), lengths.get(2));
    }
}