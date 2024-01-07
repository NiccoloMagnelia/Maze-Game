package byow.Core;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import org.junit.Test;
import static org.junit.Assert.*;


public class Tester extends TERenderer {
    @Test
    public void testMain() {

        String[] args3 = new String[2];

        args3[0] = "-s";


        args3[1] = "N9393548S";
        Engine myE = new Engine();
        TETile[][] myM = myE.interactWithInputString("220054");
        TERenderer myR = new TERenderer();
        myR.initialize(90, 40);
        boolean x = true;
        while (x) {
            myR.renderFrame(myM);
        }

    }
}
