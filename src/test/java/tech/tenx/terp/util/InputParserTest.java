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

import org.junit.jupiter.api.Test;
import tech.tenx.terp.model.ExchangeCurrency;
import tech.tenx.terp.model.ExchangeRate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputParserTest {

    @Test
    void priceUpdateFormatAccepted() {
        String priceUpdate = "2018-05-26T09:42:23+00:00 BITFINEX BTC ETH 9.9 0.0998";
        assertTrue(InputParser.isPriceUpdate(priceUpdate));
    }

    @Test
    void priceUpdateParsed() {
        String priceUpdate = "2018-05-26T09:42:23+00:00 BITFINEX BTC ETH 9.9 0.0998";
        ExchangeRate[] exchangeRates = InputParser.parsePriceUpdate(priceUpdate);
        assertEquals(exchangeRates.length, 2);
        assertEquals(exchangeRates[0].getSrcCurrency(), new ExchangeCurrency("BITFINEX", "BTC"));
        assertEquals(exchangeRates[0].getDestCurrency(), new ExchangeCurrency("BITFINEX", "ETH"));
        assertEquals(exchangeRates[0].getRate(), new BigDecimal("9.9"));
        assertEquals(exchangeRates[0].getTimestamp(), OffsetDateTime.parse("2018-05-26T09:42:23+00:00"));
        assertEquals(exchangeRates[1].getSrcCurrency(), new ExchangeCurrency("BITFINEX", "ETH"));
        assertEquals(exchangeRates[1].getDestCurrency(), new ExchangeCurrency("BITFINEX", "BTC"));
        assertEquals(exchangeRates[1].getRate(), new BigDecimal("0.0998"));
        assertEquals(exchangeRates[1].getTimestamp(), OffsetDateTime.parse("2018-05-26T09:42:23+00:00"));
    }

    @Test
    void priceUpdateWithWrongYear() {
        String priceUpdate = "018-05-26T09:42:23+00:00 BITFINEX BTC ETH 9.9 0.0998";
        assertFalse(InputParser.isPriceUpdate(priceUpdate));
        priceUpdate = "a018-05-26T09:42:23+00:00 BITFINEX BTC ETH 9.9 0.0998";
        assertFalse(InputParser.isPriceUpdate(priceUpdate));
    }

    @Test
    void priceUpdateWithWrongMonth() {
        String priceUpdate = "2018-15-26T09:42:23+00:00 BITFINEX BTC ETH 9.9 0.0998";
        assertFalse(InputParser.isPriceUpdate(priceUpdate));
        priceUpdate = "2018-5-26T09:42:23+00:00 BITFINEX BTC ETH 9.9 0.0998";
        assertFalse(InputParser.isPriceUpdate(priceUpdate));
    }

    @Test
    void priceUpdateWithWrongHour() {
        String priceUpdate = "2018-05-26T29:42:23+00:00 BITFINEX BTC ETH 9.9 0.0998";
        assertFalse(InputParser.isPriceUpdate(priceUpdate));
    }

    @Test
    void priceUpdateWithWrongMinute() {
        String priceUpdate = "2018-05-26T19:62:23+00:00 BITFINEX BTC ETH 9.9 0.0998";
        assertFalse(InputParser.isPriceUpdate(priceUpdate));
    }

    @Test
    void priceUpdateWithWrongSecond() {
        String priceUpdate = "2018-05-26T19:52:60+00:00 BITFINEX BTC ETH 9.9 0.0998";
        assertFalse(InputParser.isPriceUpdate(priceUpdate));
    }

    @Test
    void priceUpdateWithWrongTimeZone() {
        String priceUpdate = "2018-05-26T19:52:23+0:00 BITFINEX BTC ETH 9.9 0.0998";
        assertFalse(InputParser.isPriceUpdate(priceUpdate));
        priceUpdate = "2018-05-26T19:52:60+24:23 BITFINEX BTC ETH 9.9 0.0998";
        assertFalse(InputParser.isPriceUpdate(priceUpdate));
        priceUpdate = "2018-05-26T19:52:60+23:23 BITFINEX BTC ETH 9.9 0.0998";
        assertFalse(InputParser.isPriceUpdate(priceUpdate));
    }

    @Test
    void priceUpdateMissingSegments() {
        String priceUpdate = "2018-05-26T19:52:23+00:00 BITFINEX BTC ETH 9.9";
        assertFalse(InputParser.isPriceUpdate(priceUpdate));
    }

    @Test
    void priceUpdateWithExtraSegments() {
        String priceUpdate = "2018-05-26T19:52:23+00:00 BITFINEX BTC ETH 9.9 0.0998 XXX";
        assertFalse(InputParser.isPriceUpdate(priceUpdate));
    }

    @Test
    void priceUpdateWithInvalidFactors() {
        String priceUpdate = "2018-05-26T19:52:23+00:00 BITFINEX BTC ETH 10 0.1000001";
        assertFalse(InputParser.isPriceUpdateValid(priceUpdate));
    }

    @Test
    void exchangeRateRequestWellFormatted() {
        String exchangeRateRequest = "EXCHANGE_RATE_REQUEST BITFINEX BTC KRAKEN USD";
        assertTrue(InputParser.isExchangeRateRequest(exchangeRateRequest));
    }

    @Test
    void exchangeRateRequestMissingKeyword() {
        String exchangeRateRequest = "XCHANGE_RATE_REQUEST BITFINEX BTC KRAKEN USD";
        assertFalse(InputParser.isExchangeRateRequest(exchangeRateRequest));
    }

    @Test
    void exchangeRateRequestMissingSegments() {
        String exchangeRateRequest = "XCHANGE_RATE_REQUEST BITFINEX BTC KRAKEN";
        assertFalse(InputParser.isExchangeRateRequest(exchangeRateRequest));
    }

    @Test
    void exchangeRateRequestWithExtraSegments() {
        String exchangeRateRequest = "XCHANGE_RATE_REQUEST BITFINEX BTC KRAKEN USD XXX";
        assertFalse(InputParser.isExchangeRateRequest(exchangeRateRequest));
    }
}
