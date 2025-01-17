package com.sunzequn.geo.data.baike.jena;

import com.sunzequn.geo.data.algorithm.hanyu.Pinyin;
import com.sunzequn.geo.data.baike.bean.ExtendedType;
import com.sunzequn.geo.data.baike.bean.InfoBoxTemplate;
import com.sunzequn.geo.data.baike.bean.InfoBoxTemplateProp;
import com.sunzequn.geo.data.baike.dao.ExtendedTypeDao;
import com.sunzequn.geo.data.baike.dao.InfoBoxTemplateDao;
import com.sunzequn.geo.data.baike.dao.InfoBoxTemplatePropDao;
import com.sunzequn.geo.data.utils.ReadUtils;
import com.sunzequn.geo.data.utils.StringUtils;
import com.sunzequn.geo.data.utils.WriteUtils;
import org.apache.jena.assembler.Mode;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.XSD;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunzequn on 2016/4/3.
 */
public class OntologyGenerator {

    private static final String ONTOLOGY_FILE = "Data/src/main/resources/baike/ontology.owl";
    private static final String MAPPING_FILE = "    Data/src/main/resources/baike/mapping.owl";
    private static final String DBO = "http://dbpedia.org/ontology/";
    private static final String GEO = "http://www.geonames.org/ontology#";
    private static final String GEO_F = "http://www.geonames.org/ontology#featureCode";
    private static final String FOAF = "http://xmlns.com/foaf/0.1/";
    //百度百科uri前缀
    private static final String CLINGA = "http://ws.nju.edu.cn/clinga/";
    private static Pinyin pinyin = new Pinyin();

    public static void main(String[] args) {
        generateOntology();
    }

    /**
     * 处理ontology
     */
    private static void generateOntology() {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        model = generateOriginOntology(model);
        model = generateExtendedType(model);
        model = generateProperty(model);
        toFile(model, ONTOLOGY_FILE);
    }

    private static OntModel generateProperty(OntModel model) {

        InfoBoxTemplatePropDao propDao = new InfoBoxTemplatePropDao();
        List<InfoBoxTemplateProp> props = propDao.getAll();
        for (InfoBoxTemplateProp prop : props) {
            prop.initAll();
            //非0的是objectProperty
            if (prop.getType() > 0) {
                ObjectProperty property = model.createObjectProperty(CLINGA + pinyin.getPinyinWithFirstOneLower(prop.getName().trim()));
                model = completeProperty(model, property, prop);
                for (String domain : prop.getDomains()) {
                    property.addDomain(model.getOntClass(CLINGA + pinyin.getPinyinWithFirstOneUpper(domain)));
                }
                if (!StringUtils.isNullOrEmpty(prop.getRange1())) {
                    for (String range : prop.getRanges()) {
                        if (range.contains("foaf")) {
                            property.addRange(model.createResource(FOAF + StringUtils.removeStart(range, "foaf:")));
                        } else {
                            property.addRange(model.getOntClass(CLINGA + pinyin.getPinyinWithFirstOneUpper(range)));
                        }
                    }
                }
            } else {
                DatatypeProperty property = model.createDatatypeProperty(CLINGA + pinyin.getPinyinWithFirstOneLower(prop.getName()));
                model = completeProperty(model, property, prop);
                for (String domain : prop.getDomains()) {
                    property.addDomain(model.getOntClass(CLINGA + pinyin.getPinyinWithFirstOneUpper(domain)));
                }
                String range = prop.getRange1();
                if (range.equals("int")) {
                    property.setRange(XSD.integer);
                } else if (range.equals("double")) {
                    property.setRange(XSD.decimal);
                } else {
                    property.setRange(XSD.xstring);
                }
            }
        }
        handlePropSameAs(model, props);
        return model;
    }

