package careshare.nominationService.model;

import careshare.nominationService.utils.NominationDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;

@JsonDeserialize(using = NominationDeserializer.class)
public class NominationList extends ArrayList<Nomination> {
    // wrapper class to allow easy deserializing JSON into an array of Nominations
    public NominationList() {}
}
