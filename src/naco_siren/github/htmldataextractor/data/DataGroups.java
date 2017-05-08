package naco_siren.github.htmldataextractor.data;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by naco_siren on 3/29/17.
 */
public class DataGroups {
    /* Data: DataGroup list */
    private ArrayList<DataGroup> _data;
    public ArrayList<DataGroup> getDataGroupList() {
        return _data;
    }

    /* Data: DataGroup list size */
    public int size(){
        return _data.size();
    }

    /**
     * Constructor
     */
    public DataGroups(){
        _data = new ArrayList<>();
    }

    /**
     * Collections: getting, adding, removing, adding all
     */
    public DataGroup get(int index) {
        return _data.get(index);
    }
    public void add(DataGroup dataGroup){
        _data.add(dataGroup);
    }
    public DataGroup remove(int index){
        return _data.remove(index);
    }
    public void addAll(ArrayList<DataGroup> dataGroups) {
        _data.addAll(dataGroups);
    }
    public void addAll(DataGroups dataGroups) {
        _data.addAll(dataGroups._data);
    }

    /**
     * Collections: sort, reverse
     */
    public void sort(){
        Collections.sort(_data);
    }
    public void reverse(){
        Collections.reverse(_data);
    }


    /**
     * Statistics: mean, standard deviation
     */
    public double getAvgSig(){
        double sum = 0;
        for (DataGroup dataGroup : _data) {
            sum += dataGroup._significance;
        }
        return sum / size();
    }
    public double getStdevSig(){
        double sum = 0;
        double squareSum = 0;
        for (int i = 0; i < size(); i++) {
            double sig = _data.get(i)._significance;
            sum += sig;
            squareSum += sig * sig;
        }
        double mean = sum / size();
        double variance = squareSum / size() - mean * mean;
        return Math.sqrt(variance);
    }

    /**
     * Refine data by adopting a specified strategy
     * TODO: Define several strategies!
     */
    public double refine(){
        int beginSize = size();

        double avgSig = getAvgSig();
        double stdevSig = getStdevSig();

        ArrayList<DataGroup> dataGroupsToRemove = new ArrayList<>(size());
        for (DataGroup dataGroup : _data) {
            if (dataGroup._significance < avgSig + stdevSig) {
                dataGroupsToRemove.add(dataGroup);
            }
        }

        _data.removeAll(dataGroupsToRemove);
        return (double) size() / (double) beginSize;
    }


    public int clean() {
        int retval = 0;
        for (DataGroup dataGroup : _data) {
            retval += dataGroup.clean();
        }
        return retval;
    }


    @Override
    public String toString() {
        return "DataGroups of size " + size();
    }


}
