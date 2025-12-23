package com.ddlab.rnd.build.modifier;

import java.io.StringReader;
import java.nio.file.Path;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MavenPomModifier implements BuildModifiable {

	@Override
	public void modifyBuild(String content, Map<String, String> fixedDependencyMap, Path destnPath) throws Exception {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(content)));

		doc.getDocumentElement().normalize();

		NodeList dependencies = doc.getElementsByTagName("dependency");

		modify(dependencies,fixedDependencyMap);

		// Write changes back to file or console
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

        // TODO: Fix the issue if both the contents are equal
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(destnPath.toFile());
		transformer.transform(source, result);

	}

	public void modify(NodeList dependencies, Map<String, String> fixedDependencyMap) {
		for (int i = 0; i < dependencies.getLength(); i++) {
			Node depNode = dependencies.item(i);
			if (depNode.getNodeType() == Node.ELEMENT_NODE) {
				Element dependency = (Element) depNode;

				String pomGroupId = dependency.getElementsByTagName("groupId").item(0).getTextContent();
				String pomArtifactId = dependency.getElementsByTagName("artifactId").item(0).getTextContent();

				String key = pomGroupId + ":" + pomArtifactId;

				if (fixedDependencyMap.containsKey(key)) {

					String mapDependencyVersion = fixedDependencyMap.get(key);
					Node versionNode = dependency.getElementsByTagName("version").item(0);
					if (versionNode != null) {
						versionNode.setTextContent(mapDependencyVersion);
					}

				}

			}
		}
	}

}
