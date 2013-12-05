package com.tactical_foul.bootroom;

import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: scaldwell
 * Date: 12/4/13
 * Time: 9:22 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class JSONExportable extends Exportable {

    /**
     * Convert the object to JSON
     * @return json representation
     */
    protected abstract JSONObject toJSON();

    public void exportJSON() {
        JSONObject j = toJSON();
        ExportTask et = new ExportTask();
        et.execute(j.toString(), exportURL());
    }

    @Override
    /**
     * Append a subpath to the base url for exporting
     */
    protected String extendURL(String extension) {
        return super.extendURL(extension) + ".json";
    }


}
