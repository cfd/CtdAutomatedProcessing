package processing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Writer {
	XMLOutputter xmlOutput;
	
	
	public Writer(){
		 xmlOutput = new XMLOutputter(Format.getPrettyFormat());
	}

	/**
	 * @param args
	 */

	public void writeCalcArray() throws IOException{
			Document doc = new Document();
			Element root = new Element("Data_Conversion");
			doc.setRootElement(root);
			
			
			Element calcArray = new Element("CalcArray");
			Element calcArrayItem = new Element("CalcArrayItem");
			Element calc = new Element("Calc");
			Element fullname = new Element("FullName");

			calcArray.setAttribute("Size", "");
			
			calcArrayItem.setAttribute("index", "");
			calcArrayItem.setAttribute("CalcId", "");
			
			calc.setAttribute("UnitID", "");
			calc.setAttribute("Ordinal", "");
			fullname.setAttribute("value", "");
			
			calcArrayItem.addContent(calc);
			calc.addContent(fullname);
			
			
			root.addContent(calcArray);
			calcArray.addContent(calcArrayItem);
			
			
			
			

			xmlOutput.output(doc, new FileOutputStream(new File("C:\\Users\\Chris\\Desktop\\AIMS2012\\mockup.psa")));

			System.out.println("Wrote to file");
	}

}
