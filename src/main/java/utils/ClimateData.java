package utils;

import java.util.ArrayList;
import java.util.List;

public class ClimateData {

    private List<ClimateRecord> records;

    public ClimateData(){
        records = new ArrayList<>();
    }

    public ClimateData addRecord(ClimateRecord cr){
        records.add(cr);
        return this;
    }

    public int size(){
        return records.size();
    }

    public ClimateRecord getRecord(int i){
        return records.get(i);
    }

}