    private static void handlePropSameAs(Model model, List<InfoBoxTemplateProp> props) {
        for (InfoBoxTemplateProp prop : props) {
            //处理sameAs
            if (!StringUtils.isNullOrEmpty(prop.getSameas())) {
                String uri = CLINGA + pinyin.getPinyinWithFirstOneLower(prop.getName());
                String sameAsUri = CLINGA + pinyin.getPinyinWithFirstOneLower(prop.getSameas());
                Resource property = model.getResource(uri);
                Resource sameProp = model.getResource(sameAsUri);
                property.addProperty(OWL.equivalentProperty, sameProp);
            }
        }
    }


    private static OntModel completeProperty(OntModel model, OntProperty property, InfoBoxTemplateProp prop) {

//        property.addProperty(RDF.type, RDF.Property);

        property.addProperty(SKOS.prefLabel, model.createLiteral(prop.getName(), MultiLang.ZH));
        property.addLabel(prop.getName(), MultiLang.ZH);

        if (!StringUtils.isNullOrEmpty(prop.getAltname())) {
            for (String altname : prop.getAltnames()) {
                property.addProperty(SKOS.altLabel, model.createLiteral(altname, MultiLang.ZH));
            }
        }
        if (!StringUtils.isNullOrEmpty(prop.getEname())) {
            property.addProperty(SKOS.altLabel, model.createLiteral(prop.getEname(), MultiLang.EN));
        }
        if (!StringUtils.isNullOrEmpty(prop.getComment())) {
            property.addComment(prop.getComment(), MultiLang.ZH);
        }
        return model;
    }

    private static OntModel generateOriginOntology(OntModel model) {
        InfoBoxTemplateDao templateDao = new InfoBoxTemplateDao();
        List<InfoBoxTemplate> templates = templateDao.getAll();
        List<InfoBoxTemplate> templates1 = new ArrayList<>();
        List<InfoBoxTemplate> templates2 = new ArrayList<>();
        List<InfoBoxTemplate> templates3 = new ArrayList<>();
        for (InfoBoxTemplate template : templates) {
            if (template.getLevel() == 1) {
                templates1.add(template);
            } else if (template.getLevel() == 2) {
                templates2.add(template);
            } else {
                templates3.add(template);
            }
        }

        for (InfoBoxTemplate template : templates1) {
            handleTemplate(model, templates, template);
        }
        for (InfoBoxTemplate template : templates2) {
            handleTemplate(model, templates, template);
        }
        for (InfoBoxTemplate template : templates3) {
            handleTemplate(model, templates, template);
        }
        return model;
    }

    private static OntModel generateExtendedType(OntModel model) {
        ExtendedTypeDao dao = new ExtendedTypeDao();
        List<ExtendedType> types = dao.getAll();
        for (ExtendedType type : types) {
            type.initAltLabels();
            String ns = CLINGA + pinyin.getPinyinWithFirstOneUpper(type.getTypeName().trim());
            OntClass ontClass = model.getOntClass(ns);
            if (ontClass == null) {
                model = addClass(model, type);
            }
        }

        for (ExtendedType type : types) {
            String ns1 = CLINGA + pinyin.getPinyinWithFirstOneUpper(type.getTypeName().trim());
            OntClass ontClass1 = model.getOntClass(ns1);

            String ns2 = CLINGA + pinyin.getPinyinWithFirstOneUpper(type.getSuperType().trim());
            OntClass ontClass2 = model.getOntClass(ns2);

            ontClass1.setSuperClass(ontClass2);
        }
        return model;
    }

