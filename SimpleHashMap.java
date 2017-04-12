/**
 *  此类实现一个简单的HashMap，支持对Key, Value的基本操作
 *  @author Scuderia
 */
package com.map;

import java.util.Map;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import com.linked.LinkedList;

public class SimpleHashMap<K, V> extends AbstractMap<K, V> {

    // 默认的桶大小
    private static final int INITIAL_CAPACITY = 1 << 4;
    // 最大初始桶大小容量
    private static final int MAX_INITIAL_CAPACITY = 64;
    // 最大可以扩充的桶大小
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    // 存储节点的桶
    private LinkedList<Node<K, V>>[] buckets;
    // 数组扩容时的装载因子
    private static final float loadFactor = 0.75f;
    // 阈值
    private int threshold;
    // 节点个数
    private int size = 0;
    // 节点结构
    static class Node<K, V> implements Map.Entry<K,V> {

        private final K key;
        private V value;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public final K getKey() { 
            return key; 
        }

        public final V getValue() { 
            return value; 
        }

        public final String toString() { 
            return key + " -> " + value; 
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            this.value = newValue;
            return oldValue;
        }
        public final int hashCode() {
            return key.hashCode() ^ value.hashCode();
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (key.equals(e.getKey()) &&
                    value.equals(e.getValue()))
                    return true;
            }
            return false;
        }
    }

    public SimpleHashMap() {
        this(INITIAL_CAPACITY);
    }

    public SimpleHashMap(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        if (initialCapacity > MAX_INITIAL_CAPACITY)
            initialCapacity = MAX_INITIAL_CAPACITY;
        buckets = new LinkedList[initialCapacity];
        threshold = tableSizeFor(loadFactor);
    }
    /**
     *  hash函数
     *  @param key 键值
     */
    private static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
    /**
     *  向Map中添加元素
     *  @param key 键值
     *  @param value 值
     */
    public V put(K key, V value) {
        V oldValue = null;
        if (size >= threshold) {
            resize(2 * buckets.length);
        }
        // 获取桶下标
        int index = indexFor(hash(key), buckets.length);
        // 若桶中为空，则创建链
        if (buckets[index] == null) {
            buckets[index] = new LinkedList<Node<K, V>>();
        }
        // 找到桶中的链
        LinkedList<Node<K, V>> bucket = buckets[index];
        // 新加入的键值对对象
        Node<K, V> pair = new Node<K, V>(key, value);
        // 判断是否找到相同key
        boolean found = false;
        // 遍历对应桶中的链
        Iterator<Node<K, V>> iter = bucket.iterator();
        // 遍历对应桶中的链
        while (iter.hasNext()) {
            Node<K, V> iPair = iter.next();
            if (iPair.getKey().equals(key)) {
                oldValue = iPair.getValue();
                iPair.setValue(value);
                found = true;
                break;
            }
        }
        if (!found) {
            buckets[index].addFirst(pair);
            size++;
        }
        return oldValue;
    }
    /**
     *  获得值
     *  @param key 键值
     */
    public V get(Object key) {
        int index = indexFor(hash(key), buckets.length);
        // 若桶中无节点
        if (buckets[index] == null)
            return null;
        for (Node<K, V> iPair : buckets[index]) {
            if (iPair.getKey().equals(key))
                return iPair.getValue();
        }
        return null;
    }
    /**
     *  从hashmap中移除指定key的映射关系
     *  @param key 键值
     */
    public V remove(Object key) {
        V oldValue = null;
        int index = indexFor(hash(key), buckets.length);
        // 若桶中无节点
        if (buckets[index] == null)
            return null;
        for (Node<K, V> iPair : buckets[index]) {
            if (iPair.getKey().equals(key)) {
                oldValue = iPair.getValue();
                buckets[index].remove(iPair);
                return oldValue;
            }
        }
        return null;
    }
    /**
     *   返回节点个数   
     */
    public int size() {
        return size;
    }

    /**
     *  对桶进行扩容
     *  @param capacity 扩充后桶的大小  
     */
    private void resize(int capacity) {
        LinkedList<Node<K, V>>[] oldBuckets = buckets;
        int oldCapacity = oldBuckets.length;
        // 已经达到最大容量
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }
        LinkedList<Node<K, V>>[] newBuckets = new LinkedList[capacity];
        // 将旧桶节点转入新的桶中
        transfer(newBuckets);
        buckets = newBuckets;
        // 修改阈值
        threshold = tableSizeFor(loadFactor);
    }

    /**
     *  将旧桶节点转入新的桶中
     *  @param newBuckets 新创建桶的引用
     */
    private void transfer(LinkedList<Node<K, V>>[] newBuckets) {
        LinkedList<Node<K, V>>[] temp = buckets;
        int oldCapacity = temp.length;
        int index = 0;
        boolean found = false;
        // 遍历旧桶中的值
        for (int i = 0; i < oldCapacity; i++) {
            // 找到桶中的链
            LinkedList<Node<K, V>> bucket = temp[i];
            // 若桶为空，则跳过
            if (bucket == null) continue;
            // 遍历对应桶中的链
            Iterator<Node<K, V>> iter = bucket.iterator();
            // 遍历对应桶中的链
            while (iter.hasNext()) {
                Node<K, V> iPair = iter.next();
                // 重新获得桶下标值，因为桶的大小发生变化
                index = indexFor(hash(iPair.getKey()), newBuckets.length);
                // 若桶中为空，则创建链
                if (newBuckets[index] == null) {
                    newBuckets[index] = new LinkedList<Node<K, V>>();
                }
                // 找到桶中的链
                LinkedList<Node<K, V>> bucketTemp = newBuckets[index];
                // 判断是否找到相同key
                found = false;
                // 遍历桶中的链
                Iterator<Node<K, V>> iterator = bucketTemp.iterator();
                // 遍历桶中的链
                while (iterator.hasNext()) {
                    Node<K, V> iTemp = iterator.next();
                    if (iTemp.getKey().equals(iPair.getKey())) {
                        iTemp.setValue(iPair.getValue());
                        found = true;
                        break;
                    }
                }
                // 未找到相同key值，则进行添加
                if (!found) {
                    newBuckets[index].addFirst(iPair);
                }
            }
        }
    }
    /**
     *  计算桶下标
     *  @param h 哈希方法值
     *  @param length 当前桶大小
     */
    private int indexFor(int h, int length) {
        return h & (length-1);
    }

    /**
     *  计算阈值，因为涉及float与int运算，只是对float进行了运算时扩大
     *  @param loadFactor 装载因子
     */
    private int tableSizeFor(float loadFactor) {
        int temp = (int)(loadFactor * 100);
        int result = (int)(buckets.length * temp) / 100;
        return Math.min(result, MAXIMUM_CAPACITY+1);
    }

    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = new HashSet<Map.Entry<K, V>>();
        for (LinkedList<Node<K, V>> bucket : buckets) {
            if (bucket == null) continue;
            for (Node<K, V> mpair : bucket)
                set.add(mpair);
        }
        return set;
    }
    /**
     *  测试
     */
    // public static void main(String[] args) {
    //     SimpleHashMap<String, String> m = new SimpleHashMap<String, String>(2);
    //     for (int i = 0; i < 25; i++) 
    //         m.put(("test" + i), ("testhashmap" + i));
    //     System.out.println(m.get("test4"));
    //     m.remove("test19");
    //     for (LinkedList<Node<String, String>> b : m.buckets)
    //         System.out.println(b);
    // }
}