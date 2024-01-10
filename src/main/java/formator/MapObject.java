package formator;

import entitybuilder.gobuilder.goentity.Location;
import org.antlr.v4.runtime.Token;
import uerr.RelationInterface;
import priextractor.goextractor.GoRelationInf;
import priextractor.py3extractor.PyRelationInf;
import util.Configure;
import util.Tuple;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;

public class MapObject {
    private RelationInterface relationInterface;
    private ArrayList<String> files;

    // update2

    private ArrayList<String> entities;
    private Map<Integer, Map<Integer, Map<String, Integer>>> newfinalRes = new HashMap<Integer, Map<Integer, Map<String, Integer>>>();
    private Map<Integer, Map<Integer, Map<String,Location>>> newfinalLoc = new HashMap<Integer, Map<Integer, Map<String, Location>>>();

    // end
    private Map<Integer, Map<Integer, Map<String, Integer>>> finalRes = new HashMap<Integer, Map<Integer, Map<String, Integer>>>();
    private String[] depStrs;


    // update2


    public RelationInterface getRelationInterface() {
        return relationInterface;
    }

    public void setRelationInterface(RelationInterface relationInterface) {
        this.relationInterface = relationInterface;
    }

    public ArrayList<String> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<String> entities) {
        this.entities = entities;
    }

    public Map<Integer, Map<Integer, Map<String, Integer>>> getNewfinalRes() {
        return newfinalRes;
    }

    public void setNewfinalRes(Map<Integer, Map<Integer, Map<String, Integer>>> newfinalRes) {
        this.newfinalRes = newfinalRes;
    }

    public Map<Integer, Map<Integer, Map<String, Location>>> getNewfinalLoc() {
        return newfinalLoc;
    }

    public void setNewfinalLoc(Map<Integer, Map<Integer, Map<String, Location>>> newfinalLoc) {
        this.newfinalLoc = newfinalLoc;
    }

    // end

    // update
    private Map<Integer, Map<Integer, Map<String,Location>>> finalLoc = new HashMap<Integer, Map<Integer, Map<String, Location>>>();

    public Map<Integer, Map<Integer, Map<String, Location>>> getFinalLoc() {
        return finalLoc;
    }

    public void setFinalLoc(Map<Integer, Map<Integer, Map<String, Location>>> finalLoc) {
        this.finalLoc = finalLoc;
    }

    // end

    public MapObject(String[] depStrs) {
        this.depStrs = depStrs;
        init();

        //summarize the entity and dependency
        //System.out.println(relationInterface.entityStatis());
        //System.out.println(relationInterface.dependencyStatis());
        //System.out.println(depSummaryWithNoWeight());
    }

    /**
     * based on Map<Integer, Map<Integer, Map<String, Integer>>> finalRes
     */
    private String depSummaryWithNoWeight() {
        Map<String, Integer> res = new HashMap<String, Integer>();
        for(Map.Entry<Integer, Map<Integer, Map<String, Integer>>> entry1 : finalRes.entrySet()) {
            for(Map.Entry<Integer, Map<String, Integer>> entry2: entry1.getValue().entrySet()) {
                for (Map.Entry<String, Integer> entry3: entry2.getValue().entrySet()) {
                    String dep = entry3.getKey();
                    if(!res.containsKey(dep)) {
                        res.put(dep, 1);
                    }
                    res.put(dep, 1 + res.get(dep));
                }
            }
        }
        String str = "";
        for(Map.Entry<String, Integer> entry : res.entrySet()) {
            str += entry.getKey();
            str += ":           ";
            str += Integer.toString(entry.getValue());
            str += "\n";
        }
        return str;
    }

    private void init() {
        Configure configure = Configure.getConfigureInstance();
        if(configure.getLang().equals(Configure.GO_LANG)) {
            relationInterface = new GoRelationInf();
        }
        else if(configure.getLang().equals(Configure.PYTHON_LANG)) {
            relationInterface = new PyRelationInf();
        }
        else {
            System.out.println("Not support this language!\n");
            exit(0);
        }
        files =  relationInterface.getAllFiles();

        // update2

        entities = relationInterface.getAllEntities();
        buildNewDepMap(entities);

        // end

        buildDepMap(files);
    }


    public ArrayList<String> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<String> files) {
        this.files = files;
    }


    public String[] getDepStrs() {
        return depStrs;
    }

    public void setDepStrs(String[] depStrs) {
        this.depStrs = depStrs;
    }

    public Map<Integer, Map<Integer, Map<String, Integer>>> getFinalRes() {
        return finalRes;
    }

    public void setFinalRes(Map<Integer, Map<Integer, Map<String, Integer>>> finalRes) {
        this.finalRes = finalRes;
    }

    /**
     * build map from fileName to new id
     * store into fileName2Id.
     * @param files
     */
    private Map<String, Integer> buildFileMap(ArrayList<String> files) {
        Map<String, Integer> fileName2Id = new HashMap<String, Integer>();
        int index = 0;
        for (String fileName : files) {
            fileName2Id.put(fileName, index);
            index ++;
        }
        return fileName2Id;
    }

    /**
     * build fileDeps into a map.
     * @param files
     */
    // update
    private void buildDepMap(ArrayList<String> files) {

        Map<String, Integer> fileName2Id =  buildFileMap(files);
        for (int i = 0; i < depStrs.length; i++) {
            String depType = depStrs[i];
            //System.out.println(depType);
            ArrayList<Tuple<String, String>> deps = relationInterface.getDepByType(Configure.RELATION_LEVEL_FILE, depType);

            // update
            ArrayList<Location> location = relationInterface.getLocationByType(Configure.RELATION_LEVEL_FILE, depType);

            if (deps != null){
                addDepsInMap(deps, depType, fileName2Id,location);
                //System.out.println("dep not null: " + depType);
            }
            // end
            //else {
            //    System.out.println("dep is null: " + depType);
            //}
        }

    }

    // update2

    private void buildNewDepMap(ArrayList<String> entities) {

        Map<String, Integer> fileName2Id =  buildFileMap(entities);
        for (int i = 0; i < depStrs.length; i++) {
            String depType = depStrs[i];

            ArrayList<Tuple<String, String>> deps = relationInterface.getDepByType(Configure.RELATION_LEVEL_FILE, depType);

            ArrayList<Location> location = relationInterface.getLocationByType(Configure.RELATION_LEVEL_FILE, depType);

            if (deps != null){
                newaddDepsInMap(deps, depType, fileName2Id,location);
            }
            // end
        }

    }

    /**
     *
     * @param deps
     * @param depType
     * @param fileName2Id
     */
    // update

    private void addDepsInMap(ArrayList<Tuple<String, String>> deps, String depType, Map<String, Integer> fileName2Id,ArrayList<Location> location) {
        for(Tuple<String, String> dep : deps) {
            String name1 = dep.x;
            String name2 = dep.y;
            int index1 = -1;
            int index2 = -1;
            if(fileName2Id.containsKey(name1)) {
                index1 = fileName2Id.get(name1);
            }
            if (fileName2Id.containsKey(name2)) {
                index2 = fileName2Id.get(name2);
            }

            if(name1.equals(name2) || index1 == -1 || index2 == -1) {
                continue;
            }
            if(!finalRes.containsKey(index1)) {
                finalRes.put(index1, new HashMap<Integer, Map<String, Integer>>());
            }
            if(!finalRes.get(index1).containsKey(index2)) {
                finalRes.get(index1).put(index2, new HashMap<String, Integer>());
            }
            if(!finalRes.get(index1).get(index2).containsKey(depType)) {
                finalRes.get(index1).get(index2).put(depType, 0);
            }


            int newWeight = finalRes.get(index1).get(index2).get(depType) + 1;
            finalRes.get(index1).get(index2).put(depType, newWeight);

        }
        // update
        if (location == null) {

        } else {
            for(Location loc:location){
                String name1 = loc.getSrc_dst().x;
                String name2 = loc.getSrc_dst().y;

                int index1 = -1;
                int index2 = -1;

                if(fileName2Id.containsKey(name1)) {
                    index1 = fileName2Id.get(name1);
                }
                if (fileName2Id.containsKey(name2)) {
                    index2 = fileName2Id.get(name2);
                }

                if(name1.equals(name2) || index1 == -1 || index2 == -1) {
                    continue;
                }

                if(!finalLoc.containsKey(index1)) {
                    finalLoc.put(index1, new HashMap<Integer, Map<String, Location>>());
                }
                if(!finalLoc.get(index1).containsKey(index2)) {
                    finalLoc.get(index1).put(index2, new HashMap<String, Location>());
                }
                if(!finalLoc.get(index1).get(index2).containsKey(depType)) {
                    finalLoc.get(index1).get(index2).put(depType, null);
                }

                finalLoc.get(index1).get(index2).put(depType, loc);
            }
        }
        // end
    }

    // update2

    private void newaddDepsInMap(ArrayList<Tuple<String, String>> deps, String depType, Map<String, Integer> fileName2Id,ArrayList<Location> location) {
        for(Tuple<String, String> dep : deps) {
            String name1 = dep.x;
            String name2 = dep.y;
            int index1 = -1;
            int index2 = -1;
            if(fileName2Id.containsKey(name1)) {
                index1 = fileName2Id.get(name1);
            }
            if (fileName2Id.containsKey(name2)) {
                index2 = fileName2Id.get(name2);
            }

            if(name1.equals(name2) || index1 == -1 || index2 == -1) {
                continue;
            }
            if(!newfinalRes.containsKey(index1)) {
                newfinalRes.put(index1, new HashMap<Integer, Map<String, Integer>>());
            }
            if(!newfinalRes.get(index1).containsKey(index2)) {
                newfinalRes.get(index1).put(index2, new HashMap<String, Integer>());
            }
            if(!newfinalRes.get(index1).get(index2).containsKey(depType)) {
                newfinalRes.get(index1).get(index2).put(depType, 0);
            }


            int newWeight = newfinalRes.get(index1).get(index2).get(depType) + 1;
            newfinalRes.get(index1).get(index2).put(depType, newWeight);

        }
        // update
        if (location == null) {

        } else {
            for(Location loc:location){
                String name1 = loc.getSrc_dst().x;
                String name2 = loc.getSrc_dst().y;

                int index1 = -1;
                int index2 = -1;

                if(fileName2Id.containsKey(name1)) {
                    index1 = fileName2Id.get(name1);
                }
                if (fileName2Id.containsKey(name2)) {
                    index2 = fileName2Id.get(name2);
                }

                if(name1.equals(name2) || index1 == -1 || index2 == -1) {
                    continue;
                }

                if(!newfinalLoc.containsKey(index1)) {
                    newfinalLoc.put(index1, new HashMap<Integer, Map<String, Location>>());
                }
                if(!newfinalLoc.get(index1).containsKey(index2)) {
                    newfinalLoc.get(index1).put(index2, new HashMap<String, Location>());
                }
                if(!newfinalLoc.get(index1).get(index2).containsKey(depType)) {
                    newfinalLoc.get(index1).get(index2).put(depType, null);
                }

                newfinalLoc.get(index1).get(index2).put(depType, loc);
            }
        }
        // end
    }
}
