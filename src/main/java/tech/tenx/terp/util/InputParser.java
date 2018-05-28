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

package tech.tenx.terp.util;

import tech.tenx.terp.model.BestRateRequest;
import tech.tenx.terp.model.ExchangeCurrency;
import tech.tenx.terp.model.ExchangeRate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.regex.Pattern;

public class InputParser {

    private static final String TIME_REGEX = "\\d{4}-(0[1-9]|1[0-2])-[0-2]\\dT([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d[+\\-]([01]\\d|2[0-3]):[0-5]\\d";

    private static final String TEXT_REGEX = " \\S+";

    private static final String DECIMAL_REGEX = " (\\d+|\\d+\\.\\d+)";

    private static final String PRICE_UPDATE_REGEX = "^" + TIME_REGEX + TEXT_REGEX + TEXT_REGEX + TEXT_REGEX + DECIMAL_REGEX + DECIMAL_REGEX + "$";

    private static final String EXCHANGE_RATE_REQ_REGEX = "^EXCHANGE_RATE_REQUEST" + TEXT_REGEX + TEXT_REGEX + TEXT_REGEX + TEXT_REGEX + "$";

    private static final Pattern PRICE_UPDATE_PATTERN = Pattern.compile(PRICE_UPDATE_REGEX);

    private static final Pattern EXCHANGE_RATE_REQ_PATTERN = Pattern.compile(EXCHANGE_RATE_REQ_REGEX);

    public static boolean isPriceUpdate(String input) {
        return PRICE_UPDATE_PATTERN.matcher(input).find();
    }

    public static boolean isExchangeRateRequest(String input) {
        return EXCHANGE_RATE_REQ_PATTERN.matcher(input).find();
    }

    public static boolean isPriceUpdateValid(String priceUpdate) {
        String[] segments = priceUpdate.split(" ");
        BigDecimal product = new BigDecimal(segments[4]).multiply(new BigDecimal(segments[5]));
        return !(product.compareTo(BigDecimal.ONE) > 0);
    }

    public static ExchangeRate[] parsePriceUpdate(String priceUpdate) {
        ExchangeRate[] exchangeRates = new ExchangeRate[2];
        String[] segments = priceUpdate.split(" ");
        OffsetDateTime timestamp = OffsetDateTime.parse(segments[0]);
        ExchangeCurrency srcCurrency = new ExchangeCurrency(segments[1], segments[2]);
        ExchangeCurrency destCurrency = new ExchangeCurrency(segments[1], segments[3]);
        exchangeRates[0] = new ExchangeRate(timestamp, srcCurrency, destCurrency, new BigDecimal(segments[4]));
        exchangeRates[1] = new ExchangeRate(timestamp, destCurrency, srcCurrency, new BigDecimal(segments[5]));
        return exchangeRates;
    }

    public static BestRateRequest parseExchangeRateRequest(String exchangeRateRequest) {
        String[] segments = exchangeRateRequest.split(" ");
        ExchangeCurrency srcCurrency = new ExchangeCurrency(segments[1], segments[2]);
        ExchangeCurrency destCurrency = new ExchangeCurrency(segments[3], segments[4]);
        return new BestRateRequest(srcCurrency, destCurrency);
    }
}
