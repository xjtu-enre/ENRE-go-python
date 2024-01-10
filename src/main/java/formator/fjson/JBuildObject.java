package formator.fjson;


import entitybuilder.gobuilder.goentity.Location;
import formator.MapObject;
import util.Configure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JBuildObject {


    public JDepObject buildObjectProcess(MapObject mapObject) {
        Configure configure = Configure.getConfigureInstance();
        ArrayList<String> files = mapObject.getFiles();
        Map<Integer, Map<Integer, Map<String, Integer>>> finalRes = mapObject.getFinalRes();
        // update

        Map<Integer,Map<Integer,Map<String, Location>>> finalLoc = mapObject.getFinalLoc();

        ArrayList<JCellObject> cellObjects = buildCellObjects(finalRes,finalLoc); //transform finalRes into cellObjects

        // end


        JDepObject depObject = new JDepObject();
        depObject.setVariables(files);
        depObject.setName(configure.getAttributeName());
        depObject.setSchemaVersion(configure.getSchemaVersion());
        depObject.setCells(cellObjects);

        return depObject;
    }

    /**
     *
     * @return
     */
    private ArrayList<JCellObject> buildCellObjects(Map<Integer, Map<Integer, Map<String, Integer>>> finalRes,Map<Integer,Map<Integer,Map<String, Location>>> finalLoc) {
        ArrayList<JCellObject> cellObjects = new ArrayList<JCellObject>();

        for (Map.Entry<Integer, Map<Integer, Map<String, Integer>>> entry1 : finalRes.entrySet()) {
            int src = entry1.getKey();

            Map<Integer, Map<String, Integer>> values1 = entry1.getValue();
            for (Map.Entry<Integer, Map<String, Integer>> entry2: values1.entrySet()) {
                int dst = entry2.getKey();

                // update
                Map<String,Location> check_location = null;
                for (Map.Entry<Integer,Map<Integer,Map<String, Location>>> entry3: finalLoc.entrySet()){
                    int check_src = entry3.getKey();
                    Map<Integer,Map<String, Location>> check_value = entry3.getValue();
                    if ( src == check_src) {
                        for (Map.Entry<Integer,Map<String,Location>> entry4: check_value.entrySet()) {
                            int check_dst = entry4.getKey();
                            if (dst == check_dst) {
                                check_location = entry4.getValue();
                            }
                        }
                    }
                }
                Map<String,LocationObject> location = null;
                if (check_location != null) {
                    location = buildLocationObject(check_location);
                }

                // end


                Map<String, Integer> values2 = entry2.getValue();
                Map<String, Float> valueObject = buildValueObject(values2);
                JCellObject cellObject = new JCellObject();
                cellObject.setSrc(src);
                cellObject.setDest(dst);
                cellObject.setValues(valueObject);

                // update

                if (location != null) {
                    cellObject.setLocation(location);
                } else {
                    cellObject.setLocation(null);
                }

                // end
                cellObjects.add(cellObject);
            }
        }
        return cellObjects;
    }

    private Map<String, LocationObject> buildLocationObject(Map<String, Location> check_location) {
        Map<String, LocationObject> valueObject = new HashMap<String, LocationObject>();
        for (Map.Entry<String, Location> entry : check_location.entrySet()) {
            String depType = entry.getKey();
//            float weight = (float) entry3.getValue();
//            valueObject.put(depType, weight);
            int start = entry.getValue().getStart().getLine();
            int end = entry.getValue().getEnd().getLine();
            valueObject.put(depType,new LocationObject(start,end));
        }
        return valueObject;
    }


    /**
     *
     * @param values2
     * @return
     */
    private Map<String, Float> buildValueObject(Map<String, Integer> values2) {
        Map<String, Float> valueObject = new HashMap<String, Float>();
        for (Map.Entry<String, Integer> entry3 : values2.entrySet()) {
            String depType = entry3.getKey();
            float weight = (float) entry3.getValue();
            valueObject.put(depType, weight);
        } //end for
        return valueObject;
    }






}
