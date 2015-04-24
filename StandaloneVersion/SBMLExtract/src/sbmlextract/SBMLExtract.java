/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sbmlextract;

import java.io.File;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;

/**
 *
 * @author Mathialakan.Thavappi
 */
public class SBMLExtract {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SBMLExtract sBMLExtract = new SBMLExtract();
        if (args.length != 3) {
            System.out.println("Error: parameter mismatched");
        } else {
            System.out.println(sBMLExtract.splitXml(sBMLExtract.read(args[2]), args[0], args[1], getFilename(args[2])));
        }

    }

    public static String getFilename(String fullPath) {
        int dot = fullPath.lastIndexOf(".");
        int sep = fullPath.lastIndexOf(File.separator);
        return fullPath.substring(sep + 1, dot);
    }

    public String splitXml(SBMLDocument sBMLDocument, String how, String id, String filename) {
        SplitElement splitElement = new SplitElement();
        splitElement.setDocument(sBMLDocument);

        String docName = filename+"_"+how + "_" + id + ".xml";
        write(splitElement.gripDocument(id, how), docName);

        return id + " model has been extracted"; 

    }

    public SBMLDocument read(String string) {
        try {
            return JSBML.readSBMLFromFile(string);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (SBMLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean write(SBMLDocument sBMLDocument, String name) {
        try {
            JSBML.writeSBML(sBMLDocument, name);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (SBMLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
