package formator;


import formator.fjson.JBuildObject;
import formator.fjson.JDepObject;
import formator.fjson.NewJBuildObject;
import formator.fjson.NewJDepObject;
import formator.fxml.XBuildObject;
import formator.fxml.XDepObject;
import formator.spreadsheet.Csvgrapher;

public class Formator {
    private MapObject mapObject;

    public Formator(String[] depTypes) {

        mapObject = new MapObject(depTypes);
    }

    public XDepObject getfXmlDataModel() {
        XBuildObject xBuildObject = new XBuildObject();
        XDepObject xDepObject = xBuildObject.buildObjectProcess(mapObject);
        return xDepObject;
    }

    public JDepObject getfJsonDataModel() {
        JBuildObject jBuildObject = new JBuildObject();
        JDepObject jDepObject = jBuildObject.buildObjectProcess(mapObject);
        return jDepObject;
    }


    // update2

    public NewJDepObject getfNewJsonDataModel() {
        NewJBuildObject newJBuildObject = new NewJBuildObject();
        NewJDepObject newJDepObject = newJBuildObject.buildObjectProcess(mapObject);
        return newJDepObject;
    }

    // end


}
