package lu.circl.mispbump.models.restModels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MispOrganisation {
    @SerializedName("Organisation")
    @Expose
    public Organisation organisation;
}
