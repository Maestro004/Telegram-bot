package java13.exchange_rates_bot_2.service;

import java13.exchange_rates_bot_2.exception.ServiceException;

public interface ExchangeRatesBotService {
    String getUSDExchangeRate() throws ServiceException;
    String getEURExchangeRate() throws ServiceException;
}
