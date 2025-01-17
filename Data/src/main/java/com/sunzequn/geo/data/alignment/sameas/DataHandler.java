package com.sunzequn.geo.data.alignment.sameas;

import com.sunzequn.geo.data.jena.Rdf;
import com.sunzequn.geo.data.utils.ReadUtils;
import com.sunzequn.geo.data.utils.WriteUtils;
import org.apache.jena.rdf.model.*;
import org.neo4j.cypher.internal.compiler.v2_2.functions.Str;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Sloriac on 16/2/22.
 */
public class DataHandler {
    private static final String MAPPINTS_FILE = "Data/src/main/resources/data/sw/geonames_mappings.rdf";
    private static final String GEONAMES_ONTOLOGY_FILE = "Data/src/main/resources/data/sw/geonames_ontology.rdf";

    private static final String GEONAMES_LINK = "Data/src/main/resources/data/sw/geonames_links.nt";
    private static final String GEONAMES_LINK_EN = "Data/src/main/resources/data/sw/geonames_links_en.nt";
    private static final String NEW_LINK = "Data/src/main/resources/data/sw/geonames_new_links.nt";

    public static void main(String[] args) {
//        countMappings();
        countLinks();
//        printGeonamesOntology();
    }

    public static void countLinks() {
        ReadUtils readUtils = new ReadUtils(GEONAMES_LINK);
        List<String> links = readUtils.readByLine();
        System.out.println(links.size());
        readUtils = new ReadUtils(GEONAMES_LINK_EN);
        List<String> enlinks = readUtils.readByLine();
        System.out.println(enlinks.size());

        Set<String> linkNoSames = links.stream().collect(Collectors.toSet());
        System.out.println(linkNoSames.size());
        linkNoSames.addAll(enlinks);
        System.out.println(linkNoSames.size());

//        WriteUtils writeUtils = new WriteUtils(NEW_LINK, true);
//        Iterator<String> iterator = linkNoSames.iterator();
//        while (iterator.hasNext()) {
//            String line = iterator.next();
//            writeUtils.write(line);
//        }
//        writeUtils.close();

    }

    public static void countMappings() {
        Rdf rdf = new Rdf();
        Model model = rdf.getModel(MAPPINTS_FILE, "RDF/XML");

        Set<String> subjects = new HashSet<>();
        Set<Resource> resources = new HashSet<>();
        StmtIterator stmtIterator = model.listStatements();
        int i = 0;
        int num = 0;
        while (stmtIterator.hasNext()) {
            i++;
            Statement stmt = stmtIterator.nextStatement();
            Resource subject = stmt.getSubject();
            RDFNode object = stmt.getObject();
            if (subject.getURI().contains("http://dbpedia.org/ontology") && object.toString().contains("http://www.geonames.org/ontology")) {
                System.out.println(subject.getURI() + " / " + object.toString());

            }
        }
    }

    public static void printGeonamesOntology() {
        Rdf rdf = new Rdf();
        rdf.print(GEONAMES_ONTOLOGY_FILE, "RDF/XML");
    }
}
