package java13.exchange_rates_bot_2.service.impl;

import java13.exchange_rates_bot_2.client.CbrClient;
import java13.exchange_rates_bot_2.exception.ServiceException;
import java13.exchange_rates_bot_2.service.ExchangeRatesBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import javax.swing.text.Document;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

@Service
public class ExchangeRatesBotServiceImpl implements ExchangeRatesBotService {

    private static final String USD_XPATH = "ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH = "ValCurs//Valute[@ID='R01239']/Value";

    @Autowired
    private CbrClient client;

    @Override
    public String getUSDExchangeRate() throws ServiceException {
        var xml = client.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml,USD_XPATH);
    }

    @Override
    public String getEURExchangeRate() throws ServiceException {
        var xml = client.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml,EUR_XPATH);
    }

    private static String extractCurrencyValueFromXML(String xml, String xpathExpression) throws ServiceException {
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (org.w3c.dom.Document) xpath.evaluate("/", source, XPathConstants.NODE);

            return xpath.evaluate(xpathExpression, document);
        } catch (XPathExpressionException e) {
            throw new ServiceException("Не удалось распарсить XML", e);
        }
    }

}
