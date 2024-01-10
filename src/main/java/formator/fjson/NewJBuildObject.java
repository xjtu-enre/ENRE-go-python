package formator.fjson;

import entitybuilder.gobuilder.goentity.*;
import formator.MapObject;
import uerr.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// update2
public class NewJBuildObject {

    public NewJDepObject buildObjectProcess(MapObject mapObject) {
        // update2

//        ArrayList<String> entities = mapObject.getEntities();;
//        Map<Integer, Map<Integer, Map<String, Integer>>> newfinalRes = mapObject.getNewfinalRes();
//        Map<Integer, Map<Integer, Map<String, Location>>> newfinalLoc = mapObject.getNewfinalLoc();
//        NewJDepObject newJDepObject = new NewJDepObject();
        SingleCollect singleCollect = mapObject.getRelationInterface().getSingleCollect();
        NewJDepObject newJDepObject = new NewJDepObject();
        ArrayList<JEntityObject> entities = buildEntityObjects(singleCollect);
        ArrayList<JRelationObject> relations = buildRelationObjects(singleCollect);

        newJDepObject.setEntities(entities);
        newJDepObject.setRelations(relations);

        return newJDepObject;
        // end
    }

    private ArrayList<JEntityObject> buildEntityObjects(SingleCollect singleCollect) {
        ArrayList<JEntityObject> entities = new ArrayList<JEntityObject>();
        for(AbsEntity entity : singleCollect.getEntities()) {
            JEntityObject jEntityObject = new JEntityObject();
            jEntityObject.setId(entity.getId());
            jEntityObject.setName(entity.getSimpleName());
            jEntityObject.setParent(entity.getParentId());
            Map<String,JLocation> identifier = new HashMap<String, JLocation>();
            JLocation start = new JLocation();
            if (entity.getStart() == null) {
                start.setLine(-1);
                start.setColumn(-1);
            } else {
                start.setLine(entity.getStart().getLine());
                start.setColumn(entity.getStart().getCharPositionInLine());
            }
            JLocation end = new JLocation();
            if (entity.getEnd() == null) {
                end.setLine(-1);
                end.setColumn(-1);
            } else {
                end.setLine(entity.getEnd().getLine());
                end.setColumn(entity.getEnd().getCharPositionInLine());
            }
            identifier.put("start",start);
            identifier.put("end",end);
            jEntityObject.setIdentifier(identifier);
            if (entity instanceof AbsFILEntity) {
                // *
                jEntityObject.setCategory("FILE");
            } else if (entity instanceof InterfaceEntity) {
                // *
                jEntityObject.setCategory("Interface");
            } else if (entity instanceof StructEntity) {
                // *
                jEntityObject.setCategory("struct");
            } else if(entity instanceof AliasTypeEntity) {
                // *
                jEntityObject.setCategory("alias");
            } else if (entity instanceof AbsFLDEntity) {
                // *
                jEntityObject.setCategory("Package");
            } else if ((entity instanceof AbsFUNEntity) && !(entity instanceof MethodEntity)) {
                // *
                jEntityObject.setCategory("Func");
            } else if (entity instanceof MethodEntity) {
                // *
                jEntityObject.setCategory("Method");
            } else if (entity instanceof AbsVAREntity) {
                // *
                int parentId = entity.getParentId();
                if(parentId != -1) {
                    if(!(singleCollect.getEntities().get(parentId) instanceof StructEntity)) {
                        jEntityObject.setCategory("Var");
                    }
                } else {
                    jEntityObject.setCategory("EVar");
                }
            } else {
                jEntityObject.setCategory("Else");
            }
            entities.add(jEntityObject);
        }
        return entities;
    }

    private ArrayList<JRelationObject> buildRelationObjects(SingleCollect singleCollect) {
        ArrayList<JRelationObject> relations = new ArrayList<JRelationObject>();
        for(AbsEntity entity : singleCollect.getEntities()) {
            int size = entity.getRelations().size();
            if (size == 0) {
                continue;
            } else {
                for (int i=0;i<size;i++) {
                    JRelationObject jRelationObject = new JRelationObject();
                    jRelationObject.setFrom(entity.getId());
                    jRelationObject.setTo(entity.getRelations().get(i).y);
                    jRelationObject.setCategory(entity.getRelations().get(i).x);
                    JLocation jLocation = new JLocation();
                    if (entity.getStart()!=null){
                        jLocation.setLine(entity.getStart().getLine());
                        jLocation.setColumn(entity.getStart().getCharPositionInLine());
                    } else {
                        jLocation.setLine(-1);
                        jLocation.setColumn(-1);
                    }
                    jRelationObject.setLocation(jLocation);
                    relations.add(jRelationObject);
                }
            }

        }
        return relations;
    }
}
