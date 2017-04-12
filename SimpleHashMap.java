/**
 *  ����ʵ��һ���򵥵�HashMap��֧�ֶ�Key, Value�Ļ�������
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

    // Ĭ�ϵ�Ͱ��С
    private static final int INITIAL_CAPACITY = 1 << 4;
    // ����ʼͰ��С����
    private static final int MAX_INITIAL_CAPACITY = 64;
    // �����������Ͱ��С
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    // �洢�ڵ��Ͱ
    private LinkedList<Node<K, V>>[] buckets;
    // ��������ʱ��װ������
    private static final float loadFactor = 0.75f;
    // ��ֵ
    private int threshold;
    // �ڵ����
    private int size = 0;
    // �ڵ�ṹ
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
     *  hash����
     *  @param key ��ֵ
     */
    private static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
    /**
     *  ��Map�����Ԫ��
     *  @param key ��ֵ
     *  @param value ֵ
     */
    public V put(K key, V value) {
        V oldValue = null;
        if (size >= threshold) {
            resize(2 * buckets.length);
        }
        // ��ȡͰ�±�
        int index = indexFor(hash(key), buckets.length);
        // ��Ͱ��Ϊ�գ��򴴽���
        if (buckets[index] == null) {
            buckets[index] = new LinkedList<Node<K, V>>();
        }
        // �ҵ�Ͱ�е���
        LinkedList<Node<K, V>> bucket = buckets[index];
        // �¼���ļ�ֵ�Զ���
        Node<K, V> pair = new Node<K, V>(key, value);
        // �ж��Ƿ��ҵ���ͬkey
        boolean found = false;
        // ������ӦͰ�е���
        Iterator<Node<K, V>> iter = bucket.iterator();
        // ������ӦͰ�е���
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
     *  ���ֵ
     *  @param key ��ֵ
     */
    public V get(Object key) {
        int index = indexFor(hash(key), buckets.length);
        // ��Ͱ���޽ڵ�
        if (buckets[index] == null)
            return null;
        for (Node<K, V> iPair : buckets[index]) {
            if (iPair.getKey().equals(key))
                return iPair.getValue();
        }
        return null;
    }
    /**
     *  ��hashmap���Ƴ�ָ��key��ӳ���ϵ
     *  @param key ��ֵ
     */
    public V remove(Object key) {
        V oldValue = null;
        int index = indexFor(hash(key), buckets.length);
        // ��Ͱ���޽ڵ�
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
     *   ���ؽڵ����   
     */
    public int size() {
        return size;
    }

    /**
     *  ��Ͱ��������
     *  @param capacity �����Ͱ�Ĵ�С  
     */
    private void resize(int capacity) {
        LinkedList<Node<K, V>>[] oldBuckets = buckets;
        int oldCapacity = oldBuckets.length;
        // �Ѿ��ﵽ�������
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }
        LinkedList<Node<K, V>>[] newBuckets = new LinkedList[capacity];
        // ����Ͱ�ڵ�ת���µ�Ͱ��
        transfer(newBuckets);
        buckets = newBuckets;
        // �޸���ֵ
        threshold = tableSizeFor(loadFactor);
    }

    /**
     *  ����Ͱ�ڵ�ת���µ�Ͱ��
     *  @param newBuckets �´���Ͱ������
     */
    private void transfer(LinkedList<Node<K, V>>[] newBuckets) {
        LinkedList<Node<K, V>>[] temp = buckets;
        int oldCapacity = temp.length;
        int index = 0;
        boolean found = false;
        // ������Ͱ�е�ֵ
        for (int i = 0; i < oldCapacity; i++) {
            // �ҵ�Ͱ�е���
            LinkedList<Node<K, V>> bucket = temp[i];
            // ��ͰΪ�գ�������
            if (bucket == null) continue;
            // ������ӦͰ�е���
            Iterator<Node<K, V>> iter = bucket.iterator();
            // ������ӦͰ�е���
            while (iter.hasNext()) {
                Node<K, V> iPair = iter.next();
                // ���»��Ͱ�±�ֵ����ΪͰ�Ĵ�С�����仯
                index = indexFor(hash(iPair.getKey()), newBuckets.length);
                // ��Ͱ��Ϊ�գ��򴴽���
                if (newBuckets[index] == null) {
                    newBuckets[index] = new LinkedList<Node<K, V>>();
                }
                // �ҵ�Ͱ�е���
                LinkedList<Node<K, V>> bucketTemp = newBuckets[index];
                // �ж��Ƿ��ҵ���ͬkey
                found = false;
                // ����Ͱ�е���
                Iterator<Node<K, V>> iterator = bucketTemp.iterator();
                // ����Ͱ�е���
                while (iterator.hasNext()) {
                    Node<K, V> iTemp = iterator.next();
                    if (iTemp.getKey().equals(iPair.getKey())) {
                        iTemp.setValue(iPair.getValue());
                        found = true;
                        break;
                    }
                }
                // δ�ҵ���ͬkeyֵ����������
                if (!found) {
                    newBuckets[index].addFirst(iPair);
                }
            }
        }
    }
    /**
     *  ����Ͱ�±�
     *  @param h ��ϣ����ֵ
     *  @param length ��ǰͰ��С
     */
    private int indexFor(int h, int length) {
        return h & (length-1);
    }

    /**
     *  ������ֵ����Ϊ�漰float��int���㣬ֻ�Ƕ�float����������ʱ����
     *  @param loadFactor װ������
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
     *  ����
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