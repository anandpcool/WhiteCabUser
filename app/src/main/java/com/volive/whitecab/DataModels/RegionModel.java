package com.volive.whitecab.DataModels;

import java.io.Serializable;

/**
 * Created by volive on 7/10/2018.
 */

public class RegionModel implements Serializable {
    String id,name,is_unep;

    public RegionModel(String id, String name, String is_unep) {
        this.id = id;
        this.name = name;
        this.is_unep = is_unep;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIs_unep() {
        return is_unep;
    }

    public void setIs_unep(String is_unep) {
        this.is_unep = is_unep;
    }
}
