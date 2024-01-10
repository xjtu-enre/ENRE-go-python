package entitybuilder.gobuilder.goentity;

import org.antlr.v4.runtime.Token;
import util.Tuple;

import java.util.ArrayList;

public class Location {

    // update

    private Token start;

    private Token end;

    private Tuple<String, String> src_dst;

    public Location(Tuple<String, String> sd,Token s,Token e) {
        setStart(s);
        setEnd(e);
        setSrc_dst(sd);
    }


    public Tuple<String, String> getSrc_dst() {
        return src_dst;
    }

    public void setSrc_dst(Tuple<String, String> src_dst) {
        this.src_dst = src_dst;
    }

    public Token getStart() {
        return start;
    }

    public void setStart(Token start) {
        this.start = start;
    }

    public Token getEnd() {
        return end;
    }

    public void setEnd(Token end) {
        this.end = end;
    }


}
