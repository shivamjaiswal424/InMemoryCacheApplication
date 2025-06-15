package org.example.service;

public class DoublyLinkedList<K> {
    private LRUNode<K> head, tail;

    public LRUNode<K> addToFront(K key) {
        LRUNode<K> node = new LRUNode<>(key);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        return node;
    }

    public void moveToFront(LRUNode<K> node) {
        if (node == head) return;
        remove(node);
        node.prev = node.next = null;
        node.next = head;
        head.prev = node;
        head = node;
    }

    public void remove(LRUNode<K> node) {
        if (node == head) head = node.next;
        if (node == tail) tail = node.prev;
        if (node.prev != null) node.prev.next = node.next;
        if (node.next != null) node.next.prev = node.prev;
    }

    public LRUNode<K> removeTail() {
        if (tail == null) return null;
        LRUNode<K> removed = tail;
        remove(tail);
        return removed;
    }
}