package software.sigma.training.po.survey.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import software.sigma.training.po.survey.data.dao.RespondentDao;
import software.sigma.training.po.survey.data.domain.*;
import software.sigma.training.po.survey.services.api.CSVProcessorService;
import software.sigma.training.po.survey.services.transform.*;

@Service
public class CSVProcessorServiceImpl implements CSVProcessorService {

    private static final Logger LOG = LoggerFactory.getLogger(CSVProcessorServiceImpl.class);
    
    private TransformerFactory transformerFactory;
    
    @Autowired
    private RespondentDao respondentDao;
    
    @PostConstruct
    public void init() {
        LOG.debug("Initializing CSV parser configuration");
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .ignoreAllProperties("id")
                .setKeysCapitalization(ConfigurationBuilder.KeysCapitalization.CAPITAL)
                .addType(Respondent.class)
                .mapProperty("respondentName", "Respondent")
                .addType(Assess.class)
                .addType(CompanySize.class)
                .addType(Country.class)
                .addType(Education.class)
                .addType(Employment.class)
                .addType(Equipment.class)
                .addType(ExCoder.class)
                .addType(ExperienceLevel.class)
                .addType(HaveWorkedAndWant.class)
                .addType(ImportantHiring.class)
                .addType(Influence.class)
                .addType(JobInfo.class)
                .addType(RespondentDetails.class)
                .addType(StackOverflowInfo.class)
                .addType(TechnicalDetails.class);
        transformerFactory = new TransformerFactoryImpl(configurationBuilder.build());
    }
    
    @Override
    public void process(InputStream is) throws IOException {
        LOG.debug("Starting CSV processing");
        CSVParser parser = new CSVParser(new InputStreamReader(is), CSVFormat.DEFAULT.withFirstRecordAsHeader());
        LOG.debug("Header map: " + parser.getHeaderMap());
        Transformer<Respondent> transformer = transformerFactory.getTransformer(Respondent.class);
        try {
            int i = 0;
            for (CSVRecord record : parser) {
                Map<String, String> map = record.toMap();
                Respondent item = transformer.transform(map, null);
                respondentDao.save(item);
            }
        } catch (TransformerException e) {
            LOG.error("Error during transforming data", e);
        }
        parser.close();
        LOG.debug("CSV processed succefully");
    }
    
}
