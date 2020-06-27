[![Build Status](https://travis-ci.com/dejankos/Java-HashMap-Analyser.svg?branch=master)](https://travis-ci.com/dejankos/Java-HashMap-Analyser)
[![codecov](https://codecov.io/gh/dejankos/Java-HashMap-Analyser/branch/master/graph/badge.svg)](https://codecov.io/gh/dejankos/Java-HashMap-Analyser)
# Java Hash Map Analyzer

Simple HashMap analyser which can give you an info about:
- underlying hash table size (bucket count)
- used bucket and indexes
- data stored in buckets with hash - for simplicity data from bucket is converted to Linked List 
- type of buckets (before conversion) - linked nodes or self balanced red black binary search tree



# Examples 

Analyze map usage 

```java
HashMap<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.put("b", 2);
map.put("c", 3);

// new analyzer intance with key and value types
HashMapAnalyzer<String, Integer> analyzer = new HashMapAnalyzer<>(String.class, Integer.class);

// HashMapMetadata provides analyzed data
HashMapMetadata<String, Integer> hashMapMetadata = analyzer.analyse(map);
```

HashMapMetadata structure for given example 
<details><summary>Collapse to display structure</summary>

```
HashMapMetadata{
    totalBucketsCount=16, 
    usedBucketsCount=3, 
    bucketsMetadata=
        [
        BucketMetadata{
            bucketIndex=1, 
            nodeType=LINKED_LIST_NODE,
            nodesData=[
                NodeData{
                    key=a, 
                    value=1, 
                    hashCode=97
                    }]}, 
        BucketMetadata{
            bucketIndex=2, 
            nodeType=LINKED_LIST_NODE, 
            nodesData=[
                NodeData{
                    key=b, 
                    value=2, 
                    hashCode=98
                    }]}, 
        BucketMetadata{
            bucketIndex=3, 
            nodeType=LINKED_LIST_NODE, 
            nodesData=[
                NodeData{
                    key=c, 
                    value=3, 
                    hashCode=99
                    }]}
        ]
}
```
</details>

Sort analysed data and check largest bucket

```java
HashMapAnalyzer<String, Integer> analyzer = new HashMapAnalyzer<>(String.class, Integer.class);
HashMapMetadata<String, Integer> mapMetadata = analyzer.analyse(mapWithBucketCollision);

BucketSorter.sort(mapMetadata);

System.out.println(mapMetadata.getBucketsMetadata().stream().findFirst());

```

Tested with OpenJDK 12 & 13.


#### More examples under /src/test/java

## License

Java-HashMap-Analyser is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
