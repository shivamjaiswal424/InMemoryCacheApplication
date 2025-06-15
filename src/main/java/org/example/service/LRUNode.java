package org.example.service;

public class LRUNode<K> {
    public K key;
    public LRUNode<K> prev, next;
    public LRUNode(K key) {
        this.key = key;
    }
}