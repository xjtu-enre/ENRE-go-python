package uerr;

import entitybuilder.gobuilder.goentity.Location;
import sun.security.krb5.Config;
import sun.security.krb5.Confounder;
import util.Configure;
import util.Tuple;

import java.util.ArrayList;

public abstract class RelationInterface {

    protected SingleCollect singleCollect = SingleCollect.getSingleCollectInstance();

    public abstract String entityStatis();

    public abstract String dependencyStatis();

    // update2

    public SingleCollect getSingleCollect() {
        return singleCollect;
    }

    public void setSingleCollect(SingleCollect singleCollect) {
        this.singleCollect = singleCollect;
    }

    // end

    public ArrayList<Tuple<String, String>> getDepByType(String level, String depType) {
        if(depType.equals(Configure.RELATION_IMPLEMENT)) {
            return getImplementDeps(level);
        }
        if(depType.equals(Configure.RELATION_INHERIT)) {
            //ArrayList<Tuple<String, String>> deps;
            //deps =  getEmbedInterfaceDep(level);
            //deps.addAll(getEmbedStructDep(level));
//            //return deps;
            return getInheritDeps(level);
//            return getInheritDepsLocations(level);
        }
        if(depType.equals(Configure.RELATION_SET)) {
            return getFunctionSets(level);
        }
        if(depType.equals(Configure.RELATION_USE)) {
            return getFunctionUses(level);
        }
        if(depType.equals(Configure.RELATION_PARAMETER)) {
            return getFunctionParas(level);
        }
        if(depType.equals(Configure.RELATION_RETURN)) {
            return getFunctionRets(level);
        }
        //if(depType.equals(Configure.RELATION_RECEIVE)) {
        //    return getMethodReceiveDeps(level);
        //}
        if(depType.equals(Configure.RELATION_CALL)) {
            return getFunctionCalls(level);
        }
        if(depType.equals(Configure.RELATION_IMPORT)) {
            return getImportDeps(level);
        }
        if(depType.equals(Configure.RELATION_IMPLICIT_EXTERNAL_CALL)) {
            return getImplicitExternalCalls(level);
        }
        return null;

    }


    // update
    public ArrayList<Location> getLocationByType(String level, String depType) {
        if(depType.equals(Configure.RELATION_IMPLEMENT)) {
//            return getImplementDeps(level);
            return getImplementDepsLocation(level);
        }
        if(depType.equals(Configure.RELATION_INHERIT)) {
            //ArrayList<Tuple<String, String>> deps;
            //deps =  getEmbedInterfaceDep(level);
            //deps.addAll(getEmbedStructDep(level));
            //return deps;
//            return getInheritDeps(level);
            return getInheritDepsLocation(level);
        }
        if(depType.equals(Configure.RELATION_SET)) {
//            return getFunctionSets(level);
            return null;
        }
        if(depType.equals(Configure.RELATION_USE)) {
            // return getFunctionUses(level);
            return getFunctionUsesLocation(level);
        }
        if(depType.equals(Configure.RELATION_PARAMETER)) {
//            return getFunctionParas(level);
            return getParmeterDepsLocation(level);
        }
        if(depType.equals(Configure.RELATION_RETURN)) {
            // return getFunctionRets(level);
            return getFunctionRetsLocation(level);
        }
        //if(depType.equals(Configure.RELATION_RECEIVE)) {
        //    return getMethodReceiveDeps(level);
        //}
        if(depType.equals(Configure.RELATION_CALL)) {
            return getFunctionCallsLocation(level);
        }
        if(depType.equals(Configure.RELATION_IMPORT)) {
//            return getImportDeps(level);
            return getImportDepsLocation(level);
        }
        if(depType.equals(Configure.RELATION_IMPLICIT_EXTERNAL_CALL)) {
//            return getImplicitExternalCalls(level);
            return null;
        }
        return null;

    }
    // end

    public ArrayList<String> getAllFiles() {
        ArrayList<String> files = new ArrayList<String>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            if(entity instanceof AbsFILEntity) {
                String fileName = entity.getName();
                files.add(fileName);
            }
        }
        return files;
    }

    // update2

    public ArrayList<String> getAllEntities() {
        ArrayList<String> entities = new ArrayList<String>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            String fileName = entity.getName();
            entities.add(fileName);
        }
        return entities;
    }

    // end

    // update
    public abstract ArrayList<Location> getImportDepsLocation(String level);
    public abstract ArrayList<Location> getImplementDepsLocation(String level);
    public abstract ArrayList<Location> getInheritDepsLocation(String level);
    public abstract ArrayList<Location> getFunctionCallsLocation(String level);
    public abstract ArrayList<Location> getParmeterDepsLocation(String level);
    public abstract ArrayList<Location> getFunctionRetsLocation(String level);
    public abstract ArrayList<Location> getFunctionUsesLocation(String level);
    // end

    public abstract ArrayList<Tuple<String, String>> getImportDeps(String level);
    public abstract ArrayList<Tuple<String, String>> getImplementDeps(String level);
    public abstract ArrayList<Tuple<String, String>> getInheritDeps(String level);
    public abstract ArrayList<Tuple<String, String>> getFunctionCalls(String level);
    public abstract ArrayList<Tuple<String, String>> getFunctionSets(String level);
    public abstract ArrayList<Tuple<String, String>> getFunctionUses(String level);
    public abstract ArrayList<Tuple<String, String>> getFunctionParas(String level);
    public abstract ArrayList<Tuple<String, String>> getFunctionRets(String level);
    public abstract ArrayList<Tuple<String, String>> getImplicitExternalCalls(String level);
}
