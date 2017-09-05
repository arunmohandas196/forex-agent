package com.amohandas.forex.service;

import com.amohandas.forex.exception.DataFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.List;

/* 
 * a sample scheduled task. Spring scheduled is not cluster-friendly. If you are running a cluster
 *  you have to enable scheduling in one app instance and disable it in all others. (see application.yml) 
 */

@Component
public class ForexAgentScheduledTask {

    private static final Logger log = LoggerFactory
            .getLogger(ForexAgentScheduledTask.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "HH:mm:ss");
    // set this to false to disable this job;
    @Value("${forex.agent.scheduledJob.enabled:false}")
    private boolean scheduledJobEnabled;
    private String currencyRateUrl;
    @Value("${forex.agent.exchangeDate}")
    private String exchangeDate;
    @Value("${forex.agent.sourceCurrency}")
    private String sourceCurrency;
    @Value("${forex.agent.targetCurrencies}")
    private String targetCurrencies;

    @Autowired
    public ForexAgentScheduledTask(@Value("${forex.agent.exchangeDate}")
                                           String exchangeDate,
                                   @Value("${forex.agent.sourceCurrency}")
                                           String sourceCurrency,
                                   @Value("${forex.agent.targetCurrencies}")
                                           String targetCurrencies, @Value("${forex.agent.service.url}")
                                           String forexServiceUrl) {
        this.exchangeDate = exchangeDate;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrencies = targetCurrencies;
        this.currencyRateUrl = getCurrencyRateUrl(forexServiceUrl);
    }

    @Scheduled(fixedRate = 30000)  // every 30 seconds
    public void retrieveCurrencyRates() {
        log.info("testing..........." + currencyRateUrl);
        if (!scheduledJobEnabled) {
            return;
        }

        RestTemplate restTemplate = new RestTemplate();
        log.info("Starting task for: "+currencyRateUrl);
        List output;
        try {
            if (!StringUtils.isEmpty(targetCurrencies)) {
                output = restTemplate.getForObject(currencyRateUrl, List.class, exchangeDate, sourceCurrency, targetCurrencies);
            } else {
                output = restTemplate.getForObject(currencyRateUrl, List.class, exchangeDate, sourceCurrency);
            }
            log.info("Task completed with output:" + output);
        }catch (Exception e){
            log.info("Task failed with error:",e);
        }
    }

    private String getCurrencyRateUrl(String forexServiceUrl) {
        validateParameters(forexServiceUrl);
        StringBuilder url = new StringBuilder(forexServiceUrl);
        url.append("/{exchangeDate}?sourceCurrency={sourceCurrency}");
        if (!StringUtils.isEmpty(targetCurrencies)) {
            url.append("&targetCurrencies={targetCurrencies}");
        }

        return url.toString();

    }

    private void validateParameters(String forexServiceUrl) {
        if (StringUtils.isEmpty(forexServiceUrl)) {
            throw new DataFormatException("Please add forexServiceUrl in application.yml");
        }
        if (StringUtils.isEmpty(sourceCurrency)) {
            throw new DataFormatException("Please add sourceCurrency in application.yml");
        }
    }

    // examples of other CRON expressions
    // * "0 0 * * * *" = the top of every hour of every day.
    // * "*/10 * * * * *" = every ten seconds.
    // * "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
    // * "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
    // * "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
    // * "0 0 0 25 12 ?" = every Christmas Day at midnight

}
