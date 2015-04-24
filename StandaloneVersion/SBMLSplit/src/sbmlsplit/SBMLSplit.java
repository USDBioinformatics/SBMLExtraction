/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sbmlsplit;

import java.io.File;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLWriter;

/**
 *
 * @author Mathialakan.Thavappi
 */
public class SBMLSplit {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SBMLSplit sBMLSplit = new SBMLSplit();
        if (args.length != 2) {
            System.out.println("Error: parameter mismatched");
        } else {
            System.out.println(sBMLSplit.splitXml(sBMLSplit.read(args[1]), args[0],getFilename(args[1])));
        }

    }

    public static String getFilename(String fullPath) {
        int dot = fullPath.lastIndexOf(".");
        int sep = fullPath.lastIndexOf(File.separator);
        return fullPath.substring(sep + 1, dot);
    }

    public String splitXml(SBMLDocument sBMLDocument, String how, String filename) {
        int nDoc = 0;
        SplitElement splitElement = new SplitElement();
        splitElement.setDocument(sBMLDocument);
        for (org.sbml.jsbml.SBMLDocument doc : splitElement.gripDocuments(how)) {
            String docName = filename+"S" + nDoc+++".xml";
            write(doc, docName);
        }
        return nDoc + " model has been splited"; //sbmlfile.writeString(doc);

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
