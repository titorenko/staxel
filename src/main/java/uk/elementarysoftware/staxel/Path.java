package uk.elementarysoftware.staxel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.xml.stream.events.StartElement;


class Path {
    
    private Stack<String> path = new Stack<>();

    Path() {
    }
    
    Path(String name) {
        path.add(name);
    }

    void push(StartElement se) {
        push(se.getName().getLocalPart());
    }
    
    void push(String name) {
        path.push(name);
    }

    String pop() {
        return path.pop();
    }

    Optional<String> popOpt() {
        return path.isEmpty() ? Optional.empty() : Optional.of(path.pop());
    }

    Collection<String> fullPath() {
        return new ArrayList<>(path);
    }
    
    String last() {
        return path.peek();
    }

    boolean endsWith(String... segments) {
        int size = path.size();
        if (size < segments.length) return false;
        for (int si = segments.length - 1, pi = size-1; si >= 0; si--, pi--) {
            if (!segments[si].equals(path.get(pi))) return false;
        }
        return true;
    }
    
    int size() {
        return path.size();
    }
    
    @Override
    public String toString() {
        return path.stream().collect(Collectors.joining("/"));
    }
}