    private static OntModel addClass(OntModel model, ExtendedType type) {

        OntClass ontClass = model.createClass(CLINGA + pinyin.getPinyinWithFirstOneUpper(type.getTypeName().trim()));
        //处理comment
        if (!StringUtils.isNullOrEmpty(type.getComment())) {
            ontClass.addComment(type.getComment().trim(), MultiLang.ZH);
        }
        if (!StringUtils.isNullOrEmpty(type.getEncomment())) {
            ontClass.addComment(type.getEncomment().trim(), MultiLang.EN);
        }
        //设置preflabel
        ontClass.addProperty(SKOS.prefLabel, model.createLiteral(type.getTypeName().trim(), MultiLang.ZH));
        ontClass.addLabel(type.getTypeName().trim(), MultiLang.ZH);
        //处理中文altlabel
        if (type.getAltLabels() != null) {
            for (String label : type.getAltLabels())
                ontClass.addProperty(SKOS.altLabel, model.createLiteral(label, MultiLang.ZH));
        }
        //处理英文altlabel
        if (!StringUtils.isNullOrEmpty(type.getEntype())) {
            ontClass.addProperty(SKOS.altLabel, model.createLiteral(type.getEntype().trim(), MultiLang.EN));
        }
        return model;
    }


    private static void handleTemplate(OntModel model, List<InfoBoxTemplate> templates, InfoBoxTemplate template) {
        String ns = CLINGA + pinyin.getPinyinWithFirstOneUpper(template.getTitle().trim());
        OntClass ontClass = model.getOntClass(ns);
        //这个类不存在，就创建
        if (ontClass == null) {
            OntClass newClass = model.createClass(ns);
            System.out.println(newClass);
            newClass.setRDFType(OWL.Class);
            //设置父类
            List<String> parents = getParentsByName(templates, template.getTitle());
            if (parents != null) {
                if (parents.size() > 1) {
                    System.out.println(template.getTitle());
                    System.out.println(parents);
                }
                for (String parent : parents) {
                    String parentNs = CLINGA + pinyin.getPinyinWithFirstOneUpper(parent);
                    OntClass parentClass = model.getOntClass(parentNs);
                    if (parentClass == null) {
                        System.out.println("出错");
                    } else {
                        newClass.addSuperClass(parentClass);
                    }
                }
            }
            //设置preflabel
            newClass.addProperty(SKOS.prefLabel, model.createLiteral(template.getTitle(), MultiLang.ZH));
            newClass.addLabel(template.getTitle().trim(), MultiLang.ZH);
            //设置altlabel
            if (!StringUtils.isNullOrEmpty(template.getEntitle())) {
                newClass.addProperty(SKOS.altLabel, model.createLiteral(template.getEntitle().trim(), MultiLang.EN));
            }
            //设置commet
            if (!StringUtils.isNullOrEmpty(template.getComment())) {
                newClass.addComment(template.getComment().trim(), MultiLang.ZH);
            }
            if (!StringUtils.isNullOrEmpty(template.getEncomment())) {
                newClass.addComment(template.getEncomment().trim(), MultiLang.EN);
            }
        }
        //存在就不管了
    }

    private static List<String> getParentsByName(List<InfoBoxTemplate> templates, String localName) {
        List<String> parents = new ArrayList<>();
        for (InfoBoxTemplate template : templates) {
            if (template.getTitle().trim().equals(localName)) {
                String parent = getTitleById(templates, template.getParentid());
                //一级目录的父节点是自己
                if (parent != null && !parent.equals(localName)) {
                    parents.add(parent);
                }
            }
        }
        if (parents.size() > 0) {
            return parents;
        }
        return null;
    }

    private static String getTitleById(List<InfoBoxTemplate> templates, int id) {
        for (InfoBoxTemplate template : templates) {
            if (template.getId() == id) {
                return template.getTitle().trim();
            }
        }
        return null;
    }

    private static void toFile(OntModel model, String file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        RDFDataMgr.write(fileOutputStream, model, Lang.RDFXML);
    }

    private static void reformatRDF(String file) {
        ReadUtils readUtils = new ReadUtils(file);
        List<String> lines = readUtils.readByLine();
        readUtils.close();
        if (lines.size() > 0) {
            WriteUtils writeUtils = new WriteUtils(file, false);
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).contains("rdfs:range")) {
                    writeUtils.write(lines.get(i + 1));
                    writeUtils.write(lines.get(i));
                    i++;
                } else {
                    writeUtils.write(lines.get(i));
                }
            }
            writeUtils.close();
        }
    }
}
