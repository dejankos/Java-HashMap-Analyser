[![CI Status](https://github.com/dejankos/Java-HashMap-Analyser/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/dejankos/Java-HashMap-Analyser/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/dejankos/Java-HashMap-Analyser/branch/master/graph/badge.svg)](https://codecov.io/gh/dejankos/Java-HashMap-Analyser)
# Java Hash Map Analyser

Simple HashMap analyser which can give you an info about:
- underlying hash table size (bucket count)
- used bucket and indexes
- data stored in buckets with hash - for simplicity data from bucket is converted to Array List 
- type of buckets (before conversion) - linked nodes or self balanced red black binary search tree



# Examples 

Analyse map usage 

```java
HashMap<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.put("b", 2);
map.put("c", 3);

// new analyser intance with key and value types
HashMapAnalyser<String, Integer> analszer = new HashMapAnalszer<>(String.class, Integer.class);

// HashMapMetadata provides analysed data
HashMapMetadata<String, Integer> hashMapMetadata = analyser.analyse(map);
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
HashMapAnalszer<String, Integer> analyser = new HashMapAnalyser<>(String.class, Integer.class);
HashMapMetadata<String, Integer> mapMetadata = analyser.analyse(mapWithBucketCollision);

BucketSorter.sort(mapMetadata);

System.out.println(mapMetadata.getBucketsMetadata().stream().findFirst());

```

Tested with OpenJDK `[ '12', '13', '15' ]`.


#### More examples under /src/test/java

## License

Java-HashMap-Analyser is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
