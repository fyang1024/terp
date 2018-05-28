/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tenx.terp.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * This class is not thread safe.
 */
public class ExchangeRateGraph {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateGraph.class);

    private Set<ExchangeCurrency> existingCurrencies = new HashSet<>();
    private Map<ExchangeCurrency, Map<ExchangeCurrency, ExchangeRate>> rates = new HashMap<>();

    public void addOrUpdateExchangeRate(ExchangeRate... exchangeRates) {

        for(ExchangeRate exchangeRate: exchangeRates) {

            logger.info("Adding {}", exchangeRate);

            ExchangeCurrency srcCurrency = exchangeRate.getSrcCurrency();
            rates.putIfAbsent(srcCurrency, new HashMap<>());

            ExchangeCurrency destCurrency = exchangeRate.getDestCurrency();
            ExchangeRate currentRate = rates.get(srcCurrency).get(destCurrency);
            if (currentRate == null || currentRate.isOlderThan(exchangeRate)) {
                rates.get(srcCurrency).put(destCurrency, exchangeRate);
                if (currentRate == null) {
                    logger.info("Added {}", exchangeRate);
                } else {
                    logger.info("Updated {}", currentRate);
                }
            } else {
                logger.info("Found newer {}", currentRate);
            }

            if (!existingCurrencies.contains(srcCurrency)) {
                addSameCurrencyRates(srcCurrency);
                existingCurrencies.add(srcCurrency);
            }

            if (!existingCurrencies.contains(destCurrency)) {
                addSameCurrencyRates(destCurrency);
                existingCurrencies.add(destCurrency);
            }
        }
    }

    private void addSameCurrencyRates(ExchangeCurrency newCurrency) {
        for (ExchangeCurrency existingCurrency : existingCurrencies) {
            if (newCurrency.isSameCurrency(existingCurrency)) { // but different exchange
                ExchangeRate sameCurrencyRate = new ExchangeRate(OffsetDateTime.now(), existingCurrency, newCurrency, BigDecimal.ONE);
                rates.putIfAbsent(existingCurrency, new HashMap<>());
                rates.get(existingCurrency).put(newCurrency, sameCurrencyRate);
                logger.info("Added same currency rate {}", sameCurrencyRate);
                sameCurrencyRate = new ExchangeRate(OffsetDateTime.now(), newCurrency, existingCurrency, BigDecimal.ONE);
                rates.putIfAbsent(newCurrency, new HashMap<>());
                rates.get(newCurrency).put(existingCurrency, sameCurrencyRate);
                logger.info("Added same currency rate {}", sameCurrencyRate);
            }
        }
    }

    ExchangeRate getExchangeRate(ExchangeCurrency srcCurrency, ExchangeCurrency destCurrency) {
        return rates.getOrDefault(srcCurrency, new HashMap<>()).get(destCurrency);
    }


    /* modified Floyd-Warshall algorithm */
    private TempResult computeBestRates() {

        Map<ExchangeCurrency, Map<ExchangeCurrency, BigDecimal>> bestRates = new HashMap<>();
        Map<ExchangeCurrency, Map<ExchangeCurrency, ExchangeCurrency>> nextCurrencies = new HashMap<>();

        List<ExchangeCurrency> currencyList = new ArrayList<>(existingCurrencies);

        /* initialise bestRates and nextCurrencies */
        for (int i = 0; i < currencyList.size(); i++) {
            ExchangeCurrency srcCurrency = currencyList.get(i);
            Map<ExchangeCurrency, BigDecimal> rateMap = new HashMap<>();
            bestRates.put(srcCurrency, rateMap);
            Map<ExchangeCurrency, ExchangeCurrency> currencyMap = new HashMap<>();
            nextCurrencies.put(srcCurrency, currencyMap);
            for (ExchangeCurrency destCurrency : currencyList) {
                bestRates.get(srcCurrency).put(destCurrency, srcCurrency.equals(destCurrency) ? BigDecimal.ONE : null);
                nextCurrencies.get(srcCurrency).put(destCurrency, null);
            }
        }

        /* copy current graph to bestRates and nextCurrencies */
        for (ExchangeCurrency srcCurrency : rates.keySet()) {
            for (ExchangeCurrency destCurrency : rates.get(srcCurrency).keySet()) {
                bestRates.get(srcCurrency).put(destCurrency, rates.get(srcCurrency).get(destCurrency).getRate());
                nextCurrencies.get(srcCurrency).put(destCurrency, destCurrency);
            }
        }

        /* find best rates and path to achieve the best rates */
        for (int k = 0; k < currencyList.size(); k++) {
            ExchangeCurrency kCurrency = currencyList.get(k);
            for (int i = 0; i < currencyList.size(); i++) {
                ExchangeCurrency iCurrency = currencyList.get(i);
                for (ExchangeCurrency jCurrency : currencyList) {
                    BigDecimal ijRate = bestRates.get(iCurrency).get(jCurrency);
                    BigDecimal ikRate = bestRates.get(iCurrency).get(kCurrency);
                    BigDecimal kjRate = bestRates.get(kCurrency).get(jCurrency);
                    if (ikRate != null && kjRate != null) {
                        BigDecimal ikjRate = ikRate.multiply(kjRate);
                        if (ijRate == null || ijRate.compareTo(ikjRate) < 0) {
                            bestRates.get(iCurrency).put(jCurrency, ikjRate);
                            nextCurrencies.get(iCurrency).put(jCurrency, nextCurrencies.get(iCurrency).get(kCurrency));
                        }
                    }
                }
            }
        }

        TempResult tempResult = new TempResult();
        tempResult.setBestRates(bestRates);
        tempResult.setNextCurrencies(nextCurrencies);
        return tempResult;
    }

    public BestRateResponse getBestRate(BestRateRequest request) {
        TempResult tempResult = computeBestRates();
        ExchangeCurrency srcCurrency = request.getSrcCurrency();
        ExchangeCurrency destCurrency = request.getDestCurrency();
        List<ExchangeCurrency> steps = new ArrayList<>();
        boolean circular = false;
        if (tempResult.getNextCurrency(srcCurrency, destCurrency) != null) {
            Set<ExchangeCurrency> differentSteps = new HashSet<>();
            ExchangeCurrency next = srcCurrency;
            steps.add(next);
            differentSteps.add(next);
            while ((next = tempResult.getNextCurrency(next, destCurrency)) != null) {
                if (differentSteps.contains(next)) {
                    circular = true;
                }
                steps.add(next);
                if (circular) {
                    break;
                } else {
                    differentSteps.add(next);
                }
            }
        }
        Number rate = circular ? Double.POSITIVE_INFINITY : tempResult.getBestRate(srcCurrency, destCurrency);
        return new BestRateResponse(request, rate, new BestRatePath(steps), rates);
    }

    private static class TempResult {

        private Map<ExchangeCurrency, Map<ExchangeCurrency, BigDecimal>> bestRates;
        private Map<ExchangeCurrency, Map<ExchangeCurrency, ExchangeCurrency>> nextCurrencies;

        void setBestRates(Map<ExchangeCurrency, Map<ExchangeCurrency, BigDecimal>> bestRates) {
            this.bestRates = bestRates;
        }

        void setNextCurrencies(Map<ExchangeCurrency, Map<ExchangeCurrency, ExchangeCurrency>> nextCurrencies) {
            this.nextCurrencies = nextCurrencies;
        }

        ExchangeCurrency getNextCurrency(ExchangeCurrency srcCurrency, ExchangeCurrency destCurrency) {
            return nextCurrencies.get(srcCurrency) == null ? null : nextCurrencies.get(srcCurrency).get(destCurrency);
        }

        BigDecimal getBestRate(ExchangeCurrency srcCurrency, ExchangeCurrency destCurrency) {
            return bestRates.get(srcCurrency) == null ? null : bestRates.get(srcCurrency).get(destCurrency);
        }
    }
}
