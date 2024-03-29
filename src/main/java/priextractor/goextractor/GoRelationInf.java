package priextractor.goextractor;

import entitybuilder.gobuilder.goentity.*;
import org.antlr.v4.runtime.Token;
import uerr.*;
import uerr.RelationInterface;

import util.Configure;
import util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoRelationInf extends RelationInterface {
    @Override
    public String entityStatis() {
        int fileCount = 0;
        int packageCount = 0;
        int functionCount = 0;
        int methodCount = 0;
        int interfaceCount = 0;
        int structCount = 0;
        int aliasCount = 0;
        int varCount = 0;

        for(AbsEntity entity : singleCollect.getEntities()) {
            if(entity instanceof AbsFLDEntity) {
                packageCount ++;
            }
            else if(entity instanceof AbsFILEntity) {
                fileCount ++;
            }
            else if(entity instanceof AbsFUNEntity
                    && !(entity instanceof MethodEntity)) {
                functionCount ++;
            }
            else if (entity instanceof MethodEntity) {
                methodCount ++;
            }
            else if(entity instanceof StructEntity) {
                structCount ++;
            }
            else if(entity instanceof AliasTypeEntity) {
                aliasCount ++;
            }
            else if(entity instanceof InterfaceEntity) {
                interfaceCount ++;
            }
            else if(entity instanceof AbsVAREntity) {
                int parentId = entity.getParentId();
                if(parentId != -1) {
                    if(!(singleCollect.getEntities().get(parentId) instanceof StructEntity)) {
                        varCount ++;
                    }
                }

            }
        }

        String str = "";
        str += ("Package:     " + Integer.toString(packageCount) + "\n");
        str += ("File:        " + Integer.toString(fileCount) + "\n");
        str += ("Struct:      " + Integer.toString(structCount) + "\n");
        str += ("Alias:       " + Integer.toString(aliasCount) + "\n");
        str += ("Interface:   " + Integer.toString(interfaceCount) + "\n");
        str += ("Function:    " + Integer.toString(functionCount) + "\n");
        str += ("Method:      " + Integer.toString(methodCount) + "\n");
        str += ("Variable:    " + Integer.toString(varCount) + "\n");
        return str;
    }

    @Override
    public String dependencyStatis() {
        Map<String, Integer> depMap = new HashMap<String, Integer>();
        depMap.put(Configure.RELATION_IMPORT, 0);
        depMap.put(Configure.RELATION_IMPLEMENT, 0);
        depMap.put(Configure.RELATION_INHERIT, 0);
        depMap.put(Configure.RELATION_CALL, 0);
        depMap.put(Configure.RELATION_SET, 0);
        depMap.put(Configure.RELATION_USE, 0);
        depMap.put(Configure.RELATION_PARAMETER, 0);
        depMap.put(Configure.RELATION_RETURN, 0);

        for (AbsEntity entity :singleCollect.getEntities()) {
            for (Tuple<String, Integer> re : entity.getRelations()) {
                if(re.x.equals(Configure.RELATION_IMPORT) ||
                        re.x.equals(Configure.RELATION_INHERIT) ||
                        re.x.equals(Configure.RELATION_IMPLEMENT) ||
                        re.x.equals(Configure.RELATION_SET) ||
                        re.x.equals(Configure.RELATION_USE) ||
                        re.x.equals(Configure.RELATION_CALL) ||
                        re.x.equals(Configure.RELATION_PARAMETER) ||
                        re.x.equals(Configure.RELATION_RETURN)
                ) {
                    int old = depMap.get(re.x);
                    depMap.put(re.x, old + 1);
                }
            }
        }
        String str = Configure.NULL_STRING;
        for(Map.Entry<String, Integer> entry : depMap.entrySet()) {
            str += entry.getKey();
            str += ":    ";
            str += Integer.toString(entry.getValue());
            str += "\n";
        }
        return str;
    }

    @Override
    public ArrayList<Tuple<String, String>> getFunctionCalls(String level) {
        ArrayList<Tuple<String, String>> deps = new ArrayList<Tuple<String, String>>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            if (entity instanceof AbsFUNEntity) {
                String methodName1 =entity.getName();
                String fileName1 = singleCollect.getEntities().get(entity.getParentId()).getName();

                for (Tuple<String, Integer> relation : entity.getRelations()) {
                    String relationType = relation.x;
                    int entityId2 = relation.y;
                    if(relationType.equals(Configure.RELATION_CALL)) {
                        AbsEntity entity2 = singleCollect.getEntities().get(entityId2);
                        String methodName2 = entity2.getName();
                        String fileName2 = singleCollect.getEntities().get(entity2.getParentId()).getName();
                        Tuple<String, String> oneCall;
                        if(level.equals(Configure.RELATION_LEVEL_FILE)) {
                            oneCall = new Tuple<String, String>(fileName1, fileName2);
                        }
                        else {
                            oneCall = new Tuple<String, String>(methodName1, methodName2);
                        }
                        deps.add(oneCall);
                    }
                }
            }
        } // end big for
        return deps;
    }

    // update
    @Override
    public  ArrayList<Location> getFunctionCallsLocation(String level) {
        ArrayList<Location> locations = new ArrayList<Location>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            if (entity instanceof AbsFUNEntity) {
                String methodName1 = entity.getName();
                String fileName1 = singleCollect.getEntities().get(entity.getParentId()).getName();

                for (Tuple<String, Integer> relation : entity.getRelations()) {
                    String relationType = relation.x;
                    int entityId2 = relation.y;
                    if(relationType.equals(Configure.RELATION_CALL)) {
                        AbsEntity entity2 = singleCollect.getEntities().get(entityId2);
                        String methodName2 = entity2.getName();
                        String fileName2 = singleCollect.getEntities().get(entity2.getParentId()).getName();
                        Tuple<String, String> oneCall;
                        Token start;
                        Token end;
                        if(level.equals(Configure.RELATION_LEVEL_FILE)) {
                            oneCall = new Tuple<String, String>(fileName1, fileName2);
                            start = singleCollect.getEntities().get(entity.getParentId()).getStart();
                            end = singleCollect.getEntities().get(entity.getParentId()).getEnd();
                        }
                        else {
                            oneCall = new Tuple<String, String>(methodName1, methodName2);
                            start = entity.getStart();
                            end = entity.getEnd();
                        }
                        locations.add(new Location(oneCall,start,end));
                    }
                }
            }
        } // end big for
        return locations;
    }


    @Override
    public ArrayList<Tuple<String, String>> getFunctionParas(String level) {
        ArrayList<Tuple<String, String>> deps = new ArrayList<Tuple<String, String>>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            if(entity instanceof AbsFUNEntity) {
                int functionId = entity.getId();
                String functionName = entity.getName();
                int fileId1 = singleCollect.getEntities().get(functionId).getParentId();
                String fileName1 = singleCollect.getEntities().get(fileId1).getName();

                for(Tuple<String, Integer> relation : entity.getRelations()) {
                    if(relation.x.equals(Configure.RELATION_PARAMETER)) {
                        int varTypeId2 = relation.y;
                        String varTypeName2 = singleCollect.getEntities().get(varTypeId2).getName();
                        if(varTypeId2 != -1) {
                            int fileId2 = singleCollect.getEntities().get(varTypeId2).getParentId();
                            String fileName2 = singleCollect.getEntities().get(fileId2).getName();

                            Tuple<String, String> oneSet;
                            if(level.equals(Configure.RELATION_LEVEL_FILE)) {
                                oneSet = new Tuple<String, String>(fileName1, fileName2);
                            }
                            else {
                                oneSet = new Tuple<String, String>(functionName, varTypeName2);
                            }
                            deps.add(oneSet);
                        }
                    }
                }
            }
        }
        return deps;
    }

    @Override
    public ArrayList<Location> getParmeterDepsLocation(String level) {
        ArrayList<Location> locations = new ArrayList<Location>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            if(entity instanceof AbsFUNEntity) {
                int functionId = entity.getId();
                String functionName = entity.getName();
                int fileId1 = singleCollect.getEntities().get(functionId).getParentId();
                String fileName1 = singleCollect.getEntities().get(fileId1).getName();

                for(Tuple<String, Integer> relation : entity.getRelations()) {
                    if(relation.x.equals(Configure.RELATION_PARAMETER)) {
                        int varTypeId2 = relation.y;
                        String varTypeName2 = singleCollect.getEntities().get(varTypeId2).getName();
                        if(varTypeId2 != -1) {
                            int fileId2 = singleCollect.getEntities().get(varTypeId2).getParentId();
                            String fileName2 = singleCollect.getEntities().get(fileId2).getName();

                            Tuple<String, String> oneSet;
                            Token start;
                            Token end;
                            if(level.equals(Configure.RELATION_LEVEL_FILE)) {
                                oneSet = new Tuple<String, String>(fileName1, fileName2);
                                start = singleCollect.getEntities().get(fileId1).getStart();
                                end = singleCollect.getEntities().get(fileId1).getEnd();
                            }
                            else {
                                oneSet = new Tuple<String, String>(functionName, varTypeName2);
                                start = entity.getStart();
                                end = entity.getEnd();
                            }
                            locations.add(new Location(oneSet,start,end));
                        }
                    }
                }
            }
        }
        return locations;
    }


    @Override
    public ArrayList<Tuple<String, String>> getFunctionRets(String level) {
        ArrayList<Tuple<String, String>> deps = new ArrayList<Tuple<String, String>>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            if(entity instanceof AbsFUNEntity) {
                int functionId = entity.getId();
                String functionName = entity.getName();
                int fileId1 = singleCollect.getEntities().get(functionId).getParentId();
                String fileName1 = singleCollect.getEntities().get(fileId1).getName();

                for(Tuple<String, Integer> relation : entity.getRelations()) {
                    if(relation.x.equals(Configure.RELATION_RETURN)) {
                        int varTypeId2 = relation.y;
                        String varTypeName2 = singleCollect.getEntities().get(varTypeId2).getName();
                        if(varTypeId2 != -1) {
                            int fileId2 = singleCollect.getEntities().get(varTypeId2).getParentId();
                            String fileName2 = singleCollect.getEntities().get(fileId2).getName();

                            Tuple<String, String> oneSet;
                            if(level.equals(Configure.RELATION_LEVEL_FILE)) {
                                oneSet = new Tuple<String, String>(fileName1, fileName2);
                            }
                            else {
                                oneSet = new Tuple<String, String>(functionName, varTypeName2);
                            }
                            deps.add(oneSet);
                        }
                    }
                }
            }
        }
        return deps;
    }

    // update
    @Override
    public  ArrayList<Location> getFunctionRetsLocation(String level) {
        ArrayList<Location> locations = new ArrayList<Location>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            if(entity instanceof AbsFUNEntity) {
                int functionId = entity.getId();
                String functionName = entity.getName();
                int fileId1 = singleCollect.getEntities().get(functionId).getParentId();
                String fileName1 = singleCollect.getEntities().get(fileId1).getName();

                for(Tuple<String, Integer> relation : entity.getRelations()) {
                    if(relation.x.equals(Configure.RELATION_RETURN)) {
                        int varTypeId2 = relation.y;
                        String varTypeName2 = singleCollect.getEntities().get(varTypeId2).getName();
                        if(varTypeId2 != -1) {
                            int fileId2 = singleCollect.getEntities().get(varTypeId2).getParentId();
                            String fileName2 = singleCollect.getEntities().get(fileId2).getName();

                            Tuple<String, String> oneSet;
                            Token start;
                            Token end;
                            if(level.equals(Configure.RELATION_LEVEL_FILE)) {
                                oneSet = new Tuple<String, String>(fileName1, fileName2);
                                start = singleCollect.getEntities().get(fileId1).getStart();
                                end = singleCollect.getEntities().get(fileId1).getEnd();
                            }
                            else {
                                oneSet = new Tuple<String, String>(functionName, varTypeName2);
                                start = entity.getStart();
                                end = entity.getEnd();
                            }
                            locations.add(new Location(oneSet,start,end));
                        }
                    }
                }
            }
        }
        return locations;
    }
    // end

    @Override
    public ArrayList<Tuple<String, String>> getFunctionSets(String level) {
        ArrayList<Tuple<String, String>> deps = new ArrayList<Tuple<String, String>>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            if (entity instanceof AbsFUNEntity) {
                String methodName1 =entity.getName();
                String fileName1 = singleCollect.getEntities().get(entity.getParentId()).getName();

                for (Tuple<String, Integer> relation : entity.getRelations()) {
                    String relationType = relation.x;
                    int entityId2 = relation.y;
                    if(relationType.equals(Configure.RELATION_SET)) {
                        AbsEntity entity2 = singleCollect.getEntities().get(entityId2);
                        String varName2 = entity2.getName();
                        int fileId2 = getFileForVar(entityId2);
                        String fileName2 = "";
                        if(fileId2 != -1){
                            fileName2 = singleCollect.getEntities().get(fileId2).getName();
                        }
                        Tuple<String, String> oneSet;
                        if(level.equals(Configure.RELATION_LEVEL_FILE)) {
                            oneSet = new Tuple<String, String>(fileName1, fileName2);
                        }
                        else {
                            oneSet = new Tuple<String, String>(methodName1, varName2);
                        }
                        deps.add(oneSet);
                    }
                }
            }
        } // end big for
        return deps;
    }

    @Override
    public ArrayList<Tuple<String, String>> getFunctionUses(String level) {
        ArrayList<Tuple<String, String>> deps = new ArrayList<Tuple<String, String>>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            if (entity instanceof AbsFUNEntity) {
                String methodName1 =entity.getName();
                String fileName1 = singleCollect.getEntities().get(entity.getParentId()).getName();

                for (Tuple<String, Integer> relation : entity.getRelations()) {
                    String relationType = relation.x;
                    int entityId2 = relation.y;
                    if(relationType.equals(Configure.RELATION_USE)) {
                        AbsEntity entity2 = singleCollect.getEntities().get(entityId2);
                        String varName2 = entity2.getName();
                        int fileId2 = getFileForVar(entityId2);
                        String fileName2 = "";
                        if(fileId2 != -1){
                            fileName2 = singleCollect.getEntities().get(fileId2).getName();
                        }

                        Tuple<String, String> oneSet;
                        if(level.equals(Configure.RELATION_LEVEL_FILE)) {
                            oneSet = new Tuple<String, String>(fileName1, fileName2);
                        }
                        else {
                            oneSet = new Tuple<String, String>(methodName1, varName2);
                        }
                        deps.add(oneSet);
                    }
                }
            }
        } // end big for
        return deps;
    }

    // update
    @Override
    public ArrayList<Location> getFunctionUsesLocation(String level){
        ArrayList<Location> locations = new ArrayList<Location>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            if (entity instanceof AbsFUNEntity) {
                String methodName1 = entity.getName();
                String fileName1 = singleCollect.getEntities().get(entity.getParentId()).getName();

                for (Tuple<String, Integer> relation : entity.getRelations()) {
                    String relationType = relation.x;
                    int entityId2 = relation.y;
                    if(relationType.equals(Configure.RELATION_USE)) {
                        AbsEntity entity2 = singleCollect.getEntities().get(entityId2);
                        String varName2 = entity2.getName();
                        int fileId2 = getFileForVar(entityId2);
                        String fileName2 = "";
                        if(fileId2 != -1){
                            fileName2 = singleCollect.getEntities().get(fileId2).getName();
                        }

                        Tuple<String, String> oneSet;
                        Token start;
                        Token end;
                        if(level.equals(Configure.RELATION_LEVEL_FILE)) {
                            oneSet = new Tuple<String, String>(fileName1, fileName2);
                            start = singleCollect.getEntities().get(entity.getParentId()).getStart();
                            end = singleCollect.getEntities().get(entity.getParentId()).getEnd();
                        }
                        else {
                            oneSet = new Tuple<String, String>(methodName1, varName2);
                            start = entity.getStart();
                            end = entity.getEnd();
                        }
                        locations.add(new Location(oneSet,start,end));
                    }
                }
            }
        }
        return locations;
    }
    // end

    @Override
    public ArrayList<Tuple<String, String>> getImplementDeps(String level) {
        ArrayList<Tuple<String, String>> deps = new ArrayList<Tuple<String, String>>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            if(entity instanceof StructEntity || entity instanceof AliasTypeEntity) {
                //int typeId = uerr.getId();
                String typeName = entity.getName();
                int fileId = entity.getParentId();
                if(fileId != -1) {
                    String fileName1 = singleCollect.getEntities().get(fileId).getName();
                    for(Tuple<String, Integer> relation : entity.getRelations()) {
                        if(relation.x.equals(Configure.RELATION_IMPLEMENT)) {
                            int interfaceId = relation.y;
                            String interfaceName = singleCollect.getEntities().get(interfaceId).getName();
                            int fileId2 = singleCollect.getEntities().get(interfaceId).getParentId();
                            String fileName2 = "";
                            if(fileId2 != -1) {
                                fileName2 = singleCollect.getEntities().get(fileId2).getName();
                            }
                            if(level.equals(Configure.RELATION_LEVEL_FILE)) {
                                deps.add(new Tuple<String, String>(fileName1, fileName2));
                            }
                            else {
                                deps.add(new Tuple<String, String>(typeName, interfaceName));
                            }
                        }
                    }
                }
            }
        } // end big for
        return deps;
    }

    // update
    @Override
    public ArrayList<Location> getImplementDepsLocation(String level) {
        ArrayList<Location> locations = new ArrayList<Location>();
        for (AbsEntity entity : singleCollect.getEntities()) {
            if(entity instanceof StructEntity || entity instanceof AliasTypeEntity) {
                //int typeId = uerr.getId();
                String typeName = entity.getName();
                int fileId = entity.getParentId();
                if(fileId != -1) {
                    String fileName1 = singleCollect.getEntities().get(fileId).getName();
                    for(Tuple<String, Integer> relation : entity.getRelations()) {
                        if(relation.x.equals(Configure.RELATION_IMPLEMENT)) {
                            int interfaceId = relation.y;
                            String interfaceName = singleCollect.getEntities().get(interfaceId).getName();
                            int fileId2 = singleCollect.getEntities().get(interfaceId).getParentId();
                            String fileName2 = "";
                            if(fileId2 != -1) {
                                fileName2 = singleCollect.getEntities().get(fileId2).getName();
                            }
                            Token start;
                            Token end;
                            if(level.equals(Configure.RELATION_LEVEL_FILE)) {
//                                deps.add(new Tuple<String, String>(fileName1, fileName2));
                                start = singleCollect.getEntities().get(fileId).getStart();
                                end = singleCollect.getEntities().get(fileId).getEnd();
                                locations.add(new Location(new Tuple<String, String>(fileName1, fileName2),start,end));
                            }
                            else {
//                                deps.add(new Tuple<String, String>(typeName, interfaceName));
                                start = entity.getStart();
                                end = entity.getEnd();
                                locations.add(new Location(new Tuple<String, String>(typeName, interfaceName),start,end));
                            }
                        }
                    }
                }
            }
        }
        return locations;
    }
    // end

    @Override
    public ArrayList<Tuple<String, String>> getInheritDeps(String level) {
        ArrayList<Tuple<String, String>> embedDeps = new ArrayList<Tuple<String, String>>();
        embedDeps.addAll(getEmbedInterfaceDep(level));
        embedDeps.addAll(getEmbedStructDep(level));
        return embedDeps;
    }

    // update
    @Override
    public ArrayList<Location> getInheritDepsLocation(String level) {
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.addAll(getEmbedInterfaceDepLocation(level));
        locations.addAll(getEmbedStructDepLocation(level));
        return locations;
    }
    // end

    @Override
    public ArrayList<Tuple<String, String>> getImportDeps(String level) {
        ArrayList<Tuple<String, String>> importDeps = new ArrayList<Tuple<String, String>>();
        for(AbsEntity fileEntity : singleCollect.getEntities()) {
            if (fileEntity instanceof AbsFILEntity) {
                String fileName = fileEntity.getName();
                ArrayList<Tuple<String, Integer>> relations = fileEntity.getRelations();
                if (!relations.isEmpty()) {
                    for (Tuple<String, Integer> oneRelation : relations) {
                        if (oneRelation.x.equals(Configure.RELATION_IMPORT)) {
                            String importedPackageName = ((AbsFLDEntity) singleCollect.getEntities().get(oneRelation.y)).getFullPath();
                            if(level.equals(Configure.RELATION_LEVEL_FILE)) {
                                ArrayList<Integer> fileIds2 = singleCollect.getEntities().get(oneRelation.y).getChildrenIds();
                                for (int fileId2 : fileIds2) {
                                    if(singleCollect.getEntities().get(fileId2) instanceof AbsFILEntity) {
                                        importDeps.add(new Tuple<String, String>(fileName, singleCollect.getEntities().get(fileId2).getName()));
                                    }
                                }
                            }
                            else {
                                importDeps.add(new Tuple<String, String>(fileName, importedPackageName));
                            }

                        }
                    }
                }
            }
        }
        return importDeps;
    }

    // update
    @Override
    public ArrayList<Location> getImportDepsLocation(String level) {
        ArrayList<Location> importDepsLocation = new ArrayList<Location>();
        for(AbsEntity fileEntity : singleCollect.getEntities()) {
            if (fileEntity instanceof AbsFILEntity) {
                String fileName = fileEntity.getName();
                ArrayList<Tuple<String, Integer>> relations = fileEntity.getRelations();
                if (!relations.isEmpty()) {
                    for (Tuple<String, Integer> oneRelation : relations) {
                        if (oneRelation.x.equals(Configure.RELATION_IMPORT)) {
                            String importedPackageName = ((AbsFLDEntity) singleCollect.getEntities().get(oneRelation.y)).getFullPath();
                            if(level.equals(Configure.RELATION_LEVEL_FILE)) {
                                ArrayList<Integer> fileIds2 = singleCollect.getEntities().get(oneRelation.y).getChildrenIds();
                                for (int fileId2 : fileIds2) {
                                    if(singleCollect.getEntities().get(fileId2) instanceof AbsFILEntity) {
                                        importDepsLocation.add(new Location(new Tuple<String, String>(fileName, singleCollect.getEntities().get(fileId2).getName()),fileEntity.getStart(),fileEntity.getEnd()));
                                    }
                                }
                            }
                            else {
//                                importDeps.add(new Tuple<String, String>(fileName, importedPackageName));
                                importDepsLocation.add(new Location(new Tuple<String, String>(fileName, importedPackageName),fileEntity.getStart(),fileEntity.getEnd()));
                            }

                        }
                    }
                }
            }
        }
        return importDepsLocation;
    }

    // end


    private ArrayList<Tuple<String, String>> getEmbedStructDep(String level) {
        ArrayList<Tuple<String, String>> embedDeps = new ArrayList<Tuple<String, String>>();
        for(AbsEntity structEntity : singleCollect.getEntities()) {
            if (structEntity instanceof StructEntity) {
                String structName = structEntity.getName();
                ArrayList<Tuple<String, Integer>> relations = structEntity.getRelations();
                if (!relations.isEmpty()) {
                    String fileName1 = singleCollect.getEntities().get(structEntity.getParentId()).getName();
                    for (Tuple<String, Integer> oneRelation : relations) {
                        if (oneRelation.x.equals(Configure.RELATION_INHERIT)) {
                            String embededStructName = singleCollect.getEntities().get(oneRelation.y).getName();
                            int embededFileId = singleCollect.getEntities().get(oneRelation.y).getParentId();
                            String fileName2 = singleCollect.getEntities().get(embededFileId).getName();
                            if (level.equals(Configure.RELATION_LEVEL_FILE)) {
                                embedDeps.add(new Tuple<String, String>(fileName1, fileName2));
                            }
                            else {
                                embedDeps.add(new Tuple<String, String>(structName, embededStructName));
                            }
                        }
                    }
                }
            }
        }
        return embedDeps;
    }

    // update
    private ArrayList<Location> getEmbedStructDepLocation(String level) {
        ArrayList<Location> locations = new ArrayList<Location>();
        for(AbsEntity structEntity : singleCollect.getEntities()) {
            if (structEntity instanceof StructEntity) {
                String structName = structEntity.getName();
                ArrayList<Tuple<String, Integer>> relations = structEntity.getRelations();
                if (!relations.isEmpty()) {
                    String fileName1 = singleCollect.getEntities().get(structEntity.getParentId()).getName();
                    for (Tuple<String, Integer> oneRelation : relations) {
                        if (oneRelation.x.equals(Configure.RELATION_INHERIT)) {
                            String embededStructName = singleCollect.getEntities().get(oneRelation.y).getName();
                            int embededFileId = singleCollect.getEntities().get(oneRelation.y).getParentId();
                            String fileName2 = singleCollect.getEntities().get(embededFileId).getName();
                            Token start;
                            Token end;
                            if (level.equals(Configure.RELATION_LEVEL_FILE)) {
                                start = singleCollect.getEntities().get(structEntity.getParentId()).getStart();
                                end = singleCollect.getEntities().get(structEntity.getParentId()).getEnd();
                                locations.add(new Location(new Tuple<String, String>(fileName1, fileName2),start,end));
//                                embedDeps.add(new Tuple<String, String>(fileName1, fileName2));
                            }
                            else {
                                start = structEntity.getStart();
                                end = structEntity.getEnd();
                                locations.add(new Location(new Tuple<String, String>(structName, embededStructName),start,end));
//                                embedDeps.add(new Tuple<String, String>(structName, embededStructName));
                            }
                        }
                    }
                }
            }
        }
        return locations;
    }
    // end

    private ArrayList<Tuple<String, String>> getEmbedInterfaceDep(String level) {
        ArrayList<Tuple<String, String>> embedDeps = new ArrayList<Tuple<String, String>>();
        for(AbsEntity interfaceEntity : singleCollect.getEntities()) {
            if (interfaceEntity instanceof InterfaceEntity) {
                String interfaceName = interfaceEntity.getName();
                ArrayList<Tuple<String, Integer>> relations = interfaceEntity.getRelations();
                String fileName1 = "";
                if (interfaceEntity.getParentId() != -1) {
                    fileName1 = singleCollect.getEntities().get(interfaceEntity.getParentId()).getName();
                }
                if (!relations.isEmpty()) {
                    for (Tuple<String, Integer> oneRelation : relations) {
                        if (oneRelation.x.equals(Configure.RELATION_INHERIT)) {
                            String embededInterfaceName = singleCollect.getEntities().get(oneRelation.y).getName();
                            int embededFileId = singleCollect.getEntities().get(oneRelation.y).getParentId();
                            String fileName2 = "";
                            if (embededFileId != -1) {
                                fileName2 = singleCollect.getEntities().get(embededFileId).getName();
                            }
                            if (level.equals(Configure.RELATION_LEVEL_FILE)) {
                                embedDeps.add(new Tuple<String, String>(fileName1, fileName2));
                            }
                            else {
                                embedDeps.add(new Tuple<String, String>(interfaceName, embededInterfaceName));
                            }
                        }
                    }
                }
            }
        }
        return embedDeps;
    }

    // update

    private ArrayList<Location> getEmbedInterfaceDepLocation(String level) {
        ArrayList<Location> locations = new ArrayList<Location>();
        for(AbsEntity interfaceEntity : singleCollect.getEntities()) {
            if (interfaceEntity instanceof InterfaceEntity) {
                String interfaceName = interfaceEntity.getName();
                ArrayList<Tuple<String, Integer>> relations = interfaceEntity.getRelations();
                String fileName1 = "";
                if (interfaceEntity.getParentId() != -1) {
                    fileName1 = singleCollect.getEntities().get(interfaceEntity.getParentId()).getName();
                }
                if (!relations.isEmpty()) {
                    for (Tuple<String, Integer> oneRelation : relations) {
                        if (oneRelation.x.equals(Configure.RELATION_INHERIT)) {
                            String embededInterfaceName = singleCollect.getEntities().get(oneRelation.y).getName();
                            int embededFileId = singleCollect.getEntities().get(oneRelation.y).getParentId();
                            String fileName2 = "";
                            if (embededFileId != -1) {
                                fileName2 = singleCollect.getEntities().get(embededFileId).getName();
                            }
                            Token start;
                            Token end;
                            if (level.equals(Configure.RELATION_LEVEL_FILE)) {
                                start = singleCollect.getEntities().get(interfaceEntity.getParentId()).getStart();
                                end = singleCollect.getEntities().get(interfaceEntity.getParentId()).getEnd();
                                locations.add(new Location(new Tuple<String, String>(fileName1, fileName2),start,end));
//                                embedDeps.add(new Tuple<String, String>(fileName1, fileName2));
                            }
                            else {
                                start = interfaceEntity.getStart();
                                end = interfaceEntity.getEnd();
                                locations.add(new Location(new Tuple<String, String>(interfaceName, embededInterfaceName),start,end));
//                                embedDeps.add(new Tuple<String, String>(interfaceName, embededInterfaceName));
                            }
                        }
                    }
                }
            }
        }
        return locations;
    }



    /**
     * get the fileId of a local or global var
     * @param varId
     * @return
     */
    private int getFileForVar(int varId) {
        if(varId == -1) {
            return -1;
        }
        int parentId = singleCollect.getEntities().get(varId).getParentId();
        while (parentId != -1 &&
                !(singleCollect.getEntities().get(parentId) instanceof AbsFILEntity)) {
            parentId = singleCollect.getEntities().get(parentId).getParentId();
        }
        if(parentId != -1 &&
                singleCollect.getEntities().get(parentId) instanceof AbsFILEntity) {
            return parentId;
        }
        return -1;
    }

    public ArrayList<Tuple<String, String>> getMethodReceiveDep(String level) {
        ArrayList<Tuple<String, String>> receiveDeps = new ArrayList<Tuple<String, String>>();
        for(AbsEntity methodEntity : singleCollect.getEntities()) {
            if (methodEntity instanceof MethodEntity) {
                String methodEntityName = methodEntity.getName();
                ArrayList<Tuple<String, Integer>> relations = methodEntity.getRelations();
                if (!relations.isEmpty()) {
                    String fileName1 = singleCollect.getEntities().get(methodEntity.getParentId()).getName();
                    for (Tuple<String, Integer> oneRelation : relations) {
                        if (oneRelation.x.equals(Configure.RELATION_RECEIVE)) {
                            String structAliasName = singleCollect.getEntities().get(oneRelation.y).getName();
                            int structAliasFileId = singleCollect.getEntities().get(oneRelation.y).getParentId();
                            String fileName2 = singleCollect.getEntities().get(structAliasFileId).getName();
                            if (level.equals(Configure.RELATION_LEVEL_FILE)) {
                                receiveDeps.add(new Tuple<String, String>(fileName1, fileName2));
                            }
                            else {
                                receiveDeps.add(new Tuple<String, String>(methodEntityName, structAliasName));
                            }
                        }
                    }
                }
            }
        }
        return receiveDeps;
    }


    @Override
    public ArrayList<Tuple<String, String>> getImplicitExternalCalls(String level) {
        ArrayList<Tuple<String, String>> receiveDeps = new ArrayList<Tuple<String, String>>();
        return receiveDeps;
    }
}
