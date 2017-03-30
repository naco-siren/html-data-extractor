package gokurakujoudo.data;

import org.jsoup.nodes.Node;

import java.util.ArrayList;

/**
 * Created by naco_siren on 3/29/17.
 */
public class DataGroup implements Comparable<DataGroup> {
    /* Data */
    public ArrayList<Node> _data;
    public int size(){
        return _data.size();
    }

    /*  */
    public double _significance;

    /**
     * Constructor
     */
    public DataGroup(){
        this._data = new ArrayList<>();
        this._significance = 0;
    }

    /**
     * Comparator
     */
    @Override
    public int compareTo(DataGroup other) {
        return (int) (this._significance - other._significance);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Data size: " + _data.size() + ", Significance: " + _significance);
        return stringBuilder.toString();
    }
}
