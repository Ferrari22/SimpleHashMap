/**
 *  ����ʵ��һ������ṹ��֧�ֶ�����Ļ�������
 *  @author Scuderia
 */
package com.linked;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedList<V> implements Iterable<V>
{
    // ͷ�ڵ�
    private DataNode first;
    // β�ڵ�
    private DataNode last;
    // �����С
    private int size;

    // ����ڵ�ṹ
    class DataNode {
        V data;
        // ָ����һ���ڵ�ָ��
        DataNode previous;
        // ָ����һ���ڵ�ָ��
        DataNode next;
    }
    /**
     *  ������ͷ����ڵ�
     *  @param data ��Ҫ����Ľڵ�����
     */
    public void addFirst(V data) {
        // �����СΪ0
        if (size == 0) {
            first = new DataNode();
            last = new DataNode();
            first.data = data;
            first.previous = null;
            first.next = null;
            last = first;
            size++;
        } else {
            // ������ʱ�ڵ�
            DataNode temp = new DataNode();
            temp.data = data;
            temp.previous = null;
            temp.next = first;
            first.previous = temp;
            // ����ͷ�ڵ�
            first = temp;
            // ������ʱ��������
            temp = null;
            size++;
        }
    }
    /**
     *  ������β����ڵ�
     *  @param data ��Ҫ����Ľڵ�����
     */
    public void addLast(V data) {
        // �����СΪ0
         if (size == 0) {
            first = new DataNode();
            last = new DataNode();
            first.data = data;
            first.previous = null;
            first.next = null;
            last = first;
            size++;
        } else {
            // ������ʱ�ڵ�
            DataNode temp = new DataNode();
            temp.data = data;
            temp.next = null;
            temp.previous = last;
            last.next = temp;
            // ����β�ڵ�
            last = temp;
            // ������ʱ��������
            temp = null;
            size++;
        }
     }
     /**
      *  �������в���ڵ�
      *  @param data ��Ҫ����Ľڵ�����
      */
     public void add(V data) {
         addLast(data);
     }
     /**
      *  ɾ���ڵ�
      *  @param data ��Ҫɾ���Ľڵ�����
      */
    public boolean remove(V data) {
        // ������ֻʣһ���ڵ�
        if (size == 1) {
            first = null;
            size--;
            return true;
        }
        // ��ɾ���Ľڵ�Ϊͷ�ڵ�
        if (data.equals(first.data)) {
            DataNode temp = first;
            first = first.next;
            first.previous = null;
            size--;
            temp = null;
            return true;
        }
        // ��ɾ��β�ڵ�
        if (data.equals(last.data)) {
            DataNode temp = last;
            last = last.previous;
            last.next = null;
            size--;
            temp = null;
            return true;
        }
        // ɾ�������еĽڵ�
        for (DataNode x = first; x != null; x = x.next) {
            if (data.equals(x.data)) {
                x.previous.next = x.next;
                x.next.previous = x.previous;
                size--;
                x = null;
                return true;
            }
        }
        return false;
    }
    /**
     *   ���������С
     */
    public int size() {
        return size;
    }
    /**
     *   ���������Ƿ�Ϊ��
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     *   �������
     */
    public Iterator<V> iterator() {
         return new ListIterator();
    }
    private class ListIterator implements Iterator<V>
    {
         private DataNode current = first;
         @Override
         public boolean hasNext() {
             return current != null;
         }
         @Override
         public V next() {
             if (isEmpty()) {
                 throw new NoSuchElementException("The List is Empty!");
             } else {
                 V data = current.data;
                 current = current.next;
                 return data;
             }
         }
         @Override
         public void remove() {
             throw new UnsupportedOperationException();
         }
    }
    /**
     *   ��ʽ���������
     */
    public String toString() {

        StringBuilder sb = new StringBuilder();
        int index = size;
        // ������������
        for (DataNode x = first; x != null; x = x.next) {
            sb.append(x.data.toString());
            index--;
            if (index > 0)
                sb.append(" -> ");
        }
        return sb.toString();
    }
    /**
     *  ��������
     */
    // public static void main(String[] args) {
    //     LinkedList<String> list = new LinkedList<String>();
    //     list.addFirst("aaa");
    //     list.addFirst("bbb");
    //     list.add("ccc");
    //     list.remove("aaa");
    //     list.remove("bbb");
    //     list.remove("ccc");
    //     System.out.println(list);
    //     System.out.println(list.size());
    //     System.out.println(list.isEmpty());
    //     Iterator<String> iter = list.iterator();
    //     while(iter.hasNext())
    //         System.out.println(iter.next());
    // }
}