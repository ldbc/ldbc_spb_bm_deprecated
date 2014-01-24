package eu.ldbc.semanticpublishing.resultanalyzers;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import eu.ldbc.semanticpublishing.refdataset.model.Entity;
import eu.ldbc.semanticpublishing.resultanalyzers.saxparsers.SAXQuery24TemplateTransformer;

/**
 * A class used to extract cwork uris, geonamesids, lat and long properties from a query24.txt result.
 * Results is passed as a string, and parsed by SAXQuery24TemplateTransformer class
 */
public class Query24Analyzer {
	public ArrayList<Entity> collectEntitiesList(String result) throws UnsupportedEncodingException {
		SAXQuery24TemplateTransformer transformer = new SAXQuery24TemplateTransformer();
		transformer.transform(new ByteArrayInputStream(result.getBytes("UTF-8")));
		return transformer.getEntitiesList();
	}
}