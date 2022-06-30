package de.jadehs.mvl.data.parking.models.routing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import de.jadehs.mvl.data.parking.models.Coordinate;

public class ViaList implements List<Coordinate> {


    public static ViaList fromJSON(JSONArray viaArray) throws JSONException {
        List<Coordinate> newList = new LinkedList<>();
        for (int i = 0; i < viaArray.length(); i++) {
            newList.add(Coordinate.fromSimpleString(viaArray.getString(i)));
        }

        return new ViaList(newList);
    }

    public static ViaList fromString(String viaString) {
        List<Coordinate> newList = new LinkedList<>();
        String[] parts = viaString.split(";");
        for (String coordString : parts) {
            newList.add(Coordinate.fromSimpleString(coordString));
        }

        return new ViaList(newList);
    }


    @NonNull
    private final List<Coordinate> original;


    public ViaList(@NonNull List<Coordinate> original) {
        this.original = Collections.unmodifiableList(original);
    }

    @Override
    public int size() {
        return original.size();
    }

    @Override
    public boolean isEmpty() {
        return original.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return original.contains(o);
    }

    @NonNull
    @Override
    public Iterator<Coordinate> iterator() {
        return original.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return original.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return original.toArray(a);
    }

    @Override
    public boolean add(Coordinate coordinate) {
        return original.add(coordinate);
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return original.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return original.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Coordinate> c) {
        return original.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends Coordinate> c) {
        return original.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return original.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return original.retainAll(c);
    }

    @Override
    public void clear() {
        original.clear();
    }

    @Override
    public Coordinate get(int index) {
        return original.get(index);
    }

    @Override
    public Coordinate set(int index, Coordinate element) {
        return original.set(index, element);
    }

    @Override
    public void add(int index, Coordinate element) {
        original.add(index, element);
    }

    @Override
    public Coordinate remove(int index) {
        return original.remove(index);
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return original.indexOf(o);
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return original.lastIndexOf(o);
    }

    @NonNull
    @Override
    public ListIterator<Coordinate> listIterator() {
        return original.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<Coordinate> listIterator(int index) {
        return original.listIterator(index);
    }

    @NonNull
    @Override
    public List<Coordinate> subList(int fromIndex, int toIndex) {
        return original.subList(fromIndex, toIndex);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder viaString = new StringBuilder();

        Iterator<Coordinate> coordinateIterator = original.iterator();
        while (coordinateIterator.hasNext()) {
            viaString.append(coordinateIterator.next().toSimpleString());
            if (coordinateIterator.hasNext())
                viaString.append(";");
        }
        return viaString.toString();
    }
}
