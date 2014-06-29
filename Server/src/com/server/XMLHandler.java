package com.server;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class XMLHandler {

	private final String file_producer= "producer.xml";
	private final String file_config= "congif.xml";
	
	private final String video_dir_name= "Video";
	private final String video_tag_name= "video";
	
	
	private void open(){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setValidating(true);
	    factory.setIgnoringElementContentWhitespace(true);
	    try {
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        String dir= getClass().getClassLoader().getResource(".").getPath(); // get the dir of project
	        File file = new File(dir + file_producer);
	        
	        Document doc;
	        if(file.exists())
	        	doc = builder.parse(file);
	        else doc= builder.newDocument(); // new document
	        
	        Element rootElement = doc.createElement("company");
	        Node staff = doc.getElementsByTagName("staff").item(0);
	        
	        NamedNodeMap attr = staff.getAttributes();
			Node nodeAttr = attr.getNamedItem("id");
			nodeAttr.setTextContent("2");
			
			Element age = doc.createElement("age");
			age.appendChild(doc.createTextNode("28"));
			staff.appendChild(age);
	 
	        // Do something with the document here.
	        
	        
	    } catch (ParserConfigurationException e) {
	    } catch (SAXException e) {
	    } catch (IOException e) { 
	    }
	}
	
	public void addProducer(){
		
	}
	
	public void delProducer(){
		
	}
	
	public String getVideoDir(){
		String dir= getClass().getClassLoader().getResource(".").getPath(); // get the dir of project
		File file = new File(dir + file_config);
		
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			
	        if(file.exists()){
	        	
	        	builder = factory.newDocumentBuilder();
	        	Document doc = builder.parse(file);
	        	Node node = doc.getElementsByTagName(video_tag_name).item(0);
	        	return node.getTextContent();
	        }
	        else{ // set default video directory
	        	factory.setValidating(true);
			    factory.setIgnoringElementContentWhitespace(true);
				builder = factory.newDocumentBuilder();
	        	
	        	String res= dir + video_dir_name + File.separator;
	        	Document doc= builder.newDocument(); // new document
	        	
	        	Element root = doc.createElement("config");
	        	Element node = doc.createElement(video_tag_name);
				node.appendChild(doc.createTextNode(res));
	        	root.appendChild(node);
	        	doc.appendChild(root);
	        	
	        	// write the content into xml file
	    		TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    		Transformer transformer = transformerFactory.newTransformer();
	    		DOMSource source = new DOMSource(doc);
	    		StreamResult result = new StreamResult(file);
	    		transformer.transform(source, result);
	        	
	        	return res;
	        }
	        
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null; // never get here
        
	}
	
}
